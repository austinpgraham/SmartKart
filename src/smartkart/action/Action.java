package smartkart.action;

import java.awt.AWTException;
import java.awt.event.KeyEvent;


public class Action 
{
	private KeyPress kp;
	
	public Action() throws AWTException
	{
		kp = new KeyPress();
	}
	
	public void hardLeft()
	{
		this.kp.longPressKey(KeyEvent.VK_LEFT);
	}
	
	public void left()
	{
		this.kp.pressKey(KeyEvent.VK_LEFT);
	}
	
	public void straight()
	{
		this.kp.pressKey(KeyEvent.VK_UP);
	}
	
	public void right()
	{
		this.kp.pressKey(KeyEvent.VK_RIGHT);
	}
	
	public void hardRight()
	{
		this.kp.longPressKey(KeyEvent.VK_RIGHT);
	}
}
