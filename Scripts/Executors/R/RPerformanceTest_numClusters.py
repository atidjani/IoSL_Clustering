import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

exeTimes = []

maxNumClusters = 15
numPoints = 5000
exePath = 'Scripts/Executors/R/ROpticsScript.R'
min_pts = 10
eps = 10
angle = -0.5

for numCluster in range(1, maxNumClusters + 1):
    print numCluster

    filePath = "/tmp/" + str(numCluster) + ".txt"
    X, y = make_blobs(n_samples=numPoints, centers=numCluster, n_features=2,  random_state=0, cluster_std=0.45, center_box=(-10,10))
    f = open(filePath, 'wb')
    for a in X:
        f.write((str(a[0]) +','+ str(a[1]) + '\n').encode('utf-8'))
    f.close()

    args = ['Rscript', '--vanilla', exePath, filePath, str(eps), str(min_pts), str(angle)]
    repTime = []
    for i in range(0, 3):
        print i
        proc = s.Popen(args, stdout = s.PIPE)
        proc.wait()
        time = proc.stdout.read().decode('utf-8')
        print time
        repTime.append(time)
    exeTimes.append(repTime)
    os.remove(filePath)

with open('R_numClusters.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
