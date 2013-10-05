import java.util.ArrayList;

public class DiscreteFeature extends Feature
{
	private int mNumValues;
	private ArrayList<String> mValues = null;
	
	public DiscreteFeature(String name, int index, int numValues, ArrayList<String> values)
	{
		super(name, index, Feature.TYPE_DISCRETE);
		mNumValues = numValues;
		mValues = values;
	}
	
	public int getNumValues()
	{
		return mNumValues;
	}

	public void setNumValues(int numValues)
	{
		this.mNumValues = numValues;
	}

	public ArrayList<String> getValues()
	{
		return mValues;
	}

	public void setValues(ArrayList<String> values)
	{
		this.mValues = values;
	}

}
