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
	
	private Session session = null;
	
	private void build_graph() {
		SavedModelBundle load = SavedModelBundle.load(System.getProperty("user.dir")+"/cnn", "train");
		this.session = load.session();
	}
	
	private LearningResult getResultData(Tensor result) {
		int max = 0;
		float maxVal = -1;
		float[][] tensorVals = new float[1][5];
		float[][] matrix = result.copyTo(tensorVals);
		for(int i = 0; i < matrix[0].length; i++) {
			if(matrix[0][i] > maxVal) {
				maxVal = matrix[0][i];
				max = i;
			}
		}
		LearningResult resultData = new LearningResult(max, matrix[0]);
		return resultData;
	}
	
	public ConvolutionalNetwork() {
		this.build_graph();
	}
	
	public LearningResult feedNetwork(BufferedImage input) {
		float[] inputData = (new ImageInput(input)).getData();
		Tensor inputTensor = Tensor.create(inputData);
		Tensor result = this.session.runner().feed("input", inputTensor).fetch("output").run().get(0);
		return this.getResultData(result);
	}
	
	public void propogateNetwork(float[] actual) {
		Tensor inputTensor = Tensor.create(actual);
		this.session.runner().feed("actual", inputTensor);
	}
}