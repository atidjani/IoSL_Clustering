#!/usr/bin/env Rscript
#the RCCP here the path to the Cpp folder
#the Rcpp code only has to be called during the initialisation
library("dbscan")
Rcpp::sourceCpp('OPTICS/R/Gradiant\ Clustering/gradient_clustering.cpp')
start <- Sys.time()
args = commandArgs(trailingOnly=TRUE)

if (length(args)!=4) {
  stop("missing arguments", call.=FALSE)
}
#read the dataset
dataset <- read.table(file =args[1], sep=",")
#parameters
epsilon = as.double(args[2])
minPoints = as.integer(args[3])
tValue = as.double(args[4])

## OPTICS
res <- optics(dataset, eps = epsilon, minPts = minPoints)
# Gradient Clustering
gradientClusters <-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts, tValue)

stop <- Sys.time()

cat(as.double(stop-start))

#finalClusters <- vector(length=nrow(dataset))
#i = 0
#for (cluster in gradientClusters) {
#    for (index in cluster) {
#        finalClusters[index] = i
#    }
#    i = i+1
#}
#
#for (i in 1:nrow(dataset)) {
#    cat(dataset$V1[i], dataset$V2[i], finalClusters[i], sep=',')
#    cat('\n')
#}
#
## Print interrupt
#cat("=\n")
#
## Prepare reachability distances for printing
#for (point in res$order){
#    cat(res$reachdist[point], finalClusters[point], sep=",")
#    cat("\n")
#}
