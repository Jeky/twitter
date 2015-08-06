package twitter.analysis.testors;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter.analysis.converters.DatasetConverter;
import twitter.analysis.converters.GramAttrConverter;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.features.FeatureSelector;
import twitter.nlp.TextProcessor;
import twitter.utils.FileLineReader;
import twitter.utils.Logger;
import twitter.utils.FileLineReader.LineHandler;

public class FeatureSelectionTestor extends FileLoggerTestor {

	/**
	 * Selected Feature will be store in a file. The filename format is:
	 * [spammer/nonspammer]-features-g[GRAM_SIZE]-m[METHOD_NAME].txt
	 */
	public static final String TOP_FEATURES_FILENAME_PATTERN = TEST_PATH + "%s-features-g%d-m%s.txt";

	public FeatureSelectionTestor(FeatureSelector selector, int maxSize, int gram) {
		this.selector = selector;
		this.maxSize = maxSize;
		this.gram = gram;
	}

	@Override
	public void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception {
		Logger.info("Starting Feature Selection (GRAM = " + gram + ", MAX_SIZE = " + maxSize + ")");

		GramAttrConverter converter = new GramAttrConverter(gram);
		Dataset spammerDataset = converter.convert(spammers, DatasetConverter.SPAMMER_CLASS_LABEL);
		Dataset nonSpammerDataset = converter.convert(nonSpammers, DatasetConverter.NONSPAMMER_CLASS_LABEL);

		Dataset all = Dataset.merge(spammerDataset, nonSpammerDataset);
		Logger.info("Training Feature Selector...");
		selector.train(all);

		// save selected features
		print(getFilename(gram, selector.getClass()), selector, DatasetConverter.SPAMMER_CLASS_LABEL);
	}

	public static ArrayList<String> loadFeatures(int gram, Class<? extends FeatureSelector> selectorClass) {
		final ArrayList<String> features = new ArrayList<>(3500000);
		new FileLineReader<ArrayList<String>>(getFilename(gram, selectorClass), new LineHandler<ArrayList<String>>() {

			@Override
			public boolean readLine(int i, String line) {
				features.add(line.split("\t")[0]);
				return true;
			}

			@Override
			public ArrayList<String> getResult() {
				return null;
			}
		}).read();
		return features;
	}

	public static String getFilename(int gram, Class<? extends FeatureSelector> selectorClass) {
		return String.format(TOP_FEATURES_FILENAME_PATTERN, "all", gram, selectorClass.getSimpleName());
	}

	private void print(String filename, FeatureSelector selector, double cls) throws Exception {
		PrintWriter writer = new PrintWriter(filename);
		Logger.info("Writing Result to " + filename);
		List<String> top = null;
		if (maxSize == 0) {
			top = selector.getAll(cls);
		} else {
			top = selector.getTop(cls, maxSize);
		}
		for (String w : top) {
			writer.println(w + "\t" + selector.getResult(cls).get(w));
		}
		writer.close();
	}

	private final FeatureSelector selector;
	private final int maxSize;
	private final int gram;
}
