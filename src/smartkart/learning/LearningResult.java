/*Author: Austin Graham*/
package smartkart.learning;

/*
 * Save the predicted action and the
 * array of Q values into an object
 * so it can all be returned at once
 */
public class LearningResult 
{
	/*Values to save*/
	private int predicted = 0;
	private float[] values = null;
	
	public LearningResult(int predicted, float[] QValues)
	{
		this.predicted = predicted;
		this.values = QValues;
	}
	
	/*Getters for relevant data*/
	public int getPredictedValue()
	{
		return this.predicted;
	}
	
	public float[] getQValues()
	{
		return this.values;
	}
}
