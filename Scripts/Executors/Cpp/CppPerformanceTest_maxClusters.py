import subprocess as s
import sys, time, os

sizes = [250, 500, 1000, 1500, 2001, 3000]
maxNumClusters = [10, 25, 50]
exeTimes = []

exePath = "../STSC/cpp/runner/build/runner"
K = 6
simCut = 1
stop = 0.001


for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"

    for maxClust in maxNumClusters:
        clustTime = []
	args = [exePath, filePath, str(maxClust), str(K), str(simCut), str(stop)]
	for i in range(0, 3):
	   print i
	   start = time.time()
	   proc = s.Popen(args)
	   proc.wait()
	   stop = time.time()
	   clustTime.append(stop-start)
        exeTimes.append(clustTime)

with open('Cpp_maxCluster.txt', 'wb')  as f :
    for exe in exeTimes :
        f.write((str(exe[0]) + ',' + str(exe[1]) + ',' + str(exe[2]) + '\n').encode('utf-8'))
