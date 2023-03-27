import numpy as np
import matplotlib.pyplot as plt

# create 3D array
arr = np.array([
    [[' ', ' ', '~'], ['1', '2', '3'], [' ', ' ', ' ']],
    [['4', ' ', '6'], [' ', ' ', ' '], ['7', ' ', '9']],
    [[' ', ' ', ' '], ['a', ' ', 'c'], [' ', ' ', ' ']]
])

# create plot
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# iterate over each element in the array and plot it as a text label
for i in range(arr.shape[0]):
    for j in range(arr.shape[1]):
        for k in range(arr.shape[2]):
            text = ax.text(i, j, k, arr[i][j][k], ha='center', va='center', color='black')

# set plot parameters
ax.set_xlim(0, arr.shape[0])
ax.set_ylim(0, arr.shape[1])
ax.set_zlim(0, arr.shape[2])
ax.set_xlabel('X')
ax.set_ylabel('Y')
ax.set_zlabel('Z')

plt.show()
