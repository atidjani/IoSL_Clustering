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
    __exePath = "/home/fabio/First/IoSL/STSC/cpp/runner/build/runner"

    def __init__ (self, filePath, maxClusters, K = 6) :
        self.__filePath = filePath
        self.__K = K
        self.__maxClusters = maxClusters

    def run(self) :
        args = [self.__exePath, self.__filePath, str(self.__maxClusters), str(self.__K)]
        proc = s.Popen(args, stdout = s.PIPE)
        return proc.stdout.read().decode('utf-8')


