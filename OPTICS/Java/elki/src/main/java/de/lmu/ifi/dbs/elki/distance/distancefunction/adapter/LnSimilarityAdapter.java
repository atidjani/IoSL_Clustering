package de.lmu.ifi.dbs.elki.distance.distancefunction.adapter;

/*
 This file is part of ELKI:
 Environment for Developing KDD-Applications Supported by Index-Structures

 Copyright (C) 2015
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

import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.similarity.SimilarityQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.similarityfunction.NormalizedSimilarityFunction;
import de.lmu.ifi.dbs.elki.utilities.Alias;

/**
 * Adapter from a normalized similarity function to a distance function using
 * <code>-log(sim)</code>.
 * 
 * @author Erich Schubert
 * @since 0.2
 * 
 * @apiviz.has Instance
 * 
 * @param <O> object class to process.
 */
@Alias("de.lmu.ifi.dbs.elki.distance.distancefunction.adapter.SimilarityAdapterLn")
public class LnSimilarityAdapter<O> extends AbstractSimilarityAdapter<O> {
  /**
   * Constructor.
   * 
   * @param similarityFunction Similarity function
   */
  public LnSimilarityAdapter(NormalizedSimilarityFunction<? super O> similarityFunction) {
    super(similarityFunction);
  }

  @Override
  public <T extends O> DistanceQuery<T> instantiate(Relation<T> database) {
    SimilarityQuery<T> similarityQuery = similarityFunction.instantiate(database);
    return new Instance<>(database, this, similarityQuery);
  }

  /**
   * Distance function instance
   * 
   * @author Erich Schubert
   * 
   * @param <O> Object type
   */
  public static class Instance<O> extends AbstractSimilarityAdapter.Instance<O> {
    /**
     * Constructor.
     * 
     * @param database Database
     * @param parent Parent distance
     * @param similarityQuery similarity Query to use
     */
    public Instance(Relation<O> database, DistanceFunction<? super O> parent, SimilarityQuery<O> similarityQuery) {
      super(database, parent, similarityQuery);
    }

    @Override
    public double transform(double similarity) {
      return -Math.log(similarity);
    }
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer<O> extends AbstractSimilarityAdapter.Parameterizer<O, NormalizedSimilarityFunction<? super O>> {
    @Override
    protected LnSimilarityAdapter<O> makeInstance() {
      return new LnSimilarityAdapter<>(similarityFunction);
    }

    @Override
    protected Class<? extends NormalizedSimilarityFunction<? super O>> getSimilarityRestriction() {
      return NORMALIZED_SIMILARITY;
    }
  }
}
