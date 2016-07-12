package de.lmu.ifi.dbs.elki.algorithm.outlier.lof;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.VectorUtil;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.HashmapDatabase;
import de.lmu.ifi.dbs.elki.database.UpdatableDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.DoubleRelation;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.database.relation.RelationUtil;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.bundle.MultipleObjectsBundle;
import de.lmu.ifi.dbs.elki.distance.distancefunction.CosineDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Tests the OnlineLOF algorithm. Compares the result of the static LOF
 * algorithm to the result of the OnlineLOF algorithm, where some insertions and
 * deletions (of the previously inserted objects) have been applied to the
 * database.
 *
 * BUG: This currently does not appear to work correctly!
 *
 * @author Elke Achtert
 * @since 0.4.0
 */
@Ignore
public class OnlineLOFTest {
  // the following values depend on the data set used!
  static String dataset = "data/testdata/unittests/3clusters-and-noise-2d.csv";

  // parameter k for LOF and OnlineLOF
  static int k = 5;

  // neighborhood distance function for LOF and OnlineLOF
  @SuppressWarnings("rawtypes")
  static DistanceFunction neighborhoodDistanceFunction = EuclideanDistanceFunction.STATIC;

  // reachability distance function for LOF and OnlineLOF
  @SuppressWarnings("rawtypes")
  static DistanceFunction reachabilityDistanceFunction = CosineDistanceFunction.STATIC;

  // seed for the generator
  static int seed = 5;

  // size of the data set
  static int size = 50;

  /**
   * First, run the {@link LOF} algorithm on the database. Second, run the
   * {@link OnlineLOF} algorithm on the database, insert new objects and
   * afterwards delete them. Then, compare the two results for equality.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testOnlineLOF() {
    // LoggingConfiguration.setLevelFor("de.lmu.ifi.dbs.elki.algorithm.outlier.lof",
    // Level.FINEST.toString());

    UpdatableDatabase db = getDatabase();

    // 1. Run LOF
    FlexibleLOF<DoubleVector> lof = new FlexibleLOF<>(k, k, neighborhoodDistanceFunction, reachabilityDistanceFunction);
    OutlierResult result1 = lof.run(db);

    // 2. Run OnlineLOF (with insertions and removals) on database
    OutlierResult result2 = runOnlineLOF(db);

    // 3. Compare results
    DoubleRelation scores1 = result1.getScores();
    DoubleRelation scores2 = result2.getScores();

    for(DBIDIter id = scores1.getDBIDs().iter(); id.valid(); id.advance()) {
      double lof1 = scores1.doubleValue(id);
      double lof2 = scores2.doubleValue(id);
      assertEquals("lof(" + DBIDUtil.toString(id) + ") != lof(" + DBIDUtil.toString(id) + "): " + lof1 + " != " + lof2, lof1, lof2, 1e-10);
    }
  }

  /**
   * Run OnlineLOF (with insertions and removals) on database.
   */
  @SuppressWarnings("unchecked")
  private static OutlierResult runOnlineLOF(UpdatableDatabase db) {
    Relation<DoubleVector> rep = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);

    // setup algorithm
    OnlineLOF<DoubleVector> lof = new OnlineLOF<>(k, k, neighborhoodDistanceFunction, reachabilityDistanceFunction);

    // run OnlineLOF on database
    OutlierResult result = lof.run(db);

    // insert new objects
    ArrayList<DoubleVector> insertions = new ArrayList<>();
    NumberVector.Factory<DoubleVector> o = RelationUtil.getNumberVectorFactory(rep);
    int dim = RelationUtil.dimensionality(rep);
    Random random = new Random(seed);
    for(int i = 0; i < size; i++) {
      DoubleVector obj = VectorUtil.randomVector(o, dim, random);
      insertions.add(obj);
    }
    DBIDs deletions = db.insert(MultipleObjectsBundle.makeSimple(rep.getDataTypeInformation(), insertions));

    // delete objects
    db.delete(deletions);

    return result;
  }

  /**
   * Returns the database.
   */
  private static UpdatableDatabase getDatabase() {
    ListParameterization params = new ListParameterization();
    params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID, dataset);

    UpdatableDatabase db = ClassGenericsUtil.parameterizeOrAbort(HashmapDatabase.class, params);
    params.failOnErrors();
    if(params.hasUnusedParameters()) {
      fail("Unused parameters: " + params.getRemainingParameters());
    }

    // get database
    db.initialize();
    return db;
  }

}
