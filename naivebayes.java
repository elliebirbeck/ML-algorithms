import java.util.*;
import java.io.*;

public class naivebayes {

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

	public static ArrayList train(String trainFilename, int beta) {

		//reads in training dataset and store class values into own arraylist
		ArrayList whole = readFile(trainFilename);
		ArrayList classValues = (ArrayList) whole.get(whole.size()-1);

		//array to store weights, default at 0
		double[] weights = new double[whole.size()-1];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}

		//calculating priors
		int count0 = beta-1;
		int count1 = beta-1;
		for (int i = 1; i < classValues.size(); i++) {
			int classValue = Integer.parseInt((String) classValues.get(i));
			if (classValue == 0) {
				count0++;
			} else {
				count1++;
			}
		}
		double total = (double) count0 + (double) count1;
		double priorY0 = (double) count0 / total;
		double priorY1 = (double) count1 / total;
		double prior = priorY1 / priorY0;

		//calculating base log odds and attribute weights
		double baseLogOdds = Math.log(priorY1/priorY0);
		int x0y0count;
		int x0y1count;
		int x1y0count;
		int x1y1count;
		double tempLogX0;
		double tempLogX1;
		for (int i = 0; i < (whole.size()-1); i++) {
			ArrayList xAttr = (ArrayList) whole.get(i);
			x0y0count = beta-1;
			x0y1count = beta-1;
			x1y0count = beta-1;
			x1y1count = beta-1;
			for (int j = 1; j < xAttr.size(); j++) {
				int xVal = Integer.parseInt((String) xAttr.get(j));
				int yVal = Integer.parseInt((String) classValues.get(j));
				if ((xVal == 0) && (yVal == 0)) {
					x0y0count++;
				} else if ((xVal == 0) && (yVal == 1)) {
					x0y1count++;
				} else if ((xVal == 1) && (yVal == 0)) {
					x1y0count++;
				} else {
					x1y1count++;
				}
			}
			double px0y0 = (double) x0y0count / (double) count0;
			double px0y1 = (double) x0y1count / (double) count1;
			double px1y0 = (double) x1y0count / (double) count0;
			double px1y1 = (double) x1y1count / (double) count1;
			tempLogX0 = Math.log(px0y1/px0y0);
			tempLogX1 = Math.log(px1y1/px1y0);
			baseLogOdds = baseLogOdds + tempLogX0;
			weights[i] = tempLogX1 - tempLogX0;
		}

		ArrayList returnObjects = new ArrayList();
		returnObjects.add(whole);
		returnObjects.add(baseLogOdds);
		returnObjects.add(weights);
		return returnObjects;

	}


	public static void test(String testFilename, double[] weights, double baseLogOdds) {

		//reads in test dataset
		ArrayList whole = readFile(testFilename);

		ArrayList temp;
		int xvalue;
		double total1 = 0;
		double total2 = 0;
		int predClass;
		int trueClass;
		int correctExamples = 0;
		int totalExamples = 0;
		ArrayList classValues = (ArrayList) whole.get(whole.size()-1);
		
		//loop through test examples
		for (int i = 1; i < classValues.size(); i++) {

			double sum1 = baseLogOdds;
			double sum2 = baseLogOdds;

			//loop through each x attribute
			for (int j = 0; j < weights.length; j++) {
				temp = (ArrayList) whole.get(j);
				xvalue = Integer.parseInt((String) temp.get(i));
				if (xvalue == 1) {
					sum1 = sum1 + weights[j];
					total1++;
				} else {
					sum2 = sum2 + weights[j];
					total2++;
				}
			}
			sum1 = sum1 / total1;
			sum2 = sum2 / total2;

			//prints probability of Y=1 to console
			System.out.println("P(Y=1|example" + i + ") = " + sum1);

			//for checking accuracy
			if (sum1>sum2) {
				predClass = 1;
			} else {
				predClass = 0;
			}
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
		int beta = Integer.parseInt(args[2]);
		String modelFilename = args[3];

		ArrayList objects = train(trainingFilename, beta);
		ArrayList whole = (ArrayList) objects.get(0);
		double baseLogOdds = (double) objects.get(1);
		double[] weights = (double[]) objects.get(2);

		//write model file
		writeFile(modelFilename, whole, weights, baseLogOdds);

		//test on the new data
		test(testFilename, weights, baseLogOdds);

	}

}	