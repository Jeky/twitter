package twitter.nlp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter.analysis.Sampler;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.utils.Counter;
import twitter.utils.IOUtils;
import twitter.utils.Logger;
import twitter.utils.Counter.CounterEntry;

public class TweetsGramLoader {
	
	public static final String SPAMMER_TWEET_GRAMS = UserLoader.PATH + "spammer-grams.obj";
	public static final String NONSPAMMER_TWEETS_GRAMS = UserLoader.PATH + "nonspammer-grams.obj";

	public static List<CounterEntry<String>> loadSpammerGramFeatures(){
		return (List<CounterEntry<String>>) IOUtils.loadObject(SPAMMER_TWEET_GRAMS);
	}

	public static List<CounterEntry<String>> loadNonSpammerGramFeatures(){
		return (List<CounterEntry<String>>) IOUtils.loadObject(NONSPAMMER_TWEETS_GRAMS);
	}
	
	public static void collectSpammerGrams() {
		Logger.info("Start Analyzing Spammer Grams...");
		collectAllGrams(UserLoader.loadSpammers(), SPAMMER_TWEET_GRAMS);
		Logger.info("Finished...");
	}

	public static void collectNonSpammerGrams() {
		Logger.info("Start Analyzing NonSpammer Grams...");
		collectAllGrams(Sampler.loadSampledUsers(), NONSPAMMER_TWEETS_GRAMS);
		Logger.info("Finished...");
	}

	public static void collectAllGrams(Map<Long, User> users, String filename) {
		Map<Long, List<CounterEntry<String>>> gramFeatures = new HashMap<>();
		
		for (int i = 1; i <= 3; i++) {
			Logger.info("Collecting Grams: N = " + i);
			collectGrams(gramFeatures, users, i, filename);
		}

		IOUtils.saveObject(gramFeatures, filename);
	}

	public static void collectGrams(Map<Long, List<CounterEntry<String>>> gramFeatures, Map<Long, User> users, int n, String filename) {
		int i = 0;
		for (User u : users.values()) {
			if (i % 100 == 0) {
				Logger.info(i + " Users Processed");
			}
			Counter<String> counter = new Counter<>();
			for (Tweet t : u.getTweets()) {
				TextProcessor.countGramsInTweet(t, counter, n);
			}

			gramFeatures.put(u.getId(), counter.getSortedResult());
			i++;
		}
	}
}
