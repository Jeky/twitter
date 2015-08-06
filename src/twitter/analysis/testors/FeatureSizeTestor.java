package twitter.analysis.testors;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import twitter.analysis.converters.DatasetConverter;
import twitter.analysis.converters.GramAttrConverter;
import twitter.data.User;
import twitter.ml.Dataset;
import twitter.ml.Evaluation;
import twitter.ml.VariableFeatureSizeDataset;
import twitter.ml.classifications.Classifier;
import twitter.ml.features.FeatureSelector;
import twitter.utils.FileLineReader;
import twitter.utils.FileLineReader.LineHandler;
import twitter.utils.Logger;

public class FeatureSizeTestor extends Testor {

	/**
	 * Feature Size Changing result will be store in a file. The filename format
	 * is: features-result-g[GRAM_SIZE]-m[METHOD_NAME]-c[CLASSIFIER_NAME].txt
	 */
	public static final String FEATURES_TEST_RESULT_FILENAME_PATTERN = TEST_PATH + "features-result-g%d-m%s-c%s.txt";

	public FeatureSizeTestor(FeatureSelector selector, Classifier classifier, int gram, int maxSize) {
		this.selector = selector;
		this.classifier = classifier;
		this.gram = gram;
		this.maxSize = maxSize;
	}

	@Override
	public void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception {
		Logger.info("Starting Feature Selection (GRAM = " + gram + ", MAX_SIZE = " + maxSize + ")");
		Logger.info("Loading Features...");
		GramAttrConverter converter = new GramAttrConverter(gram);
		Dataset spammerDataset = converter.convert(spammers, DatasetConverter.SPAMMER_CLASS_LABEL);
		Dataset nonSpammerDataset = converter.convert(nonSpammers, DatasetConverter.NONSPAMMER_CLASS_LABEL);

		ArrayList<String> features = FeatureSelectionTestor.loadFeatures(gram, selector.getClass());
		Logger.info("Feature Size = " + features.size());

		VariableFeatureSizeDataset vfsSpammerDataset = new VariableFeatureSizeDataset(spammerDataset, features);
		VariableFeatureSizeDataset vfsNonSpammerDataset = new VariableFeatureSizeDataset(nonSpammerDataset, features);

		Logger.info("Testing Feature Size Change...");
		PrintWriter writer = new PrintWriter(getFilename(gram, selector, classifier));
		for (int i = 100; i < maxSize + 1; i += 100) {
			Logger.info("Current Feature Size = " + i);
			vfsSpammerDataset.setFeatureSize(i);
			vfsNonSpammerDataset.setFeatureSize(i);
			int[][] m = Evaluation.crossValidation(classifier, vfsSpammerDataset, vfsNonSpammerDataset, 10);

			writer.print(m[0][0]);
			writer.print("\t");
			writer.print(m[0][1]);
			writer.print("\t");
			writer.print(m[1][0]);
			writer.print("\t");
			writer.print(m[1][1]);
			writer.print("\t");

			writer.print(Evaluation.getAccuracy(m));
			writer.print("\t");
			writer.print(Evaluation.getRecall(m));
			writer.print("\t");
			writer.print(Evaluation.getPrecision(m));
			writer.print("\t");
			writer.print(Evaluation.getF1(m));
			writer.print("\t");

			writer.println();
			writer.flush();
		}

		writer.close();
	}

	public static String getFilename(int gram, FeatureSelector selector, Classifier classifier) {
		return String.format(FEATURES_TEST_RESULT_FILENAME_PATTERN, gram, selector.getClass().getSimpleName(),
				classifier.getClass().getSimpleName());
	}

	private FeatureSelector selector;
	private Classifier classifier;
	private int gram;
	private int maxSize;
}
