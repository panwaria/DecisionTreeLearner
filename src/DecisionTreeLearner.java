import java.util.ArrayList;

/**
 * Class DecisionTreeLearner
 * 
 * @author Prakhar Panwaria
 * @date 10/05/2013
 * @hw 1
 */

/*
 * DecisionTreeLearner.java
 * 
 * Program accept three command-line arguments as follows: dt-learn
 * <train-set-file> <test-set-file> m
 * 
 * where, train-set-file = Training Set Filename test-set-file = Test Set
 * Filename m = threshold value used as a stopping criteria
 */

public class DecisionTreeLearner
{
	// "Main" reads in the names of the files we want to use, then reads
	// in their examples.
	public static void main(String[] args)
	{
		// Program expects three command-line arguments.
		if (args.length != 3)
		{
			System.err
			.println("Error: Insufficient number of arguments."
					+ "\nRequired Arguments: <train_Data_file> <test_data_file> m");
			System.exit(1);
		}

		// Read the file names.
		String trainFilename = args[0];
		String testFilename = args[1];

		int stoppingThreshold = 0;
		try
		{
			stoppingThreshold = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			System.err
			.println("Error: Invalid value of 'm'. M should be an integer.");
			System.exit(1);
		}

		// Read examples from the files.
		DataSet trainDataSet = new DataSet();
		DataSet testDataSet = new DataSet();

		if (!trainDataSet.ReadInExamplesFromFile(trainFilename)
				|| !testDataSet.ReadInExamplesFromFile(testFilename))
		{
			System.err.println("Error: Not able to read the datasets.");
			System.exit(1);
		}
		else
		{
			// TESTING THE DATA SETS
//			trainDataSet.DescribeDataset();
//			testDataSet.DescribeDataset();

			// SETTING DEFAULT LABEL
			Feature outputLabel = trainDataSet.getOutputFeature();
			String defaultStr = trainDataSet.MajorityValue();// (outputLabel);

			// GETTING ALL FEATURES
			ArrayList<Feature> features = new ArrayList<Feature>(trainDataSet.getFeatures());
			features.remove(features.size() - 1);	// Removing the output feature from the list.

			// BUILDING DECISION TREE
			root = BuildDecisionTree(features, trainDataSet, defaultStr, stoppingThreshold);// outputLabel, stoppingThreshold);
			System.out.println("Decision Tree built!\n");

			// PRINTING DECISION TREE
			System.out
			.println("Printing Decision Tree\n----------------------");
			printDecisionTree(root, 0);

			// TESTING DECISION TREE
			System.out.println("\n\n\nTesting Decision  on TrainDataSet\n---------------------\n");
			Double accuracy = FindDTreeAccuracy(trainDataSet, root); // outputLabel);
			System.out.println("\n\n\nTesting Decision  on TestDataSet\n---------------------\n");
			accuracy = FindDTreeAccuracy(testDataSet, root); // outputLabel);
		}
	}

	/**
	 * Method to build Decision Tree
	 * 
	 * @param features
	 *            Feature Set
	 * @param examples
	 *            Examples available
	 * @param defaultStr
	 *            Default Label
	 * @param outputLabel
	 *            Set of labels
	 * @return Decision Tree Node
	 */
	public static DecisionTreeNode BuildDecisionTree(ArrayList<Feature> features, DataSet examples, String defaultStr, int stoppingThreshold) // Feature outputLabel, int stoppingThreshold)
	{
		examples.DescribeDataset();	// TODO: Testing. Remove it.
		
		// NO EXAMPLES
		if (examples.size() == 0) // Take the majority of the parent node (which is passed as default)
		{
			return (new DecisionTreeNode(defaultStr));
		}

		// EXAMPLES COUNT LESS THAN A THRESHOLD, OR OUT OF FEATURES
		String majorityLabel = examples.MajorityValue();
		if (examples.size() < stoppingThreshold || features.size() == 0) // Take the majority label of m instances
		{
			return (new DecisionTreeNode(majorityLabel));
		}

		// SAME CLASSIFICATION
		String classification = examples.isSameClassification();// (outputLabel); // Return that label
		if (classification != null)
		{
			return (new DecisionTreeNode(classification));
		}

		// ELSE, CHOOSE BEST FEATURE
		Feature bestFeature = examples.ChooseBestFeature(features);// ,
		// outputLabel);

		if (bestFeature == null) // In case, there are no features with positive Information Gain.
			return (new DecisionTreeNode(majorityLabel));

		// Remove Best Feature from the list
		ArrayList<Feature> newFeatures = new ArrayList<Feature>(features);

		// Create a new Decision Tree Node
		DecisionTreeNode node = new DecisionTreeNode(bestFeature);

		// Now, create branches for this Decision Tree Node.
		// If Numeric Feature, it should be converted to a Binary Features, with two branches-
		// (1) x <= threshold, and (2) x > threshold
		DataSet[] dataSetArray;
		if (bestFeature.getType() == Feature.TYPE_NUMERIC)
		{
			dataSetArray = examples.ExamplesForNumericFeatureBranches(bestFeature.getIndex(), ((NumericFeature) bestFeature).getThreshold());
		}
		else // Discrete Valued Feature
		{
			dataSetArray = examples.ExamplesForDiscreteFeatureBranches(bestFeature.getIndex());
			newFeatures.remove(bestFeature);
		}

		if (dataSetArray != null)
		{
			int arrayLen = dataSetArray.length;
			node.labelCountArray = new int[arrayLen];
			for(int i = 0; i < arrayLen; i++)
				node.labelCountArray[i] = dataSetArray[i].size();
				
			for (int i = 0; i < arrayLen; i++)
			{
				DecisionTreeNode n = BuildDecisionTree(newFeatures, dataSetArray[i], majorityLabel, stoppingThreshold); // outputLabel, stoppingThreshold);
				node.mChildNodeList.add(n);
			}
		}

		return node;
	}

	/**
	 * Method to print the Decision Tree.
	 * 
	 * @param node
	 *            Decision Tree Node
	 * @param level
	 *            Level of the Node in the Tree
	 */
	public static void printDecisionTree(DecisionTreeNode node, int level)
	{
		if (node == null) return;

		if (node.isLeaf) // If Leaf Node, i.e., Label Value in the node
		{
			System.out.print(node.leafValue);
		}
		else // Not a Leaf Node, i.e., Feature in the node
		{
			// Prepare Indentation
			String spaceStr = "";
//			for (int i = 0; i < 4 * level; i++)
//				spaceStr += " ";
			for (int i = 0; i < level; i++)
				spaceStr += "|    ";

			// Recursively print the Node value corresponding to the feature value.
			if(node.feature.getType() == Feature.TYPE_NUMERIC)
			{
				NumericFeature nf = (NumericFeature) node.feature;
				Double threshold = nf.getThreshold();
				
				System.out.print("\n" + spaceStr + node.feature.getName() + " <= " + threshold + " : ");
				printDecisionTree(node.mChildNodeList.get(0), level + 1);					
				
				System.out.print("\n" + spaceStr + node.feature.getName() + " > " + threshold + " : ");
				printDecisionTree(node.mChildNodeList.get(1), level + 1);
			}
			else	// DISCRETE
			{
				DiscreteFeature df = (DiscreteFeature) node.feature;
				int i = 0;
				for(String value : df.getValues())
				{
//					System.out.print("\n" + spaceStr + node.feature.getName() + " = " + value + "[ ");
//					for(int count : node.labelCountArray)
//						System.out.print(count + " ");
//					System.out.print("] : ");
					System.out.print("\n" + spaceStr + node.feature.getName() + " = " + value + " : ");
					printDecisionTree(node.mChildNodeList.get(i++), level + 1);					
				}
			}

			
//			System.out.print("\n" + spaceStr + node.feature.getName() + " = "
//					+ node.feature.getFirstValue() + " : ");
//			printDecisionTree(node.firstValueNode, level + 1);
//
//			// Recursively print the Node value corresponding to the second feature value.
//			System.out.print("\n" + spaceStr + node.feature.getName() + " = "
//					+ node.feature.getSecondValue() + " : ");
//			printDecisionTree(node.secondValueNode, level + 1);
		}
	}

	/**
	 * Method to find the accuracy of the induced Decision Tree
	 * 
	 * @param examples
	 *            Test Examples
	 * @param node
	 *            Root of Decision Tree
	 * @param outputLabel
	 *            Output Labels
	 * @return Accuracy of the Decision Tree
	 */
	public static Double FindDTreeAccuracy(ArrayList<Example> examples, DecisionTreeNode root)// , Feature outputLabel)
	{
		if (examples.size() == 0) return 100.0;

		long hits = 0;
		long misses = 0;

		// Find number of hits for all the test examples
		for (Example e : examples)
		{
			// Expected output already known for the example
			String expectedOutput = e.getLabel();

			// Predict the output for this example
			String predictedOutput = predictOutput(root, e);

			if (expectedOutput.equals(predictedOutput))
				hits++;
			else
			{
				misses++;

				if (misses == 1)
					System.out
					.println("FOLLOWING ARE INCORRECTLY CLASSIFIED TEST SET EXAMPLES: \n");

				// Print the INCORRECTLY CLASSIFIED EXAMPLES
				System.out.println(e.getName() + " : " + e
						+ "\tExpected Output = " + expectedOutput
						+ "\tPredicted Output = " + predictedOutput);
			}
		}

		Double missFraction = ((double) (examples.size() - hits))
				/ examples.size();
		System.out
		.println("\nFRACTION OF TEST SET EXAMPLES INCORRECTLY CLASSIFIED = [ "
				+ missFraction + " ]");

		Double accuracy = ((double) hits * 100) / examples.size();

		System.out.println("TEST SET ACCURACY = [ " + accuracy
				+ "% ]\nTEST SET HIT COUNT = [ " + hits
				+ " ]\nTEST SET EXAMPLES COUNT = [ " + examples.size() + " ]");
		return 0.0;
	}

	/**
	 * Method to predict the output of an example using already build
	 * Decision-Tree.
	 * 
	 * @param node
	 *            Decision Tree Node
	 * @param e
	 *            Example
	 * @return Predicted Output Label
	 */
	public static String predictOutput(DecisionTreeNode node, Example e)
	{
		while (true)
		{
			if (node.isLeaf)
			{
				return node.leafValue;
			}
			else
			{
				int featureIndex = node.feature.getIndex();
				
				if(node.feature.getType() == Feature.TYPE_NUMERIC)
				{
					NumericFeature nf = (NumericFeature) node.feature;
					Double threshold = nf.getThreshold();
					
					Double val = Double.parseDouble(e.get(featureIndex));
					if(val.compareTo(threshold) <= 0)
						node = node.mChildNodeList.get(0);
					else
						node = node.mChildNodeList.get(1);
				}
				else // DISCRETE
				{
					DiscreteFeature df = (DiscreteFeature) node.feature;
					
					int i = 0;
					for(String val : df.getValues())
					{
						if(e.get(featureIndex).equals(val))
							node = node.mChildNodeList.get(i);
						i++;
					}
				}

			}
		}
	}

	private static DecisionTreeNode root;
}
