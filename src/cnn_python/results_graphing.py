#!usr/bin/python -tt
import numpy as np
import matplotlib.pyplot as plt
 
file1 = open("DQN.txt")
pointsd = np.loadtxt(fname = file1, delimiter = ':')

file2 = open("Q-Table.txt")
pointsq = np.loadtxt(fname = file2, delimiter = ':')

plt.plot(pointsd[:,0], pointsd[:,1], '-b', label="Deep Q-Learning")
plt.plot(pointsq[:,0], pointsq[:,1], '-r', label="Q-Table Learning")
plt.axis([0, 1000, -450, 50])
plt.xlabel('Episode Number')
plt.ylabel('Total Reward')
plt.title('Total Reward vs. Episode')
plt.legend(loc='lower left')
plt.show()