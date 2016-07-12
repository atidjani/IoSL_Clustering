package de.lmu.ifi.dbs.elki.application.cache;

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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import de.lmu.ifi.dbs.elki.application.AbstractApplication;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DoubleDBIDListIter;
import de.lmu.ifi.dbs.elki.database.ids.KNNList;
import de.lmu.ifi.dbs.elki.database.query.DatabaseQuery;
import de.lmu.ifi.dbs.elki.database.query.distance.DistanceQuery;
import de.lmu.ifi.dbs.elki.database.query.knn.KNNQuery;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.utilities.exceptions.AbortException;
import de.lmu.ifi.dbs.elki.utilities.io.ByteArrayUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.FileParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;
import de.lmu.ifi.dbs.elki.workflow.InputStep;

/**
 * Precompute the k nearest neighbors in a disk cache.
 * 
 * @author Erich Schubert
 * @since 0.2
 * 
 * @apiviz.has DistanceFunction
 * 
 * @param <O> Object type
 */
public class CacheDoubleDistanceKNNLists<O> extends AbstractApplication {
  /**
   * The logger for this class.
   */
  private static final Logging LOG = Logging.getLogger(CacheDoubleDistanceKNNLists.class);

  /**
   * Data source to process.
   */
  private InputStep input;

  /**
   * Distance function that is to be cached.
   */
  private DistanceFunction<O> distance;

  /**
   * Number of neighbors to precompute.
   */
  private int k;

  /**
   * Output file.
   */
  private File out;

  /**
   * Magic number to identify files.
   * 
   * Note, when cloning this class, and performing any incompatible change to
   * the file format, you should also change this magic ID!
   */
  public static final int KNN_CACHE_MAGIC = 0xCAC43D1C;

  /**
   * Constructor.
   * 
   * @param input Data source
   * @param distance Distance function
   * @param k Number of nearest neighbors
   * @param out Matrix output file
   */
  public CacheDoubleDistanceKNNLists(InputStep input, DistanceFunction<O> distance, int k, File out) {
    super();
    this.input = input;
    this.distance = distance;
    this.k = k;
    this.out = out;
  }

  @Override
  public void run() {
    Database database = input.getDatabase();
    Relation<O> relation = database.getRelation(distance.getInputTypeRestriction());
    DistanceQuery<O> distanceQuery = database.getDistanceQuery(relation, distance);
    KNNQuery<O> knnQ = database.getKNNQuery(distanceQuery, DatabaseQuery.HINT_HEAVY_USE);

    // open file.
    try (RandomAccessFile file = new RandomAccessFile(out, "rw");
        FileChannel channel = file.getChannel();
        // and acquire a file write lock
        FileLock lock = channel.lock()) {
      // write magic header
      file.writeInt(KNN_CACHE_MAGIC);

      int bufsize = k * 12 * 2 + 10; // Initial size, enough for 2 kNN.
      ByteBuffer buffer = ByteBuffer.allocateDirect(bufsize);

      FiniteProgress prog = LOG.isVerbose() ? new FiniteProgress("Computing kNN", relation.size(), LOG) : null;

      for(DBIDIter it = relation.iterDBIDs(); it.valid(); it.advance()) {
        final KNNList nn = knnQ.getKNNForDBID(it, k);
        final int nnsize = nn.size();

        // Grow the buffer when needed:
        if(nnsize * 12 + 10 > bufsize) {
          while(nnsize * 12 + 10 > bufsize) {
            bufsize <<= 1;
          }
          buffer = ByteBuffer.allocateDirect(bufsize);
        }

        buffer.clear();
        ByteArrayUtil.writeUnsignedVarint(buffer, it.internalGetIndex());
        ByteArrayUtil.writeUnsignedVarint(buffer, nnsize);
        int c = 0;
        for(DoubleDBIDListIter ni = nn.iter(); ni.valid(); ni.advance(), c++) {
          ByteArrayUtil.writeUnsignedVarint(buffer, ni.internalGetIndex());
          buffer.putDouble(ni.doubleValue());
        }
        if(c != nn.size()) {
          throw new AbortException("Sizes did not agree. Cache is invalid.");
        }

        buffer.flip();
        channel.write(buffer);
        LOG.incrementProcessed(prog);
      }
      LOG.ensureCompleted(prog);
      lock.release();
    }
    catch(IOException e) {
      LOG.exception(e);
    }
    // FIXME: close!
  }

  /**
   * Parameterization class.
   * 
   * @author Erich Schubert
   * 
   * @apiviz.exclude
   */
  public static class Parameterizer<O> extends AbstractApplication.Parameterizer {
    /**
     * Parameter that specifies the name of the directory to be re-parsed.
     * <p>
     * Key: {@code -loader.diskcache}
     * </p>
     */
    public static final OptionID CACHE_ID = new OptionID("loader.diskcache", "File name of the disk cache to create.");

    /**
     * Parameter that specifies the name of the directory to be re-parsed.
     * <p>
     * Key: {@code -loader.distance}
     * </p>
     */
    public static final OptionID DISTANCE_ID = new OptionID("loader.distance", "Distance function to cache.");

    /**
     * Parameter that specifies the number of neighbors to precompute.
     * <p>
     * Key: {@code -loader.k}
     * </p>
     */
    public static final OptionID K_ID = new OptionID("loader.k", "Number of nearest neighbors to precompute.");

    /**
     * Data source to process.
     */
    private InputStep input = null;

    /**
     * Distance function that is to be cached.
     */
    private DistanceFunction<O> distance = null;

    /**
     * Number of neighbors to precompute.
     */
    private int k;

    /**
     * Output file.
     */
    private File out = null;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      input = config.tryInstantiate(InputStep.class);
      // Distance function parameter
      final ObjectParameter<DistanceFunction<O>> dpar = new ObjectParameter<>(DISTANCE_ID, DistanceFunction.class);
      if(config.grab(dpar)) {
        distance = dpar.instantiateClass(config);
      }
      final IntParameter kpar = new IntParameter(K_ID);
      kpar.addConstraint(CommonConstraints.GREATER_EQUAL_ONE_INT);
      if(config.grab(kpar)) {
        k = kpar.intValue();
      }
      // Output file parameter
      final FileParameter cpar = new FileParameter(CACHE_ID, FileParameter.FileType.OUTPUT_FILE);
      if(config.grab(cpar)) {
        out = cpar.getValue();
      }
    }

    @Override
    protected CacheDoubleDistanceKNNLists<O> makeInstance() {
      return new CacheDoubleDistanceKNNLists<>(input, distance, k, out);
    }
  }

  /**
   * Main method, delegate to super class.
   * 
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    runCLIApplication(CacheDoubleDistanceKNNLists.class, args);
  }
}
