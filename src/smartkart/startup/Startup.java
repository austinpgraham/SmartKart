/* Authors:
 * Austin Graham
 * Lauren Wells
 */
package smartkart.startup;

import smartkart.learning.ConvolutionalNetwork;
import smartkart.learning.ImageInput;
import smartkart.learning.LearningResult;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import smartkart.action.Action;
import smartkart.action.KeyPress;
import smartkart.action.SShot;
import smartkart.action.GasPedal;


/*
 * Runs a learning agent to play Mario Kart on 
 * a full screen N64 emulator. Run the emulator first,
 * then run this application. Will navigate to the 
 * race for you.
 */
public class Startup
{
	/*Declare various configuration parameters*/
	final static int EPISODES = 10000;
	final static double LEARNING_RATE = 0.01;
	final static double GAMMA = 0.5;
	final static float EPSILON = 0.2f;
	final static int ACTIONS = 5;
	final static int STATES = 3;
	final static int EXIT = -1;
	
	/*Keep the total reward*/
	static int totalReward = 0;
	
	/*Perform actions*/
	static Action actionTaker = new Action();
	
	/*Keep history of rewards*/
	static ArrayList<Integer> rewards = new ArrayList<Integer>();
	
	/*Create Q-Table for relevant approaches*/
	static float[][] qTable = new float[3][5];
	
	/*Signal to press the gas pedal*/
	public static GasPedalComponent hitGas()
	{
		GasPedal pedal = new GasPedal();
		Thread shiftThread = new Thread(pedal);
		shiftThread.start();
		return new GasPedalComponent(shiftThread, pedal);
	}
	
	/*Get state by taking screenshot and extracting data*/
	public static ImageInput getCurrentState() {
		BufferedImage image = SShot.capture();
		return new ImageInput(image);
	}
	
	/*Epsilon-greedy action selection*/
	public static int chooseAction(int predictedValue) {
		return (Math.random() < EPSILON) ? (int)(Math.random()*5) : predictedValue;
	}
	
	/*Perform the designated action*/
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
		case -1:
			actionTaker.exit();
			break;
		}
	}
	
	/* Grab the reward based on state. Uses the simple
	 * state representation
	 */
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
	
	/*Deep-Q-Learning*/
	public static ImageInput DQNLearning(ImageInput state, ConvolutionalNetwork agent) {
		// If no state, get next state and continue
		if(state == null) {
			actionTaker.straight();
			return getCurrentState();
		}
		// Feed the current state
		LearningResult results = agent.feedNetwork(state);
		// Pick the action
		int nextAction = chooseAction(results.getPredictedValue());
		// Do the action
		doAction(nextAction);
		// Get the next state
		ImageInput newState = getCurrentState();
		// Re-feed to get new state information
		LearningResult secondResult = agent.feedNetwork(newState);
		int reward = getReward(newState);
		// Calculate expected Q versus predicted
		float[] target = results.getQValues();
		float[] second = secondResult.getQValues();
		target[results.getPredictedValue()] = (float) (reward + GAMMA*second[secondResult.getPredictedValue()]);
		// Propage network 
		agent.propogateNetwork(target);
		// Track reward
		totalReward += reward;
		rewards.add(totalReward);
		return newState;
	}
	
	// Get the max Q-value in the q-table
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
	/*Q-Table learning*/
	public static ImageInput QTableLearning(ImageInput state) {
		// Get predicted values
		int maxIdx = maxQ(qTable[state.getGrayIndex()]);
		// Select action
		int nextAction = chooseAction(maxIdx);
		// Do the action
		doAction(nextAction);
		// Get the next state
		ImageInput nextState = getCurrentState();
		// Get the reward
		int reward = getReward(nextState);
		// Get best Q and perform learning
		float maxQNew = qTable[nextState.getGrayIndex()][maxQ(qTable[state.getGrayIndex()])];
		qTable[state.getGrayIndex()][nextAction] = (float) (qTable[state.getGrayIndex()][nextAction] + LEARNING_RATE*(reward + GAMMA*maxQNew - qTable[state.getGrayIndex()][nextAction]));
		// Track the reward
		totalReward += reward;
		rewards.add(totalReward);
		return nextState;
	}
	
	/*Write the rewards to a file*/
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
		// Create Q-Table
		for(int i = 0; i < STATES; i++) {
			for(int j = 0; j < ACTIONS; j++) {
				qTable[i][j] = (float)Math.random();
			}
		}
		
		// Get a fake measurement image
		BufferedImage initImage = SShot.capture();
		ImageInput init = new ImageInput(initImage);
		// create command to signal python to build the network
		String[] cmd = {
				"/bin/bash",
				"-c",
				"python3 src/cnn_python/construct.py " + init.getHeight() + " " + init.getWidth()
		};
		// If it hasn't been build, build it
		if(!(new File("cnn")).exists())
		{
			try {
				System.out.println("Building convolutional network...");
				Runtime.getRuntime().exec(cmd);
				Thread.sleep(5000);
				System.out.println("Network built.");
			} catch (IOException e1) {
				System.out.println("Network could no"
						+ ""
						+ "t be built. Stack trace: ");
				e1.printStackTrace();
			}
		}
		
		// Navigate the race
		try 
		{
			System.out.println("Starting race...");
			RaceStart.navigateToRace();
		} 
		catch (AWTException e) 
		{
			System.out.println("Could not navigate to race, exiting...");
		}
		
		// Wait, when race starts, hit gas
		System.out.println("Race starting...");
		Thread.sleep(10000);
		GasPedalComponent gasComponent = hitGas();
		// Build network
		ConvolutionalNetwork agent = new ConvolutionalNetwork();
		// Get first state
		ImageInput state = getCurrentState();
		while(state == null) {
			state = getCurrentState();
		}
		// Learn for a number of episodes
		for(int i = 0; i < EPISODES; i++) {
			ImageInput newState = DQNLearning(state, agent);
			state = newState;
		}
		// When done, write rewards to a file
		try {
			writeRewardFile("DQNSimple.txt", rewards);
			//writeRewardFile("Table.txt", rewards);
		} catch (IOException e) {
			System.out.println("Could not write reward file.");
		}
		// Stop thread and exit emulator
		System.out.println("Run complete. Stopping gas...");
		gasComponent.stopGas();
		System.out.println("Exiting...");
		doAction(EXIT);
	}
}