/*Author: Austin Graham*/
package smartkart.startup;

import smartkart.action.GasPedal;

/*
 * Manage the GasPedal Thread
 */
public class GasPedalComponent {
	
	private Thread shiftThread;
	private GasPedal pedal;
	
	public GasPedalComponent(Thread t, GasPedal g) {
		this.shiftThread = t;
		this.pedal = g;
	}
	
	/*Generic getters*/
	public Thread getThread() {
		return this.shiftThread;
	}
	
	public GasPedal getPedal() {
		return this.pedal;
	}
	
	/*Signal to stop pressing the gas pedal*/
	public void stopGas() {
		this.pedal.stop();
		try {
			this.shiftThread.join();
		} catch (InterruptedException e) {
			return;
		}
	}
}
