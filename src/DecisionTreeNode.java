import java.util.ArrayList;

/**
 * Class to maintain the Decision-Tree
 * 
 * @date 10/05/2013
 * @hw 1
 */

class DecisionTreeNode
{
	public boolean isLeaf;		// Whether the Node represents a label or feature.
	public String leafValue;	// If LeafNode, it represents the label value.
	public Feature feature;		// If FeatureNode, it represents the feature.
	
	public int[][] labelCountArray;		// Helps debugging. Maintains the output label 
										// counts for different feature values.
	
	public ArrayList<DecisionTreeNode> mChildNodeList = new ArrayList<DecisionTreeNode>();
	
	/**
	 * Constructor
	 */
	public DecisionTreeNode(String str)
	{
		isLeaf = true;
		leafValue = str;
		feature = null;
	}
	
	/**
	 * Constructor
	 */
	public DecisionTreeNode(Feature f)
	{
		isLeaf = false;
		leafValue = null;
		feature = f;
	}
}
