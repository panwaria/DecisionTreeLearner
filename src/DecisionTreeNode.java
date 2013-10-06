import java.util.ArrayList;


/**
 * Class to maintain the Decision-Tree
 */
class DecisionTreeNode
{
	public boolean isLeaf;		// Whether the Node represents a label or feature.
	public String leafValue;	// If LeafNode, it represents the label value.
	public Feature feature;		// If FeatureNode, it represents the feature.
	
	public int[] labelCountArray;
	
	public ArrayList<DecisionTreeNode> mChildNodeList = new ArrayList<DecisionTreeNode>();
	
	public DecisionTreeNode firstValueNode;		// Reference to the Node if Feature value is the First Value.
	public DecisionTreeNode secondValueNode;	// Reference to the Node if Feature value is the Second Value. 
	
	/**
	 * Constructor
	 */
	public DecisionTreeNode(String str)
	{
		isLeaf = true;
		leafValue = str;
		feature = null;
//		firstValueNode = null;
//		secondValueNode = null;			
	}
	
	/**
	 * Constructor
	 */
	public DecisionTreeNode(Feature f)
	{
		isLeaf = false;
		leafValue = null;
		feature = f;
//		firstValueNode = null;
//		secondValueNode = null;			
	}
}
