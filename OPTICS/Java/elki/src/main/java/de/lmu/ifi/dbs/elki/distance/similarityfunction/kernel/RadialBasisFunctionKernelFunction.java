package de.lmu.ifi.dbs.elki.distance.similarityfunction.kernel;

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

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractNumberVectorDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.similarityfunction.AbstractVectorSimilarityFunction;
import de.lmu.ifi.dbs.elki.utilities.Alias;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;

/**
 * Gaussian radial basis function kernel (RBF Kernel).
 * 
 * @author Erich Schubert
 * @since 0.2
 */
@Alias("rbf")
public class RadialBasisFunctionKernelFunction extends AbstractVectorSimilarityFunction {
  /**
   * Scaling factor gamma. (= - 1/(2sigma^2))
   */
  private final double gamma;

  /**
   * Constructor.
   * 
   * @param sigma Scaling parameter sigma
   */
  public RadialBasisFunctionKernelFunction(double sigma) {
    super();
    this.gamma = -.5 / (sigma * sigma);
  }

  @Override
  public double similarity(NumberVector o1, NumberVector o2) {
    final int dim = AbstractNumberVectorDistanceFunction.dimensionality(o1, o2);
    double sim = 0.;
    for(int i = 0; i < dim; i++) {
      final double v = o1.doubleValue(i) - o2.doubleValue(i);
      sim += v * v;
    }
    return Math.exp(gamma * sim);
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    /**
     * Sigma parameter: standard deviation.
     */
    public static final OptionID SIGMA_ID = new OptionID("kernel.rbf.sigma", "Standard deviation of the Gaussian RBF kernel.");

    /**
     * Sigma parameter
     */
    protected double sigma = 1.;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      final DoubleParameter sigmaP = new DoubleParameter(SIGMA_ID, 1.);
      sigmaP.addConstraint(CommonConstraints.GREATER_THAN_ZERO_DOUBLE);
      if(config.grab(sigmaP)) {
        sigma = sigmaP.doubleValue();
      }
    }

    @Override
    protected RadialBasisFunctionKernelFunction makeInstance() {
      return new RadialBasisFunctionKernelFunction(sigma);
    }
  }
}
