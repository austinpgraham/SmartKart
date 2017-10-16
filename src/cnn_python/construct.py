import click
import tensorflow as tf

n_classes = 5

x = tf.placeholder('float', [None, 784])
y = tf.placeholder('float')

def conv2d(x, W):
	# Move over 1px at a time during convolution
	# Pad repeated pixels
	return tf.nn.conv2d(x, W, strides=[1,1,1,1], padding='SAME')

def maxpool2d(x):
	# Moving pool window by 2px at a time
	return tf.nn.max_pool(x, ksize=[1,2,2,1], strides=[1,2,2,1], padding='SAME')

def _build_network(x):
	weights = {'W_conv1': tf.Variable(tf.random_normal([5, 5, 1, 32])),
			   'W_conv2': tf.Variable(tf.random_normal([5, 5, 32, 64])),
			   'W_fc': tf.Variable(tf.random_normal([7*7*64, 1024])),
			   'out': tf.Variable(tf.random_normal([1024, n_classes]))}
	
	biases = {'B_conv1': tf.Variable(tf.random_normal([32])),
			   'B_conv2': tf.Variable(tf.random_normal([64])),
			   'B_fc': tf.Variable(tf.random_normal([1024])),
			   'out': tf.Variable(tf.random_normal([n_classes]))}
			   
	x = tf.reshape(x, shape=[-1,28,28,1])
	conv1 = conv2d(x, weights['W_conv1']+biases['B_conv1'])
	conv1 = maxpool2d(conv1)
	
	conv2 = conv2d(conv1, weights['W_conv2']+biases['B_conv2'])
	conv2 = maxpool2d(conv2)
	
	fc = tf.reshape(conv2, [-1, 7*7*64])
	fc = tf.nn.relu(tf.matmul(fc, weights['W_fc'])+biases['B_fc'])
	
	output = tf.matmul(fc, weights['out'])+biases['out']
	
	return output

if __name__ == '__main__':
	model = _build_network(x)

	with tf.Session() as s:
		s.run(tf.global_variables_initializer())
		save_path = tf.train.Saver().save(s, "cnn.ckpt")
		print("Model saved.")