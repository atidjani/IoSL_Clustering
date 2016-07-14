import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

exeTimes = []

maxNumClusters = 15
numPoints = 1500
exePath = "STSC/cpp/runner/build/runner"
K = 6
simCut = 10
stop = 0.001
maxClust = 15

for numCluster in range(1, maxNumClusters + 1):
    print numCluster

    filePath = "/tmp/" + str(numCluster) + ".txt"
    X, y = make_blobs(n_samples=numPoints, centers=numCluster, n_features=2,  random_state=0, cluster_std=0.45, center_box=(-10,10))
    f = open(filePath, 'wb')
    for a in X:
        f.write((str(a[0]) +','+ str(a[1]) + '\n').encode('utf-8'))
    f.close()

    args = [exePath, filePath, str(maxClust), str(K), str(simCut), str(stop)]
    repTime = []
    for i in range(0, 3):
        print i
        start = time.time()
        proc = s.Popen(args, stdout = s.PIPE)
        proc.wait()
        stop = time.time()
        repTime.append(stop-start)
    exeTimes.append(repTime)
    os.remove(filePath)

with open('Cpp_numClusters.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
