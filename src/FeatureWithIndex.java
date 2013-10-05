/**
 *	Class to store a Feature with its index corresponding to the 
 *	BinaryFeature[] maintained in the ListOfExamples class.
 */
class FeatureWithIndex
{
	public Feature f;
	public int index;
	
	/**
	 * Constructor
	 */
	public FeatureWithIndex(Feature f, int i)
	{
		this.f = f;
		this.index = i;
	}
}