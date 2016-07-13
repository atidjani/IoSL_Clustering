import subprocess as s
import sys, time, os

sys.path.append("OPTICS/Python") #Change this path when running on your machine

from demo import Optics
from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
thresholds = [0.5, 0.75, 0.90]
exeTimes = []
eps = 10
min_pts = 10

for size in sizes :
    print size
    filePath = "/tmp/" + str(size) + ".txt"
    for threshold in thresholds:
        print threshold
        thresholdRun = []
        for i in range(0, 3):
            print i
            start = time.time()
            opt = Optics(filePath, eps, min_pts, threshold)
            numClusters, clusters, rList = opt.demo()
            stop = time.time()
            thresholdRun.append(stop-start)
        exeTimes.append(thresholdRun)

with open('P_minPts.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
