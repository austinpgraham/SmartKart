package smartkart.learning;

public class LearningResult 
{
	private int predicted = 0;
	private float[] values = null;
	
	public LearningResult(int predicted, float[] QValues)
	{
		this.predicted = predicted;
		this.values = QValues;
	}
	
	public int getPredictedValue()
	{
		return this.predicted;
	}
	
	public float[] getQValues()
	{
		return this.values;
	}
}
