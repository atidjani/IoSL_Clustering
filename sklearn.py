from sklearn.datasets.samples_generator import make_blobs
X, y = make_blobs(n_samples=1000, centers=[[1,1],[4,4]], n_features=2,  random_state=0, cluster_std=0.3, center_box=(-10,10))
print X
f = open('ds.txt', 'wb')
for a in X:
    f.write(str(a[0]) +','+ str(a[1]) + '\n')
#print y
#print(X.shape)
