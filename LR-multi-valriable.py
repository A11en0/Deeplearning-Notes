import numpy as np
import os
import matplotlib.pyplot as plt
from mxnet import autograd, nd
import random

# data = np.loadtxt(os.path.join('../Data/linear', 'ex1data2.txt'), delimiter=',')
# X = data[:, :2]
# y = data[:, 2]
# m = y.size

def featureNormalize(X):
    X_norm = X.copy()
    mu = np.zeros(X.shape[1])
    sigma = np.zeros(X.shape[1])

    mu = np.mean(X_norm, axis=0)
    sigma = np.std(X_norm, axis=0)
    X_norm = (X_norm - mu) / sigma

    return X_norm, mu, sigma

# X_norm, mu ,sigma = featureNormalize(X)
# X = np.concatenate([np.ones((m, 1)), X_norm], axis=1)

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

# alpha = 0.1
# epoch = 500
#
# theta = np.zeros(3)
# theta, J_history = gradientDescent(X, y ,theta, alpha, epoch)
#
# plt.plot(np.arange(len(J_history)), J_history, lw=2)
# plt.xlabel("epoch")
# plt.ylabel("Cost J")
# print(theta)
#
# data = np.array([1650, 3])
# data = np.hstack([1, (data - mu) / sigma])
# price = np.dot(theta, data)
# print("Price : ", price)
#
# plt.show()

num_inputs = 2
num_examples = 1000
true_w = [2, -3.4]
true_b = 8.8

X = np.random.normal(scale=1, size=(num_examples, num_inputs))
y = true_w[0]*X[:, 0] + true_w[1]*X[:, 1] + true_b
y += np.random.normal(scale=1, size=y.shape)
m = y.shape[0]

X_norm, mu ,sigma = featureNormalize(X)
X = np.concatenate([np.ones((m, 1)), X_norm], axis=1)

def data_iter(batch_size, features, lables):
    num_examples = len(features)
    indices = list(range(num_examples))
    random.shuffle(indices)
    for i in range(0, num_examples, batch_size):
        j = nd.array(indices[i: min(i + batch_size, num_examples)])
        yield features.take(j), lables.take(j)

w = np.random.normal(scale=0.01, size=(num_inputs,))
w = np.hstack([1, w])

alpha = 0.1
epoch = 5
w, J_history = gradientDescent(X, y, w, alpha, epoch)
print(w)