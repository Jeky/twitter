package twitter.classification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import twitter.analysis.Sampler;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.nlp.TweetsGramLoader;
import twitter.utils.IOUtils;
import twitter.utils.Logger;
import twitter.utils.Counter.CounterEntry;
import twitter.utils.Utils;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class UserInstances implements Serializable {

    public static final String INSTANCES_PATH = UserLoader.PATH + "instances";

    public static final String URL = "basic-url";
    public static final String FOLLOW_PER_FRIEND = "basic-fpf";
    public static final String RETWEET_COUNT = "basic-ret";
    public static final String FOLLOWER_COUNT = "basic-follow";
    public static final String FRIEND_COUNT = "basic-friend";
    public static final String HASHTAG_PER_TWEET = "basic-hpt";
    public static final String INCREASED_FOLLOWER_COUNT = "basic-incfollow";
    public static final String INCREASED_FRIEND_COUNT = "basic-incfriend";
    public static final String CLASS_KEY = "basic-label";

    public static final String SPAMMER_CLASS_LABEL = "spammer";
    public static final String NONSPAMMER_CLASS_LABEL = "non-spammer";

    public static final String RELATION_NAME = "twitter";

    public static final Map<String, Attribute> WEKA_ATTRIBUTE_MAP = new HashMap<String, Attribute>() {
        {
            put(URL, new Attribute(URL));
            put(FOLLOW_PER_FRIEND, new Attribute(FOLLOW_PER_FRIEND));
            put(RETWEET_COUNT, new Attribute(RETWEET_COUNT));
            put(FOLLOWER_COUNT, new Attribute(FOLLOWER_COUNT));
            put(FRIEND_COUNT, new Attribute(FRIEND_COUNT));
            put(HASHTAG_PER_TWEET, new Attribute(HASHTAG_PER_TWEET));
            put(INCREASED_FOLLOWER_COUNT, new Attribute(INCREASED_FOLLOWER_COUNT));
            put(INCREASED_FRIEND_COUNT, new Attribute(INCREASED_FRIEND_COUNT));
            put(CLASS_KEY, new Attribute(CLASS_KEY, new FastVector() {
                {
                    addElement(SPAMMER_CLASS_LABEL);
                    addElement(NONSPAMMER_CLASS_LABEL);
                }
            }));
        }
    };

    public static UserInstances loadUserInstances(int n) {
        return (UserInstances) IOUtils.loadObject(INSTANCES_PATH + n + ".obj");
    }

    public Instances toInstances() {
        Logger.info("Converting user features to weka instances...");
        // init attInfo
        FastVector attInfo = new FastVector(features.size());
        Map<String, Attribute> attributeMap = new HashMap<>();
        for (String f : features) {
            Attribute a = null;
            if (WEKA_ATTRIBUTE_MAP.containsKey(f)) {
                a = WEKA_ATTRIBUTE_MAP.get(f);
            } else {
                a = new Attribute(f);
            }
            attributeMap.put(f, a);
            attInfo.addElement(a);
        }

        // init weka instances
        Instances wekaInstances = new Instances(RELATION_NAME, attInfo, instances.size());
        wekaInstances.setClass(WEKA_ATTRIBUTE_MAP.get(CLASS_KEY));
        int count = 0;
        for (Map<String, Object> i : instances) {
            if (count % 100 == 0) {
                Logger.info("filled instance: " + count);
            }
            Instance wekaI = new Instance(features.size());
            wekaI.setDataset(wekaInstances);
            for (Entry<String, Object> e : i.entrySet()) {
                if (e.getKey().equals(CLASS_KEY)) {
                    wekaI.setValue(WEKA_ATTRIBUTE_MAP.get(e.getKey()), (String) e.getValue());
                    wekaI.setClassValue((String) e.getValue());
                } else if (WEKA_ATTRIBUTE_MAP.containsKey(e.getKey())) {
                    wekaI.setValue(WEKA_ATTRIBUTE_MAP.get(e.getKey()), (double) e.getValue());
                } else {
                    int value = (int) e.getValue();
                    wekaI.setValue(attributeMap.get(e.getKey()), (double) value);
                }
            }
            wekaInstances.add(wekaI);
            count++;
        }

        return wekaInstances;
    }

    public static void collectUserInstances() {
        Map<Long, User> spammers = UserLoader.loadSpammers();
        Map<Long, User> nonSpammers = Sampler.loadSampledUsers();

        for (int i = 1; i <= 3; i++) {
            convertToInstancesWithGramLength(spammers, nonSpammers, i);
        }
    }

    private static void convertToInstancesWithGramLength(Map<Long, User> spammers, Map<Long, User> nonSpammers, int n) {
        Logger.info("Collecting Features (Gram = " + n + ")...");
        Map<Long, List<CounterEntry<String>>> spammerGramFeatures = TweetsGramLoader.loadSpammerGramFeatures(n);
        Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures = TweetsGramLoader.loadNonSpammerGramFeatures(n);

        Set<String> features = new HashSet<>();
        // add basic feature names
        features.add(URL);
        features.add(FOLLOW_PER_FRIEND);
        features.add(RETWEET_COUNT);
        features.add(FOLLOWER_COUNT);
        features.add(FRIEND_COUNT);
        features.add(HASHTAG_PER_TWEET);
        features.add(INCREASED_FOLLOWER_COUNT);
        features.add(INCREASED_FRIEND_COUNT);
        features.add(CLASS_KEY);

        // add gram feature names
        for (List<CounterEntry<String>> list : spammerGramFeatures.values()) {
            for (CounterEntry<String> e : list) {
                features.add(e.getKey());
            }
        }
        for (List<CounterEntry<String>> list : nonSpammerGramFeatures.values()) {
            for (CounterEntry<String> e : list) {
                features.add(e.getKey());
            }
        }
        Logger.info("Total Feature Count: " + features.size());

        Logger.info("Computing Features...");
        List<Map<String, Object>> instances = new ArrayList<>(spammers.size() + nonSpammers.size());
        for (Entry<Long, User> e : spammers.entrySet()) {
            fillInstance(e.getValue(), spammerGramFeatures.get(e.getKey()), SPAMMER_CLASS_LABEL, instances);
        }
        for (Entry<Long, User> e : nonSpammers.entrySet()) {
            fillInstance(e.getValue(), nonSpammerGramFeatures.get(e.getKey()), NONSPAMMER_CLASS_LABEL, instances);
        }

        IOUtils.saveObject(new UserInstances(instances, features), INSTANCES_PATH + n + ".obj");
    }

    private static void fillInstance(User user, List<CounterEntry<String>> grams, String label,
            List<Map<String, Object>> collection) {
        Map<String, Object> instance = new HashMap<>();
        // computing basic features
        // URL
        double url = 0.0;
        for (Tweet t : user.getTweets()) {
            if (!Utils.isEmptyStr(t.getUrl())) {
                url += 1.0;
            }
        }
        url /= user.getTweets().size();
        instance.put(URL, url);
        // FOLLOW_PER_FRIEND
        instance.put(FOLLOW_PER_FRIEND, 1.0 * user.getInDegree() / user.getOutDegree());
        // RETWEET_COUNT
        double retweet = 0.0;
        for (Tweet t : user.getTweets()) {
            retweet += t.getRetCount();
        }
        instance.put(RETWEET_COUNT, retweet);
        // FOLLOWER_COUNT;
        instance.put(FOLLOW_PER_FRIEND, (double) user.getInDegree());
        // FRIEND_COUNT
        instance.put(FRIEND_COUNT, (double) user.getOutDegree());
        // HASHTAG_PER_TWEET
        double hpt = 0.0;
        for (Tweet t : user.getTweets()) {
            if (!Utils.isEmptyStr(t.getHashtags())) {
                hpt += 1.0;
            }
        }
        hpt /= user.getTweets().size();
        instance.put(HASHTAG_PER_TWEET, hpt);
        // INCREASED_FOLLOWER_COUNT
        instance.put(INCREASED_FOLLOWER_COUNT, (double) (user.getInDegree() - user.getOldInDegree()));
        // INCREASED_FRIEND_COUNT
        instance.put(INCREASED_FRIEND_COUNT, (double) (user.getOutDegree() - user.getOldOutDegree()));
        // LABEL
        instance.put(CLASS_KEY, label);

        // fill gram features
        for (CounterEntry<String> e : grams) {
            instance.put(e.getKey(), e.getValue());
        }

        collection.add(instance);
    }

    private UserInstances(List<Map<String, Object>> instances, Set<String> features) {
        this.instances = instances;
        this.features = features;
    }

    private List<Map<String, Object>> instances;
    private Set<String> features;
    private static final long serialVersionUID = 1L;

}
