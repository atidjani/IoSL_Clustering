import subprocess as s
import sys, time, os

sys.path.append("OPTICS/Python") #Change this path when running on your machine

from demo import Optics
from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
minPoints = [5, 10, 20, 30]
exeTimes = []
eps = 10
threshold = 0.75

for size in sizes :
    print size
    filePath = "/tmp/" + str(size) + ".txt"
    for min_pts in minPoints :
        print min_pts
        minPointsRun = []
        for i in range(0, 3):
            print i
            start = time.time()
            opt = Optics(filePath, eps, min_pts, threshold)
            numClusters, clusters, rList = opt.demo()
            stop = time.time()
            minPointsRun.append(stop-start)
        exeTimes.append(minPointsRun)

with open('P_minPts.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
