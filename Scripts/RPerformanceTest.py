import subprocess as s
import sys, time, os

from sklearn.datasets.samples_generator import make_blobs

sizes = [250, 500, 1000, 1500, 2000, 3000]
exeTimes = []

exePath = '../OPTICS/R/optics_gradient_commandline.R'
filePath = "ds.txt"
eps = 10
min_pts = 15
angle = -0.5



for size in sizes :
    print size
    X, y = make_blobs(n_samples=size, centers=[[0,-0.5],[4,1],[2,2]], n_features=2,  random_state=0, cluster_std=0.45, center_box=(-10,10))
    f = open(filePath, 'wb')
    for a in X:
        f.write((str(a[0]) +','+ str(a[1]) + '\n').encode('utf-8'))

    sizeTime = []
    args = ['Rscript', exePath, filePath, str(eps), str(min_pts), str(angle)]

    for i in range(0, 3):
       print i
       start = time.clock()
       proc = s.Popen(args, stdout = s.PIPE)
       proc.wait()
       stop = time.clock()
       sizeTime.append(stop-start)

    exeTimes.append(sizeTime)
    os.remove(filePath)

with open('result.txt', 'wb')  as f :
    for sizeRun in exeTimes :
        f.write((str(sizeRun[0]) + ',' + str(sizeRun[1]) + ',' + str(sizeRun[2]) + '\n').encode('utf-8'))
