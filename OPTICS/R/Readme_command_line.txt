#Command line readme R environment
#to get the data
Rcpp::sourceCpp('folder_of_cpp/gradient_clustering.cpp')

#for linux of course the other \

 
#dataset to import
mySet<-read.table("folder/5.txt", sep=",")
#import library
library(dbscan)
#import the dataset from the environment
res <-optics(`mySet`,8,10)
result<-gradient_clustering(res$order,res$reachdist,res$coredist,res$minPts,-0.2)



#To get the plot
plot(res$reachdist[res$order], type="h", col=result[res$order]+1L, ylab = "Reachability dist.", xlab = "OPTICS order", main = "Reachability Plot")
