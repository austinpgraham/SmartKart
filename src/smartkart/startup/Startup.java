package smartkart.startup;

import smartkart.learning.ConvolutionalNetwork;
import smartkart.learning.ImageInput;
import smartkart.learning.LearningResult;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
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
	
	final static int EPISODES = 100;
	
	final static double LEARNING_RATE = 0.99;
	
	static int totalReward = 0;
	
	static Action actionTaker = new Action();
	
	public static Thread hitGas() throws AWTException
	{
		final KeyPress kp = new KeyPress();
		
		Thread shiftThread = new Thread(new Runnable() {
		    public void run() {
		    	while(!Thread.currentThread().isInterrupted()){
		    		kp.pressKey(KeyEvent.VK_SHIFT);
		    	}
		    }
		});
		shiftThread.start();
		return shiftThread;
	}
	
	public static void releaseGas(Thread gasThread) throws InterruptedException
	{
		gasThread.interrupt();
		gasThread.join();
	}
	
	
	public static BufferedImage getCurrentState() {
		BufferedImage image = SShot.capture();
		return image;
	}
	
	public static BufferedImage takeAction(BufferedImage state, ConvolutionalNetwork agent) {
		if(state == null) {
			actionTaker.straight();
			return getCurrentState();
		}
		LearningResult results = agent.feedNetwork(state);
		int reward = -1;
		switch(results.getPredictedValue()) {
			case 0:
				actionTaker.hardLeft();
				break;
			case 1:
				actionTaker.left();
				reward = 10;
				break;
			case 2:
				actionTaker.straight();
				break;
			case 3:
				actionTaker.right();
				break;
			case 4:
				actionTaker.hardRight();
				break;
		}
		BufferedImage newState = getCurrentState();
		LearningResult secondResult = agent.feedNetwork(newState);
		
		float[] target = results.getQValues();
		float[] second = secondResult.getQValues();
		target[results.getPredictedValue()] = (float) (reward + LEARNING_RATE*second[secondResult.getPredictedValue()]);
		agent.propogateNetwork(target);
		totalReward += reward;
		return newState;
	}
	
	public static void main(String[] args) throws AWTException, InterruptedException
	{
		BufferedImage initImage = SShot.capture();
		ImageInput init = new ImageInput(initImage);
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
		
		try 
		{
			System.out.println("Starting race...");
			RaceStart.navigateToRace();
		} 
		catch (AWTException e) 
		{
			System.out.println("Could not navigate to race, exiting...");
		}
		
		
		System.out.println("Race starting...");
		Thread.sleep(9000);
		Thread gasThread = hitGas();
		ConvolutionalNetwork agent = new ConvolutionalNetwork();
		BufferedImage state = getCurrentState();
		while(state == null) {
			state = getCurrentState();
		}
		for(int i = 0; i < EPISODES; i++) {
			BufferedImage newState = takeAction(state, agent);
			state = newState;
			System.out.println(totalReward);
		}
		releaseGas(gasThread);
	}
}