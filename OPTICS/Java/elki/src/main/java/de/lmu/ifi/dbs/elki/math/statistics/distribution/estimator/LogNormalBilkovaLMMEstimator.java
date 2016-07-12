package de.lmu.ifi.dbs.elki.math.statistics.distribution.estimator;

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

import de.lmu.ifi.dbs.elki.math.statistics.distribution.LogNormalDistribution;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.NormalDistribution;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;

/**
 * Alternate estimate the parameters of a log Gamma Distribution, using the
 * methods of L-Moments (LMM) for the Generalized Normal Distribution.
 * 
 * Reference:
 * <p>
 * D. Bílková<br />
 * Lognormal distribution and using L-moment method for estimating its
 * parameters<br />
 * Int. Journal of Mathematical Models and Methods in Applied Sciences (NAUN)
 * </p>
 * 
 * See also {@link LogNormalLMMEstimator} for a similar estimator, based on the
 * generalized normal distribution, as used by Hosking.
 * 
 * @author Erich Schubert
 * @since 0.6.0
 * 
 * @apiviz.has LogNormalDistribution
 */
@Reference(authors = "D. Bílková", title = "Lognormal distribution and using L-moment method for estimating its parameters", booktitle = "Int. Journal of Mathematical Models and Methods in Applied Sciences (NAUN)", url = "http://www.naun.org/multimedia/NAUN/m3as/17-079.pdf")
public class LogNormalBilkovaLMMEstimator extends AbstractLMMEstimator<LogNormalDistribution> {
  /**
   * Static instance.
   */
  public static final LogNormalBilkovaLMMEstimator STATIC = new LogNormalBilkovaLMMEstimator();

  /**
   * Scaling constant.
   */
  private static final double SQRT8_3 = Math.sqrt(8. / 3.);

  /**
   * Constructor. Private: use static instance.
   */
  private LogNormalBilkovaLMMEstimator() {
    super();
  }

  @Override
  public int getNumMoments() {
    return 3;
  }

  @Override
  public LogNormalDistribution estimateFromLMoments(double[] xmom) {
    if (!(xmom[1] > 0.) || !(Math.abs(xmom[2]) < 1.0) || !(xmom[2] > 0.)) {
      throw new ArithmeticException("L-Moments invalid");
    }
    final double z = SQRT8_3 * NormalDistribution.standardNormalQuantile(.5 * (1. + xmom[2])), z2 = z * z;
    final double sigma = 0.999281 * z - 0.006118 * z * z2 + 0.000127 * z * z2 * z2;
    final double sigmasqhalf = sigma * sigma * .5;
    final double logmu = Math.log(xmom[1] / NormalDistribution.erf(.5 * sigma)) - sigmasqhalf;
    return new LogNormalDistribution(logmu, Math.max(sigma, Double.MIN_NORMAL), xmom[0] - Math.exp(logmu + sigmasqhalf));
  }

  @Override
  public Class<? super LogNormalDistribution> getDistributionClass() {
    return LogNormalDistribution.class;
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    @Override
    protected LogNormalBilkovaLMMEstimator makeInstance() {
      return STATIC;
    }
  }
}
