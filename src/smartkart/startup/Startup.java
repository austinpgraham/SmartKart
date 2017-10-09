package smartkart.startup;

import java.awt.AWTException;

/*
 * Runs a learning agent to play Mario Kart on 
 * a full screen N64 emulator. Run the emulator first,
 * then run this application. Will navigate to the 
 * race for you.
 */
public class Startup
{
	public static void main(String[] args)
	{
		// Start the race
		try 
		{
			RaceStart.navigateToRace();
		} 
		catch (AWTException e) 
		{
			System.out.println("Could not navigate to race, exiting...");
		}
		
		
		
		// Start the learning agent
		// TODO: Build the agent
	}
}