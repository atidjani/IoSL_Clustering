#!/usr/bin/env Rscript
#the RCCP here the path to the Cpp folder
#the Rcpp code only has to be called during the initialisation
library("dbscan")
library("stringr")
Rcpp::sourceCpp('Gradient_Clustering/gradient_clustering.cpp')
args = commandArgs(trailingOnly=TRUE)

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
res <- optics(dataset, eps = epsilon, minPts = minPoints)
# Gradient Clustering
clusters <-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts, tValue)

# Prepare clusters list for printing
clustersList = unlist(lapply(clusters, paste, collapse=" "))
clustersList <- str_replace_all(clustersList, " " , ",") #Replace spaces with commas
clustersList <- gsub("^\\s+|\\s+$", "", clustersList)

# Print clusters list
print(clustersList, quote=FALSE)

# Prepare reachability distances for printing
cat("[-1] ")
cat(res$reachdist, sep=",")
cat("\n")
