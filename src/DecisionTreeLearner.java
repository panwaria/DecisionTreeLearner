import java.util.ArrayList;

/**
 * Main class which learns the Decision Tree.
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
 * where, train-set-file = Training Set Filename,
 * 		  test-set-file = Test Set Filename,
 * 		  m = threshold value used as a stopping criteria
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
			boolean solveProb1 = true;
			
			if(solveProb1)
			{
				// SETTING DEFAULT LABEL
				String defaultStr = trainDataSet.MajorityValue();
	
				// GETTING ALL FEATURES
				ArrayList<Feature> features = new ArrayList<Feature>(trainDataSet.getFeatures());
				features.remove(features.size() - 1);	// Removing the output feature from the list.
	
				// BUILDING DECISION TREE
				root = BuildDecisionTree(features, trainDataSet, defaultStr, stoppingThreshold);
	
				// PRINTING DECISION TREE
				printDecisionTree(root, 0);
				System.out.println("\n");
	
				// TESTING DECISION TREE
				FindDTreeAccuracy(root, testDataSet, true);
			}
			else
			{
				// Solve Part 2 -- Make sure 'm' is set to '4'.
				System.out.println("\n\n\nSTRATIFIED SAMPLING STARTS\n---------------------\n");
				StratifiedSampling(stoppingThreshold, trainDataSet, testDataSet);
			}
			
		}
	}

	private static final int NUM_ROUNDS = 10;
	public static void StratifiedSampling(int stoppingThreshold, DataSet trainDataSet, DataSet testDataSet)
	{
		// Assumption: Output Feature is Binary.
		
		DiscreteFeature outputFeature = trainDataSet.getOutputFeature();
		int outputIndex = outputFeature.getIndex();
		int numOutputValues = outputFeature.getNumValues();
		ArrayList<String> outputValues = outputFeature.getValues();
		
		DataSet[] dataSetArray = new DataSet [numOutputValues];
		dataSetArray[0] = new DataSet(trainDataSet);	// Transferring the data set properties.
		dataSetArray[1] = new DataSet(trainDataSet);
		
		for (Example e : trainDataSet)
		{
			if(e.get(outputIndex).equals(outputValues.get(0)))	// Match 1st label
				dataSetArray[0].add(e);
			else	// Match 2nd label
				dataSetArray[1].add(e);
		}
		
		Double firstLabelFraction = ((double) dataSetArray[0].size())/trainDataSet.size();
		System.out.println("firstLabelFraction = " + firstLabelFraction);
		
		int [] trainingSetSize = {25, 50, 100};
		
		for (int i = 0; i < trainingSetSize.length; i++)
		{
			int newDataSetSize = trainingSetSize[i];
			
			int numFirstLabelInstances = (int) Math.round(newDataSetSize * firstLabelFraction);
			int numSecondLabelInstances = newDataSetSize - numFirstLabelInstances;
			
			Double avgAccuracy = 0.0, minAccuracy = Double.MAX_VALUE, maxAccuracy = Double.MIN_VALUE;
			
			for (int round = 0 ; round < NUM_ROUNDS; round++)
			{
				// Prepare new data set
				DataSet newDataSet = new DataSet(trainDataSet);	// Transferring the data set properties
				
				// Select FirstLabel Instances randomly
				for(int f = 0 ; f < numFirstLabelInstances; f++)
				{
					newDataSet.add( dataSetArray[0].get( (int)(Math.random() * dataSetArray[0].size()) ) );
				}
				
				// Select SecondLabel Instances randomly
				for(int s = 0 ; s < numSecondLabelInstances; s++)
				{
					newDataSet.add( dataSetArray[1].get( (int)(Math.random() * dataSetArray[1].size()) ) );
				}			
				
				// Now, build Decision Tree for this new data set
				
				// SETTING DEFAULT LABEL
				String defaultStr = newDataSet.MajorityValue();
				
				// GETTING ALL FEATURES
				ArrayList<Feature> features = new ArrayList<Feature>(newDataSet.getFeatures());
				features.remove(features.size() - 1);	// Removing the output feature from the list.

				// BUILDING DECISION TREE
				DecisionTreeNode node = BuildDecisionTree(features, newDataSet, defaultStr, stoppingThreshold);

				// FIND ACCURACY OF THIS TREE
				Double accuracy = FindDTreeAccuracy(node, testDataSet, false);
				
				avgAccuracy += accuracy;
				if(accuracy > maxAccuracy)
					maxAccuracy = accuracy;
				if(accuracy < minAccuracy)
					minAccuracy = accuracy;
				
			}
			
			avgAccuracy =  avgAccuracy/NUM_ROUNDS; 
			
			System.out.println("\n================================" +
								"\nCONFIGURATION: " + "m = " + stoppingThreshold + "\tTraining Set Size = " + trainingSetSize[i] +
								"\nMin Accuracy = " + minAccuracy +
								"\nMax Accuracy = " + maxAccuracy +
								"\nAvg Accuracy = " + avgAccuracy +
								"\n================================");
			
		}

	}
	
	/**
	 * Method to build Decision Tree
	 * @return Decision Tree Node
	 */
	public static DecisionTreeNode BuildDecisionTree(ArrayList<Feature> features, DataSet examples, String defaultStr, int stoppingThreshold)
	{
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
			int numBranches = dataSetArray.length;
			int numOutputValues = dataSetArray[0].getOutputFeature().getNumValues();
			node.labelCountArray = new int[numBranches][numOutputValues];
			for(int i = 0; i < numBranches; i++)
			{
				node.labelCountArray[i] = dataSetArray[i].getBreakUpOfOutputValues();	
			}
				
			for (int i = 0; i < numBranches; i++)
			{
				DecisionTreeNode n = BuildDecisionTree(newFeatures, dataSetArray[i], majorityLabel, stoppingThreshold);
				node.mChildNodeList.add(n);
			}
		}

		return node;
	}

	/**
	 * Method to print the Decision Tree.
	 * 
	 * @param node	 Decision Tree Node
	 * @param level	 Level of the Node in the Tree
	 */
	public static void printDecisionTree(DecisionTreeNode node, int level)
	{
		if (node == null) return;

		if (node.isLeaf) // If Leaf Node, i.e., Label Value in the node
		{
			System.out.print(" : " + node.leafValue);
		}
		else // Not a Leaf Node, i.e., Feature in the node
		{
			// Prepare Indentation
			String spaceStr = ""; 
			String tabSpace = "       "; // 7 whitespaces
			for (int i = 0; i < level; i++)
				spaceStr += "|" + tabSpace;

			// Recursively print the Node value corresponding to the feature value.
			if(node.feature.getType() == Feature.TYPE_NUMERIC)
			{
				NumericFeature nf = (NumericFeature) node.feature;
				Double threshold = nf.getThreshold();
				
				System.out.print("\n" + spaceStr + node.feature.getName() + " <= " + threshold + " [ ");
				for(int count : node.labelCountArray[0])
					System.out.print(count + " ");
				System.out.print("]");
				printDecisionTree(node.mChildNodeList.get(0), level + 1);					
				
				System.out.print("\n" + spaceStr + node.feature.getName() + " > " + threshold + " [ ");
				for(int count : node.labelCountArray[1])
					System.out.print(count + " ");
				System.out.print("]");
				printDecisionTree(node.mChildNodeList.get(1), level + 1);
			}
			else	// DISCRETE
			{
				DiscreteFeature df = (DiscreteFeature) node.feature;
				int i = 0;
				for(String value : df.getValues())
				{
					System.out.print("\n" + spaceStr + node.feature.getName() + " = " + value + " [ ");
					for(int count : node.labelCountArray[i])
						System.out.print(count + " ");
					System.out.print("]");
					printDecisionTree(node.mChildNodeList.get(i++), level + 1);					
				}
			}
		}
	}

	/**
	 * Method to find the accuracy of the induced Decision Tree
	 * 
	 * @param examples		Test Examples
	 * @param node	 		Root of Decision Tree
	 * @param outputLabel	Output Labels
	 * @return 				Accuracy of the Decision Tree
	 */
	public static Double FindDTreeAccuracy(DecisionTreeNode node, ArrayList<Example> examples, boolean printLogs)
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
			String predictedOutput = predictOutput(node, e);

			if (expectedOutput.equals(predictedOutput))
				hits++;
			else
			{
				misses++;
			}
			
			if(printLogs)
			{
				for (int i = 0; i < e.size() - 1; i ++)
					System.out.print(e.get(i) + " ");
				System.out.println("| " + predictedOutput + " | " + expectedOutput);
			}
		}

		Double accuracy = ((double) hits * 100) / examples.size();

		if(printLogs)
		{
			System.out.println("\nCOUNT OF TOTAL TEST  INSTANCES = [ " + examples.size()
					+ " ]\nCOUNT OF CORRECTLY CLASSIFIED TEST INSTANCE = [ " + hits
					+ " ]\nTEST SET ACCURACY = [ " + accuracy + " % ]");
		}
		return accuracy;
	}

	/**
	 * Method to predict the output of an example using already build
	 * Decision-Tree.
	 * 
	 * @param node	Decision Tree Node
	 * @param e		Example
	 * @return 		Predicted Output Label
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
