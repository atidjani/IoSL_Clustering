import sys,math
import matplotlib.pyplot as plt

sizes = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]

def mean(vect):
    sum = 0
    for i in vect:
        sum += i
    return sum/len(vect)

def variance(vect):
    m = mean(vect)
    m *= m
    ssum = 0
    for i in vect:
        ssum += i*i
    return math.sqrt(ssum/len(vect) - m)

resultTimes = []

with open(sys.argv[1], 'r')  as f:
    for line in f.readlines() :
        line = line.split(',')
        times = list(map(float, line))
        print(times)
        resultTimes.append(times)

time = []

for i in range(0, len(sizes)):
    time.append([mean(resultTimes[i]), variance(resultTimes[i])])

print(time)

fig = plt.figure(figsize=(9,9))
plt.errorbar(sizes, [i[0] for i in time], yerr=[i[1] for i in time], fmt='gs', label="")
plt.xlim([0, 16])
plt.ylim([0, 9])
plt.xlabel('# Clusters')
plt.ylabel('Execution time (s)')

fig.savefig("numClusters.pdf", format="pdf")
