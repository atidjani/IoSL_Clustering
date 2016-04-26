set.seed(2)
n <- 400
x <- cbind(
  x = runif(4, 0, 1) + rnorm(n, sd=0.1),
  y = runif(4, 0, 1) + rnorm(n, sd=0.1)
)
# write.csv(x, "dataset.csv")
# Or in a file text
write.table(x,"dataset.txt", sep="\t")
