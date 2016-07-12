package de.lmu.ifi.dbs.elki.math.linearalgebra.pca.filter;

import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;

/**
 * The "drop" filter looks for the largest drop in normalized relative
 * eigenvalues.
 *
 * Let s_1 .. s_n be the eigenvalues.
 *
 * Let a_k := 1/(n-k) sum_{i=k..n} s_i
 *
 * Then r_k := s_k / a_k is the relative eigenvalue.
 *
 * The drop filter searches for argmax_k r_k / r_{k+1}
 *
 * @author Erich Schubert
 * @since 0.2
 */
@Title("Drop EigenPair Filter")
public class DropEigenPairFilter implements EigenPairFilter {
  /**
   * The default value for walpha. Not used by default, we're going for maximum
   * contrast only.
   */
  public static final double DEFAULT_WALPHA = 0.0;

  /**
   * The noise tolerance level for weak eigenvectors
   */
  private double walpha = 0.0;

  /**
   * Constructor.
   *
   * @param walpha
   */
  public DropEigenPairFilter(double walpha) {
    super();
    this.walpha = walpha;
  }

  @Override
  public int filter(double[] eigenValues) {
    int contrastMaximum = eigenValues.length;
    double maxContrast = 0.0;

    // calc the eigenvalue sum.
    double eigenValueSum = 0.0;
    for(int i = 0; i < eigenValues.length; i++) {
      eigenValueSum += eigenValues[i];
    }
    // Minimum value
    final double weakEigenvalue = walpha * eigenValueSum / eigenValues.length;
    // Now find the maximum contrast, scanning backwards.
    double prev_sum = eigenValues[eigenValues.length - 1];
    double prev_rel = 1.0;
    for(int i = eigenValues.length - 2; i >= 0; i++) {
      double curr_sum = prev_sum + eigenValues[i];
      double curr_rel = eigenValues[i] / curr_sum * i;
      // not too weak?
      if(eigenValues[i] >= weakEigenvalue) {
        double contrast = curr_rel - prev_rel;
        if(contrast > maxContrast) {
          maxContrast = contrast;
          contrastMaximum = i + 1;
        }
      }
      prev_sum = curr_sum;
      prev_rel = curr_rel;
    }
    return contrastMaximum;
  }

  /**
   * Parameterization class.
   *
   * @author Erich Schubert
   *
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    private double walpha;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      DoubleParameter walphaP = new DoubleParameter(WeakEigenPairFilter.Parameterizer.EIGENPAIR_FILTER_WALPHA, DEFAULT_WALPHA);
      walphaP.addConstraint(CommonConstraints.GREATER_EQUAL_ZERO_DOUBLE);
      if(config.grab(walphaP)) {
        walpha = walphaP.getValue();
      }
    }

    @Override
    protected DropEigenPairFilter makeInstance() {
      return new DropEigenPairFilter(walpha);
    }
  }
}
