package twitter.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter.utils.FileLineReader;
import twitter.utils.IOUtils;
import twitter.utils.Logger;
import twitter.utils.FileLineReader.LineHandler;

public class UserLoader {

	public static String PATH = "/Users/jeky/data/thesis/";
	public static String NETWORK_FILE = PATH + "network.txt";
	public static String PROFILE_FILE = PATH + "users.txt";
	public static String TWEET_PATH = PATH + "tweets/";
	public static String DEGREE_FILE = PATH + "degree.txt";
	public static String SUSPENDED_FILE = PATH + "suspended.txt";
	public static String MERGED_FILE = PATH + "merged.txt";
	public static String DEG_2009_FILE = "/Users/jeky/data/twitter/twitter-deg-decode.list";
	// saved objects
	public static String SPAMMERS = PATH + "spammers.obj";

	public static Map<Long, User> loadAllUsers() {
		return new FileLineReader<>(PROFILE_FILE, new AllUserFileHandler()).read();
	}

	public static Map<Long, Integer[]> loadOldDegree() {
		return new FileLineReader<>(DEG_2009_FILE, new OldDegreeFileHandler()).read();
	}

	public static Map<Long, User> collectSpammers() {
		Map<Long, Integer[]> oldDegMap = loadOldDegree();
		Map<Long, User> users = loadAllUsers();
		List<Long> suspendedIds = new FileLineReader<>(SUSPENDED_FILE, new SuspendedFileHandler()).read();
		Map<Long, User> spammers = new HashMap<>();
		for (Long id : suspendedIds) {
			User u = users.get(id);
			u.setOldInDegree(oldDegMap.get(id)[0]);
			u.setOldOutDegree(oldDegMap.get(id)[1]);
			spammers.put(id, u);
			u.setTweets(loadTweet(id));
		}

		return spammers;
	}

	public static List<Tweet> loadTweet(long uid) {
		if (!new File(TWEET_PATH + uid).exists()) {
			return Collections.emptyList();
		} else {
			return new FileLineReader<>(TWEET_PATH + uid, new TweetFileHandler()).read(false);
		}
	}

	public static Map<Long, User> loadSpammers() {
		Logger.info("Loading Spammers...");
		return (Map<Long, User>) IOUtils.loadObject(SPAMMERS);
	}

	private static class AllUserFileHandler implements LineHandler<Map<Long, User>> {

		public AllUserFileHandler() {
			userMap = new HashMap<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			String[] info = line.split("\t");
			User u = new User();
			u.setId(Long.parseLong(info[0]));
			u.setName(info[1]);
			u.setOutDegree(Integer.parseInt(info[2]));
			u.setInDegree(Integer.parseInt(info[3]));
			u.setStatusCount(Integer.parseInt(info[4]));
			u.setFavCount(Integer.parseInt(info[5]));
			u.setAge(info[6]);
			if (info.length > 7) {
				StringBuilder loc = new StringBuilder();
				for (int j = 7; j < info.length; j++) {
					loc.append(info[j]);
				}
				u.setLocation(loc.toString());
			}

			userMap.put(u.getId(), u);
			return true;
		}

		@Override
		public Map<Long, User> getResult() {
			return userMap;
		}

		private Map<Long, User> userMap;
	}

	private static class OldDegreeFileHandler implements LineHandler<Map<Long, Integer[]>> {
		public OldDegreeFileHandler() {
			oldDegMap = new HashMap<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			String[] deg = line.split("\t");
			oldDegMap.put(Long.parseLong(deg[0]), new Integer[] { Integer.parseInt(deg[1]), Integer.parseInt(deg[2]) });

			return true;
		}

		@Override
		public Map<Long, Integer[]> getResult() {
			return oldDegMap;
		}

		private Map<Long, Integer[]> oldDegMap;

	}

	private static class SuspendedFileHandler implements LineHandler<List<Long>> {
		public SuspendedFileHandler() {
			ids = new LinkedList<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			ids.add(Long.parseLong(line));
			return true;
		}

		@Override
		public List<Long> getResult() {
			return ids;
		}

		private List<Long> ids;
	}

	private static class TweetFileHandler implements LineHandler<List<Tweet>> {
		public TweetFileHandler() {
			tweets = new LinkedList<>();
			start = false;
			buf = new StringBuilder();
		}

		@Override
		public boolean readLine(int i, String line) {
			if (line.trim().equals("***")) {
				if (start) {
					String content = buf.toString();
					Matcher matcher = TWEET_PATTERN.matcher(content);
					if (matcher.find()) {
						Tweet t = new Tweet();
						t.setType(matcher.group(1).replace("\n", "").trim());
						t.setOrigin(matcher.group(2).replace("\n", "").trim());
						t.setText(matcher.group(3).replace("\n", "").trim());
						t.setUrl(matcher.group(4).replace("\n", "").trim());
						t.setId(matcher.group(5).replace("\n", "").trim());
						t.setTime(matcher.group(6).replace("\n", "").trim());
						t.setRetCount(Integer.parseInt(matcher.group(7).replace("\n", "").trim()));
						t.setFavorite(Boolean.parseBoolean(matcher.group(8).replace("\n", "").trim()));
						t.setMentionedEntityIds(matcher.group(9).replace("\n", "").trim());
						t.setHashtags(matcher.group(10).replace("\n", "").trim());
						tweets.add(t);
					} else {
						//Logger.info("No group found in:\n" + content);
						Logger.info("Error when parsing tweet");
					}
					buf.setLength(0);
				}
				start = !start;
			} else {
				buf.append(line).append("\n");
			}

			return true;
		}

		@Override
		public List<Tweet> getResult() {
			return tweets;
		}

		private List<Tweet> tweets;
		private boolean start;
		private StringBuilder buf;
		private static final Pattern TWEET_PATTERN = Pattern
				.compile(
						"Type:(.*)\nOrigin:(.*)\nText:(.*)\nURL:(.*)\nID:(.*)\nTime:(.*)\nRetCount:(.*)\nFavorite:(.*)\nMentionedEntities:(.*)\nHashtags:(.*)",
						Pattern.DOTALL);
	}
}
