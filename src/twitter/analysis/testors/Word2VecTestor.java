package twitter.analysis.testors;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter.data.Tweet;
import twitter.data.User;
import twitter.utils.FileLineReader.LineHandler;
import twitter.utils.Logger;

public class Word2VecTestor extends Testor {

	public static final String TWEETS = TEST_PATH + "tweets.txt";
	@Override
	public void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception {
		Logger.info("Merging all tweets into " + TWEETS);
		PrintWriter writer = new PrintWriter(TWEETS);
		fillTweets(writer, spammers);
		fillTweets(writer, nonSpammers);
	}

	private void fillTweets(PrintWriter writer, Map<Long, User> dataset) {
		int i = 0;
		for (User u : dataset.values()) {
			if (i % 10000 == 0) {
				Logger.info("Processed " + i + " users");
			}
			for (Tweet t : u.getTweets()) {
				writer.println(t.getText());
			}
			i++;
		}
	}

}
