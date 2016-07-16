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
    __stop = 0.001

    def __init__ (self, filePath, maxClusters, K, simCut, stop) :
        self.__filePath = filePath
        self.__K = K
        self.__maxClusters = maxClusters
        self.__simCut = simCut
        self.__stop = stop

    def run(self) :
        args = [self.__exePath, self.__filePath, str(self.__maxClusters), str(self.__K), str(self.__simCut), str(self.__stop)]
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
    __path_java = 'OPTICS/Java/elki/target/elki-0.7.2-SNAPSHOT.jar'

    def __init__ (self, filePath , eps = 0.4, min_pts = 5, angle = 120, thres = 0.75, xi = 0.1):
        self.__filePath = filePath
        self.__eps = eps
        self.__min_pts = min_pts
        self.__angle = angle
        self.__thres = thres
        self.__xi = xi

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


    def run_optics_java(self) :
        # Compute cluster assignments
        args = ['java', '-jar', self.__path_java, 'KDDCLIApplication', \
                '-dbc.in', self.__filePath, \
                '-algorithm', 'clustering.optics.OPTICSXi', \
                '-opticsxi.xi', str(self.__xi), \
                '-optics.minpts', str(self.__min_pts), \
                '-optics.epsilon', str(self.__eps),
                '-resulthandler', 'ClusteringVectorDumper']

        proc = s.Popen(args, stdout = s.PIPE)
        output = proc.stdout.read().decode('utf-8').split(' ')

        clstAssignment = list(map(int,output[:-2]))
        numClusters = max(clstAssignment) + 1

        # Compute reachability plot + points coordinates
        args = ['java', '-jar', self.__path_java, 'KDDCLIApplication', \
                '-dbc.in', self.__filePath, \
                '-algorithm', 'clustering.optics.OPTICSXi', \
                '-opticsxi.xi', str(self.__xi), \
                '-optics.minpts', str(self.__min_pts), \
                '-optics.epsilon', str(self.__eps),
                '-resulthandler', 'ResultWriter']

        proc = s.Popen(args, stdout = s.PIPE)
        output = proc.stdout.read().decode('utf-8').split('\n')

        # Output format ID=121 x y reachdist=.... predecessor=...
        clusters = []
        processed = [False] * len(clstAssignment)
        # Init reachabilities
        reachabilities = []
        for line in output[:-1]:
            if (line[0] != '#'): #Ignore comments
                # Split by space
                elements = line.split(' ')
                id = int(elements[0][3:]) - 1

                if (not(processed[id])) :
                    processed[id] = True
                    # Point coordinate
                    point = [float(elements[1]), float(elements[2]), clstAssignment[id]]
                    clusters.append(point)

                    # reach distance
                    if elements[4] == 'predecessor=null':
                        reach = -1
                    else :
                        reach = float(elements[3][10:])

                    reachabilities.append([reach, clstAssignment[id]])

        # Inf reach distances equal to max
        maxReach = max(i[0] for i in reachabilities)
        for i in range(0, len(reachabilities)) :
            if (reachabilities[i][0] == -1) :
                reachabilities[i][0] = maxReach



        return {'reachabilities' : reachabilities, 'clusters': clusters, 'numClusters': numClusters}


    def run(self, name):
        points = None
        if 'python' == name.lower():
            return self.run_optics_python()
        elif 'r' == name.lower():
            return self.run_optics_r()
        elif 'java' == name.lower():
            return self.run_optics_java()

# obj = OpticsRunner('./Datasets/2.txt',0.4, 7, 0.75)
# obj.run('python')
