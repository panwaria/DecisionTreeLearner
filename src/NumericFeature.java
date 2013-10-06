

public class NumericFeature extends Feature
{
	private Double mThreshold;
	
	public NumericFeature(NumericFeature f, Double threshold)
	{
		super(f.getName(), f.getIndex(), f.getType());
		setThreshold(threshold);
	}
	
	public NumericFeature(String name, int index)
	{
		super(name, index, TYPE_NUMERIC);
		
	}

	public Double getThreshold()
	{
		return mThreshold;
	}

	public void setThreshold(Double threshold)
	{
		mThreshold = threshold;
	}
}
