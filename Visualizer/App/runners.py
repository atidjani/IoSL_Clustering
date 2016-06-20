import subprocess as s
import os,django,sys,re
curr_file_path = os.path.abspath(os.path.join(os.path.dirname(__file__)))
path = re.search(r'(.*/Visualizer)',curr_file_path).group(1)
path_proj = re.search(r'(.*/)',path).group(1)
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s' %path)))
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__),'%s/OPTICS/Python' %path_proj)))
from demo import Optics

'''
This class is responsible of running the self-tuning spectral clustering
algorithm. (C++ version)
'''

class StscRunner :
    __filePath = ""
    __K = 6
    __maxClusters = 0
    __exePath = "STSC/cpp/runner/build/runner"
    __simCut = 0.3

    def __init__ (self, filePath, maxClusters, K, simCut) :
        self.__filePath = filePath
        self.__K = K
        self.__maxClusters = maxClusters
        self.__simCut = simCut

    def run(self) :
        args = [self.__exePath, self.__filePath, str(self.__maxClusters), str(self.__K), str(self.__simCut)]
        proc = s.Popen(args, stdout = s.PIPE)
        tmp = proc.stdout.read().decode('utf-8').split('\n')

        resArray = []
        for element in tmp[:-1]:
            element = element.split(',')
            eList = list(map(float,element[:-1]))
            eList.append(int(element[-1]))
            resArray.append(eList)

        numClusters = max(i[2] for i in resArray) + 1

        return {'numClusters': numClusters, 'result': resArray }


'''
This class is responsible of running the Optics algorithm (C++, java, python)
'''
class OpticsRunner():
    __filePath = ""
    __eps = 0.4
    __min_pts = 5
    __angle = -0.5
    __thres = 0.75
    __path_rScript = 'OPTICS/R/optics_gradient_commandline.R'

    def __init__ (self, filePath , eps = 0.4, min_pts = 5, angle = 120, thres = 0.75):
        self.__filePath = filePath
        self.__eps = eps
        self.__min_pts = min_pts
        self.__angle = angle
        self.__thres = thres

    def run_optics_python(self):
        opt = Optics(self.__filePath, self.__eps, self.__min_pts, self.__thres)
        numClusters, clusters, rList = opt.demo()
        return {'reachabilities' : rList, 'clusters': clusters, 'numClusters': numClusters}

    def run_optics_r(self):
        args = ['Rscript', self.__path_rScript, self.__filePath, str(self.__eps), str(self.__min_pts), str(self.__angle)]
        proc = s.Popen(args, stdout = s.PIPE)
        tmp = proc.stdout.read().decode('utf-8').split('\n')
        # read clusters
        clusters = []
        i = 1

        while tmp[i] != '=' : # = is the separator between clusters and reachabilities
            element = tmp[i]
            element = element.split(',')
            eList = list(map(float,element[:-1]))
            eList.append(int(element[-1]))
            clusters.append(eList)
            i += 1

        numClusters = max(i[2] for i in clusters) + 1

        # read reachabilities

        i += 1
        rList = []
        while i < len(tmp)-1:
            element = tmp[i]
            element = element.split(',')
            if element[0] == 'Inf':
                eList = [-1]
            else :
                eList = [float(element[0])]
            eList.append(int(element[1]))
            rList.append(eList)
            i += 1

        # Substitute the Inf reachabilities (-1) with the max value
        maxReach = max(i[0] for i in rList) # Find Max
        # subsitute elements
        for i in range(0,len(rList)) :
            if rList[i][0] == -1:
                rList[i][0] = maxReach


        return  {'reachabilities' : rList, 'clusters': clusters, 'numClusters': numClusters}

    def run(self, name):
        points = None
        if 'python' == name.lower():
            return self.run_optics_python()
        elif 'r' == name.lower():
            return self.run_optics_r()

# obj = OpticsRunner('./Datasets/2.txt',0.4, 7, 0.75)
# obj.run('python')