package smartkart.learning;

import smartkart.action.SShot;

import smartkart.learning.ImageInput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	
	public ConvolutionalNetwork() {
		this.build_graph();
	}
	
	public void feedNetwork(String path) {
		float[] inputData = (new ImageInput(path)).getData();
		Tensor inputTensor = Tensor.create(inputData);
		System.out.println(this.session.runner().feed("input", inputTensor).fetch("output").run().get(0));
	}
}