#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
library("dbscan")

if (length(args)!=3) {
  stop("missing arguments", call.=FALSE)
}

dataset <- read.table(args[1],
                      header = FALSE)

epsilon = args[2]

minPoints = args[3]
#converting the dataframe to matrix
x <- data.matrix(dataset)

## OPTICS

opt <- optics(x, eps = epsilon, minPts = minPoints)

## create a reachability plot (extracted DBSCAN clusters at eps_cl=.4 are colored)
# plot(opt)
#opt <- opticsXi(opt,xi = 0.33)
#plot(x, col = opt$cluster+1L)
opt$order