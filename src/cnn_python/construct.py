# Author: Austin Graham
# Take arguments from the Java program
# and construct a CNN that is adaptive
# to the current screen size
import os
import sys
import tensorflow as tf

# We have five actions
n_classes = 5

# Grab height and width arguments
height = int(sys.argv[1])
width = int(sys.argv[2])

# Total image matrix size
input_size = height * width

# Reshaping convolutions
reshape_param = (int(height/4))*(int(width/4))*64

# Define the image input and action outputs
x = tf.placeholder(tf.float32, [None, input_size], name="input")
y = tf.placeholder(tf.float32)

# Perform 2D convolution
def conv2d(x, W):
	return tf.nn.conv2d(x, W, strides=[1,1,1,1], padding='SAME')

# Perform 2D pooling
def maxpool2d(x):
	# Moving pool window by 2px at a time
	return tf.nn.max_pool(x, ksize=[1,2,2,1], strides=[1,2,2,1], padding='SAME')

# Build the network architecture
def _build_network(x):
        # Define the convolution layer weignts
	weights = {'W_conv1': tf.Variable(tf.random_normal([5, 5, 1, 32])),
			   'W_conv2': tf.Variable(tf.random_normal([5, 5, 32, 64])),
			   'W_fc': tf.Variable(tf.random_normal([reshape_param, 1024])),
			   'out': tf.Variable(tf.random_normal([1024, n_classes]))}
	# Define the convolution layer biases
	biases = {'B_conv1': tf.Variable(tf.random_normal([32])),
			   'B_conv2': tf.Variable(tf.random_normal([64])),
			   'B_fc': tf.Variable(tf.random_normal([1024])),
			   'out': tf.Variable(tf.random_normal([n_classes]))}
	# Wire together the connections   
	x = tf.reshape(x, shape=[-1,height,width,1])
	conv1 = conv2d(x, weights['W_conv1']+biases['B_conv1'])
	conv1 = maxpool2d(conv1)
	
	conv2 = conv2d(conv1, weights['W_conv2']+biases['B_conv2'])
	conv2 = maxpool2d(conv2)
	
	fc = tf.reshape(conv2, [-1, reshape_param])
	fc = tf.nn.relu(tf.matmul(fc, weights['W_fc'])+biases['B_fc'])

	# Make the output accessible in Java
	output = tf.matmul(fc, weights['out'])+biases['out']
	output = tf.identity(output, name="output")
	
	return output

# Construct gradience descent accessible in Java
def buildoptimizer(output):
	actual = tf.placeholder(shape=[1, 5], dtype=tf.float32, name="actual")
	loss = tf.reduce_sum(tf.square(actual - output))
	trainer = tf.train.GradientDescentOptimizer(learning_rate=0.01)
	update = trainer.minimize(loss)
	return update

if __name__ == '__main__':
        # Build the network
	model = _build_network(x)
	optimzier = buildoptimizer(model)
	builder = tf.saved_model.builder.SavedModelBuilder(os.getcwd()+"/cnn")
	# Run the build and write to a file
	# readable from Java
	with tf.Session() as s:
		s.run(tf.global_variables_initializer())
		builder.add_meta_graph_and_variables(s, [tf.saved_model.tag_constants.TRAINING])
		builder.save(True)
		
