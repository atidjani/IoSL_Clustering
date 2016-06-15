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

    def __init__ (self, filePath, maxClusters, K = 6) :
        self.__filePath = filePath
        self.__K = K
        self.__maxClusters = maxClusters

    def run(self) :
        args = [self.__exePath, self.__filePath, str(self.__maxClusters), str(self.__K)]
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
    __name = 'python'
    # these path should be relative to the containter -- see up
    __path_java = '/home/zahin/Dropbox/EIT_ICT/2nd_semester/IOS_Lab/IoSL_Clustering/OPTICS/'
    __path_r = 'home/zahin/Dropbox/EIT_ICT/2nd_semester/IOS_Lab/IoSL_Clustering/OPTICS/'

    def __init__ (self, filePath , eps = 0.4, min_pts = 5):
        self.__filePath = filePath
        self.__eps = eps
        self.__min_pts = min_pts

    def run_optics_python(self):
         # return ''
       opt = Optics()
       numClusters, points = opt.demo(self.__filePath, str(self.__eps), str(self.__min_pts))
       return numClusters, points

    def run_optics_java(self):
        return ''

    def run_optics_r(self):
        return ''

    def run(self, name):
        points = None
        if 'python' == name.lower():
            numClusters, points = self.run_optics_python()
        elif 'java' == name.lower():
            numClusters, points = self.run_optics_java()
        elif 'r' == name.lower():
            numClusters, = self.run_optics_r()

        return {'numClusters': numClusters, 'result': points }

# path = os.getcwd()
# obj = OpticsRunner('./Datasets/1.txt',0.4,5)
# obj.run('python')

