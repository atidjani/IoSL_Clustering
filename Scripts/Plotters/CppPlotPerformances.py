import sys,math
import matplotlib.pyplot as plt

sizes = [250, 500, 1000, 1500, 2000, 3000]
maxNumClusters = [10, 15, 30]

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

ten = []
fifteen = []
thirty = []

for i in range(0, len(sizes)):
    ten.append([mean(resultTimes[i*3]), variance(resultTimes[i*3])])
    fifteen.append([mean(resultTimes[i*3 + 1]), variance(resultTimes[i*3 + 1])])
    thirty.append([mean(resultTimes[i*3 + 2]), variance(resultTimes[i*3 + 2])])

print(ten)
print(fifteen)
print(thirty)

fig = plt.figure(figsize=(9,9))
plt.errorbar(sizes, [i[0] for i in ten], yerr=[i[1] for i in ten], fmt='g^', label="10")
plt.errorbar(sizes, [i[0] for i in fifteen], yerr=[i[1] for i in fifteen], fmt='b^', label="15")
plt.errorbar(sizes, [i[0] for i in thirty], yerr=[i[1] for i in thirty], fmt='r^', label="30")
plt.xlim([0, 3500])
plt.xlabel('Dataset Sizes')
plt.ylabel('Execution time (s)')

fig.savefig("STSC_Performances.pdf", format="pdf")
