package de.lmu.ifi.dbs.elki.math;

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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.math.linearalgebra.fitting.FittingFunction;
import de.lmu.ifi.dbs.elki.math.linearalgebra.fitting.GaussianFittingFunction;
import de.lmu.ifi.dbs.elki.math.linearalgebra.fitting.LevenbergMarquardtMethod;
import de.lmu.ifi.dbs.elki.math.statistics.KernelDensityEstimator;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.NormalDistribution;
import de.lmu.ifi.dbs.elki.math.statistics.kernelfunctions.GaussianKernelDensityFunction;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * JUnit test that does a complete Kernel and Levenberg-Marquadt fitting.
 * 
 * @author Erich Schubert
 * @since 0.2
 */
public class KernelDensityFittingTest {
  // the following values depend on the data set used!
  String dataset = "data/testdata/unittests/gaussian-1d-for-fitting.csv";

  // data set size for verification.
  int realsize = 100;

  double realmean = 0.5;

  double realstdd = 1.5;

  /**
   * The test will load the given data set and perform a Levenberq-Marquadt
   * fitting on a kernelized density estimation. The test evaluates the fitting
   * quality to ensure that the results remain stable and significantly better
   * than traditional estimation.
   * 
   * @throws ParameterException on errors.
   */
  @Test
  public final void testFitDoubleArray() {
    ListParameterization config = new ListParameterization();
    // Input
    config.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID, dataset);
    // This data was generated with a mean of 0.0 and stddev 1.23,

    // get database
    Database db = ClassGenericsUtil.parameterizeOrAbort(StaticArrayDatabase.class, config);
    db.initialize();
    Relation<DoubleVector> rep = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);

    // verify data set size.
    assertEquals("Data set size doesn't match parameters.", realsize, rep.size());

    double splitval = 0.5;

    double[] fulldata = new double[rep.size()];
    // transform into double array
    {
      int i = 0;
      for(DBIDIter iditer = rep.iterDBIDs(); iditer.valid(); iditer.advance()) {
        fulldata[i] = rep.get(iditer).doubleValue(0);
        i++;
      }
    }
    Arrays.sort(fulldata);

    // Check that the initial parameters match what we were expecting from the
    // data.
    double[] fullparams = estimateInitialParameters(fulldata);
    assertEquals("Full Mean before fitting", 0.4446105, fullparams[0], 0.0001);
    assertEquals("Full Stddev before fitting", 1.4012001, fullparams[1], 0.0001);

    // Do a fit using only part of the data and check the results are right.
    double[] fullfit = run(fulldata, fullparams);
    assertEquals("Full Mean after fitting", 0.64505, fullfit[0], 0.01);
    assertEquals("Full Stddev after fitting", 1.5227889, fullfit[1], 0.01);

    int splitpoint = 0;
    while(fulldata[splitpoint] < splitval && splitpoint < fulldata.length) {
      splitpoint++;
    }
    double[] halfdata = Arrays.copyOf(fulldata, splitpoint);

    // Check that the initial parameters match what we were expecting from the
    // data.
    double[] params = estimateInitialParameters(halfdata);
    assertEquals("Mean before fitting", -0.65723044, params[0], 0.0001);
    assertEquals("Stddev before fitting", 1.0112391, params[1], 0.0001);

    // Do a fit using only part of the data and check the results are right.
    double[] ps = run(halfdata, params);
    assertEquals("Mean after fitting", 0.45980, ps[0], 0.01);
    assertEquals("Stddev after fitting", 1.320427, ps[1], 0.01);
  }

  private double[] estimateInitialParameters(double[] data) {
    double[] params = new double[3];
    // compute averages
    MeanVariance mv = new MeanVariance();
    for(double d : data) {
      mv.put(d);
    }

    params[0] = mv.getMean();
    params[1] = mv.getSampleStddev();
    // guess initial amplitude for an gaussian distribution.
    double c1 = NormalDistribution.erf(Math.abs(data[0] - params[0]) / (params[1] * Math.sqrt(2)));
    double c2 = NormalDistribution.erf(Math.abs(data[data.length - 1] - params[0]) / (params[1] * Math.sqrt(2)));
    params[2] = 1.0 / Math.min(c1, c2);
    // System.out.println("Mean: " + params[0] + " Stddev: " + params[1] +
    // " Amp: " + params[2]);
    return params;
  }

  private double[] run(double[] data, double[] params) {
    FittingFunction func = GaussianFittingFunction.STATIC;
    boolean[] dofit = { true, true, true };
    KernelDensityEstimator de = new KernelDensityEstimator(data, GaussianKernelDensityFunction.KERNEL, 1e-10);
    LevenbergMarquardtMethod fit = new LevenbergMarquardtMethod(func, params, dofit, data, de.getDensity(), de.getVariance());
    // for(int i = 0; i < 100; i++) {
    // fit.iterate();
    // double[] ps = fit.getParams();
    // System.out.println("Mean: " + ps[0] + " Stddev: " + ps[1] + " Amp: " +
    // ps[2]+" Chi: "+fit.getChiSq());
    // }
    fit.run();
    // double[] ps = fit.getParams();
    // System.out.println("Result: "+ps[0]+" "+ps[1]+" "+ps[2]+" Chi: "+fit.getChiSq()+" Iter: "+fit.maxruns);
    return fit.getParams();
  }
}