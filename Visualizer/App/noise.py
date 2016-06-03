import parser
import math
import numpy as np

class Function() :
    __sigma = 0
    __nPoints = 0
    __xBot = 0
    __xTop = 0
    __code = None

    def __init__(self, code, xBot, xTop, sigma, nPoints) :
        self.__code = code
        self.__xBot = xBot
        self.__xTop = xTop
        self.__sigma = sigma
        self.__nPoints = nPoints

    def generate(self):
        points = []
        for i in range(0, self.__nPoints) :
            x = np.random.uniform(self.__xBot, self.__xTop)
            y = eval(self.__code)
            newYValue = np.random.normal(y, self.__sigma)
            points.append([x, newYValue])

        return points


class Noise() :
    __funs = []

    def __init__ (self, strFunctions) :
        self.__funs = self.parseFunctions(strFunctions)

    def parseFunctions(self, strFunctions) :
        functions = []
        # Functions format (function, x_low_limit, x_bot_limit,sigma, nPoints);(function, x_low_limit, x_bot_limit, sigma, nPoints)
        tokenFuns = strFunctions.split(';')
        for fun in tokenFuns:
            tokenList = fun.split(',')
            syntaxTree =  parser.expr(tokenList[0][1:]) #Remove initial (
            code = syntaxTree.compile()
            xBot = float(tokenList[1])
            xTop = float(tokenList[2])
            sigma = float(tokenList[3])
            nPoints = int(tokenList[4][:-1]) #Remove final )
            functions.append(Function(code, xBot, xTop, sigma, nPoints))

        return functions

    def generatePoints(self) :
        points = []
        for fun in self.__funs :
            for point in fun.generate():
                points.append(point)

        #Generate string
        pointsStr = '\n'.join([','.join(map(str,point)) for point in points])

        return pointsStr

