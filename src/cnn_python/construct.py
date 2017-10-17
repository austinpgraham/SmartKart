import os
import sys
import tensorflow as tf

n_classes = 5

height = int(sys.argv[1])
width = int(sys.argv[2])

input_size = height * width

reshape_param = (int(height/4))*(int(width/4))*64

x = tf.placeholder(tf.float32, [None, input_size], name="input")
y = tf.placeholder(tf.float32)

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
			   'W_fc': tf.Variable(tf.random_normal([reshape_param, 1024])),
			   'out': tf.Variable(tf.random_normal([1024, n_classes]))}
	
	biases = {'B_conv1': tf.Variable(tf.random_normal([32])),
			   'B_conv2': tf.Variable(tf.random_normal([64])),
			   'B_fc': tf.Variable(tf.random_normal([1024])),
			   'out': tf.Variable(tf.random_normal([n_classes]))}
			   
	x = tf.reshape(x, shape=[-1,height,width,1])
	conv1 = conv2d(x, weights['W_conv1']+biases['B_conv1'])
	conv1 = maxpool2d(conv1)
	
	conv2 = conv2d(conv1, weights['W_conv2']+biases['B_conv2'])
	conv2 = maxpool2d(conv2)
	
	fc = tf.reshape(conv2, [-1, reshape_param])
	fc = tf.nn.relu(tf.matmul(fc, weights['W_fc'])+biases['B_fc'])
	
	output = tf.matmul(fc, weights['out'])+biases['out']
	output = tf.identity(output, name="output")
	
	return output

def buildoptimizer(output):
	actual = tf.placeholder(shape=[1, 5], dtype=tf.float32, name="actual")
	loss = tf.reduce_sum(tf.square(actual - output))
	trainer = tf.train.GradientDescentOptimizer(learning_rate=0.01)
	update = trainer.minimize(loss)
	return update

if __name__ == '__main__':
	model = _build_network(x)
	optimzier = buildoptimizer(model)
	builder = tf.saved_model.builder.SavedModelBuilder(os.getcwd()+"/cnn")
	with tf.Session() as s:
		s.run(tf.global_variables_initializer())
		builder.add_meta_graph_and_variables(s, [tf.saved_model.tag_constants.TRAINING])
		builder.save(True)
		
