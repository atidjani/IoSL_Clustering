#include <Rcpp.h>
#include "ANN/ANN.h"
#include "R_regionQuery.h"
#include <math.h>
#include <stack>
#include <set>
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

float II(int y,NumericVector reachdist){
  //taking w = 1
  float reachdist1 = reachdist(y) - reachdist(y-1);
  float reachdist2 = reachdist(y) - reachdist(y+1);
  return (-1 + reachdist1 * reachdist2)
    /(sqrt(1 + pow(reachdist1,2)) * sqrt(1 + pow(reachdist2,2)));
}

float GD(int y,NumericVector reachdist){
  return (reachdist[y+1] - reachdist[y]) + (reachdist[y-1] - reachdist[y]);
}

std::set<int> BuildCluster(IntegerVector co, int startPnt, int endPnt){
  set<int> cluster;
  
}

// [[Rcpp::export]]
List gradient_clustering(IntegerVector co, NumericVector reachdist, int minPts, float t) {
  std::stack<int> startPts;
  std::set<std::set<int> > setOfClusters;
  std::set<int> currentCluster;
  int i =0;
  int o = co[i];
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
          if(reachdist[startPts.top()] <= reachdist[co[i]]){
            startPts.pop();
          }
          
          while(reachdist[startPts.top()] < reachdist[co[i]]){
            //setOfClusters.insert("set of objects from startPts.top() to last end point");
            startPts.pop();
          }
          
          //setOfClusters.insert("set of objects from startPts.top() to last end point");
          
          if(reachdist[co[i+1]] < reachdist[co[i]]){
            startPts.push(i);
          }
       
        }else{
          if(reachdist[co[i+1]] > reachdist[co[i]]){
            //currCluster := set of objects from startPts.top() to o;
          }
        }
      }
    }else{
      while(!startPts.empty()){
        //currCluster := set of objects from startPts.top() to o;
        if((reachdist[startPts.top()] > reachdist[co[i]]) && (currentCluster.size() >= minPts)){
          //setOfClusters.add(currCluster);
        }
        startPts.pop();
      }
    }
    
  }
  
}




// You can include R code blocks in C++ files processed with sourceCpp
// (useful for testing and development). The R code will be automatically 
// run after the compilation.
//

/*** R
timesTwo(42)
*/
