import java.util.*;
import java.io.*;

public class perceptron {
	
	static int maxIteration = 100;
	static double learningRate = 0.01;

	public static ArrayList readFile(String filename) {
		
		BufferedReader reader = null;
		String line = "";
		ArrayList whole = new ArrayList();

		try {

			reader = new BufferedReader(new FileReader(filename));
			ArrayList temp = new ArrayList();
			int k = 0;

			//creates a list of lists to store the dataset
			while ((line=reader.readLine()) != null) {
				String[] value = line.split(",");
				k = value.length;
				temp.add(value[0]);
			}
			whole.add(temp);

			for (int j = 1; j < k; j++) {
				reader = new BufferedReader(new FileReader(filename));
				ArrayList newtemp = new ArrayList();
				while ((line=reader.readLine()) != null) {
					String[] value = line.split(",");
					newtemp.add(value[j]);
				}
				whole.add(newtemp);
			}

			reader.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: " + e);
		} catch (IOException e) {
			System.out.println("IO Exception: " + e);
		}

		return whole;

	}

	public static void writeFile(String filename, ArrayList whole, double[] weights, double bias) {

		try {

			File file = new File(filename);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(bias + "\n");
			for (int i = 0; i < weights.length; i++) {
				ArrayList temp = (ArrayList) whole.get(i);
				String xname = (String) temp.get(0);
				bw.write(xname + " " + weights[i] + "\n");
			}
			bw.close();

		} catch (IOException e) {
			System.out.println("IO Exception: " + e);
		}	

	}	

	public static int activation(ArrayList whole, double[] weights, double bias, int instance) {

		//calculate sum for activation function
		double sum = 0;
		Integer xvalue;
		ArrayList temp;
		for (int i = 0; i < weights.length; i++) {
			temp = (ArrayList) whole.get(i);
			xvalue = Integer.parseInt((String) temp.get(instance));
			sum = sum + bias + xvalue*weights[i];
		}

		//if sum is positive then return 1, if negative return 0
		if (sum>=0) {
			return 1;
		}
		else {
			return 0;
		}

	}

	public static ArrayList function(String inFilename, boolean isTestData) {

		//reads in training dataset
		ArrayList whole = readFile(inFilename);

		//create array to store weights, set default as 0
		double[] weights = new double[whole.size()-1];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}

		//store class values into own arraylist (for ease)
		ArrayList classValues = (ArrayList) whole.get(whole.size()-1);

		//create variable for bias, set default as 0
		double bias = 0;

		
		int predResult;
		int trueResult;
		int count = 0;
		boolean converged;

		do {

			converged = true;

			for (int i = 1; i < ((ArrayList) whole.get(0)).size() - 1; i++) {

				//calculates activation value
				predResult = activation(whole, weights, bias, i);
				
				//get true class value
				trueResult = Integer.parseInt((String) classValues.get(i));
				
				//updates weights and bias if prediction is not correct
				if (predResult != trueResult) {

					converged = false;

					for (int j = 0; j < weights.length; j++) {
						ArrayList temp = (ArrayList) whole.get(j);
						int x = Integer.parseInt((String) temp.get(i));
						weights[j] = weights[j] + learningRate*(trueResult-predResult)*x;
					}

					bias = bias + learningRate*(trueResult-predResult);
				}

			}

			if (converged == true) {
				break;
			}

			count++;

		} while(count < maxIteration);

		//if the dataset is test data, information is printed to console
		if (isTestData) {

			int totalExamples = ((ArrayList) whole.get(0)).size() - 1;
			int correctExamples = 0;

			for (int j = 1; j < ((ArrayList) whole.get(0)).size() - 1; j++) {

				//print out activations
				int actvn = activation(whole, weights, bias, j);
				System.out.println("Test data: example " + j + " activation = " + actvn);

				//get real value to calculate overall accuracy
				int realValue = Integer.parseInt((String) classValues.get(j));
				if (actvn == realValue) {
					correctExamples++;
				}

			}	
	
			double accuracy = (double) correctExamples / (double) totalExamples;

			System.out.println("Total accuracy = " + accuracy*100 + "%");
		}

		ArrayList returnObjects = new ArrayList();
		returnObjects.add(whole);
		returnObjects.add(weights);
		returnObjects.add(bias);
		return returnObjects;

	}

	public static void main(String[] args) {

		//arguments from command line
		String trainingFilename = args[0];
		String testFilename = args[1];
		String modelFilename = args[2];

		ArrayList objects = function(trainingFilename, false);
		ArrayList whole = (ArrayList) objects.get(0);
		double[] weights = (double[]) objects.get(1);
		double bias = (double) objects.get(2);

		//writes resulting weights and bias to txt file
		writeFile(modelFilename, whole, weights, bias);

		ArrayList testData = function(testFilename, true);

	}




}