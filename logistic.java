import java.util.*;
import java.io.*;

public class logistic {


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

	public static double classify(ArrayList whole, double[] weights, double bias, int instance) {

		double sum = 0;
		Integer xvalue;
		ArrayList temp;

		for (int i = 0; i < weights.length; i++) {
			temp = (ArrayList) whole.get(i);
			xvalue = Integer.parseInt((String) temp.get(instance));
			sum = sum + bias + xvalue*weights[i];
		}

		double sigmoid = 1 / (1 + Math.exp(-sum));

		return sigmoid;
	}

	public static ArrayList train(String trainFilename, double learningRate, double lambda) {

		//reads in training dataset
		ArrayList whole = readFile(trainFilename);

		//create array to store weights, set default as 0
		double[] weights = new double[whole.size()-1];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}

		//store class values into own arrraylist (just for ease)
		ArrayList classValues = (ArrayList) whole.get(whole.size()-1);

		//create variable for bias, set default as 0
		double bias = 0;

		double predResult;
		int trueResult;
		double liklihood = 0.0;
		boolean converged = false;
		int count = 0;

		do {

			for(int i = 1; i < ((ArrayList) whole.get(0)).size() - 1; i++) {

				//calculates prediction
				predResult = classify(whole, weights, bias, i);

				//get true class value
				trueResult = Integer.parseInt((String) classValues.get(i));

				//update weights and bias
				for (int j = 0; j < weights.length; j++) {
					ArrayList temp = (ArrayList) whole.get(j);
					int x = Integer.parseInt((String) temp.get(i));
					weights[j] = weights[j] + learningRate*(trueResult-predResult)*x;
				}
				bias = bias + learningRate*(trueResult-predResult);

				//take logs to simplify for liklihood
				liklihood = liklihood + (trueResult*Math.log(predResult)) + ((1-trueResult)*Math.log(1-predResult));

			}
			count++;
			
			if (converged == true) {
				break;
			}

		} while (count < 100);

		//variables to return
		ArrayList returnObjects = new ArrayList();
		returnObjects.add(whole);
		returnObjects.add(weights);
		returnObjects.add(bias);
		returnObjects.add(liklihood);
		return returnObjects;

	}

	public static void test(String testFilename, double[] weights, double bias, double liklihood) {

		//reads in test dataset
		ArrayList whole = readFile(testFilename);

		//used to calculate accuracy
		int correctExamples = 0;
		int totalExamples = 0;
		int predClass;
		int trueClass;
		ArrayList classValues = (ArrayList) whole.get(whole.size()-1);
		

		for (int i = 1; i < classValues.size(); i++) {

			//gets prediction of test data
			double prediction = classify(whole, weights, bias, i);

			//prints probability of Y=1 to console
			System.out.println("P(Y=1|example" + i + ") = " + prediction);

			//classifies prediction
			if(prediction > 0.5) {
				predClass = 1;
			} else {
				predClass = 0;
			}

			//checks accuracy
			trueClass = Integer.parseInt((String) classValues.get(i));
			if (predClass == trueClass) {
				correctExamples++;
			}
			totalExamples++;
		}

		double accuracy = (double) correctExamples / (double) totalExamples;
		System.out.println("Overall accuracy = " + accuracy*100 + "%");

	}

	public static void main(String[] args) {

		//arguments from command line
		String trainingFilename = args[0];
		String testFilename = args[1];
		String modelFilename = args[4];

		double learningRate = Double.parseDouble(args[2]);
		double standardDev = Double.parseDouble(args[3]);
		double lambda = 1 / standardDev*standardDev;

		ArrayList objects = train(trainingFilename, learningRate, lambda);
		ArrayList whole = (ArrayList) objects.get(0);
		double[] weights = (double[]) objects.get(1);
		double bias = (double) objects.get(2);
		double liklihood = (double) objects.get(3);

		//write model file
		writeFile(modelFilename, whole, weights, bias);

		//test on the new data
		test(testFilename, weights, bias, liklihood);

	}

}