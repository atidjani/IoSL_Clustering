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

import de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.HiSC;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.QueryUtil;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRef;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.KNNList;
import de.lmu.ifi.dbs.elki.database.query.knn.KNNQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.database.relation.RelationUtil;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.utilities.datastructures.BitsUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.exceptions.EmptyDataException;
import de.lmu.ifi.dbs.elki.utilities.io.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * Preprocessor for HiSC preference vector assignment to objects of a certain
 * database.
 *
 * @author Elke Achtert
 * @since 0.4.0
 *
 * @see HiSC
 *
 * @param <V> Vector type
 */
@Title("HiSC Preprocessor")
@Description("Computes the preference vector of objects of a certain database according to the HiSC algorithm.")
public class HiSCPreferenceVectorIndex<V extends NumberVector> extends AbstractPreferenceVectorIndex<V> implements PreferenceVectorIndex<V> {
  /**
   * Logger to use.
   */
  private static final Logging LOG = Logging.getLogger(HiSCPreferenceVectorIndex.class);

  /**
   * Holds the value of parameter alpha.
   */
  protected double alpha;

  /**
   * Holds the value of parameter k.
   */
  protected int k;

  /**
   * Constructor.
   *
   * @param relation Relation in use
   * @param alpha Alpha value
   * @param k k value
   */
  public HiSCPreferenceVectorIndex(Relation<V> relation, double alpha, int k) {
    super(relation);
    this.alpha = alpha;
    this.k = k;
  }

  @Override
  public void initialize() {
    if(relation == null || relation.size() <= 0) {
      throw new EmptyDataException();
    }

    storage = DataStoreUtil.makeStorage(relation.getDBIDs(), DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, long[].class);

    StringBuilder msg = new StringBuilder();

    long start = System.currentTimeMillis();
    FiniteProgress progress = LOG.isVerbose() ? new FiniteProgress("Preprocessing preference vector", relation.size(), LOG) : null;

    KNNQuery<V> knnQuery = QueryUtil.getKNNQuery(relation, EuclideanDistanceFunction.STATIC, k);

    for(DBIDIter it = relation.iterDBIDs(); it.valid(); it.advance()) {
      if(LOG.isDebugging()) {
        msg.append("\n\nid = ").append(DBIDUtil.toString(it));
        // /msg.append(" ").append(database.getObjectLabelQuery().get(id));
        msg.append("\n knns: ");
      }

      KNNList knns = knnQuery.getKNNForDBID(it, k);
      long[] preferenceVector = determinePreferenceVector(relation, it, knns, msg);
      storage.put(it, preferenceVector);

      LOG.incrementProcessed(progress);
    }
    LOG.ensureCompleted(progress);

    if(LOG.isDebugging()) {
      LOG.debugFine(msg.toString());
    }

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
   * @param id the id of the object for which the preference vector should be
   *        determined
   * @param neighborIDs the ids of the neighbors
   * @param msg a string buffer for debug messages
   * @return the preference vector
   */
  private long[] determinePreferenceVector(Relation<V> relation, DBIDRef id, DBIDs neighborIDs, StringBuilder msg) {
    // variances
    double[] variances = RelationUtil.variances(relation, relation.get(id), neighborIDs);

    // preference vector
    long[] preferenceVector = BitsUtil.zero(variances.length);
    for(int d = 0; d < variances.length; d++) {
      if(variances[d] < alpha) {
        BitsUtil.setI(preferenceVector, d);
      }
    }

    if(msg != null && LOG.isDebugging()) {
      msg.append("\nalpha ").append(alpha);
      msg.append("\nvariances ");
      msg.append(FormatUtil.format(variances, ", ", FormatUtil.NF4));
      msg.append("\npreference ");
      msg.append(BitsUtil.toStringLow(preferenceVector, variances.length));
    }

    return preferenceVector;
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }

  @Override
  public String getLongName() {
    return "HiSC Preference Vectors";
  }

  @Override
  public String getShortName() {
    return "hisc-pref";
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
   * @apiviz.uses HiSCPreferenceVectorIndex oneway - - «create»
   *
   * @param <V> Vector type
   */
  public static class Factory<V extends NumberVector> extends AbstractPreferenceVectorIndex.Factory<V, HiSCPreferenceVectorIndex<V>> {
    /**
     * The default value for alpha.
     */
    public static final double DEFAULT_ALPHA = 0.01;

    /**
     * The maximum absolute variance along a coordinate axis. Must be in the
     * range of [0.0, 1.0).
     * <p>
     * Default value: {@link #DEFAULT_ALPHA}
     * </p>
     * <p>
     * Key: {@code -hisc.alpha}
     * </p>
     */
    public static final OptionID ALPHA_ID = new OptionID("hisc.alpha", "The maximum absolute variance along a coordinate axis.");

    /**
     * The number of nearest neighbors considered to determine the preference
     * vector. If this value is not defined, k is set to three times of the
     * dimensionality of the database objects.
     * <p>
     * Key: {@code -hisc.k}
     * </p>
     * <p>
     * Default value: three times of the dimensionality of the database objects
     * </p>
     */
    public static final OptionID K_ID = new OptionID("hisc.k", "The number of nearest neighbors considered to determine the preference vector. If this value is not defined, k ist set to three times of the dimensionality of the database objects.");

    /**
     * Holds the value of parameter {@link #ALPHA_ID}.
     */
    protected double alpha;

    /**
     * Holds the value of parameter {@link #K_ID}.
     */
    protected Integer k;

    /**
     * Constructor.
     *
     * @param alpha Alpha
     * @param k k
     */
    public Factory(double alpha, Integer k) {
      super();
      this.alpha = alpha;
      this.k = k;
    }

    @Override
    public HiSCPreferenceVectorIndex<V> instantiate(Relation<V> relation) {
      final int usek;
      if(k == null) {
        usek = 3 * RelationUtil.dimensionality(relation);
      }
      else {
        usek = k;
      }
      return new HiSCPreferenceVectorIndex<>(relation, alpha, usek);
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
       * Holds the value of parameter {@link #ALPHA_ID}.
       */
      protected double alpha;

      /**
       * Holds the value of parameter {@link #K_ID}.
       */
      protected Integer k;

      @Override
      protected void makeOptions(Parameterization config) {
        super.makeOptions(config);
        final DoubleParameter alphaP = new DoubleParameter(ALPHA_ID, DEFAULT_ALPHA);
        alphaP.addConstraint(CommonConstraints.GREATER_THAN_ZERO_DOUBLE);
        alphaP.addConstraint(CommonConstraints.LESS_THAN_ONE_DOUBLE);
        if(config.grab(alphaP)) {
          alpha = alphaP.doubleValue();
        }

        final IntParameter kP = new IntParameter(K_ID);
        kP.addConstraint(CommonConstraints.GREATER_EQUAL_ONE_INT);
        kP.setOptional(true);
        if(config.grab(kP)) {
          k = kP.intValue();
        }
      }

      @Override
      protected Factory<V> makeInstance() {
        return new Factory<>(alpha, k);
      }
    }
  }
}
