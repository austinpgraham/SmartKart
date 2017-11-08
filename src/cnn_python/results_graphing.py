#!usr/bin/python -tt
import numpy as np
import matplotlib.pyplot as plt
import sys

file1 = open(sys.argv[1])
title = sys.argv[2]

# file1 = open("DQN.txt")
pointsd = np.loadtxt(fname = file1, delimiter = ':')
x = [int(p[0]) for p in pointsd]
y = [int(p[1]) for p in pointsd]

# file2 = open("Q-Table.txt")

plt.scatter(x, y, c='b')
#plt.axis([0, 1000, -450, 50])
plt.xlabel('Episode Number')
plt.ylabel('On-Road')
plt.title(title)
plt.show()
