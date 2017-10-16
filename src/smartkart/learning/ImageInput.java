package smartkart.learning;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageInput {
	
	private float[] data;
	private int width;
	private int height;
	
	public ImageInput(String path) {
		BufferedImage picture = null;
		try {
			picture = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("ERROR. Could not read image at " + path);
		}
		this.width = this.closestToFour(picture.getWidth());
		this.height = this.closestToFour(picture.getHeight());
		System.out.println(this.width + " "+ this.height);
		this.data = this.toFloatArray(picture);
	}
	
	private int closestToFour(int num) {
		int diff = num % 4;
		return num - diff;
	}
	
	private float[] toFloatArray(BufferedImage image) {
		float[] data = new float[this.height * this.width];
		for(int i = 0; i < this.height; i++) {
			for(int j = 0; j < this.width; j++) {
				data[this.width * i + j] = image.getRGB(j, i);
			}
		}
		return data;
	}
	
	public float[] getData() {
		return this.data;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
}
