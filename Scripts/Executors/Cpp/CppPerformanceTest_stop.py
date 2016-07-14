import subprocess as s
import sys, time, os

sizes = [250, 500, 1000, 1500, 2001, 3000]
stops = [0.01, 0.001, 0.0001]
exeTimes = []

exePath = "../STSC/cpp/runner/build/runner"
K = 6
maxClust = 10
simCut = 5

for size in sizes :
    print size
    filePath = "/tmp/" + str(size) + ".txt"

    for stopCriteria in stops:
        stopRun = []
	args = [exePath, filePath, str(maxClust), str(K), str(simCut), str(stopCriteria)]
	for i in range(0, 3):
	   print i
	   start = time.time()
	   proc = s.Popen(args)
	   proc.wait()
	   stop = time.time()
	   stopRun.append(stop-start)
        exeTimes.append(stopRun)

with open('Cpp_stop.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
