package de.lmu.ifi.dbs.elki.algorithm.outlier.subspace;

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
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.index.preprocessed.snn.SharedNearestNeighborPreprocessor;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Tests the SOD algorithm.
 * 
 * @author Lucia Cichella
 * @since 0.4.0
 */
public class SODTest extends AbstractSimpleAlgorithmTest {
  @Test
  public void testSOD() {
    Database db = makeSimpleDatabase(UNITTEST + "outlier-axis-subspaces-6d.ascii", 1345);

    // Parameterization
    ListParameterization params = new ListParameterization();
    params.addParameter(SOD.Parameterizer.KNN_ID, 25);
    params.addParameter(SharedNearestNeighborPreprocessor.Factory.NUMBER_OF_NEIGHBORS_ID, 19);

    // setup Algorithm
    SOD<DoubleVector> sod = ClassGenericsUtil.parameterizeOrAbort(SOD.class, params);
    testParameterizationOk(params);

    // run SOD on database
    OutlierResult result = sod.run(db);

    testSingleScore(result, 1293, 1.5167500);
    testAUC(db, "Noise", result, 0.949131652);
  }
}