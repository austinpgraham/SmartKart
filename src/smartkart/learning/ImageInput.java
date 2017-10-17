package smartkart.learning;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageInput {
	
	private float[] data;
	private int width;
	private int height;
	
	public ImageInput(BufferedImage input) {
		BufferedImage scaledImage = this.reduceSize(input, 10);
//		try {
//			ImageIO.write(scaledImage, "png", new File("test.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.width = this.closestToFour(scaledImage.getWidth());
		this.height = this.closestToFour(scaledImage.getHeight());
		this.data = this.toFloatArray(scaledImage);
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
	
	public BufferedImage reduceSize(BufferedImage image, int amount)
	{
		Image rawImage = image.getScaledInstance(image.getWidth()/amount, image.getHeight()/amount, Image.SCALE_FAST);
		BufferedImage bimage = new BufferedImage(rawImage.getWidth(null), rawImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimage.createGraphics();
		g.drawImage(rawImage, 0,0, null);
		g.dispose();
		return bimage;
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
