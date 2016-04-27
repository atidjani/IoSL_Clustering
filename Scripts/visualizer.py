import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import sys
import numpy

def plot(resMatrix):
    fig = plt.figure(figsize=(9,9))
    numClusters = len(set([i[2] for i in resMatrix]))
    colors = numpy.random.rand(numClusters, 3)
    for i in range(numClusters):
        plt.scatter([x[0] for x in resMatrix if x[2] == i],
                    [x[1] for x in resMatrix if x[2] == i],
                    c = colors[i],
                    edgecolors='none')

    plt.savefig('output.pdf', format='pdf')


def main(inputFile):
    # Open Input file
    f = open(inputFile, 'r')

    # Read data into a matrix
    resMatrix = []
    for line in f:
<<<<<<< HEAD
        input = line.split(' ', 4);
        resMatrix.append([float(input[0]), float(input[1]), float(input[2])])
=======
        input = line.split(',', 4);
        resMatrix.append([int(input[0]), int(input[1]), int(input[2])])
>>>>>>> edf80baa368f3734afba6eb591182c79292c0eb8

    plot(resMatrix)

if __name__ == "__main__":
    if (len(sys.argv) != 2):
        print("Usage: python visualizer [file]")
        exit(0)
    else:
        main(sys.argv[1])
