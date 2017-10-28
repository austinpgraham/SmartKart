package smartkart.action;

import java.awt.AWTException;
import java.awt.event.KeyEvent;


public class Action 
{
	private KeyPress kp;
	
	public Action()
	{
		try {
			kp = new KeyPress();
		} catch (AWTException e) {
			System.out.println("Could not grasp keys");
		}
	}
	
	public void hardLeft()
	{
		try {
			this.kp.longPressKey(KeyEvent.VK_LEFT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	public void left()
	{
		try {
			this.kp.pressKey(KeyEvent.VK_LEFT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	public void straight()
	{
		try {
			this.kp.pressKey(KeyEvent.VK_UP);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	public void right()
	{
		try {
			this.kp.pressKey(KeyEvent.VK_RIGHT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	public void hardRight()
	{
		try {
			this.kp.longPressKey(KeyEvent.VK_RIGHT);
		} catch(InterruptedException ie) {
			return;
		}
	}
	
	public void wait(int seconds)
	{
		try {
			this.kp.wait(seconds);
		} catch (InterruptedException e) {
			return;
		}
	}
	
	public void exit() {
		try {
			this.kp.pressKey(KeyEvent.VK_ESCAPE);
		} catch (InterruptedException e) {
			return;
		}
	}
}
