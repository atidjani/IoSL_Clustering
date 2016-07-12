package de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.parallel;

import de.lmu.ifi.dbs.elki.algorithm.clustering.ClusteringAlgorithmUtil;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.AbstractKMeans;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.initialization.KMeansInitialization;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.KMeansModel;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.datastore.WritableIntegerDataStore;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.NumberVectorDistanceFunction;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.IndefiniteProgress;
import de.lmu.ifi.dbs.elki.parallel.ParallelExecutor;

/**
 * Parallel implementation of k-Means clustering.
 *
 * @author Erich Schubert
 * @since 0.7.0
 *
 * @apiviz.has KMeansProcessor
 *
 * @param <V> Vector type
 */
public class ParallelLloydKMeans<V extends NumberVector> extends AbstractKMeans<V, KMeansModel> {
  /**
   * Constructor.
   *
   * @param distanceFunction Distance function
   * @param k K parameter
   */
  public ParallelLloydKMeans(NumberVectorDistanceFunction<? super V> distanceFunction, int k, int maxiter, KMeansInitialization<? super V> initializer) {
    super(distanceFunction, k, maxiter, initializer);
  }

  /**
   * Class logger
   */
  private static final Logging LOG = Logging.getLogger(ParallelLloydKMeans.class);

  @Override
  public TypeInformation[] getInputTypeRestriction() {
    return TypeUtil.array(getDistanceFunction().getInputTypeRestriction());
  }

  @Override
  public Clustering<KMeansModel> run(Database database, Relation<V> relation) {
    DBIDs ids = relation.getDBIDs();

    // Choose initial means
    double[][] means = initializer.chooseInitialMeans(database, relation, k, getDistanceFunction());

    // Store for current cluster assignment.
    WritableIntegerDataStore assignment = DataStoreUtil.makeIntegerStorage(relation.getDBIDs(), DataStoreFactory.HINT_TEMP | DataStoreFactory.HINT_HOT, -1);
    double[] varsum = new double[k];
    KMeansProcessor<V> kmm = new KMeansProcessor<>(relation, distanceFunction, assignment, varsum);

    IndefiniteProgress prog = LOG.isVerbose() ? new IndefiniteProgress("K-Means iteration", LOG) : null;
    for (int iteration = 0; maxiter <= 0 || iteration < maxiter; iteration++) {
      LOG.incrementProcessed(prog);
      kmm.nextIteration(means);
      ParallelExecutor.run(ids, kmm);
      // Stop if no cluster assignment changed.
      if (!kmm.changed()) {
        break;
      }
      means = kmm.getMeans();
    }
    LOG.setCompleted(prog);

    // Wrap result
    ArrayModifiableDBIDs[] clusters = ClusteringAlgorithmUtil.partitionsFromIntegerLabels(ids, assignment, k);

    Clustering<KMeansModel> result = new Clustering<>("k-Means Clustering", "kmeans-clustering");
    for (int i = 0; i < clusters.length; i++) {
      DBIDs cids = clusters[i];
      if (cids.size() == 0) {
        continue;
      }
      KMeansModel model = new KMeansModel(means[i], varsum[i]);
      result.addToplevelCluster(new Cluster<>(cids, model));
    }
    return result;
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }

  /**
   * Parameterization class
   *
   * @author Erich Schubert
   *
   * @apiviz.exclude
   *
   * @param <V> Vector type
   */
  public static class Parameterizer<V extends NumberVector> extends AbstractKMeans.Parameterizer<V> {
    @Override
    protected Logging getLogger() {
      return LOG;
    }

    @Override
    protected ParallelLloydKMeans<V> makeInstance() {
      return new ParallelLloydKMeans<>(distanceFunction, k, maxiter, initializer);
    }
  }
}
