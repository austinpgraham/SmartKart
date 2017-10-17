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
	
	public static BufferedImage capture()
	{
		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e) {
			System.out.println("ERROR. Could not get state");
			return null;
		}
		BufferedImage screen = r.createScreenCapture(new Rectangle(SShot.getDimensions()));
		return screen;
	}
}
