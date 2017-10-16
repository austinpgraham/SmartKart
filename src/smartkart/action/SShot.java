package smartkart.action;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * Simple class to take screenshots
 * to determine the current state
 */
public class SShot 
{
	public static Dimension getDimensions()
	{
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public static void capture(String file_name) throws IOException, AWTException
	{
		Robot r = new Robot();
		BufferedImage screen = r.createScreenCapture(new Rectangle(SShot.getDimensions()));
		ImageIO.write(screen, "JPEG", new File(file_name));
		System.out.println("Screen written to " + file_name);
	}
}
