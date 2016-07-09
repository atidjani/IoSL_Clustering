import sys,math
import matplotlib.pyplot as plt

sizes = [250, 500, 1000, 1500, 1750]
minPoints = [5, 10, 20, 30]

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

five = []
ten = []
twenty = []
thirty = []

for i in range(0, len(sizes)):
    five.append([mean(resultTimes[i*3]), variance(resultTimes[i*3])])
    ten.append([mean(resultTimes[i*3 + 1]), variance(resultTimes[i*3 + 1])])
    twenty.append([mean(resultTimes[i*3 + 2]), variance(resultTimes[i*3 + 2])])
    thirty.append([mean(resultTimes[i*3 + 3]), variance(resultTimes[i*3 + 3])])

print(five)
print(ten)
print(twenty)
print(thirty)

fig = plt.figure(figsize=(9,9))
plt.errorbar(sizes, [i[0] for i in five], yerr=[i[1] for i in five], fmt='gs', label="5")
plt.errorbar(sizes, [i[0] for i in ten], yerr=[i[1] for i in ten], fmt='bo', label="10")
plt.errorbar(sizes, [i[0] for i in twenty], yerr=[i[1] for i in twenty], fmt='r^', label="20")
plt.errorbar(sizes, [i[0] for i in thirty], yerr=[i[1] for i in thirty], fmt='y^', label="30")
plt.xlim([0, 2000])
plt.ylim([0, 10])
plt.xlabel('Dataset Sizes')
plt.ylabel('Execution time (s)')

fig.savefig("RPerformances_minPts.pdf", format="pdf")
