import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/* This class holds all of our examples  from one dataset
 (train OR test, not BOTH).  It extends the ArrayList class.
 Be sure you're not confused.  We're using TWO types of ArrayLists.  
 An Example is an ArrayList of feature values, while a DataSet is 
 an ArrayList of examples. Also, there is one DataSet for the 
 TRAINING SET and one for the TESTING SET. 
 */

class DataSet extends ArrayList<Example>
{
	// The name of the dataset.
	private String mDataSetName = "";
	
	// Name of relation
	private String mRelationName = "";
	// ArrayList of Features
	private ArrayList<Feature> mFeatures = new ArrayList<Feature>();

	// The number of mFeaturesArray per example in the dataset.
	private int mNumFeatures = 0;

	// The number of examples in the dataset.
	private int mNumExamples = 0;

	public DataSet() {}
	
	/**
	 * Method to choose the best feature for the node in Decision Tree
	 * 
	 * @param mFeaturesArray
	 *            List of mFeaturesArray
	 * @param outputLabel
	 *            Output Label
	 * @return Best Feature
	 */
	public FeatureWithIndex ChooseBestFeature(ArrayList<FeatureWithIndex> features, Feature outputLabel)
	{
		// FIND LEAST REMAINDER
		// - If they match, Choose the feature alphabetically
		Double minRemainder = 2.0;

		FeatureWithIndex bestFeature = null;
		for (FeatureWithIndex feature : features)
		{
			DataSet firstValueExamples = examplesForFeatureFirstValue(feature);
			long firstValueFirstLabelExampleCount = firstValueExamples
					.FirstValueCount(outputLabel);
			long firstValueSecondLabelExampleCount = firstValueExamples.size()
					- firstValueFirstLabelExampleCount;

			DataSet secondValueExamples = examplesForFeatureSecondValue(feature);
			long secondValueFirstLabelExampleCount = secondValueExamples
					.FirstValueCount(outputLabel);
			long secondValueSecondLabelExampleCount = secondValueExamples
					.size() - secondValueFirstLabelExampleCount;

			// Calculate the Remainder value of the feature.
			Double tempRemainder = (((double) firstValueExamples.size() / size()) * IFunc(
					firstValueFirstLabelExampleCount,
					firstValueSecondLabelExampleCount))
					+ (((double) secondValueExamples.size() / size()) * IFunc(
							secondValueFirstLabelExampleCount,
							secondValueSecondLabelExampleCount));

			if (tempRemainder.compareTo(minRemainder) == 0)
			{
				// Choose feature alphabetically - feature and the bestFeature
				int result = bestFeature.f.getName().compareTo(
						feature.f.getName());
				if (result > 0) // i.e. feature is alphabetically smaller than
								// bestFeature.
					bestFeature = feature;
			}
			else if (tempRemainder.compareTo(minRemainder) < 0)
			{
				// Update the best feature
				minRemainder = tempRemainder;
				bestFeature = feature;
			}
		}

		return bestFeature;
	}

	/**
	 * Method to calculate the Information needed.
	 */
	public Double IFunc(long a, long b)
	{
		long total = a + b;

		if (total == 0) return 0.0;

		Double firstFraction = ((double) a) / total;
		Double secondFraction = ((double) b) / total;

		Double result = 0.0;
		if (firstFraction != 0)
			result += -(firstFraction * (Math.log(firstFraction) / Math.log(2)));
		if (secondFraction != 0)
			result += -(secondFraction * (Math.log(secondFraction) / Math
					.log(2)));

		return result;
	}

	/**
	 * Method to find which is the majority label out of all examples.
	 * 
	 * @param outputLabel
	 *            Output Label
	 * @return Majority Label
	 */
	public String MajorityValue(Feature f)
	{
		int index = f.getIndex();
		String majValue = "";
		int count, maxCount = -1;
		
		System.out.println("Data Set = " + mDataSetName);
		for (String value : ((DiscreteFeature)f).getValues())
		{
			count = 0;
			for (int i = 0; i < size(); i++)
			{
				if(this.get(i).get(index).equals(value))
					count++;				
			}

	    	System.out.println("Label = " + value + "\tCount = " + count);
	    	
			if(count > maxCount)
			{
				maxCount = count;
				majValue = value;
			}
		}
		
		return majValue;
//			
//		long firstValueCount = FirstValueCount(outputLabel);
//		long secondValueCount = size() - firstValueCount;
//
//		if (firstValueCount == secondValueCount)
//			return outputLabel.getSecondValue();
//		else
//			return (firstValueCount > secondValueCount) ? outputLabel
//					.getFirstValue() : outputLabel.getSecondValue();
	}

	/**
	 * Method to check whether all the examples have same classification.
	 * 
	 * @param outputLabel
	 *            Output Label
	 * @return Label, if it is all over the examples, else null.
	 */
	public String isSameClassification(Feature outputLabel)
	{
		if (size() == 0) return null;

		long firstValueCount = FirstValueCount(outputLabel);
		long secondValueCount = size() - firstValueCount;

		if (firstValueCount == size())
			return outputLabel.getFirstValue();

		else if (secondValueCount == size())
			return outputLabel.getSecondValue();

		return null;
	}

	// Print out a high-level description of the dataset including its mFeaturesArray.
	public void DescribeDataset()
	{
		System.out.println( "=====================================================================" +
							"\nDataset File:\t'" + mDataSetName + "' " +
							"\nRelation Name:\t '" + mRelationName + "' " +
							"\nDataset contains " + mNumExamples + " examples, each with " + mNumFeatures + " features.");
		
		System.out.println("The feature names (with their possible values) are:");
		for (int i = 0; i < mNumFeatures; i++)
		{
			Feature f = mFeatures.get(i);
			if(f.getType() == Feature.TYPE_NUMERIC)
			{
				System.out.println("\t" + f.getName().toUpperCase() + ":\t[Real Valued Feature]");
			}
			else
			{
				DiscreteFeature temp = (DiscreteFeature)f;
				System.out.print("\t" + f.getName().toUpperCase() + ":\t");
				for(String values : temp.getValues())
					System.out.print(values + ", ");
				System.out.println();
			}
		}
		System.out.println("=====================================================================");
	}

	// Print out ALL the examples.
	public void PrintAllExamples()
	{
		System.out.println("List of Examples\n================");
		for (int i = 0; i < size(); i++)
		{
			Example thisExample = this.get(i);
			thisExample.PrintFeatures();
		}
	}

	// Print out the SPECIFIED example.
	public void PrintThisExample(int i)
	{
		Example thisExample = this.get(i);
		thisExample.PrintFeatures();
	}

	// Returns the number of mFeaturesArray in the data.
	public int getNumberOfFeatures()
	{
		return mNumFeatures;
	}

	public ArrayList<Feature> getFeatures()
	{
		return mFeatures;
//		return mFeaturesArray;
	}

	// Returns the name of the ith feature.
	public String getFeatureName(int i)
	{
		return mFeatures.get(i).getName();
//		return mFeaturesArray[i].getName();
	}

	// Takes the name of an input file and attempts to open it for parsing.
	// If it is successful, it reads the dataset into its internal structures.
	// Returns true if the read was successful.
	public boolean ReadInExamplesFromFile(String dataFile)
	{
		mDataSetName = dataFile;

		// Try creating a scanner to read the input file.
		Scanner fileScanner = null;
		
		try { fileScanner = new Scanner(new File(dataFile)); }
		catch (FileNotFoundException e)	{ return false; }

		// If the file was successfully opened, read the file
		this.parse(fileScanner);
		
		return true;
	}

	/**
	 * Does the actual parsing work. We assume that the file is in proper
	 * format.
	 * 
	 * @param fileScanner  a Scanner which has been successfully opened to read the dataset file
	 */
	public void parse(Scanner fileScanner)
	{
		boolean parseExamplesNow = false;
		
		// Parse whole file
		while (fileScanner.hasNextLine())
		{
			String line = fileScanner.nextLine().trim();
			
			if (!isLineSignificant(line)) continue;
			
			if(parseExamplesNow)	// Examples
			{
				String parts[] = line.split(",");
				
				if(parts.length != mNumFeatures)
				{
					System.out.println(mNumFeatures);
					System.err.println("Error: Ignoring bad Instance (having less feature values).");
					continue;
				}
				
				Example e = new Example(this, mNumExamples);
				for (String part : parts)
				{
					e.addFeatureValue(part);
				}
				this.add(e);
				
				mNumExamples++;
			}
			else if(line.startsWith("@relation"))	// Relation Name
			{
				String parts[] = line.split(" ");
				
				if(parts[1] != null)
					mRelationName = parts[1];
			}
			else if (line.startsWith("@attribute"))	// Features
			{
				String[] parts = line.split("[,'{}\\s+]");
				
				ArrayList<String> keyParts = new ArrayList<String>();
				for (String part : parts)
				{
					if(part.trim().length() == 0) continue;
					keyParts.add(part.trim());
				}
				
				int keyPartsLength = keyParts.size();
				if(keyPartsLength < 3) continue;
				
				Feature f = null;	
				String fName = keyParts.get(1);
				if(keyParts.get(2).equals("real"))	// Numeric Feature
				{
					f = new NumericFeature(fName, mNumFeatures);
//					System.out.println("Added Numeric Feature" + f.getName());
				}
				else
				{
					ArrayList<String> values = new ArrayList<String>();
					for (int i = 2; i < keyPartsLength; i++)
						values.add(keyParts.get(i));
					
					f = new DiscreteFeature(fName, mNumFeatures, values.size(), values);
//					System.out.println("Added Discrete Feature" + f.getName());
				}
				
				mFeatures.add(f);
				mNumFeatures++;
			}
			else if (line.startsWith("@data"))		// Examples
			{
				parseExamplesNow = true;
			}
			
		}
	}

	/**
	 * Returns whether the given line is significant (i.e., not blank or a
	 * comment). The line should be trimmed before calling this.
	 * 
	 * @param line line to check
	 */
	private boolean isLineSignificant(String line)
	{
		// Blank lines are not significant. And, lines which have consecutive forward slashes as their 
		// first two characters are comments and are not significant.
		if (line.length() == 0 || (line.length() > 2 && line.substring(0, 2).equals("//")))	return false;

		return true;
	}
	
	/**
	 * Returns the first token encountered on a significant line in the file.
	 * 
	 * @param fileScanner
	 *            a Scanner used to read the file.
	 */
	/*
	private String parseSingleToken(Scanner fileScanner)
	{
		String line = findSignificantLine(fileScanner);

		// Once we find a significant line, parse the first token on the
		// line and return it.
		Scanner lineScanner = new Scanner(line);
		return lineScanner.next();
	}
	*/
	/**
	 * Reads in the feature metadata from the file.
	 * 
	 * @param fileScanner
	 *            a Scanner used to read the file.
	 */
	/*
	private void parseFeatures(Scanner fileScanner)
	{
		// Initialize the array of mFeaturesArray to fill.
		mFeaturesArray = new Feature[mNumFeatures];

		for (int i = 0; i < mNumFeatures; i++)
		{
			String line = findSignificantLine(fileScanner);

			// Once we find a significant line, read the feature description
			// from it.
			Scanner lineScanner = new Scanner(line);
			String name = lineScanner.next();
			String dash = lineScanner.next(); // Skip the dash in the file.
			String firstValue = lineScanner.next();
			String secondValue = lineScanner.next();
			mFeaturesArray[i] = new Feature(name, firstValue, secondValue);
		}
	}
	*/

	/*
	private void parseExamples(Scanner fileScanner)
	{
		// Parse the expected number of examples.
		for (int i = 0; i < mNumExamples; i++)
		{
			String line = findSignificantLine(fileScanner);
			Scanner lineScanner = new Scanner(line);

			// Parse a new example from the file.
			Example ex = new Example(this, i);

			// String name = lineScanner.next();
			ex.setIndex(i);

			// String label = lineScanner.next();
			// ex.setLabel(label);

			// Iterate through the mFeaturesArray and increment the count for any
			// feature that has the first possible value.
			for (int j = 0; j < mNumFeatures; j++)
			{
				String feature = lineScanner.next();
				ex.addFeatureValue(feature);
			}

			lineScanner.close();

			// Add this example to the list.
			this.add(ex);
		}
	}
	*/

	/**
	 * Returns the next line in the file which is significant (i.e. is not all
	 * whitespace or a comment.
	 * 
	 * @param fileScanner
	 *            a Scanner used to read the file
	 */
	/*
	private String findSignificantLine(Scanner fileScanner)
	{
		// Keep scanning lines until we find a significant one.
		while (fileScanner.hasNextLine())
		{
			String line = fileScanner.nextLine().trim();
			if (isLineSignificant(line))
			{
				return line;
			}
		}

		// If the file is in proper format, this should never happen.
		System.err.println("Unexpected problem in findSignificantLine.");

		return null;
	}
	*/

	public String getFeatureFirstValue(int i)
	{
		return mFeaturesArray[i].getFirstValue();
	}

	public String getFeatureSecondValue(int i)
	{
		return mFeaturesArray[i].getSecondValue();
	}


	/**
	 * Method to return the list of examples where value of the feature F it
	 * equal to its first value.
	 * 
	 * @param f
	 *            Feature
	 * @return List of matching examples
	 */
	DataSet examplesForFeatureFirstValue(FeatureWithIndex f)
	{
		DataSet subListOfExamples = new DataSet();

		if (f == null) return subListOfExamples;

		for (int i = 0; i < mNumExamples; i++)
		{
			Example e = get(i);
			if (e.get(f.index).equals(f.f.getFirstValue()))
				subListOfExamples.add(e);
		}

		subListOfExamples.mNumExamples = subListOfExamples.size();
		return subListOfExamples;
	}

	/**
	 * Method to return the list of examples where value of the feature F it
	 * equal to its second value.
	 * 
	 * @param f
	 *            Feature
	 * @return List of matching examples
	 */
	DataSet examplesForFeatureSecondValue(FeatureWithIndex f)
	{
		DataSet subListOfExamples = new DataSet();

		if (f == null) return subListOfExamples;

		for (int i = 0; i < mNumExamples; i++)
		{
			Example e = get(i);
			if (e.get(f.index).equals(f.f.getSecondValue()))
				subListOfExamples.add(e);
		}

		subListOfExamples.mNumExamples = subListOfExamples.size();
		return subListOfExamples;
	}

	/**
	 * Method to calculate the number of examples matching the first label.
	 * 
	 * @param outputLabel
	 *            Output Label
	 * @return Count of the matching examples
	 */
	public long FirstValueCount(Feature outputLabel)
	{
		long firstValueCount = 0;
		for (int i = 0; i < size(); i++)
		{
			Example thisExample = this.get(i);
			if (thisExample.getLabel().equals(outputLabel.getFirstValue()))
				firstValueCount++;
		}

		return firstValueCount;
	}

	// An array of the parsed mFeaturesArray in the data.
	private Feature[] mFeaturesArray;

	// A binary feature representing the output label of the dataset.
	private Feature outputLabel;

	public Feature getOutputLabel()
	{
		return mFeatures.get(mNumFeatures - 1);
//		return outputLabel;
	}

}
