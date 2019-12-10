import os
import numpy as np

import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D  # needed to plot 3-D surfaces

# The first column is the population of a city (in 10,000s) and
# the second column is the profit of a food truck in that city (in $10,000s).

data = np.loadtxt(os.path.join("../Data/linear", "ex1data1.txt"),
                  delimiter=',')
X, y = data[:, 0], data[:, 1]
m = y.size

# plot the Data
def plotData(x, y):
    fig = plt.figure()
    plt.plot(x, y, 'ro', ms=10, mec='k')
    plt.xlabel('Population of City in 10,000s')
    plt.xlabel('Profit in $10,000')

plotData(X, y)
# pyplot.show()

X = np.stack([np.ones(m), X], axis=1)

def computeCost(X, y, theta):
    m = y.size
    J = 0
    h = X.dot(theta)
    J = 1 / (2*m)*np.sum(np.square(h-y))
    return J

J = computeCost(X, y, theta=np.array([0.0, 0.0]))
# print(J)
J = computeCost(X, y, theta=np.array([-1, 2]))
# print(J)

def gradientDescent(X, y, theta, alpha, epoch):
    m = y.shape[0]
    theta = theta.copy()
    J_history = []

    for i in range(epoch):
        h = X.dot(theta)
        theta = theta - alpha*(1.0 / m)*(X.T.dot(h-y))
        J_history.append(computeCost(X, y, theta))
    return theta, J_history

theta = np.zeros(2)
epoch = 1500
alpha = 0.01
theta, J_history = gradientDescent(X, y, theta,alpha, epoch)
print("Theta found by GDS: {:.4f}, {:.4f}".format(*theta))

plotData(X[:, 1], y)
plt.plot(X[:, 1], np.dot(X, theta), '-')
plt.legend(['Training Data', 'Linear regretion'])
# plt.show()

predict1 = np.dot([1, 3.5], theta)
print(predict1*10000)

theta0_vals = np.linspace(-10, 10, 100)
theta1_vals = np.linspace(-1, 4, 100)

# print(theta1_vals.shape)
# print(theta0_vals.shape)

J_vals = np.zeros((theta0_vals.shape[0],
                   theta1_vals.shape[0]))

for i, theta0 in enumerate(theta0_vals):
    for j, theta1 in enumerate(theta1_vals):
        J_vals[i, j] = computeCost(X, y, [theta0, theta1])

J_vals = J_vals.T
fig = plt.figure(figsize=(12, 5))
ax = fig.add_subplot(121, projection='3d')
ax.plot_surface(theta0_vals, theta1_vals, J_vals, cmap='viridis')
plt.xlabel('theta0')
plt.ylabel('theta1')
plt.title('Surface')
# plt.show()

ax = plt.subplot(122)

plt.contour(theta0_vals, theta1_vals, J_vals, linewidths=2, cmap='viridis',
            levels = np.logspace(-2, 3, 30))

plt.xlabel('theta0')
plt.xlabel('theta1')
plt.plot(theta[0], theta[1], 'ro', ms=10, lw=2)
plt.title('Contour, showing minimum')
# plt.show()

