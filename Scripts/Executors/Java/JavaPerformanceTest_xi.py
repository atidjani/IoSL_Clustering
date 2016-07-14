import subprocess as s
import sys, time, os

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
xis = [0.05, 0.075, 0.1]
exeTimes = []

exePath = 'OPTICS/Java/elki/target/elki-0.7.2-SNAPSHOT.jar'
min_pts = 10
eps = 10

for size in sizes :
    filePath = "/tmp/" + str(size) + ".txt"
    for xi in xis:
        args = ['java', '-jar', exePath, 'KDDCLIApplication', \
                '-dbc.in', filePath, \
                '-algorithm', 'clustering.optics.OPTICSXi', \
                '-opticsxi.xi', str(xi), \
                '-optics.minpts', str(min_pts), \
                '-optics.epsilon', str(eps)]
        print xi
        xiRun = []
        for i in range(0, 3):
            print i
            start = time.time()
            proc = s.Popen(args)
            proc.wait()
            stop = time.time()
            xiRun.append(stop-start)
        exeTimes.append(xiRun)

with open('J_xi.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
