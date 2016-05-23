import subprocess as s

'''
This class is responsible of running the self-tuning spectral clustering
algorithm. (C++ version)
'''

class StscRunner :
    __filePath = ""
    __K = 6
    __maxClusters = 0
    # The absolute path is totally BS. I'll change it. Have faith
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

