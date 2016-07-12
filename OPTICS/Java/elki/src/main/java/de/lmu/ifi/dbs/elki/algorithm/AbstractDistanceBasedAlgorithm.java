package de.lmu.ifi.dbs.elki.algorithm;

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

import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * Abstract base class for distance-based algorithms.
 *
 * @author Arthur Zimek
 * @since 0.2
 *
 * @apiviz.landmark
 * @apiviz.has DistanceFunction
 * @apiviz.excludeSubtypes
 *
 * @param <O> the type of objects handled by this Algorithm
 * @param <R> the type of result to retrieve from this Algorithm
 */
public abstract class AbstractDistanceBasedAlgorithm<O, R extends Result> extends AbstractAlgorithm<R> implements DistanceBasedAlgorithm<O> {
  /**
   * Holds the instance of the distance function specified by
   * {@link DistanceBasedAlgorithm#DISTANCE_FUNCTION_ID}.
   */
  private DistanceFunction<? super O> distanceFunction;

  /**
   * Constructor.
   *
   * @param distanceFunction Distance function
   */
  protected AbstractDistanceBasedAlgorithm(DistanceFunction<? super O> distanceFunction) {
    super();
    this.distanceFunction = distanceFunction;
  }

  /**
   * Returns the distanceFunction.
   *
   * @return the distanceFunction
   */
  @Override
  public DistanceFunction<? super O> getDistanceFunction() {
    return distanceFunction;
  }

  /**
   * Parameterization helper class.
   *
   * @author Erich Schubert
   *
   * @apiviz.exclude
   */
  public abstract static class Parameterizer<O> extends AbstractParameterizer {
    /**
     * The distance function to use.
     */
    protected DistanceFunction<? super O> distanceFunction;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      ObjectParameter<DistanceFunction<? super O>> distanceFunctionP = makeParameterDistanceFunction(EuclideanDistanceFunction.class, DistanceFunction.class);
      if(config.grab(distanceFunctionP)) {
        distanceFunction = distanceFunctionP.instantiateClass(config);
      }
    }
  }
}