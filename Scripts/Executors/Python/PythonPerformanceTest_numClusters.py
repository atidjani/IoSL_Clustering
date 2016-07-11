import subprocess as s
import sys, time, os

sys.path.append("/home/fabio/First/IoSL/OPTICS/Python") #Change this path when running on your machine

from demo import Optics
from sklearn.datasets.samples_generator import make_blobs

exeTimes = []

maxNumClusters = 10
numPoints = 1000
min_pts = 10
eps = 15
threshold = 0.75

for numCluster in range(1, maxNumClusters + 1):
    print numCluster

    filePath = "/tmp/" + str(numCluster) + ".txt"
    X, y = make_blobs(n_samples=numPoints, centers=numCluster, n_features=2,  random_state=0, cluster_std=0.45, center_box=(-10,10))
    f = open(filePath, 'wb')
    for a in X:
        f.write((str(a[0]) +','+ str(a[1]) + '\n').encode('utf-8'))
    f.close()

    sizeTime = []
    for i in range(0, 3):
        print i
        start = time.time()
        opt = Optics(filePath, eps, min_pts, threshold)
        numClusters, clusters, rList = opt.demo()
        stop = time.time()
        sizeTime.append(stop-start)
    exeTimes.append(sizeTime)
    os.remove(filePath)

with open('P_numClusters.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
