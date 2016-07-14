import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2001, 3000]
similarities = [0.5, 1, 5]
exeTimes = []

exePath = "../STSC/cpp/runner/build/runner"
K = 6
stop = 0.001
maxClust = 10

for size in sizes :
    print size
    filePath = "/tmp/" + str(size) + ".txt"

    for similarity in similaritites:
        similarityRun = []
	args = [exePath, filePath, str(maxClust), str(K), str(similarity), str(stop)]
	for i in range(0, 3):
	   print i
	   start = time.time()
	   proc = s.Popen(args)
	   proc.wait()
	   stop = time.time()
	   similarityRun.append(stop-start)
        exeTimes.append(similarityRun)

with open('Cpp_similarity.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
