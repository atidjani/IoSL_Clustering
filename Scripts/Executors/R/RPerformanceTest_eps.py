import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
epsilons = [0.5, 1, 5]
exeTimes = []

exePath = 'OPTICS/R/optics_gradient_commandline.R'
min_pts = 10
angle = -0.5

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"
    sizeTime = []
    for eps in epsilons:
        args = ['Rscript', exePath, filePath, str(eps), str(min_pts), str(angle)]
        print eps
        for i in range(0, 3):
            print i
            start = time.time()
            proc = s.Popen(args, stdout = s.PIPE)
            proc.wait()
            stop = time.time()
            sizeTime.append(stop-start)
        exeTimes.append(sizeTime)

with open('R_eps.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
