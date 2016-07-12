package de.lmu.ifi.dbs.elki.index.preprocessed;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.VectorUtil;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.HashmapDatabase;
import de.lmu.ifi.dbs.elki.database.UpdatableDatabase;
import de.lmu.ifi.dbs.elki.database.ids.ArrayDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DoubleDBIDList;
import de.lmu.ifi.dbs.elki.database.ids.DoubleDBIDListIter;
import de.lmu.ifi.dbs.elki.database.ids.KNNList;
import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.knn.KNNQuery;
import de.lmu.ifi.dbs.elki.database.query.knn.LinearScanDistanceKNNQuery;
import de.lmu.ifi.dbs.elki.database.query.rknn.LinearScanRKNNQuery;
import de.lmu.ifi.dbs.elki.database.query.rknn.RKNNQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.database.relation.RelationUtil;
import de.lmu.ifi.dbs.elki.datasource.FileBasedDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.bundle.MultipleObjectsBundle;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.index.preprocessed.knn.MaterializeKNNAndRKNNPreprocessor;
import de.lmu.ifi.dbs.elki.index.preprocessed.knn.MaterializeKNNPreprocessor;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;

/**
 * Test case to validate the dynamic updates of materialized kNN and RkNN
 * preprocessors.
 *
 * some index structures for accuracy. For a known data set and query point, the
 * top 10 nearest neighbors are queried and verified.
 *
 *
 * @author Elke Achtert
 * @since 0.4.0
 */
public class MaterializedKNNAndRKNNPreprocessorTest {
  // the following values depend on the data set used!
  static String dataset = "data/testdata/unittests/3clusters-and-noise-2d.csv";

  // number of kNN to query
  int k = 10;

  // the size of objects inserted and deleted
  int updatesize = 12;

  int seed = 5;

  // size of the data set
  int shoulds = 330;

  @Test
  public void testPreprocessor() {
    ListParameterization params = new ListParameterization();
    params.addParameter(FileBasedDatabaseConnection.Parameterizer.INPUT_ID, dataset);

    // get database
    UpdatableDatabase db = ClassGenericsUtil.parameterizeOrAbort(HashmapDatabase.class, params);
    db.initialize();
    Relation<DoubleVector> rep = db.getRelation(TypeUtil.DOUBLE_VECTOR_FIELD);
    DistanceQuery<DoubleVector> distanceQuery = db.getDistanceQuery(rep, EuclideanDistanceFunction.STATIC);

    // verify data set size.
    assertEquals("Data set size doesn't match parameters.", shoulds, rep.size());

    // get linear queries
    LinearScanDistanceKNNQuery<DoubleVector> lin_knn_query = new LinearScanDistanceKNNQuery<>(distanceQuery);
    LinearScanRKNNQuery<DoubleVector> lin_rknn_query = new LinearScanRKNNQuery<>(distanceQuery, lin_knn_query, k);

    // get preprocessed queries
    ListParameterization config = new ListParameterization();
    config.addParameter(MaterializeKNNPreprocessor.Factory.DISTANCE_FUNCTION_ID, distanceQuery.getDistanceFunction());
    config.addParameter(MaterializeKNNPreprocessor.Factory.K_ID, k);
    MaterializeKNNAndRKNNPreprocessor<DoubleVector> preproc = new MaterializeKNNAndRKNNPreprocessor<>(rep, distanceQuery.getDistanceFunction(), k);
    KNNQuery<DoubleVector> preproc_knn_query = preproc.getKNNQuery(distanceQuery, k);
    RKNNQuery<DoubleVector> preproc_rknn_query = preproc.getRKNNQuery(distanceQuery);
    // add as index
    db.getHierarchy().add(rep, preproc);
    assertFalse("Preprocessor knn query class incorrect.", preproc_knn_query instanceof LinearScanDistanceKNNQuery);
    assertFalse("Preprocessor rknn query class incorrect.", preproc_rknn_query instanceof LinearScanDistanceKNNQuery);

    // test queries
    testKNNQueries(rep, lin_knn_query, preproc_knn_query, k);
    testRKNNQueries(rep, lin_rknn_query, preproc_rknn_query, k);
    // also test partial queries, forward only
    testKNNQueries(rep, lin_knn_query, preproc_knn_query, k / 2);

    // insert new objects
    List<DoubleVector> insertions = new ArrayList<>();
    NumberVector.Factory<DoubleVector> o = RelationUtil.getNumberVectorFactory(rep);
    int dim = RelationUtil.dimensionality(rep);
    Random random = new Random(seed);
    for(int i = 0; i < updatesize; i++) {
      DoubleVector obj = VectorUtil.randomVector(o, dim, random);
      insertions.add(obj);
    }
    // System.out.println("Insert " + insertions);
    DBIDs deletions = db.insert(MultipleObjectsBundle.makeSimple(rep.getDataTypeInformation(), insertions));

    // test queries
    testKNNQueries(rep, lin_knn_query, preproc_knn_query, k);
    testRKNNQueries(rep, lin_rknn_query, preproc_rknn_query, k);

    // delete objects
    // System.out.println("Delete " + deletions);
    db.delete(deletions);

    // test queries
    testKNNQueries(rep, lin_knn_query, preproc_knn_query, k);
    testRKNNQueries(rep, lin_rknn_query, preproc_rknn_query, k);
  }

  private void testKNNQueries(Relation<DoubleVector> rep, KNNQuery<DoubleVector> lin_knn_query, KNNQuery<DoubleVector> preproc_knn_query, int k) {
    ArrayDBIDs sample = DBIDUtil.ensureArray(rep.getDBIDs());
    List<? extends KNNList> lin_knn_ids = lin_knn_query.getKNNForBulkDBIDs(sample, k);
    List<? extends KNNList> preproc_knn_ids = preproc_knn_query.getKNNForBulkDBIDs(sample, k);
    for(int i = 0; i < rep.size(); i++) {
      KNNList lin_knn = lin_knn_ids.get(i);
      KNNList pre_knn = preproc_knn_ids.get(i);
      DoubleDBIDListIter lin = lin_knn.iter(), pre = pre_knn.iter();
      for(; lin.valid() && pre.valid(); lin.advance(), pre.advance(), i++) {
        assertTrue(DBIDUtil.equal(lin, pre) || lin.doubleValue() == pre.doubleValue());
      }
      assertEquals("kNN sizes do not agree.", lin_knn.size(), pre_knn.size());
      for(int j = 0; j < lin_knn.size(); j++) {
        assertTrue("kNNs of linear scan and preprocessor do not match!", DBIDUtil.equal(lin_knn.get(j), pre_knn.get(j)));
        assertEquals("kNNs of linear scan and preprocessor do not match!", lin_knn.get(j).doubleValue(), pre_knn.get(j).doubleValue(), 0.);
      }
    }
  }

  private void testRKNNQueries(Relation<DoubleVector> rep, RKNNQuery<DoubleVector> lin_rknn_query, RKNNQuery<DoubleVector> preproc_rknn_query, int k) {
    ArrayDBIDs sample = DBIDUtil.ensureArray(rep.getDBIDs());
    List<? extends DoubleDBIDList> lin_rknn_ids = lin_rknn_query.getRKNNForBulkDBIDs(sample, k);
    List<? extends DoubleDBIDList> preproc_rknn_ids = preproc_rknn_query.getRKNNForBulkDBIDs(sample, k);
    for(int i = 0; i < rep.size(); i++) {
      DoubleDBIDList lin_rknn = lin_rknn_ids.get(i);
      DoubleDBIDList pre_rknn = preproc_rknn_ids.get(i);

      DoubleDBIDListIter lin = lin_rknn.iter(), pre = pre_rknn.iter();
      for(; lin.valid() && pre.valid(); lin.advance(), pre.advance(), i++) {
        assertTrue(DBIDUtil.equal(lin, pre) || lin.doubleValue() == pre.doubleValue());
      }
      assertEquals("rkNN sizes do not agree for k=" + k, lin_rknn.size(), pre_rknn.size());
      for(int j = 0; j < lin_rknn.size(); j++) {
        assertTrue("rkNNs of linear scan and preprocessor do not match!", DBIDUtil.equal(lin_rknn.get(j), pre_rknn.get(j)));
        assertEquals("rkNNs of linear scan and preprocessor do not match!", lin_rknn.get(j).doubleValue(), pre_rknn.get(j).doubleValue(), 0.);
      }
    }
  }
}