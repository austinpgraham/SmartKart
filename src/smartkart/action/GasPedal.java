/*Author: Austin Graham*/
package smartkart.action;

import java.awt.AWTException;

import com.sun.glass.events.KeyEvent;

/*
 * Define a thread that once run,
 * will constantly press the shift
 * key so Mario will continually
 * be "hitting the gas" once
 * the race has started
 */
public class GasPedal implements Runnable {

	/*Allow stopping of thread execution*/
	private volatile boolean running = true;
	
	/*Signal the thread to stop*/
	public void stop() {
		running = false;
	}
	
	/*Run the thread*/
	@Override
	public void run() {
		// Grab the keyboard
		KeyPress kp = null;
		try {
			kp = new KeyPress();
		} catch (AWTException e) {
			System.out.println("Could not start gas thread. Closing thread.");
			running = false;
		}
		// While we havent been signaled to stop,
		// press the shift key
		while(running) {
			try {
				kp.pressKey(KeyEvent.VK_SHIFT);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}
}
