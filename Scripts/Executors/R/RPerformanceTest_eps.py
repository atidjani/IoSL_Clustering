import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
epsilons = [1, 5, 10]
exeTimes = []

exePath = 'Scripts/Executors/R/ROpticsScript.R'
min_pts = 10
angle = -0.5

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"
    for eps in epsilons:
        args = ['Rscript', '--vanilla', exePath, filePath, str(eps), str(min_pts), str(angle)]
        print filePath
        print eps
        epsRun = []
        for i in range(0, 3):
            print i
            proc = s.Popen(args, stdout = s.PIPE)
            proc.wait()
            time = proc.stdout.read().decode('utf-8')
            print time
            epsRun.append(float(time))
        exeTimes.append(epsRun)

with open('R_eps.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
