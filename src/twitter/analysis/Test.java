package twitter.analysis;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twitter.analysis.testors.BasicStatisticTestor;
import twitter.analysis.testors.UserVectorClassificationTestor;
import twitter.analysis.testors.Word2VecTestor;
import twitter.data.NewDatasetLoader;
import twitter.data.Sampler;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.utils.Counter;
import twitter.utils.IOUtils;
import twitter.utils.Logger;

public class Test {
	public static final String PATH = "/Users/jeky/data/thesis/final/";

	public static void printTweets(Map<Long, User> users, String idFile, String tweetsDir, boolean load)
			throws Exception {
		Logger.info("Achieved Tweets to " + tweetsDir);
		Logger.info("Total User : " + users.size());
		int i = 0;
		int tweetCount = 0;
		PrintWriter writer = new PrintWriter(PATH + idFile);
		for (User u : users.values()) {
			if (i % 1000 == 0 && i != 0) {
				Logger.info("Processed " + i + " users");
				Logger.info("Total Tweets: " + tweetCount);
			}

			List<Tweet> tweets = null;
			if (load) {
				tweets = UserLoader.loadTweet(u.getId());
			} else {
				tweets = u.getTweets();
			}

			writer.println(u.getId() + "\t" + u.getName() + "\t" + u.getTweets().size());
			if (!tweets.isEmpty()) {
				PrintWriter tweetsWriter = new PrintWriter(PATH + tweetsDir + u.getId());
				for (Tweet t : tweets) {
                    if(t.getOrigin()!= null && !t.getOrigin().trim().equals("")){
					    tweetsWriter.println(t.getOrigin());
                    }else{
                        tweetsWriter.println(t.getText());
                    }
				}
				tweetCount += tweets.size();
				tweetsWriter.close();
				i++;
			}
		}
		Logger.info("Total User: " + users.size());
		Logger.info("Total Tweets: " + tweetCount);

		writer.close();
	}

	public static void main(String[] args) {
		try {
//			Map<Long, User> nonSpammers = UserLoader.loadAllUsers();
			//PrintWriter writer = new PrintWriter(PATH + "non-spammer-id.txt");
			//for (Long key : nonSpammers.keySet()) {
			//	writer.println(key);
			//}
			//writer.close();
//			printTweets(nonSpammers, "non-spammer-id.txt", "non-spammer-tweets/", true);
			Map<Long, User> spammers = NewDatasetLoader.loadAllSuspendedUsers();
			Logger.info("Spammer Size: " + spammers.size());
			printTweets(spammers, "non-suspended-id.txt", "non-suspended-tweets/", false);
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}

}
