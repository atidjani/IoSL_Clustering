import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

exeTimes = []

maxNumClusters = 10
numPoints = 5000
exePath = 'OPTICS/Java/elki/target/elki-0.7.2-SNAPSHOT.jar'
min_pts = 10
eps = 15
xi = 0.10

for numCluster in range(1, maxNumClusters + 1):
    print numCluster

    filePath = "/tmp/" + str(numCluster) + ".txt"
    X, y = make_blobs(n_samples=numPoints, centers=numCluster, n_features=2,  random_state=0, cluster_std=0.45, center_box=(-10,10))
    f = open(filePath, 'wb')
    for a in X:
        f.write((str(a[0]) +','+ str(a[1]) + '\n').encode('utf-8'))
    f.close()

    args = ['java', '-jar', exePath, 'KDDCLIApplication', \
            '-dbc.in', filePath, \
            '-algorithm', 'clustering.optics.OPTICSXi', \
            '-opticsxi.xi', str(xi), \
            '-optics.minpts', str(min_pts), \
            '-optics.epsilon', str(eps)]
    runTimes = []
    for i in range(0, 3):
        print i
        start = time.time()
        proc = s.Popen(args)
        proc.wait()
        stop = time.time()
        runTimes.append(stop-start)
    exeTimes.append(sizeTime)
    os.remove(filePath)

with open('result.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
