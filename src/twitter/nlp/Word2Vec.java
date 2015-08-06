package twitter.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter.data.Sampler;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.utils.FileLineReader;
import twitter.utils.IOUtils;
import twitter.utils.FileLineReader.LineHandler;

public class Word2Vec {

	public static final String VECTOR_PATH = UserLoader.PATH + "vec/";
	public static final String SPAMMER_VECTOR = VECTOR_PATH + "spam-tweets.bin.txt";
	public static final String NONSPAMMER_VECTOR = VECTOR_PATH + "nonspam-tweets.bin.txt";
	public static final String USER_VECTOR = VECTOR_PATH + "users.obj";

	public static Map<String, List<Double>> loadSpammerVector() {
		return loadVector(SPAMMER_VECTOR);
	}

	public static Map<String, List<Double>> loadNonSpammerVector() {
		return loadVector(NONSPAMMER_VECTOR);
	}

	public static Map<Long, UserVector> convertUserVectorMap() {
		Map<String, List<Double>> spammerVector = loadSpammerVector();
		Map<String, List<Double>> nonSpammerVector = loadNonSpammerVector();
		Map<Long, User> spammers = UserLoader.loadSpammers();
		Map<Long, User> nonSpammers = Sampler.loadSampledUsers();

		Map<Long, UserVector> userVectorMap = new HashMap<>();
		int vectorLen = spammerVector.values().iterator().next().size();

		fillUsers(userVectorMap, true, vectorLen, spammerVector, spammers);
		fillUsers(userVectorMap, false, vectorLen, nonSpammerVector, nonSpammers);

		IOUtils.saveObject(userVectorMap, USER_VECTOR);
		return userVectorMap;
	}

	public static Map<Long, UserVector> loadUserVectorMap() {
		return (Map<Long, UserVector>) IOUtils.loadObject(USER_VECTOR);
	}

	public static void fillUsers(Map<Long, UserVector> userVectorMap, boolean spammer, int vectorLen,
			Map<String, List<Double>> wordVectorMap, Map<Long, User> users) {
		for (User u : users.values()) {
			double[] vector = new double[vectorLen];
			for (Tweet t : u.getTweets()) {
				List<String> words = TextProcessor.tweetToGram(t, 1);
				for (String w : words) {
					if(wordVectorMap.containsKey(w)){
						List<Double> wordVector = wordVectorMap.get(w);
						for (int i = 0; i < vectorLen; i++) {
							vector[i] += wordVector.get(i);
						}
					}
				}
			}
			UserVector userVector = new UserVector(u.getId(), spammer, vector);

			userVectorMap.put(u.getId(), userVector);
		}
	}

	public static Map<String, List<Double>> loadVector(String filename) {
		return new FileLineReader<>(filename, new VectorFileHandler()).read();
	}

	private static class VectorFileHandler implements LineHandler<Map<String, List<Double>>> {

		public VectorFileHandler() {
			vectorMap = new HashMap<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			if (i != 0) {
				String[] split = line.split(" ");
				String word = split[0];
				List<Double> vector = new ArrayList<>();
				for (int j = 1; j < split.length; j++) {
					vector.add(Double.parseDouble(split[j]));
				}

				vectorMap.put(word, vector);
			}

			return true;
		}

		@Override
		public Map<String, List<Double>> getResult() {
			return vectorMap;
		}

		private Map<String, List<Double>> vectorMap;
	}
}
