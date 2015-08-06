package twitter.ml;

import twitter.ml.classifications.Classifier;
import twitter.utils.Logger;

public class Evaluation {

	public static int[][] crossValidation(Classifier classifier, Dataset d1, Dataset d2, int foldN) {
		d1.shuffle();
		d2.shuffle();

		int[] folds1 = computeFolds(d1.size(), foldN);
		int[] folds2 = computeFolds(d2.size(), foldN);

		int[][] confusionMatrix = new int[2][2];
		double posCls = d1.getInstance(0).getClassValue();

		for (int i = 0; i < foldN; i++) {
			Dataset trainingDataset = mergeTrainingDataset(d1, d2, folds1, folds2, i);
			Dataset testingDataset = mergeTestingDataset(d1, d2, folds1, folds2, i);
			trainingDataset.shuffle();
			testingDataset.shuffle();
			
			classifier.reset();
			classifier.train(trainingDataset);
			
			for (Instance instance : testingDataset) {
				double cls = classifier.classify(instance);
				if (instance.getClassValue() == posCls) {
					if (cls == posCls) {
						confusionMatrix[0][0]++;
					} else {
						confusionMatrix[0][1]++;
					}
				} else {
					if (cls == posCls) {
						confusionMatrix[1][0]++;
					} else {
						confusionMatrix[1][1]++;
					}
				}
			}
		}

		return confusionMatrix;
	}

	public static double getAccuracy(int[][] m) {
		return 1.0 * (m[0][0] + m[1][1]) / (m[0][0] + m[0][1] + m[1][0] + m[1][1]);
	}

	public static double getRecall(int[][] m) {
		return 1.0 * m[0][0] / (m[1][0] + m[0][0]);
	}
	
	public static double getPrecision(int[][] m ){
		return 1.0 * m[0][0] / (m[0][1] + m[0][0]);
	}
	
	public static double getF1(int [][] m){
		double recall = getRecall(m);
		double precision = getPrecision(m);
		return 2.0 * recall * precision / (recall + precision);
	}

	public static void showResult(int[][] m) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			builder.append(m[i][0]).append("\t").append(m[i][1]).append("\n");
		}

		double acc = getAccuracy(m);
		double recall = getRecall(m);
		double precision = getPrecision(m);
		double f1 = getF1(m);

		Logger.info("Confusion Matrix:\n" + builder);
		Logger.info("Accuracy: " + acc);
		Logger.info("Recall: " + recall);
		Logger.info("Precision: " + precision);
		Logger.info("F1: " + f1);
	}

	private static Dataset mergeTrainingDataset(Dataset d1, Dataset d2, int[] folds1, int[] folds2, int index) {
		Dataset d = new Dataset();
		d.setClassLabel(d1.getClassLabel());
		for (int i = 0; i < folds1[index]; i++) {
			d.addInstance(d1.getInstance(i));
		}
		for (int i = folds1[index + 1]; i < d1.size(); i++) {
			d.addInstance(d1.getInstance(i));
		}
		for (int i = 0; i < folds2[index]; i++) {
			d.addInstance(d2.getInstance(i));
		}
		for (int i = folds2[index + 1]; i < d2.size(); i++) {
			d.addInstance(d2.getInstance(i));
		}

		return d;
	}

	private static Dataset mergeTestingDataset(Dataset d1, Dataset d2, int[] folds1, int[] folds2, int index) {
		Dataset d = new Dataset();
		d.setClassLabel(d1.getClassLabel());
		for (int i = folds1[index]; i < folds1[index + 1]; i++) {
			d.addInstance(d1.getInstance(i));
		}
		for (int i = folds2[index]; i < folds2[index + 1]; i++) {
			d.addInstance(d2.getInstance(i));
		}

		return d;
	}

	public static int[] computeFolds(int totalSize, int foldN) {
		int[] folds = new int[foldN + 1];
		int each = totalSize / foldN;

		for (int i = 0; i < foldN; i++) {
			folds[i] = each * i;
		}

		for (int i = 0; i < totalSize % each; i++) {
			folds[i + 1] += i + 1;
		}

		folds[foldN] = totalSize;

		return folds;
	}

}
