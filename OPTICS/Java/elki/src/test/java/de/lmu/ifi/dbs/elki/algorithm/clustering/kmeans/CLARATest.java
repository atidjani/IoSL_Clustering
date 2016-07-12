package de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans;

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
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.MedoidModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Performs a full KMeans run, and compares the result with a clustering derived
 * from the data set labels. This test ensures that KMeans's performance doesn't
 * unexpectedly drop on this data set (and also ensures that the algorithms
 * work, as a side effect).
 *
 * @author Katharina Rausch
 * @author Erich Schubert
 * @since 0.4.0
 */
public class CLARATest extends AbstractSimpleAlgorithmTest {
  /**
   * Run CLARA with fixed parameters and compare the result to a golden
   * standard.
   */
  @Test
  public void testCLARA() {
    Database db = makeSimpleDatabase(UNITTEST + "different-densities-2d-no-noise.ascii", 1000);

    // Setup algorithm
    ListParameterization params = new ListParameterization();
    params.addParameter(KMeans.K_ID, 5);
    // These parameters are chosen suboptimal, for better regression testing.
    params.addParameter(CLARA.Parameterizer.RANDOM_ID, 1);
    params.addParameter(CLARA.Parameterizer.NUMSAMPLES_ID, 2);
    params.addParameter(CLARA.Parameterizer.SAMPLESIZE_ID, 50);
    CLARA<DoubleVector> kmedians = ClassGenericsUtil.parameterizeOrAbort(CLARA.class, params);
    testParameterizationOk(params);

    // run KMedians on database
    Clustering<MedoidModel> result = kmedians.run(db);
    testFMeasure(db, result, 0.9960200);
    testClusterSizes(result, new int[] { 198, 200, 200, 200, 202 });
  }
}