package twitter.classification;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import twitter.data.User;
import weka.core.Instances;

public class Classification {
	public static Instances toInstancesWithTweets(Map<Long, User> spammers, Map<Long, User> nonSpammers, int gram,
			int acceptedGramFeatureLength) {
		int capacity = spammers.size() + nonSpammers.size();
		String name = "twitter";
		Set<String> attributes = new HashSet<>();

		// add basic features

		// add tweet features

		return null;
	}

	public static Instances toInstancesWithTweets(Map<Long, User> spammers, Map<Long, User> nonSpammers, int gram) {
		return toInstancesWithTweets(spammers, nonSpammers, gram, -1);
	}
}