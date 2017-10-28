package smartkart.startup;

import smartkart.learning.ConvolutionalNetwork;
import smartkart.learning.ImageInput;
import smartkart.learning.LearningResult;
import smartkart.learning.SARSAReturn;

import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import smartkart.action.Action;
import smartkart.action.KeyPress;
import smartkart.action.SShot;


/*
 * Runs a learning agent to play Mario Kart on 
 * a full screen N64 emulator. Run the emulator first,
 * then run this application. Will navigate to the 
 * race for you.
 */
public class Startup
{
	final static String INIT_PIC = "init.jpeg";
	final static int EPISODES = 1000;
	final static double LEARNING_RATE = 0.01;
	final static double GAMMA = 0.5;
	final static float EPSILON = 0.2f;
	final static int ACTIONS = 5;
	final static int STATES = 3;
	
	static int totalReward = 0;
	
	static Action actionTaker = new Action();
	
	static ArrayList<Integer> rewards = new ArrayList<Integer>();
	
	static float[][] qTable = new float[3][5];
	
	public static Thread hitGas() throws AWTException
	{
		final KeyPress kp = new KeyPress();
		
		Thread shiftThread = new Thread(new Runnable() {
		    public void run() {
		    	while(!Thread.currentThread().isInterrupted()){
		    		kp.pressKey(KeyEvent.VK_SHIFT);
		    	}
		    }
		});
		shiftThread.start();
		return shiftThread;
	}
	
	public static void releaseGas(Thread gasThread) throws InterruptedException
	{
		gasThread.interrupt();
		gasThread.join();
	}
	
	
	public static ImageInput getCurrentState() {
		BufferedImage image = SShot.capture();
		return new ImageInput(image);
	}
	
	public static int chooseAction(int predictedValue) {
		return (Math.random() < EPSILON) ? (int)(Math.random()*5) : predictedValue;
	}
	
	public static void doAction(int action) {
		switch(action) {
		case 0:
			actionTaker.hardLeft();
			break;
		case 1:
			actionTaker.left();
			break;
		case 2:
			actionTaker.straight();
			break;
		case 3:
			actionTaker.right();
			break;
		case 4:
			actionTaker.hardRight();
			break;
		}
	}
	
	public static int getReward(ImageInput state) {
		int grayIndex = state.getGrayIndex();
		int reward = 0;
		switch(grayIndex) {
		case 0:
			reward = -5;
			break;
		case 1:
			reward = 5;
			break;
		case 2:
			reward = 1;
			break;
		}
		return reward;
	}
		
	public static ImageInput DQNLearning(ImageInput state, ConvolutionalNetwork agent) {
		if(state == null) {
			actionTaker.straight();
			return getCurrentState();
		}
		LearningResult results = agent.feedNetwork(state);
		int nextAction = chooseAction(results.getPredictedValue());
		doAction(nextAction);
		ImageInput newState = getCurrentState();
		LearningResult secondResult = agent.feedNetwork(newState);
		int reward = getReward(newState);
		float[] target = results.getQValues();
		float[] second = secondResult.getQValues();
		target[results.getPredictedValue()] = (float) (reward + GAMMA*second[secondResult.getPredictedValue()]);
		agent.propogateNetwork(target);
		totalReward += reward;
		rewards.add(totalReward);
		return newState;
	}
	
	public static int maxQ(float[] qVals) {
		int maxIdx = 0;
		float maxVal = -1;
		for (int i = 0; i < qVals.length; i++) {
			if(qVals[i] > maxVal) {
				maxIdx = i;
				maxVal = qVals[i];
			}
		}
		return maxIdx;
	}
	
	public static ImageInput QTableLearning(ImageInput state) {
		int maxIdx = maxQ(qTable[state.getGrayIndex()]);
		int nextAction = chooseAction(maxIdx);
		doAction(nextAction);
		ImageInput nextState = getCurrentState();
		int reward = getReward(nextState);
		float maxQNew = qTable[nextState.getGrayIndex()][maxQ(qTable[state.getGrayIndex()])];
		qTable[state.getGrayIndex()][nextAction] = (float) (qTable[state.getGrayIndex()][nextAction] + LEARNING_RATE*(reward + GAMMA*maxQNew - qTable[state.getGrayIndex()][nextAction]));
		totalReward += reward;
		rewards.add(totalReward);
		return nextState;
	}
	
	public static void writeRewardFile(String filename, ArrayList<Integer> rewards) throws IOException 
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for(int i = 0; i < rewards.size(); i++) {
			bw.write((i+1)+":"+rewards.get(i) + "\n");
		}
		bw.close();
	}
	
	public static void main(String[] args) throws AWTException, InterruptedException
	{
		for(int i = 0; i < STATES; i++) {
			for(int j = 0; j < ACTIONS; j++) {
				qTable[i][j] = (float)Math.random();
			}
		}
		
		BufferedImage initImage = SShot.capture();
		ImageInput init = new ImageInput(initImage);
		String[] cmd = {
				"/bin/bash",
				"-c",
				"python3 src/cnn_python/construct.py " + init.getHeight() + " " + init.getWidth()
		};
		
		try {
			System.out.println("Building convolutional network...");
			Runtime.getRuntime().exec(cmd);
			System.out.println("Network built.");
		} catch (IOException e1) {
			System.out.println("Network could no"
					+ ""
					+ "t be built. Stack trace: ");
			e1.printStackTrace();
		}
		
		try 
		{
			System.out.println("Starting race...");
			RaceStart.navigateToRace();
		} 
		catch (AWTException e) 
		{
			System.out.println("Could not navigate to race, exiting...");
		}
		
		
		System.out.println("Race starting...");
		Thread.sleep(10000);
		Thread gasThread = hitGas();
		ConvolutionalNetwork agent = new ConvolutionalNetwork();
		ImageInput state = getCurrentState();
		while(state == null) {
			state = getCurrentState();
		}
		for(int i = 0; i < EPISODES; i++) {
			ImageInput newState = DQNLearning(state, agent);
			state = newState;
		}
		try {
			writeRewardFile("DQN.txt", rewards);
			//writeRewardFile("Table.txt", rewards);
		} catch (IOException e) {
			System.out.println("Could not write reward file.");
		}
		releaseGas(gasThread);
	}
}