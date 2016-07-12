package de.lmu.ifi.dbs.elki.algorithm.clustering.correlation;

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

import org.junit.Test;

import de.lmu.ifi.dbs.elki.algorithm.AbstractSimpleAlgorithmTest;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.math.linearalgebra.pca.filter.LimitEigenPairFilter;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.ParameterException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Perform a full 4C run, and compare the result with a clustering derived from
 * the data set labels. This test ensures that 4C performance doesn't
 * unexpectedly drop on this data set (and also ensures that the algorithms
 * work, as a side effect).
 *
 * @author Erich Schubert
 * @author Katharina Rausch
 * @since 0.3
 */
public class FourCTest extends AbstractSimpleAlgorithmTest {
  /**
   * Run 4F with fixed parameters and compare the result to a golden standard.
   *
   * @throws ParameterException on errors.
   */
  @Test
  public void testFourCResults() {
    Database db = makeSimpleDatabase(UNITTEST + "hierarchical-3d2d1d.csv", 600);

    // Setup 4C
    ListParameterization params = new ListParameterization();
    params.addParameter(DBSCAN.Parameterizer.EPSILON_ID, 0.30);
    params.addParameter(DBSCAN.Parameterizer.MINPTS_ID, 50);
    params.addParameter(LimitEigenPairFilter.Parameterizer.EIGENPAIR_FILTER_DELTA, 0.5);
    params.addParameter(FourC.Settings.Parameterizer.LAMBDA_ID, 1);

    FourC<DoubleVector> fourc = ClassGenericsUtil.parameterizeOrAbort(FourC.class, params);
    testParameterizationOk(params);

    // run 4C on database
    Clustering<Model> result = fourc.run(db);

    testFMeasure(db, result, 0.7052);
    testClusterSizes(result, new int[] { 218, 382 });
  }

  /**
   * Run ERiC with fixed parameters and compare the result to a golden standard.
   *
   * @throws ParameterException on errors.
   */
  @Test
  public void testFourCOverlap() {
    Database db = makeSimpleDatabase(UNITTEST + "correlation-overlap-3-5d.ascii", 650);

    // Setup algorithm
    ListParameterization params = new ListParameterization();
    // 4C
    params.addParameter(DBSCAN.Parameterizer.EPSILON_ID, 3);
    params.addParameter(DBSCAN.Parameterizer.MINPTS_ID, 50);
    params.addParameter(LimitEigenPairFilter.Parameterizer.EIGENPAIR_FILTER_DELTA, 0.5);
    params.addParameter(FourC.Settings.Parameterizer.LAMBDA_ID, 3);

    FourC<DoubleVector> fourc = ClassGenericsUtil.parameterizeOrAbort(FourC.class, params);
    testParameterizationOk(params);

    // run 4C on database
    Clustering<Model> result = fourc.run(db);
    testFMeasure(db, result, 0.9073744);
    testClusterSizes(result, new int[] { 200, 202, 248 });
  }
}