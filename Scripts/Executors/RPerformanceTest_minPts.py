import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

sizes = [5000]
minPoints = [5, 10, 20, 30]
exeTimes = []

exePath = 'OPTICS/R/optics_gradient_commandline.R'
eps = 10
angle = -0.5

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"
    sizeTime = []
    for minPoint in minPoints:
        args = ['Rscript', exePath, filePath, str(eps), str(minPoint), str(angle)]
        print minPoint
        for i in range(0, 3):
            print i
            start = time.time()
            proc = s.Popen(args, stdout = s.PIPE)
            proc.wait()
            stop = time.time()
            sizeTime.append(stop-start)
        exeTimes.append(sizeTime)

with open('result.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
