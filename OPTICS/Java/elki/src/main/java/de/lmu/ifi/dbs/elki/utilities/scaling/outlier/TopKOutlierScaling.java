package de.lmu.ifi.dbs.elki.utilities.scaling.outlier;

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

import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.datastructures.QuickSelect;
import de.lmu.ifi.dbs.elki.utilities.datastructures.arraylike.ArrayLikeUtil;
import de.lmu.ifi.dbs.elki.utilities.datastructures.arraylike.NumberArrayAdapter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * Outlier scaling function that only keeps the top k outliers.
 * 
 * @author Erich Schubert
 * @since 0.3
 */
public class TopKOutlierScaling implements OutlierScalingFunction {
  /**
   * Parameter to specify the number of outliers to keep
   * <p>
   * Key: {@code -topk.k}
   * </p>
   */
  public static final OptionID K_ID = new OptionID("topk.k", "Number of outliers to keep.");

  /**
   * Parameter to specify the lambda value
   * <p>
   * Key: {@code -topk.binary}
   * </p>
   */
  public static final OptionID BINARY_ID = new OptionID("topk.binary", "Make the top k a binary scaling.");

  /**
   * Number of outliers to keep.
   */
  private int k = -1;

  /**
   * Do a binary decision
   */
  private boolean binary = false;

  /**
   * The value we cut off at.
   */
  private double cutoff;

  /**
   * The "ground" value
   */
  private double ground;

  /**
   * The maximum value
   */
  private double max;

  /**
   * Constructor.
   * 
   * @param k
   * @param binary
   */
  public TopKOutlierScaling(int k, boolean binary) {
    super();
    this.k = k;
    this.binary = binary;
  }

  @Override
  public void prepare(OutlierResult or) {
    if(k <= 0) {
      LoggingUtil.warning("No k configured for Top-k outlier scaling!");
    }
    DBIDIter order = or.getOrdering().order(or.getOrdering().getDBIDs()).iter();
    for(int i = 0; i < k && order.valid(); i++, order.advance()) {
      cutoff = or.getScores().doubleValue(order);
    }
    max = or.getOutlierMeta().getActualMaximum();
    ground = or.getOutlierMeta().getTheoreticalBaseline();
    if(Double.isInfinite(ground) || Double.isNaN(ground)) {
      ground = or.getOutlierMeta().getTheoreticalMinimum();
    }
    if(Double.isInfinite(ground) || Double.isNaN(ground)) {
      ground = or.getOutlierMeta().getActualMinimum();
    }
    if(Double.isInfinite(ground) || Double.isNaN(ground)) {
      ground = Math.min(0.0, cutoff);
    }
  }

  @Override
  public <A> void prepare(A array, NumberArrayAdapter<?, A> adapter) {
    if(k <= 0) {
      LoggingUtil.warning("No k configured for Top-k outlier scaling!");
    }
    double[] scores = ArrayLikeUtil.toPrimitiveDoubleArray(array, adapter);
    QuickSelect.quickSelect(scores, k);
    cutoff = scores[k - 1];
    max = Double.NEGATIVE_INFINITY;
    for(double v : scores) {
      max = Math.max(max, v);
    }
    ground = Math.min(0.0, cutoff);
  }

  @Override
  public double getMax() {
    if(binary) {
      return 1.0;
    }
    return max;
  }

  @Override
  public double getMin() {
    if(binary) {
      return 0.0;
    }
    return ground;
  }

  @Override
  public double getScaled(double value) {
    if(binary) {
      if(value >= cutoff) {
        return 1;
      }
      else {
        return 0;
      }
    }
    else {
      if(value >= cutoff) {
        return (value - ground) / (max - ground);
      }
      else {
        return 0.0;
      }
    }
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    protected int k = 0;

    protected boolean binary = false;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      IntParameter kP = new IntParameter(K_ID);
      kP.addConstraint(CommonConstraints.GREATER_THAN_ONE_INT);
      if(config.grab(kP)) {
        k = kP.intValue();
      }

      Flag binaryF = new Flag(BINARY_ID);
      if(config.grab(binaryF)) {
        binary = binaryF.isTrue();
      }
    }

    @Override
    protected TopKOutlierScaling makeInstance() {
      return new TopKOutlierScaling(k, binary);
    }
  }
}
