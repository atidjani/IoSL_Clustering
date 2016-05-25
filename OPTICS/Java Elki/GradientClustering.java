package de.lmu.ifi.dbs.elki.algorithm.clustering.optics;
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
import java.util.ArrayList;
import java.util.Stack;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.ClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.model.OPTICSModel;
import de.lmu.ifi.dbs.elki.data.type.TypeInformation;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.datastore.DoubleDataStore;
import de.lmu.ifi.dbs.elki.database.ids.ArrayDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBIDArrayIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDVar;
import de.lmu.ifi.dbs.elki.database.ids.HashSetModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.math.MathUtil;
import de.lmu.ifi.dbs.elki.result.IterableResult;
import de.lmu.ifi.dbs.elki.utilities.Alias;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.AbstractParameterizer;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.CommonConstraints;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
/**
 * Class to handle Gradient Clustering.
 *
 * Note: this implementation includes an additional filter step that prunes
 * elements from a steep up area that don't have the predecessor in the cluster.
 * This removes a popular type of artifacts.
 *
 * @author Erich Schubert
 * @since 0.7.0
 *
 * @apiviz.composedOf OPTICSTypeAlgorithm oneway - «runs»
 * @apiviz.uses ClusterOrder oneway - «reads»
 * @apiviz.has SteepAreaResult oneway - «produces»
 */
@Alias("de.lmu.ifi.dbs.elki.algorithm.clustering.GradientClustering")
class GradientClustering extends AbstractAlgorithm<Clustering<OPTICSModel>> implements ClusteringAlgorithm<Clustering<OPTICSModel>> {
  /**
   * The logger for this class.
   */
  private static final Logging LOG = Logging.getLogger(GradientClustering.class);

  /**
   * The actual algorithm we use.
   */
  OPTICSTypeAlgorithm optics;
  /**
   * t parameter
   */
  double t;
  /**
   * Disable the predecessor correction.
   */
  boolean nocorrect;
  /**
   * Constructor.
   *
   * @param optics OPTICS algorithm to use
   * @param t t value
   */
  public GradientClustering(OPTICSTypeAlgorithm optics, double t, boolean nocorrect) {
    super();
    this.optics = optics;
    this.t = t;
    this.nocorrect = nocorrect;
  }
  
  @Override
  public TypeInformation[] getInputTypeRestriction() {
    return optics.getInputTypeRestriction();
  }

  @Override
  protected Logging getLogger() {
    return LOG;
  }

  
  
  public Clustering<OPTICSModel> run(Database database, Relation<?> relation) {
    // TODO: ensure we are using the same relation?
    ClusterOrder opticsresult = optics.run(database);
    if(LOG.isVerbose()) {
      LOG.verbose("Extracting clusters with t: " + t);
    }
    return extractClusters(opticsresult, relation, 1.0 - t, optics.getMinPts());
  }
  
  /**
   * Extract clusters from a cluster order result.
   *
   * @param clusterOrderResult cluster order result
   * @param relation Relation
   * @param ixi Parameter 1 - t
   * @param minpts Parameter minPts
   */
 
  private Clustering<OPTICSModel> extractClusters(ClusterOrder clusterOrderResult, Relation<?> relation, double it, int minpts) {
    ArrayDBIDs clusterOrder = clusterOrderResult.ids;
    DoubleDataStore reach = clusterOrderResult.reachability;
    DBIDArrayIter tmp = clusterOrder.iter();
    DBIDVar tmp2 = DBIDUtil.newVar();
    Stack <Double> startPts=new Stack <Double>();
    final Clustering<OPTICSModel> clustering = new Clustering<>("OPTICS Gradient-Clusters", "optics");
    List <Double> curClusters = new ArrayList <Double>();
    List <Double> setOfClusters = new ArrayList <Double>();
    HashSetModifiableDBIDs unclaimedids = DBIDUtil.newHashSet(relation.getDBIDs());
    FiniteProgress scanprog = LOG.isVerbose() ? new FiniteProgress("OPTICS Gradient cluster extraction", clusterOrder.size(), LOG) : null;
    GradientScanPosition scan = new GradientScanPosition(clusterOrderResult);
    startPts.push(scan.getFirst());
    while(scan.hasNext()) {
           
      if(scanprog != null) {
        scanprog.setProcessed(scan.index, LOG);
      }
      scan.next();
      if(scan.hasNext()){
    
     Double second=scan.inflexionPoint( scan.getFirst());
     
        if(second > (Double) it){
        if( (double) scan.gradientDeterminant() > (double) 0.0){
            if(curClusters.size() >=minpts){
              setOfClusters.add( curClusters);
            }
            curClusters=null;
          
            //IF startPts.top().R <= o.R THEN
            //startPts.pop();
            //ENDIF
            if((Double) startPts.peek() < scan.getFirst()){
              startPts.pop();
            }
            while( (Double) startPts.peek()< scan.getFirst()){
              //setOfClusters.add(set of objects from startPts.top() to last end point);
              setOfClusters.addAll(startPts);
              startPts.pop();
              }
           //setOfClusters.add(set of objects from startPts.top() to last end point);
           setOfClusters.add( (Double) startPts);
           // IF o.next.R < o.R THEN // o is a starting point
           // startPts.push(o);
           // ENDIF
            if(scan.getSecond()<scan.getFirst()){
              startPts.push(scan.getFirst());
            }
            else{
              if(scan.getSecond()>scan.getFirst()){
                //currCluster := set of objects from startPts.top() to o;
                while(!startPts.empty())
                curClusters.add( (Double) startPts.pop());
              }
            }
          }
        else{ //add clusters at end of the plot 
          while(!startPts.isEmpty()){
            curClusters.add(startPts);
            //IF (startPts.top().R > o.R) AND (currCluster.size() >= MinPts) THEN
            if((Double) startPts.peek()>scan.getFirst()){
            setOfClusters.add( (Double) curClusters);
            }
            startPts.pop();
          }
        }
      }
    }
 /*   if(!curClusters.isEmpty()) {
      if(!unclaimedids.isEmpty()) {
        final Cluster<OPTICSModel> allcluster;
        if(reach.doubleValue(tmp.seek(clusterOrder.size() - 1)) >= Double.POSITIVE_INFINITY) {
          allcluster = new Cluster<>("Noise", true, new OPTICSModel(0, clusterOrder.size() - 1));
        }
        else {
          allcluster = new Cluster<>("Cluster", new OPTICSModel(0, clusterOrder.size() - 1));
        } 
        for(Cluster<OPTICSModel> cluster : curclusters) {
          clustering.addChildCluster(allcluster, cluster);
        }
        clustering.addToplevelCluster(allcluster);
      }
      else {
        for(Cluster<OPTICSModel> cluster : curClusters) {
          clustering.addToplevelCluster(cluster);
        }
      }
      clustering.addChildResult(clusterOrderResult);
      
      return clustering;
    } */
      return null;   
    
  }
  }
    
  /**
   * Position when scanning for steep areas
   *
   * @author Paul Velthuis
   *
   * @apiviz.exclude
   */
  private static class GradientScanPosition {
    /**
     * Cluster order
     */
    ClusterOrder co;
    
    /**
     * Current position
     */
    int index;

    /**
     * Width between two objects
     */
    double w;
    
    /** 
     * Variable for accessing.
     */
    private DBIDArrayIter cur, next, next2;

    /**
     * Constructor.
     *
     * @param co Cluster order
     */
    public GradientScanPosition(ClusterOrder co) {
      super();
      this.co = co;
      this.index = 0;
      this.cur = co.ids.iter();
      this.next = co.ids.iter();
      this.next2= co.ids.iter();
      if(next.valid()) {
        next.advance();
      }
    }
    
    public void empty(){
      this.cur=null;
      this.next=null;
    }
    
   public double getFirst(){
     return (double) co.reachability.doubleValue(index);
   }
   
   public double getSecond(){
      return (double) co.reachability.doubleValue(next);
   }
    /**
     * Gives the distance between the 
     * @return
     */
    public double width(){
      return co.reachability.doubleValue(cur) - co.reachability.doubleValue(next);
    }
    /**
     * The inflexion index
     * from the pseudo code
     * The o1.R is:  co.reachability.doubleValue(cur)
     * the 02.R is: co.reachability.doubleValue(next) 
     * the 03.R is: co.reachability.doubleValue(next2) 
     */
    public double inflexionIndex(){
     return (double) (-w*w + (co.reachability.doubleValue(next)*co.reachability.doubleValue(cur))*(co.reachability.doubleValue(next)* co.reachability.doubleValue(next2))/(gradient().norm()*-gradient2().norm()));
     
    }
    
    class Vector {
      double v1, v2;
      
      double norm() {
        return Math.sqrt(v1*v1 + v2*v2);
      }
    }
    /**
     * the gradient of (x,y)
     * @return
     */
    public Vector gradient(){
      Vector v = new Vector();
      v.v1=(double) co.reachability.doubleValue(cur) - co.reachability.doubleValue(next);
      v.v2= (double) co.reachability.doubleValue(next) - co.reachability.doubleValue(cur);    
      return v;
    }
    
    /**
     * the gradient of y,z
     * @return
     */
    public Vector gradient2(){
      Vector v = new Vector();
      v.v1= (double) co.reachability.doubleValue(next) - co.reachability.doubleValue(next2);
      v.v2= (double) co.reachability.doubleValue(cur) - co.reachability.doubleValue(next);    
      return v;
    }
    /**
     * the determant defined as gd(g(x, y), g(z, y)) := |     w         −w      |
                                                        |  x.R − y.R z.R − y.R  |
     */
    public double gradientDeterminant(){
      double [][] Matrix=new double [(co.reachability.doubleValue(cur) - co.reachability.doubleValue(next)) *  (co.reachability.doubleValue(cur) - co.reachability.doubleValue(next))][(co.reachability.doubleValue(cur) - co.reachability.doubleValue(next))*(co.reachability.doubleValue(next) - co.reachability.doubleValue(cur))];
   //TODO 
      return 1.0;
    }
    
    /**
     * Advance to the next entry
     */
    public void next() {
      index++;
      cur.advance();
      next.advance();
    }

    /**
     * Test whether there is a next value.
     *
     * @return end of cluster order
     */
    public boolean hasNext() {
      return index < co.size();
    }
    
    public double inflexionPoint(double o){
      
      if(co.reachability.doubleValue(cur) >= Double.POSITIVE_INFINITY) {
        return 0.0;
      }
      
      if(!next.valid()) {
        return 1.0;
      }
    return (Double) inflexionIndex();
    
   
   
    }
  }
  
    /**
   * Parameterization class.
   *
   * @author Paul Velthuis
   *
   * @apiviz.exclude
   */
  public static class Parameterizer extends AbstractParameterizer {
    /**
     * Parameter to specify the actual OPTICS algorithm to use.
     */
    public static final OptionID XIALG_ID = new OptionID("GradientClustering.algorithm", "The actual OPTICS-type algorithm to use.");

    /**
     * Parameter to specify the angle threshold.
     */
    public static final OptionID T_ID = new OptionID("GradientClustering.t", "Threshold for the angle requirement.");

    /**
     * Parameter to disable the correction function.
     */
    public static final OptionID NOCORRECT_ID = new OptionID("GradientClustering.nocorrect", "Disable the predecessor correction.");

   
    protected OPTICSTypeAlgorithm optics;

    protected double t = 0.;

    protected boolean nocorrect = false;

    @Override
    protected void makeOptions(Parameterization config) {
      super.makeOptions(config);
      DoubleParameter xiP = new DoubleParameter(T_ID)//
      .addConstraint(CommonConstraints.GREATER_EQUAL_ZERO_DOUBLE)//
      .addConstraint(CommonConstraints.LESS_THAN_ONE_DOUBLE);
      if(config.grab(xiP)) {
        t = xiP.doubleValue();
      }

      ClassParameter<OPTICSTypeAlgorithm> opticsP = new ClassParameter<>(XIALG_ID, OPTICSTypeAlgorithm.class, OPTICSHeap.class);
      if(config.grab(opticsP)) {
        optics = opticsP.instantiateClass(config);
      }

      Flag nocorrectF = new Flag(NOCORRECT_ID);
      if(config.grab(nocorrectF)) {
        nocorrect = nocorrectF.isTrue();
      }      
    }

    @Override
    protected GradientClustering makeInstance() {
      return new GradientClustering(optics, t, nocorrect);
    }
  }
  

}
