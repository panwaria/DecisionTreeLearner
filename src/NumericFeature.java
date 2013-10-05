

public class NumericFeature extends Feature
{
	public int mThreshold;
	
	public NumericFeature(NumericFeature f, int threshold)
	{
		super(f.getName(), f.getIndex(), f.getType());
		mThreshold = threshold;
	}
	
	public NumericFeature(String name, int index)
	{
		super(name, index, TYPE_NUMERIC);
		
	}
}
