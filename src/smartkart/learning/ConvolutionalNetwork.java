/*Author: Austin Graham*/
package smartkart.learning;

import smartkart.learning.ImageInput;
import smartkart.learning.LearningResult;

import java.awt.image.BufferedImage;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

/**
 * Builds a CNN using TensorFlow with various
 * configurations.
 * @author Austin Graham
 *
 */
public class ConvolutionalNetwork {
	
	/*TensorFlow run session*/
	private Session session = null;
	
	/*Read the network data files and load the built network*/
	private void build_graph() {
		SavedModelBundle load = SavedModelBundle.load(System.getProperty("user.dir")+"/cnn", "train");
		this.session = load.session();
	}
	
	/* 
	 * Given the result tensor of a run, extract the q values
	 * and the suggested action.
	 */
	private LearningResult getResultData(Tensor result) {
		int max = 0;
		float maxVal = -1;
		float[][] tensorVals = new float[1][5];
		// Copy the tensor results into a matrix
		float[][] matrix = result.copyTo(tensorVals);
		for(int i = 0; i < matrix[0].length; i++) {
			// Get suggested action
			if(matrix[0][i] > maxVal) {
				maxVal = matrix[0][i];
				max = i;
			}
		}
		// Save to an object 
		LearningResult resultData = new LearningResult(max, matrix[0]);
		return resultData;
	}
	
	/*Build network on construction*/
	public ConvolutionalNetwork() {
		this.build_graph();
	}
	
	/*Given an image, feed through the network and get results*/
	public LearningResult feedNetwork(ImageInput input) {
		float[] inputData = input.getData();
		// Convert to input tensor
		Tensor inputTensor = Tensor.create(inputData);
		// Feed the network
		Tensor result = this.session.runner().feed("input", inputTensor).fetch("output").run().get(0);
		return this.getResultData(result);
	}
	
	/*Given expected values, run the optimizer*/
	public void propogateNetwork(float[] actual) {
		Tensor inputTensor = Tensor.create(actual);
		this.session.runner().feed("actual", inputTensor);
	}
}