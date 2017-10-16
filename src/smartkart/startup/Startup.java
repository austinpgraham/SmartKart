package smartkart.startup;

import smartkart.learning.ConvolutionalNetwork;
import smartkart.learning.ImageInput;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.IOException;

import smartkart.action.Action;
import smartkart.action.KeyPress;
import smartkart.action.SShot;


/*
 * Runs a learning agent to play Mario Kart on 
 * a full screen N64 emulator. Run the emulator first,
 * then run this application. Will navigate to the 
 * race for you.
 */
public class Startup
{
	final static String INIT_PIC = "init.jpeg";
	
	public static void main(String[] args) throws AWTException, InterruptedException
	{
		try {
			SShot.capture(INIT_PIC);
		} catch (IOException e) {
			System.out.println("Could not capture screen environment. Exiting...");
			System.exit(-1);
		}
		
		ImageInput init = new ImageInput(INIT_PIC);
		String[] cmd = {
				"/bin/bash",
				"-c",
				"python3 src/cnn_python/construct.py " + init.getHeight() + " " + init.getWidth()
		};
		
		try {
			System.out.println("Building convolutional network...");
			Runtime.getRuntime().exec(cmd);
			System.out.println("Network built.");
		} catch (IOException e1) {
			System.out.println("Network could no"
					+ ""
					+ "t be built. Stack trace: ");
			e1.printStackTrace();
		}
		
		//Construct graph
		try {
			SShot.capture("test.jpeg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConvolutionalNetwork agent = new ConvolutionalNetwork();
		agent.feedNetwork("test.jpeg");
//		KeyPress kp = new KeyPress();
//		// Start the race
//		try 
//		{
//			System.out.println("Starting race...");
//			RaceStart.navigateToRace();
//		} 
//		catch (AWTException e) 
//		{
//			System.out.println("Could not navigate to race, exiting...");
//		}
//		
//		Thread shiftThread = new Thread(new Runnable() {
//		    public void run() {
//		    	while(!Thread.currentThread().isInterrupted()){
//		    		kp.longPressKey(KeyEvent.VK_SHIFT);
//		    	}
//		    }
//		});
//		System.out.println("Race starting...");
//		shiftThread.start();
//		//race happens here
//		shiftThread.interrupt();
//		shiftThread.join();
	}
}