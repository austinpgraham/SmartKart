package smartkart.startup;

import smartkart.action.GasPedal;

public class GasPedalComponent {
	
	private Thread shiftThread;
	private GasPedal pedal;
	
	public GasPedalComponent(Thread t, GasPedal g) {
		this.shiftThread = t;
		this.pedal = g;
	}
	
	public Thread getThread() {
		return this.shiftThread;
	}
	
	public GasPedal getPedal() {
		return this.pedal;
	}
	
	public void stopGas() {
		this.pedal.stop();
		try {
			this.shiftThread.join();
		} catch (InterruptedException e) {
			return;
		}
	}
}
