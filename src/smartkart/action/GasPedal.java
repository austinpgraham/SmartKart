package smartkart.action;

import java.awt.AWTException;

import com.sun.glass.events.KeyEvent;

public class GasPedal implements Runnable {

	private volatile boolean running = true;
	
	public void stop() {
		running = false;
	}
	
	@Override
	public void run() {
		KeyPress kp = null;
		try {
			kp = new KeyPress();
		} catch (AWTException e) {
			System.out.println("Could not start gas thread. Closing thread.");
			running = false;
		}
		while(running) {
			try {
				kp.pressKey(KeyEvent.VK_SHIFT);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}
}
