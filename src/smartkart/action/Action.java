/*Author: Austin Graham*/
package smartkart.action;

import java.awt.AWTException;
import java.awt.event.KeyEvent;

/*
 * Perform each of the actions as specified
 * by pressing the proper key for a number of 
 * seconds.
 */
public class Action 
{
	/*Class allowing us to simulate key presses*/
	private KeyPress kp;
	
	/*Constructor*/
	public Action()
	{
		// Get access to the keyboard
		try {
			kp = new KeyPress();
		} catch (AWTException e) {
			System.out.println("Could not grasp keys");
		}
	}
	
	/*Hard left turn*/
	public void hardLeft()
	{
		try {
			this.kp.longPressKey(KeyEvent.VK_LEFT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	/*Soft left turn*/
	public void left()
	{
		try {
			this.kp.pressKey(KeyEvent.VK_LEFT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	/*Continue straight*/
	public void straight()
	{
		try {
			this.kp.pressKey(KeyEvent.VK_UP);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	/*Soft right turn*/
	public void right()
	{
		try {
			this.kp.pressKey(KeyEvent.VK_RIGHT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	/*Hard right turn*/
	public void hardRight()
	{
		try {
			this.kp.longPressKey(KeyEvent.VK_RIGHT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	/*Do nothing*/
	public void wait(int seconds)
	{
		try {
			this.kp.wait(seconds);
		} catch (InterruptedException e) {
			return;
		}
	}
	
	/*Exit the emulator*/
	public void exit() {
		try {
			this.kp.pressKey(KeyEvent.VK_ESCAPE);
		} catch (InterruptedException e) {
			return;
		}
	}
}
