import java.util.*;
import java.io.*;

public class id3 {

	public static double calcEntropy(ArrayList<String> data) {
		double entropy = 0;
		int count = 0;
		for (int i = 1; i < data.size(); i++) {
			int temp = Integer.parseInt(data.get(i));
			if(temp==1) {
				count++;
			}
		}
		
		if (count==0) {
			entropy=0;
		} else {
			double ratio = (double) count / (double) (data.size()-1);
			double ratio2 = (double) (data.size()-1 - count) / (double) (data.size()-1);
			entropy = (-ratio * (Math.log(ratio) / Math.log(2)))+(-ratio * (Math.log(ratio) / Math.log(2)));
		}
		
		return entropy;
	}

	public static double calcIG(double entropyRoot, ArrayList<String> splitAttr, ArrayList<String> classAttr) {
		
		// System.out.println("Class1: " + classAttr.get(1));
		// System.out.println("Split: " + splitAttr.get(0));
		// System.out.println("Split1: " + splitAttr.get(1));
		// System.out.println("Split size: " + splitAttr.size());

		int positiveSize = 0;
		int positiveCount = 0;
		int negativeSize = 0;
		int negativeCount = 0;
		int counting = 0;

		for (int i = 1; i < splitAttr.size(); i++) {

			int temp = Integer.parseInt(splitAttr.get(i));
			int temp2 = Integer.parseInt(classAttr.get(i));

			if(temp==1) {
				positiveSize++;
				if(temp2==1) {
					positiveCount++;
				}
			} else {
				negativeSize++;
				if (temp2==1) {
					negativeCount++;
				}
			}

		}

		double aRatio = (double) positiveCount / (double) positiveSize;
		double aRatio2 = (double) (positiveSize-positiveCount) / (double) positiveSize;
		double aEntropy = (-aRatio * (Math.log(aRatio) / Math.log(2)))+(-aRatio2 * (Math.log(aRatio2) / Math.log(2)));
		double a1 = ((double)positiveSize / (double)(splitAttr.size()-1)) * aEntropy; 
		
		double bRatio = (double) negativeCount / (double) negativeSize;
		double bRatio2 = (double) (negativeSize-negativeCount) / (double) negativeSize;
		double bEntropy = (-bRatio * (Math.log(bRatio) / Math.log(2)))+(-bRatio2 * (Math.log(bRatio2) / Math.log(2)));
		double b1 = ((double)negativeSize / (double)(splitAttr.size()-1)) * bEntropy; 
		
		double infGain = entropyRoot - a1 - b1;

		return infGain;
	}


	public static ArrayList readFile(String filename) {
		
		BufferedReader reader = null;
		String line = "";
		ArrayList whole = new ArrayList();

		try {

			reader = new BufferedReader(new FileReader(filename));
			ArrayList temp = new ArrayList();
			int k = 0;

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


	public static void main(String[] args) {
		
		String trainingFilename = args[0];
		String testFilename = args[1];
		String modelFilename = args[2];

		ArrayList whole = readFile(trainingFilename);
		ArrayList classAttr = (ArrayList) whole.get(whole.size()-1);
		System.out.println("class: " + classAttr);
		double rootEntropy = calcEntropy(classAttr);
		System.out.println("entropy: " + calcEntropy(classAttr));

		ArrayList splitAttr = (ArrayList) whole.get(19);
		double ig = calcIG(rootEntropy, splitAttr, classAttr);
		System.out.println("inf gain: " + ig);

	}


}