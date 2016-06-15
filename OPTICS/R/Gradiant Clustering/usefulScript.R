library("dbscan")
plot(dataset_1, col=rep(1:4, time = 100))
### run OPTICS
res1 <- optics(dataset_1, eps =10,  minPts = 15)
plot(res1)
result <- gradient_clustering(res1$order,res1$reachdist,res1$coredist,res1$minPts,-0.99)
for(i in 1:length(result)){
  vector <- HelpFunction(result[[i]], length(res1$order))
  plot(res1$reachdist[res1$order], type="h", col=vector[res1$order]+1L,
       ylab = "Reachability dist.", xlab = "Clusters",	
       main = "Reachability Plot")
}