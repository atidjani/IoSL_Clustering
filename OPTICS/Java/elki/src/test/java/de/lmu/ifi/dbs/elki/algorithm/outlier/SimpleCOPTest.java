package de.lmu.ifi.dbs.elki.algorithm.outlier;

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
 * Tests the SimpleCOP algorithm.
 *
 * @author Erich Schubert
 * @since 0.4.0
 */
public class SimpleCOPTest extends AbstractSimpleAlgorithmTest {
  @Test
  public void testSimpleCOP() {
    Database db = makeSimpleDatabase(UNITTEST + "outlier-parabolic.ascii", 530);

    // Parameterization
    ListParameterization params = new ListParameterization();
    params.addParameter(SimpleCOP.Parameterizer.K_ID, 50);

    // setup Algorithm
    SimpleCOP<DoubleVector> cop = ClassGenericsUtil.parameterizeOrAbort(SimpleCOP.class, params);
    testParameterizationOk(params);

    OutlierResult result = cop.run(db);

    testAUC(db, "Noise", result, 0.833);
    testSingleScore(result, 416, 0.);
  }
}