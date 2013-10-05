import java.util.ArrayList;

/**
 * Class DecisionTreeLearner
 * 
 * @author 	Prakhar Panwaria
 * @date 	10/05/2013
 * @hw		1
 */

/* DecisionTreeLearner.java 

   Program accept three command-line arguments as follows:
		dt-learn <train-set-file> <test-set-file> m
		
	where,
		train-set-file 	= Training Set Filename
		test-set-file	= Test Set Filename
		m	= a threshold value used as a stopping criteria
*/

public class DecisionTreeLearner
{
	// "Main" reads in the names of the files we want to use, then reads 
	// in their examples.
	public static void main(String[] args)
	{   
		// Program expects three command-line arguments.
	    if (args.length != 3)
	      System.exit(1);
	
	    // Read the file names.
	    String trainFilename = args[0];
	    String testFilename  = args[1];
	
	    // Read examples from the files.
	    ListOfExamples trainExamples = new ListOfExamples();
	    ListOfExamples testExamples  = new ListOfExamples();
	    
	    if (!trainExamples.ReadInExamplesFromFile(trainFilename) ||
	        !testExamples.ReadInExamplesFromFile(testFilename))
		      System.exit(1);
	    else
	    { 
	    	// SETTING DEFAULT LABEL
	    	Feature outputLabel = trainExamples.getOutputLabel();
	    	String defaultStr = trainExamples.MajorityValue(outputLabel);
	     	
	    	// GETTING ALL FEATURES in an Arraylist<> where FeatureWithIndex = {Feature, Index}
	    	ArrayList<FeatureWithIndex> features = new ArrayList<FeatureWithIndex>();
	    	int i = 0;
	    	for (Feature feature : trainExamples.getFeatures())
	    	{
	    		features.add(new FeatureWithIndex(feature, i++));
	    	}
	    	
	    	// BUILDING DECISION TREE
	    	root = BuildDecisionTree(features, trainExamples, defaultStr, outputLabel);
	    	System.out.println("Decision Tree built!\n");
	    	
	    	// PRINTING DECISION TREE
	    	System.out.println("Printing Decision Tree\n----------------------");
	    	printDecisionTree(root, 0);
	    	
	    	// TESTING DECISION TREE
	    	System.out.println("\n\n\nTesting Decision Tree\n---------------------\n");
	    	Double accuracy = FindDTreeAccuracy(testExamples, root, outputLabel);
	    }
	}
  
  /**
   * Method to build Decision Tree
   * 
   * @param features	Feature Set
   * @param examples	Examples available
   * @param defaultStr	Default Label
   * @param outputLabel	Set of labels
   * @return	Decision Tree Node
   */
  public static DecisionTreeNode BuildDecisionTree(ArrayList<FeatureWithIndex> features, ListOfExamples examples, String defaultStr, Feature outputLabel)
  {
	  // NO EXAMPLES
	  if(examples.size() == 0)	// Take the majority of the parent node (which is passed as default)
	  {
		  return (new DecisionTreeNode(defaultStr));
	  }
	  
	  // SAME CLASSIFICATION
	  String classification = examples.isSameClassification(outputLabel);	// Return that label
	  if (classification != null)
	  {
		  return (new DecisionTreeNode(classification));
	  }

	  // RAN OUT OF FEATURES
	  String majorityLabel = examples.MajorityValue(outputLabel);
	  if(features.size() == 0)	// Return the Majority Value
	  {
		  return (new DecisionTreeNode(majorityLabel));
	  }
	  
	  // ELSE, CHOOSE BEST FEATURE
	  FeatureWithIndex bestFeature = examples.ChooseBestFeature(features, outputLabel);
	  
	  // Remove this feature from the list
	  ArrayList<FeatureWithIndex> newFeatures = new ArrayList<FeatureWithIndex>(features);
	  newFeatures.remove(bestFeature);
	  
	  // Create a new Decision Tree Node
	  DecisionTreeNode node = new DecisionTreeNode(bestFeature);
	  
	  // For First Value
	  ListOfExamples firstValueExamples = examples.examplesForFeatureFirstValue(bestFeature);
	  node.firstValueNode = BuildDecisionTree (newFeatures, firstValueExamples, majorityLabel, outputLabel);
	  
	  // For SecondValue
	  ListOfExamples secondValueExamples = examples.examplesForFeatureSecondValue(bestFeature);
	  node.secondValueNode = BuildDecisionTree (newFeatures, secondValueExamples, majorityLabel, outputLabel);
	  
	  return node;
  }
  
  /**
   * Method to print the Decision Tree.
   * 
   * @param node	Decision Tree Node
   * @param level	Level of the Node in the Tree
   */
  public static void printDecisionTree(DecisionTreeNode node, int level)
  {
	  if(node == null)
		  return;
	  
	  if(node.isLeaf)	// If Leaf Node, i.e., Label Value in the node
	  {
		  System.out.print(node.leafValue);
	  }
	  else				// Not a Leaf Node, i.e., Feature in the node
	  {
		  // Prepare Indentation
		  String spaceStr = "";
		  for (int i = 0; i < 4 * level; i++)
			  spaceStr += " ";
		  
		  // Recursively print the Node value corresponding to the first feature value.
		  System.out.print("\n" + spaceStr + node.feature.f.getName() + " = " + node.feature.f.getFirstValue() + " : ");
		  printDecisionTree(node.firstValueNode, level + 1);
		  
		  // Recursively print the Node value corresponding to the second feature value.
		  System.out.print("\n" + spaceStr + node.feature.f.getName() + " = " + node.feature.f.getSecondValue() + " : ");
		  printDecisionTree(node.secondValueNode, level + 1);
	  }
  }
  
  /**
   * Method to find the accuracy of the induced Decision Tree
   * 
   * @param examples	Test Examples
   * @param node		Root of Decision Tree
   * @param outputLabel	Output Labels
   * @return	Accuracy of the Decision Tree
   */
  public static Double FindDTreeAccuracy(ArrayList<Example> examples, DecisionTreeNode root, Feature outputLabel)
  {
	  if(examples.size() == 0)
		  return 100.0;
	  
	  long hits = 0; long misses = 0;
	  
	  // Find number of hits for all the test examples
	  for (Example e : examples)
	  {		  
		  // Expected output already known for the example 	
		  String expectedOutput = e.getLabel();
		  
		  // Predict the output for this example
		  String predictedOutput = predictOutput(root, e);
		  
		  if(expectedOutput.equals(predictedOutput))
			  hits++;
		  else
		  {
			  misses++;
			  
			  if(misses == 1)
				  System.out.println("FOLLOWING ARE INCORRECTLY CLASSIFIED TEST SET EXAMPLES: \n");
			  
			  // Print the INCORRECTLY CLASSIFIED EXAMPLES
			  System.out.println(e.getName() + " : " + e + "\tExpected Output = " + expectedOutput + "\tPredicted Output = " + predictedOutput);
		  }
	  }
	  
	  Double missFraction = ((double)(examples.size() - hits)) / examples.size() ;
	  System.out.println("\nFRACTION OF TEST SET EXAMPLES INCORRECTLY CLASSIFIED = [ " + missFraction + " ]");
	  
	  Double accuracy = ((double)hits * 100)/examples.size();
	  
	  System.out.println("TEST SET ACCURACY = [ " + accuracy + "% ]\nTEST SET HIT COUNT = [ " + hits + " ]\nTEST SET EXAMPLES COUNT = [ " + examples.size() +" ]");
	  return 0.0;
  }

  /**
   * Method to predict the output of an example using already build Decision-Tree.
   * 
   * @param node Decision Tree Node
   * @param e	Example
   * @return	Predicted Output Label
   */
  public static String predictOutput(DecisionTreeNode node, Example e)
  {
	  while (true)
	  {
		if(node.isLeaf)
		{
			return node.leafValue;
		}
		else
		{
			int featureIndex = node.feature.index;
			if( e.get(featureIndex).equals(node.feature.f.getFirstValue()) )
				node = node.firstValueNode;
			else
			{
//				if(node.feature.f.getName().equals("antivirals"))
//					System.out.println("Example :" + e.getName() + " with antivirals = " + e.get(featureIndex));
				node = node.secondValueNode;
			}
		}
	  }
  }
  
  private static DecisionTreeNode root;
}




