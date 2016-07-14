import subprocess as s
import sys, time, os

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
minPoints = [5, 10, 20, 30]
exeTimes = []

exePath = 'OPTICS/Java/elki/target/elki-0.7.2-SNAPSHOT.jar'
eps = 10
xi = 0.10

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"

    for min_pts in minPoints:
        args = ['java', '-jar', exePath, 'KDDCLIApplication', \
                '-dbc.in', filePath, \
                '-algorithm', 'clustering.optics.OPTICSXi', \
                '-opticsxi.xi', str(xi), \
                '-optics.minpts', str(min_pts), \
                '-optics.epsilon', str(eps)]
        minPointsRun = []
        print min_pts
        for i in range(0, 3):
            print i
            start = time.time()
            proc = s.Popen(args)
            proc.wait()
            stop = time.time()
            minPointsRun.append(stop-start)
        exeTimes.append(minPointsRun)

with open('J_minPts.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
