package twitter.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter.utils.FileLineReader;
import twitter.utils.FileLineReader.LineHandler;
import twitter.utils.IOUtils;
import twitter.utils.Logger;

public class NewDatasetLoader {

	public static final String PATH = UserLoader.PATH + "ctweets-new-sample/";
	public static final String TWEET_PATH = PATH + "Random/Tweet/";
	public static final String SUSPENDED_USERS = PATH + "suspend.obj";
	public static final String[] TWEET_NAMES = new String[] { "bishop.txt" ,
																				"Havok.txt",
																				 "Phoenix.txt",
																				 "gambit.txt"};
	public static final Pattern TWEET_COLLECTION_START = Pattern.compile("###.+### (\\d+)]");
	public static final Pattern TWEET_START = Pattern.compile("\\s*<entry>\\s*");
	public static final Pattern TWEET_END = Pattern.compile("\\s*</entry>\\s*");
	public static final Pattern CONTENT_MATCHER = Pattern.compile("\\s*<entry>\\s*<\\w+>(.+?)<.*>.+");

	/**
	 * Load all tweets from the new dataset
	 * 
	 * @return a map in which the keys are user ids and the values are a list of
	 *         tweets
	 */
	public static Map<Long, List<Tweet>> loadAllTweets() {
		Map<Long, List<Tweet>> allTweets = new HashMap<>();
		for (String name : TWEET_NAMES) {
			loadTweets(name, allTweets);
		}
		return allTweets;
	}

	public static Map<Long, User> loadSampledUsers() {
		Map<Long, User> sample = new HashMap<>();
		final List<Long> ids = new LinkedList<>();
		new FileLineReader<>("sampled_ids.txt", new LineHandler<List<Long>>() {

			@Override
			public boolean readLine(int i, String line) {
				ids.add(Long.parseLong(line));
				return true;
			}

			@Override
			public List<Long> getResult() {
				return ids;
			}
		}).read();

		for (Long id : ids) {
			sample.put(id, new FakeUser(id));
		}

		return sample;
	}

	public static class FakeUser extends User {

		public FakeUser(long id) {
			setId(id);
		}

		@Override
		public List<Tweet> getTweets() {
			return UserLoader.loadTweet(getId()).subList(0, 50);
		}

		private static final long serialVersionUID = 4409050046556033948L;
	}

	/**
	 * Load all users. This will return fake users (which only contains the
	 * tweets).
	 * 
	 * The first time of invoking this method will trigger the collecting of
	 * tweets. The second time of invoking this method will simply load from
	 * saved object.
	 * 
	 * @return fake users
	 */
	public static Map<Long, User> loadAllSuspendedUsers() {
		// if (!new File(SUSPENDED_USERS).exists()) {
//		Logger.info("Cannot find saved object. Collecting users...");
		Map<Long, List<Tweet>> tweets = loadAllTweets();
		Map<Long, User> users = new HashMap<>();

		for (Entry<Long, List<Tweet>> e : tweets.entrySet()) {
			User u = new User();
			if (USER_NAME_MAP.containsKey(e.getKey())) {
				Set<String> usernames = USER_NAME_MAP.get(e.getKey());
				if (usernames.size() > 1) {
					u.setName(usernames.toString());
				} else {
					u.setName(usernames.iterator().next());
				}
			} else {
				u.setName("");
			}
			u.setId(e.getKey());
			u.setTweets(e.getValue());
			users.put(e.getKey(), u);
		}

		// IOUtils.saveObject(users, SUSPENDED_USERS);
		return users;
		// } else {
		// return (Map<Long, User>) IOUtils.loadObject(SUSPENDED_USERS);
		// }
	}

	private static void loadTweets(String filename, Map<Long, List<Tweet>> allTweets) {
		Map<Long, List<Tweet>> tweets = new FileLineReader<>(TWEET_PATH + filename, new TweetXMLHandler()).read();
		allTweets.putAll(tweets);
	}

	static Map<Long, Set<String>> USER_NAME_MAP = new HashMap<>();

	private static class TweetXMLHandler implements LineHandler<Map<Long, List<Tweet>>> {

		public TweetXMLHandler() {
			tweets = new HashMap<>();
			builder = new StringBuilder();
		}

		@Override
		public boolean readLine(int a, String line) {
			Matcher matcher = TWEET_COLLECTION_START.matcher(line);
			if (matcher.find()) {
				if (currentId != -1) {
					if (!tweets.containsKey(currentId)) {
						tweets.put(currentId, tweetList);
					} else {
						tweets.get(currentId).addAll(tweetList);
					}
				}
				currentId = Long.parseLong(matcher.group(1));
				builder.setLength(0);
				tweetList = new ArrayList<>();
			} else {
				if (TWEET_START.matcher(line).find()) {
					t = new Tweet();
					builder.append(line).append(" ");
				} else if (TWEET_END.matcher(line).find()) {
					Matcher m = CONTENT_MATCHER.matcher(builder.toString());
					if (m.find()) {
						String content = m.group(1);
						int index = content.indexOf(":");
						if (index != -1) {
							String username = content.substring(0, index);
							if (!USER_NAME_MAP.containsKey(currentId)) {
								USER_NAME_MAP.put(currentId, new HashSet<String>());
							}
							USER_NAME_MAP.get(currentId).add(username);
							content = content.substring(index + 2).trim();
						}
						t.setText(content);

						tweetList.add(t);
						builder.setLength(0);
					} else {
						Logger.error("Cannot find title in entry: \n" + builder);
					}
				} else {
					builder.append(line);
				}
			}

			return true;
		}

		@Override
		public Map<Long, List<Tweet>> getResult() {
			if (!tweets.containsKey(currentId)) {
				tweets.put(currentId, tweetList);
			} else {
				tweets.get(currentId).addAll(tweetList);
			}
			return tweets;
		}

		private Map<Long, List<Tweet>> tweets;
		private long currentId = -1;
		private StringBuilder builder;
		private List<Tweet> tweetList;
		private Tweet t;
	}
}
