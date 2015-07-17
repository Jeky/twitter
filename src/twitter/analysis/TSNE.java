package twitter.analysis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.nlp.TextProcessor;
import twitter.nlp.TweetsGramLoader;
import twitter.utils.Counter.CounterEntry;
import twitter.utils.FileLineReader;
import twitter.utils.FileLineReader.LineHandler;
import twitter.utils.Logger;
import twitter.utils.Utils;

public class TSNE {
	/**
	 * This will print out the user attributes in the following format:
	 * UID\tIS_SPAMMER\tURL_PROP
	 */
	public static void printUserAtt() throws Exception {
		Logger.info("Print User Attributes");
		int gramLen = 1;
		Map<Long, List<CounterEntry<String>>> spammerGramFeatures = TweetsGramLoader.loadSpammerGramFeatures(gramLen);
		Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures = TweetsGramLoader
				.loadNonSpammerGramFeatures(gramLen);

		long[] ids = collectIdList(spammerGramFeatures, nonSpammerGramFeatures);

		Map<Long, User> spammers = UserLoader.loadSpammers();
		Map<Long, User> nonSpammers = Sampler.loadSampledUsers();

		PrintWriter writer = new PrintWriter("att-java.txt");
		for (long id : ids) {
			User u = null;
			int isSpammer = 0;
			if (spammers.containsKey(id)) {
				u = spammers.get(id);
				isSpammer = 1;
			}
			if (nonSpammers.containsKey(id)) {
				u = nonSpammers.get(id);
			}

			double url = 0.0;
			for (Tweet t : u.getTweets()) {
				if (!Utils.isEmptyStr(t.getUrl())) {
					url += 1.0;
				}
			}
			url /= u.getTweets().size();

			writer.printf("%d\t%d\t%.10f\n", id, isSpammer, url);
		}

		writer.close();
	}

	public static void selectedFeatureGramTo2D() throws Exception {
		Logger.info("Reducing Dimension of Selected Feature Grams");
		int gramLen = 1;

		Logger.info("Loading " + gramLen + "-Gram Features...");
		Map<Long, List<CounterEntry<String>>> spammerGramFeatures = TweetsGramLoader.loadSpammerGramFeatures(gramLen);
		Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures = TweetsGramLoader
				.loadNonSpammerGramFeatures(gramLen);

		Logger.info("Loading Selected Feature Grams...");
		Set<String> spammerSelectedFeatures = loadSelectedFeatures(UserLoader.PATH + "spammer-feature-" + gramLen
				+ "-mi.txt", 10000);
		Set<String> nonSpammerSelectedFeatures = loadSelectedFeatures(UserLoader.PATH + "nonspammer-feature-" + gramLen
				+ "-mi.txt", 10000);

		Logger.info("Converting to 2D...");

		Logger.info("Collecting Vocabulary");
		Map<String, Integer> vocabulary = new LinkedHashMap<>();
		for (String s : spammerSelectedFeatures) {
			if (!vocabulary.containsKey(s)) {
				vocabulary.put(s, vocabulary.size());
			}
		}
		for (String s : nonSpammerSelectedFeatures) {
			if (!vocabulary.containsKey(s)) {
				vocabulary.put(s, vocabulary.size());
			}
		}
		Logger.info("Total Gram Count: " + vocabulary.size());

		Logger.info("Converting Grams to High Dimension Matrix...");
		double[][] gramMatrix = fillGramMatrix(spammerGramFeatures, nonSpammerGramFeatures, vocabulary);

		String filename = "user-selected-20000-features-" + gramLen + ".dat";
		Logger.info("Saving Data to " + filename);
		PrintWriter writer = new PrintWriter(filename);
		for (int i = 0; i < gramMatrix.length; i++) {
			for (int j = 0; j < gramMatrix[0].length - 1; j++) {
				writer.print(gramMatrix[i][j] + "\t");
			}
			writer.println(gramMatrix[i][gramMatrix[0].length - 1]);
		}
		writer.close();
	}

	public static void userGramsTo2D() throws Exception {
		Logger.info("Reducing Dimension of User Grams");
		userNGramsTo2D(1);
	}

	public static Set<String> loadSelectedFeatures(String filename, int count) {
		return new FileLineReader<>(filename, new SelectedFeatureFileHandler(count)).read();
	}

	private static class SelectedFeatureFileHandler implements LineHandler<Set<String>> {

		public SelectedFeatureFileHandler(int count) {
			this.count = count;
			features = new HashSet<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			features.add(line.split("\t")[0]);
			return features.size() != count;
		}

		@Override
		public Set<String> getResult() {
			return features;
		}

		private Set<String> features;
		private int count;

	}

	private static void userNGramsTo2D(int gramLen) throws IOException {
		Logger.info("Loading " + gramLen + "-Gram Features...");
		Map<Long, List<CounterEntry<String>>> spammerGramFeatures = TweetsGramLoader.loadSpammerGramFeatures(gramLen);
		Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures = TweetsGramLoader
				.loadNonSpammerGramFeatures(gramLen);

		Logger.info("Converting to 2D...");

		Logger.info("Collecting Vocabulary");
		Map<String, Integer> vocabulary = new LinkedHashMap<>();
		collectVocabulary(spammerGramFeatures, vocabulary);
		collectVocabulary(nonSpammerGramFeatures, vocabulary);
		Logger.info("Total Gram Count: " + vocabulary.size());

		Logger.info("Converting Grams to High Dimension Matrix...");
		double[][] gramMatrix = fillGramMatrix(spammerGramFeatures, nonSpammerGramFeatures, vocabulary);

		String filename = "user-full-features-" + gramLen + ".dat";
		Logger.info("Saving Data to " + filename);
		PrintWriter writer = new PrintWriter(filename);
		for (int i = 0; i < gramMatrix.length; i++) {
			for (int j = 0; j < gramMatrix[0].length - 1; j++) {
				writer.print(gramMatrix[i][j] + "\t");
			}
			writer.println(gramMatrix[i][gramMatrix[0].length - 1]);
		}
		writer.close();
	}

	private static double[][] fillGramMatrix(Map<Long, List<CounterEntry<String>>> spammerGramFeatures,
			Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures, Map<String, Integer> vocabulary) {
		int row = nonSpammerGramFeatures.size() + spammerGramFeatures.size();
		int column = vocabulary.size();
		Logger.info("Filling Matrix: " + row + " x " + column);
		double[][] gramMatrix = new double[row][column];
		long[] idList = collectIdList(spammerGramFeatures, nonSpammerGramFeatures);

		for (int i = 0; i < gramMatrix.length / 2; i++) {
			for (CounterEntry<String> ce : spammerGramFeatures.get(idList[i])) {
				if (vocabulary.containsKey(ce.getKey())) {
					int index = vocabulary.get(ce.getKey());
					int value = ce.getValue();
					gramMatrix[i][index] = value;
				}
			}
		}

		for (int i = gramMatrix.length / 2; i < gramMatrix.length; i++) {
			for (CounterEntry<String> ce : nonSpammerGramFeatures.get(idList[i])) {
				if (vocabulary.containsKey(ce.getKey())) {
					int index = vocabulary.get(ce.getKey());
					int value = ce.getValue();
					gramMatrix[i][index] = value;
				}
			}
		}
		return gramMatrix;
	}

	private static long[] collectIdList(Map<Long, List<CounterEntry<String>>> spammerGramFeatures,
			Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures) {
		long[] idList = new long[spammerGramFeatures.size() + nonSpammerGramFeatures.size()];
		int index = 0;
		for (long id : spammerGramFeatures.keySet()) {
			idList[index] = id;
			index++;
		}
		for (long id : nonSpammerGramFeatures.keySet()) {
			idList[index] = id;
			index++;
		}

		return idList;
	}

	private static void collectVocabulary(Map<Long, List<CounterEntry<String>>> gramFeatures,
			Map<String, Integer> vocabulary) {
		for (List<CounterEntry<String>> lce : gramFeatures.values()) {
			for (CounterEntry<String> ce : lce) {
				if (!vocabulary.containsKey(ce.getKey())) {
					vocabulary.put(ce.getKey(), vocabulary.size());
				}
			}
		}
	}
}
