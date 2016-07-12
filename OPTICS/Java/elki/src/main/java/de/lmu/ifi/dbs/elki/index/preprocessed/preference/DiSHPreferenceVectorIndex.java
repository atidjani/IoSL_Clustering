package de.lmu.ifi.dbs.elki.index.preprocessed.preference;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2016
 Ludwig-Maximilians-Universität München
 Lehr- und Forschungseinheit für Datenbanksysteme
 ELKI Development Team

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.elki.algorithm.itemsetmining.APRIORI;
import de.lmu.ifi.dbs.elki.algorithm.itemsetmining.Itemset;
import de.lmu.ifi.dbs.elki.data.BitVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.type.SimpleTypeInformation;
import de.lmu.ifi.dbs.elki.data.type.VectorFieldTypeInformation;
import de.lmu.ifi.dbs.elki.database.HashmapDatabase;
import de.lmu.ifi.dbs.elki.database.UpdatableDatabase;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DoubleDBIDList;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.query.distance.PrimitiveDistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.range.RangeQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.database.relation.RelationUtil;
import de.lmu.ifi.dbs.elki.datasource.bundle.SingleObjectBundle;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.OnedimensionalDistanceFunction;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.result.FrequentItemsetsResult;
import de.lmu.ifi.dbs.elki.utilities.datastructures.BitsUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.exceptions.EmptyDataException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.WrongParameterValueException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleListParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.EnumParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * Preprocessor for DiSH preference vector assignment to objects of a certain
 * database.
 *
 * @author Elke Achtert
 * @since 0.4.0
 *
 * @param <V> Vector type
 */
@Description("Computes the preference vector of objects of a certain database according to the DiSH algorithm.")
public class DiSHPreferenceVectorIndex<V extends NumberVector> extends AbstractPreferenceVectorIndex<V> implements PreferenceVectorIndex<V> {
  /**
   * Logger to use.
   */
  private static final Logging LOG = Logging.getLogger(DiSHPreferenceVectorIndex.class);

  /**
   * Available strategies for determination of the preference vector.
   *
   * @apiviz.exclude
   */
  public enum Strategy {
    /**
     * Apriori strategy.
     */
    APRIORI,
    /**
     * Max intersection strategy.
     */
    MAX_INTERSECTION
  }

  /**
   * The epsilon value for each dimension.
   */
  protected double[] epsilon;

  /**
   * Threshold for minimum number of points in the neighborhood.
   */
  protected int minpts;

  /**
   * The strategy to determine the preference vector.
   */
  protected Strategy strategy;

  /**
   * Constructor.
   *
   * @param relation Relation to use
   * @param epsilon Epsilon value
   * @param minpts MinPts value
   * @param strategy Strategy
   */
  public DiSHPreferenceVectorIndex(Relation<V> relation, double[] epsilon, int minpts, Strategy strategy) {
    super(relation);
    this.epsilon = epsilon;
    this.minpts = minpts;
    this.strategy = strategy;
  }

  @Override
  public void initialize() {
    if(relation == null || relation.size() == 0) {
      throw new EmptyDataException();
    }

    storage = DataStoreUtil.makeStorage(relation.getDBIDs(), DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, long[].class);

    if(LOG.isDebugging()) {
      StringBuilder msg = new StringBuilder();
      msg.append("\n eps ").append(Arrays.asList(epsilon));
      msg.append("\n minpts ").append(minpts);
      msg.append("\n strategy ").append(strategy);
      LOG.debugFine(msg.toString());
    }

    long start = System.currentTimeMillis();
    FiniteProgress progress = LOG.isVerbose() ? new FiniteProgress("Preprocessing preference vector", relation.size(), LOG) : null;

    // only one epsilon value specified
    int dim = RelationUtil.dimensionality(relation);
    if(epsilon.length == 1 && dim != 1) {
      double eps = epsilon[0];
      epsilon = new double[dim];
      Arrays.fill(epsilon, eps);
    }

    // epsilons as string
    RangeQuery<V>[] rangeQueries = initRangeQueries(relation, dim);

    for(DBIDIter it = relation.iterDBIDs(); it.valid(); it.advance()) {
      StringBuilder msg = new StringBuilder();

      if(LOG.isDebugging()) {
        msg.append("\nid = ").append(DBIDUtil.toString(it));
        // msg.append(" ").append(database.get(id));
        // msg.append(" ").append(database.getObjectLabelQuery().get(id));
      }

      // determine neighbors in each dimension
      ModifiableDBIDs[] allNeighbors = new ModifiableDBIDs[dim];
      for(int d = 0; d < dim; d++) {
        DoubleDBIDList qrList = rangeQueries[d].getRangeForDBID(it, epsilon[d]);
        allNeighbors[d] = DBIDUtil.newHashSet(qrList);
      }

      if(LOG.isDebugging()) {
        for(int d = 0; d < dim; d++) {
          msg.append("\n neighbors [").append(d).append(']');
          msg.append(" (").append(allNeighbors[d].size()).append(") = ");
          msg.append(allNeighbors[d]);
        }
      }

      storage.put(it, determinePreferenceVector(relation, allNeighbors, msg));

      if(LOG.isDebugging()) {
        LOG.debugFine(msg.toString());
      }

      LOG.incrementProcessed(progress);
    }
    LOG.ensureCompleted(progress);

    long end = System.currentTimeMillis();
    // TODO: re-add timing code!
    if(LOG.isVerbose()) {
      long elapsedTime = end - start;
      LOG.verbose(this.getClass().getName() + " runtime: " + elapsedTime + " milliseconds.");
    }
  }

  /**
   * Determines the preference vector according to the specified neighbor ids.
   *
   * @param relation the database storing the objects
   * @param neighborIDs the list of ids of the neighbors in each dimension
   * @param msg a string buffer for debug messages
   * @return the preference vector
   */
  private long[] determinePreferenceVector(Relation<V> relation, ModifiableDBIDs[] neighborIDs, StringBuilder msg) {
    if(strategy.equals(Strategy.APRIORI)) {
      return determinePreferenceVectorByApriori(relation, neighborIDs, msg);
    }
    else if(strategy.equals(Strategy.MAX_INTERSECTION)) {
      return determinePreferenceVectorByMaxIntersection(neighborIDs, msg);
    }
    else {
      throw new IllegalStateException("Should never happen!");
    }
  }

  /**
   * Determines the preference vector with the apriori strategy.
   *
   * @param relation the database storing the objects
   * @param neighborIDs the list of ids of the neighbors in each dimension
   * @param msg a string buffer for debug messages
   * @return the preference vector
   */
  private long[] determinePreferenceVectorByApriori(Relation<V> relation, ModifiableDBIDs[] neighborIDs, StringBuilder msg) {
    int dimensionality = neighborIDs.length;

    // database for apriori
    UpdatableDatabase apriori_db = new HashmapDatabase();
    SimpleTypeInformation<?> bitmeta = VectorFieldTypeInformation.typeRequest(BitVector.class, dimensionality, dimensionality);
    for(DBIDIter it = relation.iterDBIDs(); it.valid(); it.advance()) {
      long[] bits = BitsUtil.zero(dimensionality);
      boolean allFalse = true;
      for(int d = 0; d < dimensionality; d++) {
        if(neighborIDs[d].contains(it)) {
          BitsUtil.setI(bits, d);
          allFalse = false;
        }
      }
      if(!allFalse) {
        SingleObjectBundle oaa = new SingleObjectBundle();
        oaa.append(bitmeta, new BitVector(bits, dimensionality));
        apriori_db.insert(oaa);
      }
    }
    APRIORI apriori = new APRIORI(minpts);
    FrequentItemsetsResult aprioriResult = apriori.run(apriori_db);

    // result of apriori
    List<Itemset> frequentItemsets = aprioriResult.getItemsets();
    if(LOG.isDebugging()) {
      msg.append("\n Frequent itemsets: ").append(frequentItemsets);
    }
    int maxSupport = 0;
    int maxCardinality = 0;
    long[] preferenceVector = BitsUtil.zero(dimensionality);
    for(Itemset itemset : frequentItemsets) {
      if((maxCardinality < itemset.length()) || (maxCardinality == itemset.length() && maxSupport == itemset.getSupport())) {
        preferenceVector = itemset.getItems();
        maxCardinality = itemset.length();
        maxSupport = itemset.getSupport();
      }
    }

    if(LOG.isDebugging()) {
      msg.append("\n preference ");
      msg.append(BitsUtil.toStringLow(preferenceVector, dimensionality));
      msg.append('\n');
      LOG.debugFine(msg.toString());
    }

    return preferenceVector;
  }

  /**
   * Determines the preference vector with the max intersection strategy.
   *
   * @param neighborIDs the list of ids of the neighbors in each dimension
   * @param msg a string buffer for debug messages
   * @return the preference vector
   */
  private long[] determinePreferenceVectorByMaxIntersection(ModifiableDBIDs[] neighborIDs, StringBuilder msg) {
    int dimensionality = neighborIDs.length;
    long[] preferenceVector = BitsUtil.zero(dimensionality);

    Map<Integer, ModifiableDBIDs> candidates = new HashMap<>(dimensionality);
    for(int i = 0; i < dimensionality; i++) {
      ModifiableDBIDs s_i = neighborIDs[i];
      if(s_i.size() > minpts) {
        candidates.put(i, s_i);
      }
    }
    if(LOG.isDebugging()) {
      msg.append("\n candidates ").append(candidates.keySet());
    }

    if(!candidates.isEmpty()) {
      int i = max(candidates);
      ModifiableDBIDs intersection = candidates.remove(i);
      BitsUtil.setI(preferenceVector, i);
      while(!candidates.isEmpty()) {
        ModifiableDBIDs newIntersection = DBIDUtil.newHashSet();
        i = maxIntersection(candidates, intersection, newIntersection);
        ModifiableDBIDs s_i = candidates.remove(i);
        // TODO: aren't we re-computing the same intersection here?
        newIntersection = DBIDUtil.intersection(intersection, s_i);
        intersection = newIntersection;

        if(intersection.size() < minpts) {
          break;
        }
        else {
          BitsUtil.setI(preferenceVector, i);
        }
      }
    }

    if(LOG.isDebugging()) {
      msg.append("\n preference ");
      msg.append(BitsUtil.toStringLow(preferenceVector, dimensionality));
      msg.append('\n');
      LOG.debug(msg.toString());
    }

    return preferenceVector;
  }

  /**
   * Returns the set with the maximum size contained in the specified map.
   *
   * @param candidates the map containing the sets
   * @return the set with the maximum size
   */
  private int max(Map<Integer, ModifiableDBIDs> candidates) {
    DBIDs maxSet = null;
    Integer maxDim = null;
    for(Integer nextDim : candidates.keySet()) {
      DBIDs nextSet = candidates.get(nextDim);
      if(maxSet == null || maxSet.size() < nextSet.size()) {
        maxSet = nextSet;
        maxDim = nextDim;
      }
    }

    return maxDim;
  }

  /**
   * Returns the index of the set having the maximum intersection set with the
   * specified set contained in the specified map.
   *
   * @param candidates the map containing the sets
   * @param set the set to intersect with
   * @param result the set to put the result in
   * @return the set with the maximum size
   */
  private int maxIntersection(Map<Integer, ModifiableDBIDs> candidates, DBIDs set, ModifiableDBIDs result) {
    Integer maxDim = null;
    for(Integer nextDim : candidates.keySet()) {
      DBIDs nextSet = candidates.get(nextDim);
      ModifiableDBIDs nextIntersection = DBIDUtil.intersection(set, nextSet);
      if(result.size() < nextIntersection.size()) {
        result = nextIntersection;
        maxDim = nextDim;
      }
    }

    return maxDim;
  }

  /**
   * Initializes the dimension selecting distancefunctions to determine the
   * preference vectors.
   *
   * @param relation the database storing the objects
   * @param dimensionality the dimensionality of the objects
   * @return the dimension selecting distancefunctions to determine the
   *         preference vectors
   */
  private RangeQuery<V>[] initRangeQueries(Relation<V> relation, int dimensionality) {
    @SuppressWarnings("unchecked")
    RangeQuery<V>[] rangeQueries = (RangeQuery<V>[]) new RangeQuery[dimensionality];
    for(int d = 0; d < dimensionality; d++) {
      rangeQueries[d] = relation.getRangeQuery(new PrimitiveDistanceQuery<>(relation, new OnedimensionalDistanceFunction(d)));
    }
    return rangeQueries;
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }

  @Override
  public String getLongName() {
    return "DiSH Preference Vectors";
  }

  @Override
  public String getShortName() {
    return "dish-pref";
  }

  @Override
  public void logStatistics() {
    // No statistics to log.
  }

  /**
   * Factory class.
   *
   * @author Erich Schubert
   *
   * @apiviz.stereotype factory
   * @apiviz.uses DiSHPreferenceVectorIndex oneway - - «create»
   *
   * @param <V> Vector type
   */
  public static class Factory<V extends NumberVector> extends AbstractPreferenceVectorIndex.Factory<V, DiSHPreferenceVectorIndex<V>> {
    /**
     * The default value for epsilon.
     */
    public static final double DEFAULT_EPSILON = 0.001;

    /**
     * A comma separated list of positive doubles specifying the maximum radius
     * of the neighborhood to be considered in each dimension for determination
     * of the preference vector (default is {@link #DEFAULT_EPSILON} in each
     * dimension). If only one value is specified, this value will be used for
     * each dimension.
     *
     * <p>
     * Key: {@code -dish.epsilon}
     * </p>
     * <p>
     * Default value: {@link #DEFAULT_EPSILON}
     * </p>
     */
    public static final OptionID EPSILON_ID = new OptionID("dish.epsilon", "A comma separated list of positive doubles specifying the " + "maximum radius of the neighborhood to be " + "considered in each dimension for determination of " + "the preference vector " + "(default is " + DEFAULT_EPSILON + " in each dimension). " + "If only one value is specified, this value " + "will be used for each dimension.");

    /**
     * Option name for {@link #MINPTS_ID}.
     */
    public static final String MINPTS_P = "dish.minpts";

    /**
     * Description for the determination of the preference vector.
     */
    private static final String CONDITION = "The value of the preference vector in dimension d_i is set to 1 " + "if the epsilon neighborhood contains more than " + MINPTS_P + " points and the following condition holds: " + "for all dimensions d_j: " + "|neighbors(d_i) intersection neighbors(d_j)| >= " + MINPTS_P + ".";

    /**
     * Positive threshold for minimum numbers of points in the
     * epsilon-neighborhood of a point, must satisfy following
     * {@link #CONDITION}.
     *
     * <p>
     * Key: {@code -dish.minpts}
     * </p>
     */
    public static final OptionID MINPTS_ID = new OptionID(MINPTS_P, "Positive threshold for minumum numbers of points in the epsilon-" + "neighborhood of a point. " + CONDITION);

    /**
     * Default strategy.
     */
    public static final Strategy DEFAULT_STRATEGY = Strategy.MAX_INTERSECTION;

    /**
     * The strategy for determination of the preference vector, available
     * strategies are: {@link Strategy#APRIORI } and
     * {@link Strategy#MAX_INTERSECTION}.
     *
     * <p>
     * Key: {@code -dish.strategy}
     * </p>
     * <p>
     * Default value: {@link #DEFAULT_STRATEGY}
     * </p>
     */
    public static final OptionID STRATEGY_ID = new OptionID("dish.strategy", "The strategy for determination of the preference vector, " + "available strategies are: [" + Strategy.APRIORI + "| " + Strategy.MAX_INTERSECTION + "]" + "(default is " + DEFAULT_STRATEGY + ")");

    /**
     * The epsilon value for each dimension.
     */
    protected double[] epsilon;

    /**
     * Threshold for minimum number of points in the neighborhood.
     */
    protected int minpts;

    /**
     * The strategy to determine the preference vector.
     */
    protected Strategy strategy;

    /**
     * Constructor.
     *
     * @param epsilon Epsilon
     * @param minpts Minpts
     * @param strategy Strategy
     */
    public Factory(double[] epsilon, int minpts, Strategy strategy) {
      super();
      this.epsilon = epsilon;
      this.minpts = minpts;
      this.strategy = strategy;
    }

    @Override
    public DiSHPreferenceVectorIndex<V> instantiate(Relation<V> relation) {
      return new DiSHPreferenceVectorIndex<>(relation, epsilon, minpts, strategy);
    }

    /**
     * Return the minpts value.
     *
     * @return minpts
     */
    public int getMinpts() {
      return minpts;
    }

    /**
     * Parameterization class.
     *
     * @author Erich Schubert
     *
     * @apiviz.exclude
     */
    public static class Parameterizer<V extends NumberVector> extends AbstractParameterizer {
      /**
       * The epsilon value for each dimension.
       */
      protected double[] epsilon;

      /**
       * Threshold for minimum number of points in the neighborhood.
       */
      protected int minpts;

      /**
       * The strategy to determine the preference vector.
       */
      protected Strategy strategy;

      @Override
      protected void makeOptions(Parameterization config) {
        super.makeOptions(config);
        final IntParameter minptsP = new IntParameter(MINPTS_ID) //
        .addConstraint(CommonConstraints.GREATER_EQUAL_ONE_INT);
        if(config.grab(minptsP)) {
          minpts = minptsP.getValue();
        }

        // parameter epsilon
        // todo: constraint auf positive werte
        final DoubleListParameter epsilonP = new DoubleListParameter(EPSILON_ID, true) //
        .setDefaultValue(new double[] { DEFAULT_EPSILON });
        if(config.grab(epsilonP)) {
          epsilon = epsilonP.getValue().clone();

          for(int d = 0; d < epsilon.length; d++) {
            if(epsilon[d] < 0) {
              config.reportError(new WrongParameterValueException(epsilonP, epsilonP.getValueAsString()));
            }
          }
        }

        // parameter strategy
        final EnumParameter<Strategy> strategyP = new EnumParameter<>(STRATEGY_ID, Strategy.class, DEFAULT_STRATEGY);
        if(config.grab(strategyP)) {
          strategy = strategyP.getValue();
        }
      }

      @Override
      protected Factory<V> makeInstance() {
        return new Factory<>(epsilon, minpts, strategy);
      }
    }
  }
}