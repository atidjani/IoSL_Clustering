library("dbscan")

## use the numeric variables in the iris dataset
data("iris")
x <- as.matrix(iris[, 1:4])

## OPTICS
opt <- optics(x, eps = 1, minPts = 4, eps_cl = .4)
opt
## create a reachability plot (extracted DBSCAN clusters at eps_cl=.4 are colored)
plot(opt)
## plot the extracted DBSCAN clustering
pairs(x, col = opt$cluster + 1L)