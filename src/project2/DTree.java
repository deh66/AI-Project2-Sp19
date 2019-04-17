/**
 * @file DTree.java
 * 
 * @author Daniel Hollo
 * @date 3/11/2019
 * 
 * This file contains the implementation of all Decision Tree based operations.
 */

package project2;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


/**
 * This file contains the implementation of all Decision Tree based operations.
 */
public class DTree 
{
	/**
	 * Decision Tree object used by DTree and Menu classes.
	 */
	protected static J48 dTree = new J48();
	//protected static NaiveBayes dTreeNB = new NaiveBayes();
	
	// TODO Document
	// TODO REBUILD traverse() -- Wrong Implementation/Functionallity
	// Should enter a new case (values for all attributes except last "class"), 
	//		then determine it's class (it currently does this)
	public static void traverse(Scanner sc, String nodeArray[])
	{
		String val = "First";
		String nextNode = "";
		String userCase = "";
		int numCases = 0;
		
		while(val != "q" && val != "Q")
		{
			for (int i = 1; i < nodeArray.length - 1; i++) 
			{
				// TODO add documentation
				if (nodeArray[i].indexOf("shape=box") != - 1) 
				{
					System.out.println("The result is: " + 
							nodeArray[i].substring(nodeArray[i].indexOf("=") + 2, nodeArray[i].indexOf(" (")) + "\n");
					break;
				}
				
				// Prompt user for input for current node
				System.out.println("Enter the value for " + 
						nodeArray[i].substring(nodeArray[i].indexOf("=") + 2, nodeArray[i].indexOf("]") - 2));
				i++;
				val = sc.next();
				
				// TODO there has to be a better way to do this
				while ((nodeArray[i].indexOf("= " + val) == - 1) && i < nodeArray.length - 1) 
				{i++;}
				
				// Get the next node
				nextNode = nodeArray[i].substring(0, nodeArray[i].indexOf(" "));
				nextNode = nextNode.substring(nextNode.indexOf("->") + 2);
				
				// TODO add documentation
				while ((!nextNode.equals(nodeArray[i + 1].substring(0,nodeArray[i + 1].indexOf(" ")))) && 
						i + 1 < nodeArray.length - 1) 
				{i++;}
			}
			// Iteration through tree complete
			
			// print("Case " + str(case) + " completed: (press q to exit, or continue entering cases)")
			System.out.println("Case " + numCases + " completed: (press q to exit, or continue entering cases)");
		}
	}
	
}