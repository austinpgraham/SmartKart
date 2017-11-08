/*Author: Lauren Wells*/
package smartkart.learning;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

/*
 * Take a BufferedImage captured from the screen and 
 * extract the needed data from it
 */
public class ImageInput {
	
	/*Data to be kept from the input image*/
	private float[] data;
	private int width;
	private int height;
	private int greyIndex;
	private String state;
	
	public ImageInput(BufferedImage input) {
		// Save the target vectors of each state
		HashMap<String, int[]> states = new HashMap<String, int[]>();
		states.put("onRoad", new int[]{95, 87, 87});
		states.put("onGrassCheckeredWall", new int[]{91, 94, 40});
		states.put("onGrassBrickWall", new int[]{97, 90, 42});
		states.put("onSandWall", new int[]{174, 148, 129});
		states.put("onRoadWall", new int[]{63, 75, 66});
		// Scale the image down for easier processing
		BufferedImage scaledImage = this.reduceSize(input, 10);
		BufferedImage cropImage = this.cropImage(scaledImage, 3);
		// Split the image into needed pieces
		BufferedImage[] images = this.splitImageInThree(cropImage);
		// Get the average colors for each section
		Color[] averageColors = this.getAverageColorArray(images);
		// Get the state of the image
		this.state = this.getState(states, averageColors[1]);
		// Get dimensions of the image
		this.width = this.closestToFour(scaledImage.getWidth());
		this.height = this.closestToFour(scaledImage.getHeight());
		// Get raw data of the image
		this.data = this.toFloatArray(scaledImage);
		// Get which piece is the most gray
		this.greyIndex = this.indexClosestToGrey(averageColors);
	}
	
	/*The closest multiple of four to a number*/
	private int closestToFour(int num) {
		int diff = num % 4;
		return num - diff;
	}
	
	/*Get the float data of an image*/
	private float[] toFloatArray(BufferedImage image) {
		// Keep in 1D array
		float[] data = new float[image.getHeight() * image.getWidth()];
		for(int i = 0; i < image.getHeight(); i++) {
			for(int j = 0; j < image.getWidth(); j++) {
				data[image.getWidth() * i + j] = image.getRGB(j, i);
			}
		}
		return data;
	}
	
	/*Scale down the image*/
	public BufferedImage reduceSize(BufferedImage image, int amount)
	{
		// Scale original image
		Image rawImage = image.getScaledInstance(image.getWidth()/amount, image.getHeight()/amount, Image.SCALE_FAST);
		BufferedImage bimage = new BufferedImage(rawImage.getWidth(null), rawImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		// Create the new image
		Graphics g = bimage.createGraphics();
		g.drawImage(rawImage, 0,0, null);
		g.dispose();
		return bimage;
	}
	
	/*Crop an image*/
	public BufferedImage cropImage(BufferedImage image, int amount)
	{
		return image.getSubimage(0, image.getHeight()/amount, image.getWidth(), image.getHeight()/amount);
	}
	
	/*Split an image into thirds*/
	public BufferedImage[] splitImageInThree(BufferedImage image)
	{
		BufferedImage images[] = new BufferedImage[3];
		
		// Get three subimages and redraw each image
		for(int i=0; i<3; i++)
		{
			images[i] = image.getSubimage(i*(image.getWidth()/3), 0, image.getWidth()/3, image.getHeight());
			Graphics2D g = images[i].createGraphics();
			g.drawImage(images[i], 0, 0, images[i].getWidth(), images[i].getHeight(), null);
			g.dispose();
		}
		
		return images;
	}
	
	/*Get the average color given RGB data*/
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
	
	/*Get the average colors as Color objects*/
	public Color[] getAverageColorArray(BufferedImage[] images)
	{
		Color[] averageArray = new Color[images.length];
		
		for(int i=0; i<images.length; i++)
		{
			averageArray[i] = getAverageColor(this.toFloatArray(images[i]));
		}
		
		return averageArray;
	}
	
	/*Get which piece is closes to the target gray*/
	public int indexClosestToGrey(Color[] colors)
	{
		int minimumDistanceIndex = 0;
		int distances[] = new int[colors.length];
		for(int i=0; i<colors.length; i++)
		{
			// Get distnace from target gray
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
	
	/*Get the color distance from the target grey*/
	public int colorDistanceFromGrey(Color color) 
	{
	    return Math.abs(color.getRed() - 128) + Math.abs(color.getGreen() - 128) + Math.abs(color.getBlue() - 128);
	}
	
	/*From the image, estimate what state I am in*/
	public String getState(HashMap<String, int[]> states, Color color)
	{
		String state = "";
		int[] distances = new int[states.size()];
		int minDistance = 500;
		int i = 0;
		Iterator<Entry<String, int[]>> it = states.entrySet().iterator();
		// Check closeness of each state
	    while (it.hasNext()) {
	        Map.Entry<String, int[]> pair = (Map.Entry<String, int[]>)it.next();
	        distances[i] = Math.abs(color.getRed() - pair.getValue()[0]) + Math.abs(color.getGreen() - pair.getValue()[1]) + Math.abs(color.getBlue() - pair.getValue()[2]);
	        System.out.println("Distance " + distances[i]);
	        if(distances[i] < minDistance)
	        {
	        	minDistance = distances[i];
	        	state = pair.getKey();
	        }
	        it.remove(); // avoids a ConcurrentModificationException
	        ++i;
	    }
		return state;
	}
	
	/*Getters for other raw data if needed*/
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
	
	public String getState() {
		return this.state;
	}
}
