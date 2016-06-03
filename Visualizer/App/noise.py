import parser
import numpy as np

class Function() :
    __xBot = 0
    __xTop = 0
    __code = None

    def __init__(self, code, xBot, xTop) :
        self.__code = code
        self.__xBot = xBot
        self.__xTop = xTop

    def generate(self):
        x = np.random.uniform(self.__xBot, self.__xTop)
        y = eval(self.__code)
        # TODO: check for exception
        return [x, y]


class Noise() :
    __fun = None
    __sigma = 0

    def __init__ (self, strFunctions, sigma) :
        # TODO: Handle multiple functions
        self.__fun = self.parseFunction(strFunctions)
        self.__sigma = sigma

    def parseFunction(self, strFunction) :
        # Function format (function, x_low_limit, x_bot_limit)
        tokenList = strFunction.split(',')
        syntaxTree =  parser.expr(tokenList[0][1:]) #Remove initial (
        code = syntaxTree.compile()
        xBot = float(tokenList[1])
        xTop = float(tokenList[2][:-1]) # Remove final )

        return Function(code, xBot, xTop)

    def generatePoints(self, number) :
        points = []
        for i in range(0, number) :
            values = self.__fun.generate()
            newYValue = np.random.normal(values[1], self.__sigma)
            points.append([values[0], newYValue])

        #Generate String
        # Add \n in front to facilitate the writing of the dataset to the file
        pointsStr = '\n'.join([','.join(map(str,point)) for point in points])

        return pointsStr

