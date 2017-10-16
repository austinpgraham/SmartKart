package smartkart.startup;

import java.awt.AWTException;

import java.awt.event.KeyEvent;

import smartkart.action.KeyPress;

class RaceStart
{
	
	public static void navigateToRace() throws AWTException
	{
		KeyPress kp = new KeyPress();
		// Wait for focus on the emulator
		kp.wait(5f);
		
		// Go through start screen
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(7f);
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(7f);
		
		// Select Grand Prix / 50 CC
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(1f);
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(1f);
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(1f);
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(3f);
		
		// Select Mario
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(2f);
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(2f);
		
		// Select Mushroom Cup
		kp.pressKey(KeyEvent.VK_ENTER);
		kp.wait(1f);
		kp.pressKey(KeyEvent.VK_ENTER);
	}
}