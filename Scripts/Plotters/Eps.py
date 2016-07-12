import sys,math
import matplotlib.pyplot as plt

sizes = [250, 500, 1000, 1500, 2000, 2500, 3000, 5000, 7500, 10000]
epsilons = [1, 5, 10]

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

one = []
five = []
ten = []

for i in range(0, len(sizes)):
    one.append([mean(resultTimes[i*3]), variance(resultTimes[i*3])])
    five.append([mean(resultTimes[i*3 + 1]), variance(resultTimes[i*3 + 1])])
    ten.append([mean(resultTimes[i*3 + 2]), variance(resultTimes[i*3 + 2])])

print(one)
print(five)
print(ten)

fig = plt.figure(figsize=(9,9))
plt.errorbar(sizes, [i[0] for i in one], yerr=[i[1] for i in one], fmt='r^', label="1")
plt.errorbar(sizes, [i[0] for i in five], yerr=[i[1] for i in five], fmt='y^', label="5")
plt.errorbar(sizes, [i[0] for i in ten], yerr=[i[1] for i in ten], fmt='b^', label="10")
plt.xlim([0, 10500])
plt.xlabel('Dataset Sizes')
plt.ylabel('Execution time (s)')

fig.savefig("Eps.pdf", format="pdf")
