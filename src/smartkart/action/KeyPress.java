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
	private Robot r;
	
	public KeyPress() throws AWTException
	{
		r = new Robot();
	}
	
	public void pressKey(int keyCode) throws InterruptedException
	{
		this.r.keyPress(keyCode);
		this.wait(0.5f);
		this.r.keyRelease(keyCode);
	}
	
	public void longPressKey(int keyCode) throws InterruptedException
	{
		this.r.keyPress(keyCode);
		this.wait(1f);
		this.r.keyRelease(keyCode);
	}
	
	public void pressAndHold(int keyCode)
	{
		this.r.keyPress(keyCode);
	}
	
	public void release(int keyCode)
	{
		this.r.keyRelease(keyCode);
	}
	
	public void wait(float seconds) throws InterruptedException
	{
		this.r.delay((int)(seconds * 1000));
	}
}