#!/usr/bin/env Rscript
#the RCCP here the path to the Cpp folder
#the Rcpp code only has to be called during the initialisation
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
sink("bracket2.txt")
sink()
lapply(result, write, "bracket2.txt", append=TRUE, ncolumns=1000)



brackets = gsub("\\[|\\]", "", result)
library(stringr)

brackets <- str_replace_all(brackets, "\n" , "")

brackets2 <- noquote(brackets)

brackets2 <- gsub("()", "",gsub("\\c", "", brackets2))
brackets2<- gsub("[()]", "", brackets2)
sink("bracket.txt")
cat(brackets2)
sink()

#write(p, "p.txt")
#here the format is c() but we want it to start on a new line everytime a cluster ends
#cat(brackets)

#plot(res$reachdist[res$order], type="h", col=result[res$order]+1L,
#     ylab = "Reachability dist.", xlab = "OPTICS order",	
#     main = "Reachability Plot")

