#include <Rcpp.h>
#include "ANN/ANN.h"
#include "R_regionQuery.h"
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

int w = 1;

float II(int y,NumericVector reachdist){
  //taking w = 1
  float reachdist1 = reachdist[y] - reachdist[y-1];
  float reachdist2 = reachdist[y] - reachdist[y+1];
  return (-w + reachdist1 * reachdist2)
    /(sqrt(pow(w,2) + pow(reachdist1,2)) * sqrt(pow(w,2) + pow(reachdist2,2)));
}

float GD(int y,NumericVector reachdist){
  return w * (reachdist[y+1] - reachdist[y]) + w * (reachdist[y-1] - reachdist[y]);
}

std::set<int> BuildCluster(IntegerVector co, int startPnt, int endPnt){
  std::set<int> cluster;
  for(int i = startPnt; i <= endPnt; i++){
    cluster.insert(co[i]);
  }
  return cluster;
}

// [[Rcpp::export]]
void helloWorld(){
  std::cout<< "hello world";
}

// [[Rcpp::export]]
List gradient_clustering(IntegerVector co, NumericVector reachdist, int minPts, float t) {
  std::stack<int> startPts;
  std::set<std::set<int> > setOfClusters;
  std::set<int> currentCluster;
  int i =0;
  int o = co[i];//first point of the ordering 
  startPts.push(i);// pushing the index of the point instead of the point itself
  i++;
  while(i < co.size()){
    o = co[i];
    i++;
    if(i < co.size()){
      if(II(i,reachdist) > t){
        if(GD(i,reachdist) > 0){

          if(currentCluster.size() >= minPts){
            setOfClusters.insert(currentCluster);
          }

          currentCluster.clear();
          if(reachdist[startPts.top()] <= reachdist[i]){
            startPts.pop();
          }

          while(reachdist[startPts.top()] < reachdist[i]){
            setOfClusters.insert(BuildCluster(co,startPts.top(),i));
            startPts.pop();
          }

          //setOfClusters.insert("set of objects from startPts.top() to last end point");
          setOfClusters.insert(BuildCluster(co,startPts.top(),i));

          if(reachdist[co[i+1]] < reachdist[i]){
            startPts.push(i);
          }

        }else{
          if(reachdist[co[i+1]] > reachdist[i]){
            //currCluster := set of objects from startPts.top() to o;
            currentCluster = BuildCluster(co,startPts.top(),i);
          }
        }
      }
    }else{
      while(!startPts.empty()){
        //currCluster := set of objects from startPts.top() to o;
        currentCluster = BuildCluster(co,startPts.top(),i);
        if((reachdist[startPts.top()] > reachdist[i]) && (currentCluster.size() >= minPts)){
          //setOfClusters.add(currCluster);
          setOfClusters.insert(currentCluster);
        }
        startPts.pop();
      }
    }

  }

  //return list of clusters
  List listOfClusters;
  for(std::set<std::set<int> >::iterator it = setOfClusters.begin(); it != setOfClusters.end(); ++it){
    std::cout << setOfClusters.size();
    std::set<int> currCluster = *it;
    NumericVector cluster(currCluster.size());
    cluster.assign(currCluster.begin(),currCluster.end());
    listOfClusters.insert(listOfClusters.end(),cluster);
  }

  return listOfClusters;
}



