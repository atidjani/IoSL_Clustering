import subprocess as s
import sys, time, os

sys.path.append("/home/fabio/First/IoSL/OPTICS/Python") #Change this path when running on your machine

from demo import Optics
from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 1750]
minPoints = [5, 10, 20, 30]
exeTimes = []
eps = 10
threshold = 0.75

for size in sizes :
    print(size)
    sizeTime = []
    filePath = "/tmp/" + str(size) + ".txt"
    for minPoint in minPoints :
        print (minPoint)
        for i in range(0, 3):
            print (i)
            start = time.time()
            opt = Optics(filePath, eps, minPoint, threshold)
            numClusters, clusters, rList = opt.demo()
            stop = time.time()
            sizeTime.append(stop-start)
        exeTimes.append(sizeTime)

with open('result.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
