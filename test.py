import numpy as np
import os
import matplotlib.pyplot as plt
from mxnet import autograd, nd
import random

data = np.loadtxt(os.path.join('../Data/linear', 'ex1data2.txt'), delimiter=',')
X = data[:, :2]
y = data[:, 2]
m = y.size

def featureNormalize(X):
    X_norm = X.copy()
    mu = np.zeros(X.shape[1])
    sigma = np.zeros(X.shape[1])

    mu = np.mean(X_norm, axis=0)
    sigma = np.std(X_norm, axis=0)
    X_norm = (X_norm - mu) / sigma

    return X_norm, mu, sigma

X_norm, mu ,sigma = featureNormalize(X)
X = np.concatenate([np.ones((m, 1)), X_norm], axis=1)

def computeCost(X, y, theta):
    m = y.shape[0]
    J = 0
    h = X.dot(theta)
    J = 1.0 / (2*m)*np.sum(np.square(h-y))
    return J

def gradientDescent(X, y, theta, alpha, epoch):
    m = y.shape[0]
    theta = theta.copy()
    J_history = []
    for i in range(epoch):
        h = X.dot(theta)
        theta = theta - alpha*(1.0 / m)*(X.T.dot(h-y))
        J_history.append(computeCost(X, y, theta))
    return theta, J_history

alpha = 0.1
epoch = 500

theta = np.zeros(3)
theta, J_history = gradientDescent(X, y, theta, alpha, epoch)
# data = np.array([1, 1650, 3])
data = np.hstack([1, (X - mu) / sigma])

price = np.dot(theta, data)
print("Price : ", price)
