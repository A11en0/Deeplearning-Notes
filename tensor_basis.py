import os
import numpy as np
import tensorflow as tf

'''
关闭控制台警告
'''
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'

# a = tf.placeholder(tf.int64)
# b = tf.placeholder(tf.int64)
# c = tf.random_normal([2, 4])
#
# add = tf.add(a, b)
#
# with tf.Session() as sess:
#     print("%i" % sess.run(add, feed_dict={a: 1, b: 2}))
#

'''
使用tensorflow定义常量、计算图、Session
'''
# x = tf.constant([[1.0, 2.0]])
# w = tf.constant([[3.0], [4.0]])
# result = tf.matmul(x, w)
# print(result)
# with tf.Session() as sess:
#     print(sess.run(result))


# x = tf.constant([[0.7, 0.5]])
#
# w1 = tf.Variable(tf.random_normal([2, 3], stddev=1, seed=1))
# w2 = tf.Variable(tf.random_normal([3, 1], stddev=1, seed=1))
#
# a = tf.matmul(x, w1)
# y = tf.matmul(a, w2)
#
# with tf.Session() as s:
#     init_vals = tf.global_variables_initializer()
#     s.run(init_vals)
#     print("y -> ", s.run(y))

'''
理解placeholder
使用random_normal生成正态分布的随机数
'''
# ### 单组数据的placeholder
# # x = tf.placeholder(tf.float32, shape=(1, 2))
#
# ## 多组数据的placeholder
# x = tf.placeholder(tf.float32, shape=(None, 2))
# w1 = tf.Variable(tf.random_normal([2, 3], stddev=1))
# w2 = tf.Variable(tf.random_normal([3, 1], stddev=1))
#
# a = tf.matmul(x, w1)
# y = tf.matmul(a, w2)
#
# with tf.Session() as s:
#     init_op = tf.global_variables_initializer()
#     s.run(init_op)
#
#     ### 喂入单组数据
#     # print("y -> ", s.run(y, feed_dict={x: [[0.7, 0.5]]}))
#
#     ### 喂入多组数据
#     print("y -> ", s.run(y, feed_dict={x: [[0.7, 0.5], [0.6, 0.9], [1.6, 2.8], [5.4, 6.7]]}))
#     print("w1 -> ", s.run(w1))
#     print("w2 -> ", s.run(w2))


'''
完整的神经网络搭建
八股： 准备、前传、反传、迭代
'''

BATCH_SIZE = 8

X = np.random.rand(32, 2)

Y = [[int(x + y < 1)] for x, y in X]

# 定义输入输出， 定义前向传播过程

x = tf.placeholder(tf.float32, shape=(None, 2))
y_ = tf.placeholder(tf.float32, shape=(None, 1))

w1 = tf.Variable(tf.random_normal([2, 3], stddev=1, seed=1))
w2 = tf.Variable(tf.random_normal([3, 2], stddev=1, seed=1))

a = tf.matmul(x, w1)
y = tf.matmul(a, w2)

# 定义反向传播，定义损失函数、求损失函数最小值

loss = tf.reduce_mean(tf.square(y-y_))
train_step = tf.train.GradientDescentOptimizer(0.001).minimize(loss)

# 生成Session， 训练STEPS轮

with tf.Session() as s:
    init_op = tf.global_variables_initializer()
    s.run(init_op)

    print("w1: \n", s.run(w1))
    print("w2: \n", s.run(w2))
    print()

    STEPS = 3000
    for i in range(STEPS):
        start = (i * BATCH_SIZE) % 32
        end = start + BATCH_SIZE
        print(" ---- ---- ---- ", start, end)
        s.run(train_step, feed_dict={x: X[start: end], y_: Y[start: end]})
        if i % 500 == 0:
            total_loss = s.run(loss, feed_dict={x: X, y_: Y})
            print(f"After {i} step(s), loss on all data is : {total_loss}")

    print()
    print("w1: \n", s.run(w1))
    print("w2: \n", s.run(w2))