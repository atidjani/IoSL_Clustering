import os

class Dataset :
    filePath = ""
    fileName = ""

    # If the file size < 2.5 Mb then it is uploaded in memory
    # It is then necessarily to save it on the disk for further computations

    def __init__(self, file, name) :
        self.fileName = name
        self.filePath = "/tmp/" + name

        # Check if another file with the same name exists. In case delete it.
        # The name of the file is the sessionid, so it's unique.
        if os.path.isfile(self.filePath) :
            self.clear()

        if file.size <= 2621440 :
            with open(self.filePath, 'wb+') as dest:
                for chunk in file.chunks():
                    dest.write(chunk)

    def fromDict(self, dict) :
        self.__dict__.update(dict)

    # Because of the visualization limits, datasets are supposed
    # to contain only 2D points.
    # The file should be a csv file with comma (,) as separator

    def checkDataset(self) :
        with open(self.filePath, 'r') as f:
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


    def clear(self) :
        os.remove(self.filePath)
