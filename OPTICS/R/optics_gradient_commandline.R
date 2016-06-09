#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
library("dbscan")

if (length(args)!=4) {
  stop("missing arguments", call.=FALSE)
}

dataset <- read.table(args[1],
                      header = FALSE, sep=",")

epsilon = args[2]

minPoints = args[3]
t = args[4]
#converting the dataframe to matrix
x <- read.table(dataset)

## OPTICS
#gives the optics result
opt <- optics(x, eps = epsilon, minPts = minPoints)
#gives the gradient result
result<-gradient_clustering(opt$order,opt$reachdist,opt$coredist,opt$minPts,t)
