package de.lmu.ifi.dbs.elki.math.statistics.distribution;

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

import java.util.Random;

import de.lmu.ifi.dbs.elki.math.MathUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.random.RandomFactory;

/**
 * Chi-Squared distribution (a specialization of the Gamma distribution).
 * 
 * @author Erich Schubert
 * @since 0.2
 */
public class ChiSquaredDistribution extends GammaDistribution {
  /**
   * Constructor.
   * 
   * @param dof Degrees of freedom.
   */
  public ChiSquaredDistribution(double dof) {
    this(dof, (Random) null);
  }

  /**
   * Constructor.
   * 
   * @param dof Degrees of freedom.
   * @param random Random generator.
   */
  public ChiSquaredDistribution(double dof, Random random) {
    super(.5 * dof, .5, random);
  }

  /**
   * Constructor.
   * 
   * @param dof Degrees of freedom.
   * @param random Random generator.
   */
  public ChiSquaredDistribution(double dof, RandomFactory random) {
    super(.5 * dof, .5, random);
  }

  /**
   * The CDF, static version.
   * 
   * @param val Value
   * @param dof Degrees of freedom.
   * @return cdf value
   */
  public static double cdf(double val, double dof) {
    return regularizedGammaP(.5 * dof, .5 * val);
  }

  /**
   * Chi-Squared distribution PDF (with 0.0 for x &lt; 0)
   * 
   * @param x query value
   * @param dof Degrees of freedom.
   * @return probability density
   */
  public static double pdf(double x, double dof) {
    if(x <= 0) {
      return 0.0;
    }
    if(dof <= 0) {
      return Double.NaN;
    }
    final double k = dof * .5;
    if(Math.abs(k - 1.0) < Double.MIN_NORMAL) {
      return Math.exp(-x * 2.0) * 2.0;
    }
    return Math.exp((k - 1.0) * Math.log(x * 2.0) - x * 2.0 - logGamma(k)) * 2.0;
  }

  /**
   * Chi-Squared distribution PDF (with 0.0 for x &lt; 0)
   * 
   * @param x query value
   * @param dof Degrees of freedom.
   * @return probability density
   */
  public static double logpdf(double x, double dof) {
    if(x <= 0) {
      return Double.NEGATIVE_INFINITY;
    }
    if(dof <= 0) {
      return Double.NaN;
    }
    final double k = dof * .5;
    final double twox = x * 2.0;
    return twox == Double.POSITIVE_INFINITY ? Double.NEGATIVE_INFINITY : //
        (k - 1.0) * Math.log(twox) - twox - logGamma(k) + MathUtil.LOG2;
  }

  /**
   * Return the quantile function for this distribution
   * 
   * Reference:
   * <p>
   * Algorithm AS 91: The percentage points of the $\chi$^2 distribution<br />
   * D.J. Best, D. E. Roberts<br />
   * Journal of the Royal Statistical Society. Series C (Applied Statistics)
   * </p>
   * 
   * @param x Quantile
   * @param dof Degrees of freedom
   * @return quantile position
   */
  @Reference(title = "Algorithm AS 91: The percentage points of the $\\chi^2$ distribution", authors = "D.J. Best, D. E. Roberts", booktitle = "Journal of the Royal Statistical Society. Series C (Applied Statistics)")
  public static double quantile(double x, double dof) {
    return GammaDistribution.quantile(x, .5 * dof, .5);
  }

  @Override
  public String toString() {
    return "ChiSquaredDistribution(dof=" + (2 * getK()) + ")";
  }

  /**
   * Parameterization class
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractDistribution.Parameterizer {
    /**
     * Degrees of freedom parameter.
     */
    public static final OptionID DOF_ID = new OptionID("distribution.chi.dof", "Chi distribution degrees of freedom parameter.");

    /** Parameters. */
    double dof;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);

      DoubleParameter dofP = new DoubleParameter(DOF_ID);
      if(config.grab(dofP)) {
        dof = dofP.doubleValue();
      }
    }

    @Override
    protected ChiSquaredDistribution makeInstance() {
      return new ChiSquaredDistribution(dof, rnd);
    }
  }
}
