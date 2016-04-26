library("dbscan")
set.seed(2)
n <- 400
x <- cbind(
  x = runif(4, 0, 1) + rnorm(n, sd=0.1),
  y = runif(4, 0, 1) + rnorm(n, sd=0.1)
)
plot(x, col=rep(1:4, time = 100))
### run OPTICS
res <- optics(x, eps = 10,  minPts = 10)
res
### get order
res$order
### plot produces a reachability plot
plot(res)
### identify clusters by cutting the reachability plot (black is noise)
res <- optics_cut(res, eps_cl = .065)
res
plot(res)
plot(x, col = res$cluster+1L)
### re-cutting at a higher eps threshold
res <- optics_cut(res, eps_cl = .1)
res
plot(res)
plot(x, col = res$cluster+1L)
### identify clusters using the hierarchically Xi method
# res <- opticsXi(res, xi = 0.05)
# res
# plot(res)
# plot(x, col = res$cluster+1L)
# Xi cluster structure
# res$clusters_xi
### use OPTICS on a precomputed distance matrix
d <- dist(x)
res <- optics(x, eps = 1, minPts = 10)
plot(res)
