"""
Created on Mar 17, 2012

@author: Amy X Zhang
axz@mit.edu 
http://people.csail.mit.edu


Demo of OPTICS Automatic Clustering Algorithm
https://github.com/amyxzhang/OPTICS-Automatic-Clustering

"""

import numpy as np
import matplotlib.pyplot as plt
import OpticsClusterArea as OP
from itertools import *
import AutomaticClustering as AutoC
import sys,os,csv

# generate some spatial data with varying densities
# np.random.seed(0)

# n_points_per_cluster = 250
#
X = np.empty((0, 2))
# X = np.r_[X, [-5,-2] + .8 * np.random.randn(n_points_per_cluster, 2)]
#
# X = np.r_[X, [4,-1] + .1 * np.random.randn(n_points_per_cluster, 2)]
#
# X = np.r_[X, [1,-2] + .2 * np.random.randn(n_points_per_cluster, 2)]
#
# X = np.r_[X, [-2,3] + .3 * np.random.randn(n_points_per_cluster, 2)]
#
# X = np.r_[X, [3,-2] + 1.6 * np.random.randn(n_points_per_cluster, 2)]
#
# X = np.r_[X, [5,6] + 2 * np.random.randn(n_points_per_cluster, 2)]
# aa=[-5,-2]
# bb=np.random.randn(n_points_per_cluster, 1)
# print bb

############Input Parameters#########
k = 9 # Min. number of neighboring points in cluster
eps = 0.4 # max raduis of the neighborhood
#####################################

if len(sys.argv)!=2:
    print "     Incorrect input paramaters    "
    print "*********How to run the script*****"
    print "python optics.py <inputfile.txt>   "
    sys.exit(1)

points_list = list()
with open(sys.argv[1], 'rb') as f:
    try:
        file_reader = csv.reader(f, delimiter=',')
    except IOError:
        print "Error Reading csv File", file_reader
        sys.exit()
    for row in file_reader:
        p_tuple = [float(row[0]),float(row[1])]
        points_list.append(p_tuple)

X = np.array(points_list)
# print X

## plot scatterplot of points

fig = plt.figure()
ax = fig.add_subplot(111)

ax.plot(X[:,0], X[:,1], 'b.', ms=2)

plt.savefig('Graph.png', dpi=None, facecolor='w', edgecolor='w',
    orientation='portrait', papertype=None, format=None,
    transparent=False, bbox_inches=None, pad_inches=0.1)
plt.show()



#run the OPTICS algorithm on the points, using a smoothing value (0 = no smoothing)
RD, CD, order = OP.optics(X,k)

RPlot = []
RPoints = []

for item in order:
    RPlot.append(RD[item]) #Reachability Plot
    RPoints.append([X[item][0],X[item][1]]) #points in their order determined by OPTICS

#hierarchically cluster the data
rootNode = AutoC.automaticCluster(RPlot, RPoints)

#print Tree (DFS)
AutoC.printTree(rootNode, 0)

#graph reachability plot and tree
AutoC.graphTree(rootNode, RPlot)

#array of the TreeNode objects, position in the array is the TreeNode's level in the tree
array = AutoC.getArray(rootNode, 0, [0])

#get only the leaves of the tree
leaves = AutoC.getLeaves(rootNode, [])

#graph the points and the leaf clusters that have been found by OPTICS
fig = plt.figure()
ax = fig.add_subplot(111)

ax.plot(X[:,0], X[:,1], 'y.')
colors = cycle('gmkrcbgrcmk')
for item, c in zip(leaves, colors):
    node = []
    for v in range(item.start,item.end):
        node.append(RPoints[v])
    node = np.array(node)
    ax.plot(node[:,0],node[:,1], c+'o', ms=5)

plt.savefig('Graph2.png', dpi=None, facecolor='w', edgecolor='w',
    orientation='portrait', papertype=None, format=None,
    transparent=False, bbox_inches=None, pad_inches=0.1)
plt.show()
