/**
 * @file NBayes.java
 * 
 * @author Daniel Hollo
 * @date 4/14/2019
 * 
 * Contains the implementation of all Naive Bayes based operations.
 */

package project2;


import java.util.Enumeration;
import java.util.Scanner;

import java.nio.file.Files;
import java.nio.file.Paths;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnassignedDatasetException;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Contains the implementation of all Naive Bayes based operations.
 * @author Daniel Hollo
 */
public class NBayes {
	/**
	 * Naive Bayes Classifier used by NBayes and Menu classes.
	 */
	protected static NaiveBayes nbClassifier = new NaiveBayes();
	
	/**
	 * Working data set read in from .arff file
	 */
	protected static Instances data;
	
	
	
	/**
	 * Attempts to create a Naive Bayes Classifier from a given filename.
	 * @param fName		Name of the file to attempt to create a NBClassifier from
	 */
	public static void loadNBClassifierARFF(String fName) 
	{
		DataSource source;
		
		// Check that file exists
		if( !Files.exists( Paths.get(fName) ) )
		{
			System.out.println("Error: Classifier file: "+ fName +" could not be found.");			
			return;
		}
		
		try 
		{
			source = new DataSource(fName);
			data = source.getDataSet();
		
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1); 			
			
			nbClassifier.buildClassifier(data);
		}
		catch(Exception e)
		{
			System.out.println("An Exception occured while loading the classifier. Classifier creation aborted.");
			System.err.println(e);
		}
		
		System.out.println("Decision Tree Created.");
		
		return;
	}
	
	/**
	 * Load a previously saved classifier from file, as well as it's data file
	 * @param fName		name of the file the classifier is saved in
	 * @param dfName	name of the data file the classifier was created from (for attribute labels)
	 * @return {@code true} if classifier loaded successfully; {@code false} if not
	 */
	public static boolean loadNBClassifierCustom(String fName, String dfName)
	{
		// Check that file exists
		if( !Files.exists( Paths.get(fName) ) )
		{
			System.out.println("Error: The classifier file (" + fName + ") could not be found.");
			return false;
		}
		if( !Files.exists( Paths.get(dfName) ) )
		{
			System.out.println("Error: The data file (" + dfName + ") could not be found.");
			return false;
		}
		
		try
		{
			// Read in the tree
			nbClassifier = (NaiveBayes) weka.core.SerializationHelper.read(fName);				
			
			// Get the data file that the classifier came from
			DataSource source = new DataSource(dfName);
			data = source.getDataSet();
			
			// Exclude the class attribute
			if (data.classIndex() == -1)
				data.setClassIndex(data.numAttributes() - 1); 
		}
		catch (Exception e)
		{
			System.out.println("An Exception Occured. Classifier creation aborted.");
			System.err.println(e);
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * Creates a new instance (case) from user input. Prompts user on valid inputs for each attribute
	 * and asks if this case should modify the classifier.
	 * @param sc	{@code Scanner} object to read in user input
	 * @throws IllegalArgumentException	If an unexpected value is entered for an attribute
	 * @throws UnassignedDatasetException	If the dataset is not initalized
	 */
	public static void createInstanceInteractive(Scanner sc)
	{
		Instance newInstance = data.firstInstance();
		int iter = 0;
		String userVal = "";
		
		// get the attributes from data
		Enumeration<Attribute> atts = data.enumerateAttributes();
		
		// Iterate through each attribute
		while(atts.hasMoreElements())
		{
			Attribute a = atts.nextElement();
			
			userVal = getAttributeValuefromUser(sc, a);
			
			try
			{
				// Set the value of the current attribute
				newInstance.setValue(iter, userVal);
			}
			catch (IllegalArgumentException e)
			{
				System.out.println("An illegal argument has been entered. Classifier modification aborted.");
				System.err.println(e);
				return;
			}
			catch (UnassignedDatasetException e)
			{
				System.err.println(e);
				return;
			}
			catch (Exception e)
			{
				System.out.println("A generic error has been encountered. Classifier modification aborted.");
				System.err.println(e);
				return;
			}
			iter++;
		}
			
		// Calculate class and probability
		System.out.println("Predicted value:\n" + getClassPrediction(newInstance));
			
		// Prompt user if this instance (case) should modify the classifier
		System.out.println("Do you want to update the classifier with this case? (y/n):");
		userVal = sc.next();
		
		if(userVal.equals("y") || userVal.equals("Y"))
		{
			try
			{
				// update classifier
				nbClassifier.updateClassifier(newInstance);
				System.out.println("Classifier Updated");
			}
			catch (Exception e)
			{
				System.out.println("A General Exception Occured. Classifier modification aborted.");
				System.err.println(e);
			}
		}
		
		return;
	}
	
	/**
	 * Prompts the user for the value of the given {@code Attribute}, displaying the valid inputs.
	 * @param sc	{@code Scanner} object to read in user input
	 * @param att	{@code Attribute} object that is to have it's value set by the user
	 * @return		String containing the value given by the user for this {@code Attribute}
	 * @see createInstanceInteractive
	 */
	public static String getAttributeValuefromUser(Scanner sc, Attribute att)
	{
		String userInput = "";
		
		System.out.println("Please enter value for attribute: " + att.name());
		System.out.print("    Values of " + att.name() + ": { ");
		
		Enumeration<Object> values = att.enumerateValues();
		
		// Iterate through all possible values, save and display them
		while(values.hasMoreElements())
		{
			System.out.print((String)values.nextElement() + ", ");
		}		
		System.out.print("}\n");
		
		userInput = sc.next();
		
		return userInput;
	}
	
	/**
	 * Returns the most likely class by its name and its probability
	 * @param inst	{@code Instance} object to have it's class determined
	 * @return		Formatted string with the name of the most likely class and its probability
	 */
	public static String getClassPrediction(Instance inst)
	{
		String prediction = "[error]";
		double[] distProb;
		double highestPred = -1;
		int index = 0;
		
		try
		{
			distProb = nbClassifier.distributionForInstance(inst);
			
			// Search for the highest probability
			for(int i = 0; i < distProb.length; i++)
			{
				// Store new highest probability
				if(distProb[i] > highestPred)
				{
					highestPred = distProb[i];
					index = i;
				}
			}
			
			// Get the class name of the predicted class and its probability
			Attribute classAttr = inst.classAttribute();
			prediction = "    Class Name: " + classAttr.name() + 
					"\n    Value: " + classAttr.value(index) + 
					"\n    Probability: " + highestPred;
		}
		catch (Exception e)
		{
			System.out.println("An Exception Occured. Tree creation aborted.");
			System.err.println(e);
		}	
		
		return prediction;
	}
	
	/**
	 * Displays all valid {@code Attribute} values for each {@code Attribute} of {@code data}
	 * @param data	{@code Instances} object
	 */
	public static void showAttributeInfo(Instances data)
	{
		Enumeration<Attribute> atts = data.enumerateAttributes();
		
		while(atts.hasMoreElements())
		{
			Attribute a = atts.nextElement();
			System.out.println("    " + a.name());
			
			Enumeration<Object> values = a.enumerateValues();
			
			while(values.hasMoreElements())
			{
				String v = (String) values.nextElement();
				System.out.println(v);
			}
		}
	}
	
	/**
	 * Displays the Attributes and Attribute values for {@code data}
	 * @param data	{@code Instance} object to be displayed
	 */
	public static void showInstanceValues(Instance data)
	{
		Enumeration<Attribute> atts = data.enumerateAttributes();
		
		System.out.println("\nNew Instance: ");
		
		while(atts.hasMoreElements())
		{
			Attribute a = atts.nextElement();
			System.out.println(a.name() + ":");
			
			//System.out.println(data.value(a));
			System.out.println("    " + a.value((int) data.value(a)));
		}
	}

}
