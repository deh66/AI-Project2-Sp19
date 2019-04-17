/**
 * @file nb.java
 * 
 * @author Daniel Hollo
 * @date 4/14/2019
 * 
 * Contains the implementation of the driver function and the menu interface.
 */

package project2;

import java.util.Scanner;

import java.lang.String;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Driver class that contains the project's implementation
 * 
 * @see NBayes
 */
public class nb extends NBayes{
	
	/**
	 * The main function for this project
	 * @param args command line arguments
	 * @throws Exception	When bad stuff happens
	 */
	public static void main(String[] args) throws Exception
	{
		mainMenu();
		
		return;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Main menu loop.
	 */
	public static void mainMenu()
	{
		Scanner scan = new Scanner(System.in);
		int input = -1;
		boolean classifInit = false;
		
		menuPrompt(classifInit);
		
		input = scan.nextInt();		
	
		// Main Loop
		while(input != 5)
		{
			if(input == 1)
			{
				menuOption1(scan);
				classifInit = true;
			}
			else if(input == 2)
			{
				if(!classifInit)
					System.out.println("A Classifier must be loaded first. Please selece option 1 or 4 first");
				else
					menuOption2(scan);				
			}
			else if(input == 3)
			{
				if(!classifInit)
					System.out.println("A Classifier must be loaded first. Please selece option 1 or 4 first");
				else
					menuOption3(scan);
			}
			else if(input == 4)
			{
				menuOption4(scan);
				classifInit = true;
			}
			
			// Re-display user prompt
			menuPrompt(classifInit);
			input = scan.nextInt();
		}		
		scan.close();
	}
	
	/**
	 * A simple user prompt for menu items. Menu output changes based on value of {@code classifInit}
	 * 
	 * @param classifInit	whether a Decision Tree is currently stored in memory
	 * TODO Change text
	 */
	public static void menuPrompt(boolean classifInit) 
	{
		if(!classifInit) 
		{
			System.out.println("Enter '1' to learn a Naive Bayes Classifier from training data.");
			System.out.println("Enter '2' to save the Classifier learned in menu item '1'. (Classifier must be initalized first)");
			System.out.println("Enter '3' to apply the Classifier to new cases. (Classifier must be initalized first)");
			System.out.println("Enter '4' to load a previously saved Classifier and apply it to new cases.");
			System.out.println("Enter '5' to quit.");
		}
		else
		{
			System.out.println("Enter '1' to learn a Naive Bayes Classifier from training data.");
			System.out.println("Enter '2' to save the Classifier learned in menu item '1'.");
			System.out.println("Enter '3' to apply the Classifier to new cases.");
			System.out.println("Enter '4' to load a previously saved Classifier and apply it to new cases.");
			System.out.println("Enter '5' to quit.");
		}
	}
	
	/**
	 * Menu Option 1: Creates a Naive Bayes Classifier from training data from a specified file.
	 * 
	 * @param sc		scanner object initialized in {@code main}
	 */
	public static void menuOption1(Scanner sc)
	{
		System.out.println("Enter training data filename (and path if not in project directry):");
		String fName = sc.next();
		
		// Create the classifier
		loadNBClassifierARFF(fName);		
		
		return;
	}
	
	/**
	 * Menu Option 2: Saves the Naive Bayes Classifier currently stored in memory to a specified file.
	 * 
	 * @param sc	scanner object initialized in {@code main}
	 */
	public static void menuOption2(Scanner sc)
	{
		System.out.println("Enter filename to save the current classifier as: ");
		String fName = sc.next();
		
		// Test if file exists
		if( Files.exists( Paths.get(fName) ) )
		{
			System.out.println(fName + " Already exists. This file will be overwritten.");
			try
			{Files.delete( Paths.get(fName) );}
			catch (Exception e)
			{
				System.out.println("Somehow a file that exists, does not exists. Classifier saving aborted. (in Menu.java: menuOption2())");
				System.err.println(e);
			}
		}
		
		try
		{ 
			weka.core.SerializationHelper.write(fName, nbClassifier);
		}
		catch (Exception e)
		{
			System.out.println("An error occured while writing the classifier to file. Classifier saving aborted. (in Menu.java: menuOption2())");
			System.err.println(e);
		}
		
		System.out.println("Naive Bayes Classifier saved.");
		
		return;
	}
	
	/**
	 * Menu Option 3: Dynamically apply the Naive Bayes Classifier to new cases specified by the user.
	 * 
	 * @param sc	scanner object initialized in {@code main}
	 */
	public static void menuOption3(Scanner sc)
	{
		System.out.println("Do you want to enter new cases manually? (y/n)");
		String input = sc.next();
		
		while(input.equals("y") || input.equals("Y"))
		{
			System.out.println("true");
			createInstanceInteractive(sc);
			
			System.out.println("\nDo you want to enter another case? (y/n)");
			input = sc.next();
		}
		
		return;
	}
	
	/**
	 * Load a previously saved Naive Bayes Classifier from a specified file and apply new cases to it dynamically.
	 * Requires classifier file as well as original data file for Attribute labeling.
	 * @param sc	scanner object initialized in {@code main}
	 */
	public static void menuOption4(Scanner sc)
	{
		System.out.println("Enter a Naive Bayes Classifier file name (and path if not in project directry):");
		String fName = sc.next();
		System.out.println("Enter the data file (.arff) that the classifier was generated from (for attribute labeling)");
		String dfName = sc.next();
		
		// Load the classifier from files
		if( !loadNBClassifierCustom(fName, dfName) )
			return;
		
		menuOption3(sc);
		return;
	}	

}
