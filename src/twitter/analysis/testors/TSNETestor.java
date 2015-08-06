package twitter.analysis.testors;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import twitter.analysis.converters.DatasetConverter;
import twitter.analysis.converters.GramAttrConverter;
import twitter.data.User;
import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.classifications.Classifier;
import twitter.ml.features.FeatureSelector;
import twitter.utils.FileLineReader;
import twitter.utils.Logger;
import twitter.utils.FileLineReader.LineHandler;
import twitter.utils.Utils;

public class TSNETestor extends Testor {

	public static final String FEATURE_MATRIX_FILENAME_PATTERN = TEST_PATH + "features-matrix-g%d-m%d.txt";
	public static final String FEATURE_MATRIX_2D_FILENAME_PATTERN = TEST_PATH + "features-2d-g%d-m%d.txt";

	public TSNETestor(Class<? extends FeatureSelector> selectorClass, int gram, int maxSize) {
		this.selectorClass = selectorClass;
		this.gram = gram;
		this.maxSize = maxSize;
	}

	public static String getFeatureMatrixFileName(int gram, int maxSize) {
		return String.format(FEATURE_MATRIX_FILENAME_PATTERN, gram, maxSize);
	}

	@Override
	public void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception {
		Logger.info("Starting Converting To 2D (GRAM = " + gram + ", MAX_SIZE = " + maxSize + ")");
		Logger.info("Loading Features...");
		GramAttrConverter converter = new GramAttrConverter(gram);
		Dataset spammerDataset = converter.convert(spammers, DatasetConverter.SPAMMER_CLASS_LABEL);
		Dataset nonSpammerDataset = converter.convert(nonSpammers, DatasetConverter.NONSPAMMER_CLASS_LABEL);

		ArrayList<String> features = FeatureSelectionTestor.loadFeatures(gram, selectorClass);

		Logger.info("Filling Feature Matrix...");

		String filename = getFeatureMatrixFileName(gram, maxSize);
		Logger.info("Writing Feature Matrix to " + filename);
		PrintWriter writer = new PrintWriter(filename);
		
		fillMatrix(writer, spammerDataset, features);
		fillMatrix(writer, nonSpammerDataset, features);
		
		writer.close();
	}

	private void fillMatrix(PrintWriter writer, Dataset dataset, ArrayList<String> features) throws Exception {
		StringBuilder builder = new StringBuilder();

		int count = 0;
		for (Instance instance : dataset) {
			if (count % 10000 == 0) {
				Logger.info("Processed " + count + " users");
			}
			for (int i = 0; i < maxSize; i++) {
				String f = features.get(i);
				builder.append(instance.getValue(f)).append("\t");
			}
			builder.deleteCharAt(builder.length() - 1);
			writer.println(builder);
			builder.setLength(0);
			count++;
		}
	}

	private Class<? extends FeatureSelector> selectorClass;
	private int gram;
	private int maxSize;
}
