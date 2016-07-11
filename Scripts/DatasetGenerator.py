import numpy as np
from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]

for size in sizes:
    filePath = str(size) + ".txt"
    X, y = make_blobs(n_samples=size, centers=5, n_features=2,  random_state=0, cluster_std=0.45, center_box=(-10,10))
    f = open(filePath, 'wb')
    for a in X:
        f.write((str(a[0]) +','+ str(a[1]) + '\n').encode('utf-8'))
