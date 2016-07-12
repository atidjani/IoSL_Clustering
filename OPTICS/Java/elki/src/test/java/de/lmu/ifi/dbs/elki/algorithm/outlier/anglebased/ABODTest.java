package de.lmu.ifi.dbs.elki.algorithm.outlier.anglebased;

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
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Tests the ABOD algorithm.
 *
 * Note: we don't implement JUnit4Test, as this test is slow.
 *
 * @author Lucia Cichella
 * @since 0.4.0
 */
public class ABODTest extends AbstractSimpleAlgorithmTest {
  @Test
  public void testABOD() {
    Database db = makeSimpleDatabase(UNITTEST + "outlier-3d-3clusters.ascii", 960);

    // Parameterization
    ListParameterization params = new ListParameterization();

    // setup Algorithm
    ABOD<DoubleVector> abod = ClassGenericsUtil.parameterizeOrAbort(ABOD.class, params);
    testParameterizationOk(params);

    // run ABOD on database
    OutlierResult result = abod.run(db);

    testAUC(db, "Noise", result, 0.9297962962962);
    testSingleScore(result, 945, 2.0897348547799E-5);
  }
}