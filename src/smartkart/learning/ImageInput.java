package smartkart.learning;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageInput {
	
	private float[] data;
	private int width;
	private int height;
	private int greyIndex;
	
	public ImageInput(BufferedImage input) {
		BufferedImage scaledImage = this.reduceSize(input, 10);
		BufferedImage cropImage = this.cropImage(scaledImage, 3);
		BufferedImage[] images = this.splitImageInThree(cropImage);
		Color[] averageColors = getAverageColorArray(images);
		this.width = this.closestToFour(scaledImage.getWidth());
		this.height = this.closestToFour(scaledImage.getHeight());
		this.data = this.toFloatArray(scaledImage);
		this.greyIndex = indexClosestToGrey(averageColors);
	}
	
	private int closestToFour(int num) {
		int diff = num % 4;
		return num - diff;
	}
	
	private float[] toFloatArray(BufferedImage image) {
		float[] data = new float[image.getHeight() * image.getWidth()];
		for(int i = 0; i < image.getHeight(); i++) {
			for(int j = 0; j < image.getWidth(); j++) {
				data[image.getWidth() * i + j] = image.getRGB(j, i);
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
	
	public BufferedImage cropImage(BufferedImage image, int amount)
	{
		return image.getSubimage(0, image.getHeight()/amount, image.getWidth(), image.getHeight()/amount);
	}
	
	public BufferedImage[] splitImageInThree(BufferedImage image)
	{
		BufferedImage images[] = new BufferedImage[3];
		
		for(int i=0; i<3; i++)
		{
			images[i] = image.getSubimage(i*(image.getWidth()/3), 0, image.getWidth()/3, image.getHeight());
			Graphics2D g = images[i].createGraphics();
			g.drawImage(images[i], 0, 0, images[i].getWidth(), images[i].getHeight(), null);
			g.dispose();
		}
		
		return images;
	}
	
	public Color getAverageColor(float[] imageData)
	{
		int redBucket = 0;
		int greenBucket = 0;
		int blueBucket = 0;
		Color pixelColor;
		
		for(int i=0; i<imageData.length; i++)
		{
			pixelColor = new Color(Math.round(imageData[i]));
			redBucket += pixelColor.getRed();
			greenBucket += pixelColor.getGreen();
			blueBucket += pixelColor.getBlue();
		}
		
		return new Color(redBucket/imageData.length, greenBucket/imageData.length, blueBucket/imageData.length);
	}
	
	public Color[] getAverageColorArray(BufferedImage[] images)
	{
		Color[] averageArray = new Color[images.length];
		
		for(int i=0; i<images.length; i++)
		{
			averageArray[i] = getAverageColor(this.toFloatArray(images[i]));
		}
		
		return averageArray;
	}
	
	public int indexClosestToGrey(Color[] colors)
	{
		int minimumDistanceIndex = 0;
		int distances[] = new int[colors.length];
		for(int i=0; i<colors.length; i++)
		{
			distances[i] = colorDistanceFromGrey(colors[i]);
			if(i != 0)
			{
				if(distances[i] < distances[i-1])
				{
					minimumDistanceIndex = i;
				}
			}
		}
		return minimumDistanceIndex;
	}
	
	public int colorDistanceFromGrey(Color color) 
	{
	    return Math.abs(color.getRed() - 128) + Math.abs(color.getGreen() - 128) + Math.abs(color.getBlue() - 128);
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
	
	public int getGrayIndex() {
		return this.greyIndex;
	}
}
