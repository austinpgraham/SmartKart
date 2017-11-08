/*Author: Austin Graham*/
package smartkart.action;

import java.awt.AWTException;
import java.awt.Robot;

/*
 * Abstraction of the Java AWT key pressing
 * technology. Just so we don't have to do 
 * three things for every key press.
 */
public class KeyPress
{
	/*Class to simulate key presses*/
	private Robot r;
	
	/*Grab access*/
	public KeyPress() throws AWTException
	{
		r = new Robot();
	}
	
	/*Press a key for half of a second*/
	public void pressKey(int keyCode) throws InterruptedException
	{
		this.r.keyPress(keyCode);
		this.wait(0.5f);
		this.r.keyRelease(keyCode);
	}
	
	/*Press a key for a whole second*/
	public void longPressKey(int keyCode) throws InterruptedException
	{
		this.r.keyPress(keyCode);
		this.wait(1f);
		this.r.keyRelease(keyCode);
	}
	
	/*Hold a key down*/
	public void pressAndHold(int keyCode)
	{
		this.r.keyPress(keyCode);
	}
	
	/*Release a held key*/
	public void release(int keyCode)
	{
		this.r.keyRelease(keyCode);
	}
	
	/*Delay thread for a number of seconds*/
	public void wait(float seconds) throws InterruptedException
	{
		this.r.delay((int)(seconds * 1000));
	}
}