#include <Rcpp.h>
#include <math.h>
#include <stack>
#include <set>
#include <iostream>
using namespace Rcpp;

// This is a simple example of exporting a C++ function to R. You can
// source this function into an R session using the Rcpp::sourceCpp
// function (or via the Source button on the editor toolbar). Learn
// more about Rcpp at:
//
//   http://www.rcpp.org/
//   http://adv-r.had.co.nz/Rcpp.html
//   http://gallery.rcpp.org/
//


float w = 0.1;
float II(int y, NumericVector reachdist, IntegerVector co){
  float reachdist_yx = reachdist[co[y]-1] - reachdist[co[y-1]-1];
  float reachdist_yz = reachdist[co[y]-1] - reachdist[co[y+1]-1];
  return (-pow(w,2) + (reachdist_yx * reachdist_yz))
    /( sqrt(pow(w,2) + pow(reachdist_yx,2)) * sqrt(pow(w,2) + pow(-reachdist_yz,2)));
}

float GD(int y,NumericVector reachdist, IntegerVector co){
  return w * (reachdist[co[y+1]-1] + reachdist[co[y-1]-1] - 2 * reachdist[co[y]-1]);
}

std::set<int> BuildCluster(IntegerVector co, int startPnt, int endPnt, NumericVector clusterId, int id){
  std::set<int> cluster;
  for(int i = startPnt; i <= endPnt; i++){
      cluster.insert(co[i]);
  }

  return cluster;
}

// [[Rcpp::export]]
NumericVector HelpFunction(NumericVector cluster, int size){
   NumericVector vector(size);
   for(int i = 0; i < cluster.size(); i++){
     vector[cluster[i]-1] = 1;
   }
   return vector;
}

float calculateW(NumericVector dists){
  float dist;
  float sum;
  float counter = 0;
  for(int i =0; i < dists.size() - 1; i++){
    if(dists[i] == R_PosInf or dists[i+1] == R_PosInf ) continue;
    dist = fabs(dists[i+1] - dists[i]);
    sum+=dist;
    counter++;
  }
  return (sum/counter) / 20 ;
}

// [[Rcpp::export]]
List gradient_clustering(IntegerVector co, NumericVector reachdist, NumericVector coredist, int minPts, float t) {
  w = 0.1;
  //w = calculateW(reachdist);
  //std::cout << w <<std::endl;

  NumericVector clusterId(co.size(),-1);
  std::stack<int> startPts;
  std::set<std::set<int> > setOfClusters;
  std::set<int> currentCluster;
  int id  = 1;
  int i =0;
  int o = co[i];//first point of the ordering
  startPts.push(i);// pushing the index of the point instead of the point itself
  while(i+1 < co.size()){
    i++;
    o = co[i];
    if(i+1 < co.size()){// i need to check this more thouroughly
      //std::cout << "II of point " << i << "  :  " << II(i, reachdist, co) << std::endl;
      //std::cout << "GD of point " << i << "  :  " << GD(i, reachdist, co) << std::endl;
      if(II(i, reachdist, co) > t){
        //std::cout << "Inflection point " << i <<std::endl;
        //std::cout << "reach dist of point " << i << "  :  " << reachdist[co[i]-1] << std::endl;
        //std::cout << "II of point " << i << "  :  " << II(i, reachdist, co) << std::endl;
        //std::cout << "GD of point " << i << "  :  " << GD(i, reachdist, co) << std::endl;
        if(GD(i, reachdist, co) > 0){

          if(currentCluster.size() >= minPts){ // first object outside a cluster
            setOfClusters.insert(currentCluster);
          }

          currentCluster.clear();

          if(reachdist[co[startPts.top()]-1] <= reachdist[co[i]-1]){
            startPts.pop();
          }

          while(reachdist[co[startPts.top()]-1] < reachdist[co[i]-1]){
            //setOfClusters.insert("set of objects from startPts.top() to last end point");
            if(i-1-startPts.top()+1 >= minPts){// not in the original paper
            setOfClusters.insert(BuildCluster(co,startPts.top(),i-1,clusterId, id));// last end point would be i-1
            id++;
            startPts.pop();
            }
          }

          //setOfClusters.insert("set of objects from startPts.top() to last end point");
          if(i-1-startPts.top()+1 >= minPts){// not in the original paper
            setOfClusters.insert(BuildCluster(co,startPts.top(),i-1,clusterId, id));// last end point would be i-1
            id++;
          }

          if(reachdist[co[i+1]-1] <= reachdist[co[i]-1]){ //start point: <= instead of < we got better result
            //std::cout << "start point : " <<  i << std::endl ;
            startPts.push(i);
          }

        }else{
          if(reachdist[co[i+1]-1] > reachdist[co[i]-1]){
            //currCluster := set of objects from startPts.top() to o;
            //if(i-1-startPts.top()+1 >= minPts){// not in the original paper
              currentCluster = BuildCluster(co,startPts.top(),i,clusterId, id);
              id++;
            //}
          }
        }
      }
    }else{
      while(!startPts.empty()){
        //currCluster := set of objects from startPts.top() to o;
        currentCluster = BuildCluster(co,startPts.top(),i,clusterId, id);
        id++;
        if((reachdist[co[startPts.top()]-1] > reachdist[co[i]-1]) && (currentCluster.size() >= minPts)){
          //setOfClusters.add(currCluster);
          setOfClusters.insert(currentCluster);
        }
        startPts.pop();
      }
    }
  }

  //return list of clusters
  //the list with points from the dataset in clusters, so the point in the cluster is the same point in the dataset
  List listOfClusters;
  int cpt = 1;
  for(std::set<std::set<int> >::iterator it = setOfClusters.begin(); it != setOfClusters.end(); ++it){
    //std::cout << setOfClusters.size();
    std::set<int> currCluster = *it;
    NumericVector cluster(currCluster.size());
    cluster.assign(currCluster.begin(),currCluster.end());
    listOfClusters.insert(listOfClusters.end(),cluster);
  }

  //the list with points from the dataset are returned in clusters, so the point in the cluster is the same point in the dataset.
  return listOfClusters;
  //return clusterId;
}



