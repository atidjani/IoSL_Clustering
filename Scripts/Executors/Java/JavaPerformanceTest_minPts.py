import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 10000, 20000]
minPoints = [5, 10, 20, 30]
exeTimes = []

exePath = 'OPTICS/R/optics_gradient_commandline.R'
eps = 10
xi = 0.10

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"
    sizeTime = []

    for minPoint in minPoints:
        args = ['java -jar', exePath, 'KDDCLIApplication' \
                '-dbc.in', filePath, \
                '-algorithm clustering.optics.OPTICSXi', \
                '-opticsxi.xi', str(xi), \
                '-optics.minpts', str(minPoint), \
                '-optics.epsilon', str(eps)]

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
