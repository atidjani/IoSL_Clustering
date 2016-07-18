"""
Created on Mar 17, 2012

@author: Amy X Zhang
axz@mit.edu
http://people.csail.mit.edu


Demo of OPTICS Automatic Clustering Algorithm
https://github.com/amyxzhang/OPTICS-Automatic-Clustering

"""

import numpy as np
import OpticsClusterArea as OP
from itertools import *
import AutomaticClustering as AutoC
import sys,os,csv
curr_file_path = os.path.abspath(os.path.join(os.path.dirname(__file__)))

use_plot = False
if use_plot:
    import matplotlib.pyplot as plt

class Optics():
    # parameters that we can manipulate to get optimal results
    __minpts = 5
    __eps = 0.5
    __thres = 0.75
    __inputfile = ''

    def __init__(self, inputfile, eps = 0.4, minpts = 7, thres = 0.75):
        self.__minpts = minpts # The parameter MinPts allows the core-distance and
        # reachability-distance of a point p to capture the point density around that point
        self.__eps = eps
        self.__thres = thres
        self.__inputfile = inputfile

    def demo(self):
        # generat   e some spatial data with varying densities
        # np.random.seed(0)

        # n_points_per_cluster = 250
        #
        X = np.empty((0, 2))
        # X = np.r_[X, [-5,-2] + .8 * np.random.randn(n_points_per_cluster, 2)]
        # X = np.r_[X, [4,-1] + .1 * np.random.randn(n_points_per_cluster, 2)]
        # X = np.r_[X, [1,-2] + .2 * np.random.randn(n_points_per_cluster, 2)]
        # X = np.r_[X, [-2,3] + .3 * np.random.randn(n_points_per_cluster, 2)]
        # X = np.r_[X, [3,-2] + 1.6 * np.random.randn(n_points_per_cluster, 2)]
        # X = np.r_[X, [5,6] + 2 * np.random.randn(n_points_per_cluster, 2)]
        # aa=[-5,-2]
        # bb=np.random.randn(n_points_per_cluster, 1)

        k = self.__minpts

        points_list = []
        # Using different starting points in the OPTICS algorithm will also produce different reachability plots
        with open(self.__inputfile, 'rb') as f:
            try:
                file_reader = csv.reader(f, delimiter=',')
            except IOError:
                print ("Error Reading csv File" + str(file_reader))
                sys.exit()
            for row in file_reader:
                p_tuple = [float(row[0]),float(row[1])]
                points_list.append(p_tuple)

        X = np.array(points_list)

        ## plot scatterplot of points

        if use_plot:
            pass
            # fig = plt.figure()
            # ax = fig.add_subplot(111)
            #
            # ax.plot(X[:,0], X[:,1], 'b.', ms=2)

            # plt.savefig('%s/Graph.png'%curr_file_path, dpi=None, facecolor='w', edgecolor='w', orientation='portrait', papertype=None, format=None,
            #     transparent=False, bbox_inches=None, pad_inches=0.1)
            # plt.show()


        #run the OPTICS algorithm on the points, using a smoothing value (0 = no smoothing)
        RD, CD, order = OP.optics(X,k,'euclidean') # hcluster uses euclideon function to calculate distance
        RPlot = []
        RPoints = []
        R_points_ = [] # Points for the visualizer
        Rpp = [] # only store raechability values
        for item in order:
            RPlot.append(RD[item]) #Reachability Plot
            # R_points.append([RD[item], 0])
            RPoints.append([X[item][0],X[item][1]]) #points in their order determined by OPTICS
            R_points_.append([X[item][0],X[item][1],RD[item]]) # contains points in order they are visited
            Rpp.append([RD[item],0])

        #hierarchically cluster the data
        rootNode = AutoC.automaticCluster(RPlot, RPoints, self.__thres)

        #print Tree (DFS)
        AutoC.printTree(rootNode, 0)

        if use_plot:
            #graph reachability plot and tree
            AutoC.graphTree(rootNode, RPlot)

        #array of the TreeNode objects, position in the array is the TreeNode's level in the tree
        array = AutoC.getArray(rootNode, 0, [0])

        #get only the leaves of the tree
        leaves = AutoC.getLeaves(rootNode, [])
        # print X[:,0]
        if use_plot:
            #graph the points and the leaf clusters that have been found by OPTICS
            fig = plt.figure()
            ax = fig.add_subplot(111)
            ax.plot(X[:,0], X[:,1], 'y.')   # X contains all the points
        colors = cycle('gmkrcbgrcmk')

        count = 0
        # these lists are used get reachability values and points in order of reachability plot
        # that includes the outliers as well
        clusters_points = []
        cluster_pts = []
        R_points = []
        for item, c in zip(leaves, colors):
            node = []
            for v in range(item.start,item.end): # start and end order value of each cluster
                node.append(RPoints[v]); #print RPoints[v]
                clusters_points.append([RPoints[v][0],RPoints[v][1],count]) # cluster points and cluster no.
                cluster_pts.append([RPoints[v][0],RPoints[v][1]]) # to get the outliers
                R_points.append([R_points_[v][2],count]) # Reachability points and cluster no.

            node = np.array(node)
            # print len(node)
            if use_plot:
                ax.plot(node[:,0],node[:,1], c+'o', ms=5)  # x and y axis values
            count += 1

        diff_points = []
        diff_reach = []
        count += 1

        # To filter out outliers from complete datasets
        for item in range(len(R_points_)):
            if R_points_[item][:2] not in cluster_pts:
                diff_points.append(R_points_[item][:2] + [count-1])
                diff_reach.append(R_points_[item][2:3] + [count-1])
            else:
                # map the reachability values with the points in clusters
                for ii, row in enumerate(clusters_points):
                    if[i for i, j in zip(R_points_[item][:2], row[:2]) if i == j]:
                        diff_points.append(row)
                        diff_reach.append(R_points[ii])
                        break

        if use_plot:
            plt.savefig('%s/Graph2.png'%curr_file_path, dpi=None, facecolor='w', edgecolor='w', orientation='portrait', papertype=None, format=None,
                transparent=False, bbox_inches=None, pad_inches=0.1)
            plt.show()

        return (count, diff_points, diff_reach)