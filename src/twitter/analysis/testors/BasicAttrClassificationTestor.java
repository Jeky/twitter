package twitter.analysis.testors;

import java.util.Map;

import twitter.analysis.converters.DatasetConverter;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.SparseInstance;
import twitter.utils.Logger;
import twitter.utils.Utils;

public class BasicAttrClassificationTestor extends ClassificationTestor {

	public static final String URL = "basic-url";
	public static final String FOLLOW_PER_FRIEND = "basic-fpf";
	public static final String RETWEET_COUNT = "basic-ret";
	public static final String FOLLOWER_COUNT = "basic-follow";
	public static final String FRIEND_COUNT = "basic-friend";
	public static final String HASHTAG_PER_TWEET = "basic-hpt";
	public static final String INCREASED_FOLLOWER_COUNT = "basic-incfollow";
	public static final String INCREASED_FRIEND_COUNT = "basic-incfriend";
	public static final String CLASS_KEY = "basic-label";

	public BasicAttrClassificationTestor() {
		super(new DatasetConverter<Map<Long, User>>() {

			@Override
			public Dataset convert(Map<Long, User> users, double cls) {
				Dataset dataset = new Dataset();
				dataset.setClassLabel(CLASS_LABEL);
				for (User user : users.values()) {
					Instance instance = new SparseInstance();
					// URL
					double url = 0.0;
					for (Tweet t : user.getTweets()) {
						if (!Utils.isEmptyStr(t.getUrl())) {
							url += 1.0;
						}
					}
					url /= user.getTweets().size();
					instance.setValue(URL, url);
					// FOLLOW_PER_FRIEND
					instance.setValue(FOLLOW_PER_FRIEND, 1.0 * user.getInDegree() / user.getOutDegree());
					// RETWEET_COUNT
					double retweet = 0.0;
					for (Tweet t : user.getTweets()) {
						retweet += t.getRetCount();
					}
					instance.setValue(RETWEET_COUNT, retweet);
					// FOLLOWER_COUNT;
					instance.setValue(FOLLOW_PER_FRIEND, (double) user.getInDegree());
					// FRIEND_COUNT
					instance.setValue(FRIEND_COUNT, (double) user.getOutDegree());
					// HASHTAG_PER_TWEET
					double hpt = 0.0;
					for (Tweet t : user.getTweets()) {
						if (!Utils.isEmptyStr(t.getHashtags())) {
							hpt += 1.0;
						}
					}
					hpt /= user.getTweets().size();
					instance.setValue(HASHTAG_PER_TWEET, hpt);
					// INCREASED_FOLLOWER_COUNT
					instance.setValue(INCREASED_FOLLOWER_COUNT, (double) (user.getInDegree() - user.getOldInDegree()));
					// INCREASED_FRIEND_COUNT
					instance.setValue(INCREASED_FRIEND_COUNT, (double) (user.getOutDegree() - user.getOldOutDegree()));

					dataset.addInstance(instance);
					instance.setClassValue(cls);
				}

				return dataset;
			}
		});
		Logger.info("Starting Classification Test using Basic User Attributes...");
	}

}
