import subprocess as s
import sys, time, os

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
minPoints = [5, 10, 20, 30]
exeTimes = []

exePath = 'Scripts/Executors/R/ROpticsScript.R'
eps = 10
angle = -0.5

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"
    for min_pts in minPoints:
        args = ['Rscript', '--vanilla', exePath, filePath, str(eps), str(min_pts), str(angle)]
        print min_pts
        minPointsRun = []
        for i in range(0, 3):
            print i
            proc = s.Popen(args, stdout = s.PIPE)
            proc.wait()
            time = proc.stdout.read().decode('utf-8')
            print time
            minPointsRun.append(time)
        exeTimes.append(minPointsRun)

with open('R_minPts.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
