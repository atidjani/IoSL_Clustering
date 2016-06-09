#!/usr/bin/env Rscript
#the RCCP here the path to the Cpp folder
Rcpp::sourceCpp('C:/Users/paulv/IoSL_Clustering/OPTICS/R/Gradient_Clustering/gradient_clustering.cpp')
args = commandArgs(trailingOnly=TRUE)
library("dbscan")

if (length(args)!=4) {
  stop("missing arguments", call.=FALSE)
}
#read the dataset
dataset <- read.table(file =args[1],
                      sep=",")
#parameters
epsilon = as.double(args[2])
minPoints = as.integer(args[3])
tValue = as.double(args[4])
## OPTICS
#gives the optics result
res <- optics(dataset, eps = epsilon, minPts = minPoints)
#gives the gradient result
result <-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts, tValue)
#plot(res$reachdist[res$order], type="h", col=result[res$order]+1L,
#     ylab = "Reachability dist.", xlab = "OPTICS order",	
#     main = "Reachability Plot")
