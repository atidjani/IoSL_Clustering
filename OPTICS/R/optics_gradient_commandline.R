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
                      header = FALSE, sep=",")
#parameters
epsilon = args[2]
minPoints = args[3]
t = args[4]



## OPTICS
#gives the optics result
opt <- optics(dataset, eps = epsilon, minPts = minPoints)
#gives the gradient result
result<-gradient_clustering(opt$order,opt$reachdist,opt$coredist,opt$minPts,t)
