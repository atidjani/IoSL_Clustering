import os

class Dataset :
    __filePath = ""
    __fileName = ""

    # If the file size < 2.5 Mb then it is uploaded in memory
    # It is then necessarily to save it on the disk for further computations

    def __init__(self, file) :
        self.__fileName = file.name
        self.__filePath = "/tmp/" + file.name
        if file.size <= 2621440 :
            with open(self.__filePath, 'wb+') as dest:
                for chunk in file.chunks():
                    dest.write(chunk)


    # Because of the visualization limits, datasets are supposed
    # to contain only 2D points.
    # The file should be a csv file with comma (,) as separator

    def checkDataset(self) :
        with open(self.__filePath, 'r') as f:
            for line in f.readlines():
                features = line.split(',')

                # Check if 2 elements where extracted.
                if len(features) != 2 :
                    return False

                # Check if all elements are float
                for f in features[:-1]:
                    try :
                        float(f)
                    except ValueError:
                        return False
        return True


    def getFilePath(self) :
        return self.__filePath

    # Destructor
    def __del__(self) :
        # File not more needed.
        os.remove(self.__filePath)

