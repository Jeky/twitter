package twitter.analysis.testors;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import twitter.data.Tweet;
import twitter.data.User;
import twitter.nlp.TextProcessor;
import twitter.utils.Counter;
import twitter.utils.Counter.CounterEntry;
import twitter.utils.IOUtils;
import twitter.utils.Logger;

public class BasicStatisticTestor extends Testor {

	@Override
	public void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception {
		Logger.info("Spammer:");

		Counter<String> spammerTokens = countTokens(spammers);
		Logger.info("Non Spammer");
		Counter<String> nonSpammerTokens = countTokens(nonSpammers);
		
		Logger.info("Spammer Token Set Count: " + spammerTokens.size());
		Logger.info("Non Spammer Token Set Count: " + nonSpammerTokens.size());

		IOUtils.saveObject(spammerTokens, TEST_PATH + "spammer-tokens.obj");
		IOUtils.saveObject(nonSpammerTokens, TEST_PATH + "nonspammer-tokens.obj");

		Set<String> intersection = new HashSet<>(spammerTokens.keySet());
		intersection.retainAll(nonSpammerTokens.keySet());
		Set<String> spammerUniqueTokens = new HashSet<>(spammerTokens.keySet());
		spammerUniqueTokens.removeAll(nonSpammerTokens.keySet());
		Set<String> nonSpammerUniqueTokens = new HashSet<>(nonSpammerTokens.keySet());
		nonSpammerUniqueTokens.removeAll(spammerTokens.keySet());

		Logger.info("Spammer Unique Token Count: " + spammerUniqueTokens.size());
		Logger.info("Non Spammer Unique Token Count: " + nonSpammerUniqueTokens.size());
		Logger.info("Shared Token Count: " + intersection.size());
		Logger.info("Total Token Count: "
				+ (spammerUniqueTokens.size() + nonSpammerUniqueTokens.size() + intersection.size()));

		Counter<String> allTokens = new Counter<>();
		for (Entry<String, Integer> e : spammerTokens.entrySet()) {
			allTokens.put(e.getKey(), e.getValue());
		}
		for (Entry<String, Integer> e : nonSpammerTokens.entrySet()) {
			allTokens.put(e.getKey(), e.getValue());
		}

		IOUtils.saveObject(allTokens, "all-tokens.obj");

		Logger.info("Collect Distribution...");
		Counter<Integer> distribution = new Counter<>();
		for (Entry<String, Integer> e : allTokens.entrySet()) {
			distribution.put(e.getValue());
		}

		List<CounterEntry<Integer>> sortedResult = distribution.getSortedResult();
		PrintWriter writer = new PrintWriter(TEST_PATH + "token-distribution.txt");
		for (CounterEntry<Integer> ce : sortedResult) {
			writer.println(ce.getKey() + "\t" + ce.getValue());
		}
		writer.close();
	}

	private Counter<String> countTokens(Map<Long, User> users) {
		Logger.info("User Count: " + users.size());
		int tweetCount = 0;
		double averageLen = 0.0;

		Counter<String> tokenCounter = new Counter<>();
		for (User u : users.values()) {
			tweetCount += u.getTweets().size();
			for (Tweet t : u.getTweets()) {
				List<String> tokens = TextProcessor.tokenize(t.getText());
				averageLen += tokens.size();
				for (String token : tokens) {
					tokenCounter.put(token);
				}
			}
		}
		averageLen = averageLen / tweetCount;
		Logger.info("Tweet Count: " + tweetCount);
		Logger.info("Average Len: " + averageLen);

		return tokenCounter;
	}

	private void printSize(Map<Long, User> users) {
	}

}
