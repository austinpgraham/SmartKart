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
	final static int EPISODES = 1000;
	final static double LEARNING_RATE = 0.01;
	final static double GAMMA = 0.6; 
	final static long REALLYFUKINBIGTEMP = (long) 10e17;
	final static float TEMP = 0.5f;
	
	static float EPSILON = 0.3f;
	
	final static int ACTIONS = 5;
	final static int STATES = 5;
	final static int EXIT = -1;
	
	static int totalReward = 0;
	
	static Action actionTaker = new Action();
	
	static ArrayList<Integer> rewards = new ArrayList<Integer>();
	static ArrayList<Integer> states = new ArrayList<Integer>();
	
	static float[][] qTable = new float[STATES][ACTIONS];
	
	public static GasPedalComponent hitGas()
	{
		GasPedal pedal = new GasPedal();
		Thread shiftThread = new Thread(pedal);
		shiftThread.start();
		return new GasPedalComponent(shiftThread, pedal);
	}
	
	public static ImageInput getCurrentState() {
		BufferedImage image = SShot.capture();
		return new ImageInput(image);
	}
	
	public static int chooseAction(int predictedValue) {
		return (Math.random() < EPSILON) ? (int)(Math.random()*5) : predictedValue;
	}
	
	public static int softmax(float[] qValues) {
		float sum = 0;
		for(float f: qValues) {
			sum += Math.exp(f/REALLYFUKINBIGTEMP);
		}
		ArrayList<Integer> select = new ArrayList<Integer>();
		for(int i = 0; i < qValues.length; i++) {
			int repeat = (int)((Math.exp(qValues[i]/REALLYFUKINBIGTEMP) / sum)*100);
			for (int j = 0; j < repeat; j++) {
				select.add(i);
			}
		}
		return select.get((int)(Math.random()*select.size()));
		
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
		case -1:
			actionTaker.exit();
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
	
	public static int getMarioReward(ImageInput state) {
		String marioState = state.getState();
		int reward = 0;
		switch(marioState) {
		case "onRoad":
			reward += 10;
			break;
		case "onGrassCheckeredWall":
		case "onGrassBrickWall":
		case "onSandWall":
			reward -= 5;
			break;
		case "onRoadWall":
			reward += 2;
			break;
		}
		return reward;
	}
		
	public static int stateToInt(String state) {
		switch(state) {
		case "onRoad":
			return 0;
		case "onGrassCheckeredWall":
			return 1;
		case "onGrassBrickWall":
			return 2;
		case "onSandWall":
			return 3;
		case "onRoadWall":
			return 4;
		}
		return -1;
	}
	
	public static ImageInput DQNLearning(ImageInput state, ConvolutionalNetwork agent) {
		if(state == null) {
			actionTaker.straight();
			return getCurrentState();
		}
		LearningResult results = agent.feedNetwork(state);
		//int nextAction = chooseAction(results.getPredictedValue());
		int nextAction = softmax(results.getQValues());
		doAction(nextAction);
		ImageInput newState = getCurrentState();
		states.add(stateToInt(newState.getState()));
		LearningResult secondResult = agent.feedNetwork(newState);
		int reward = getMarioReward(newState);
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
		int maxIdx = maxQ(qTable[stateToInt(state.getState())]);
		//int nextAction = chooseAction(maxIdx);
		int nextAction = softmax(qTable[stateToInt(state.getState())]);
		doAction(nextAction);
		ImageInput nextState = getCurrentState();
		int reward = getMarioReward(nextState);
		float maxQNew = qTable[stateToInt(nextState.getState())][maxQ(qTable[stateToInt(state.getState())])];
		qTable[stateToInt(state.getState())][nextAction] = (float) (qTable[stateToInt(state.getState())][nextAction] + LEARNING_RATE*(reward + GAMMA*maxQNew - qTable[stateToInt(state.getState())][nextAction]));
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
	
	public static void writeStateFile(String filename, ArrayList<Integer> states) throws IOException 
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		for(int i = 0; i < states.size(); i++)
		{
			int state = (states.get(i) == 0) ? 1 : 0;
			bw.write((i+1)+":"+state + "\n");
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
		GasPedalComponent gasComponent = hitGas();
		ConvolutionalNetwork agent = new ConvolutionalNetwork();
		ImageInput state = getCurrentState();
		while(state == null) {
			state = getCurrentState();
		}
		for(int i = 1; i <= EPISODES; i++) {
			ImageInput newState = DQNLearning(state, agent);
			state = newState;
			if(i % 1000 == 0) {
				EPSILON -= .02;
			}
		}
		try {
			writeRewardFile("QTablesSoftmax.txt", rewards);
			writeStateFile("QTablesSoftmaxState.txt", states);
		} catch (IOException e) {
			System.out.println("Could not write reward file.");
		}
		System.out.println("Run complete. Stopping gas...");
		gasComponent.stopGas();
		System.out.println("Exiting...");
		doAction(EXIT);
	}
}