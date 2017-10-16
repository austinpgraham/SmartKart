package smartkart.startup;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

import smartkart.action.Action;
import smartkart.action.KeyPress;


/*
 * Runs a learning agent to play Mario Kart on 
 * a full screen N64 emulator. Run the emulator first,
 * then run this application. Will navigate to the 
 * race for you.
 */
public class Startup
{
	public static void main(String[] args) throws AWTException, InterruptedException
	{
		KeyPress kp = new KeyPress();
		// Start the race
		try 
		{
			RaceStart.navigateToRace();
		} 
		catch (AWTException e) 
		{
			System.out.println("Could not navigate to race, exiting...");
		}
		
		Thread shiftThread = new Thread(new Runnable() {
		    public void run() {
		    	while(!Thread.currentThread().isInterrupted()){
		    		kp.longPressKey(KeyEvent.VK_SHIFT);
		    	}
		    }
		});
		shiftThread.start();
		//race happens here
		shiftThread.interrupt();
		shiftThread.join();
		
		// Start the learning agent
		// TODO: Build the agent
	}
}