package twitter.analysis.converters;

import java.io.File;
import java.util.List;
import java.util.Map;

import twitter.analysis.testors.Testor;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.SparseInstance;
import twitter.nlp.TextProcessor;
import twitter.utils.IOUtils;
import twitter.utils.Logger;

public class GramAttrConverter implements DatasetConverter<Map<Long, User>> {

	public static final String TOP_FEATURES_FILENAME_PATTERN = Testor.TEST_PATH + "%d-features-g%d.obj";

	public GramAttrConverter(int gram) {
		this.gram = gram;
	}

	private String getObjectFilename(double cls) {
		int label = (int) cls;
		return String.format(TOP_FEATURES_FILENAME_PATTERN, label, gram);
	}

	@Override
	public Dataset convert(Map<Long, User> origin, double cls) {
		int tweetCount = 0;
		if (!new File(getObjectFilename(cls)).exists()) {
			Logger.info("Converting User to Gram Feature Instance (Gram = " + gram + ")");
			Dataset dataset = new Dataset();
			dataset.setClassLabel(CLASS_LABEL);

			for (User u : origin.values()) {
				if (dataset.size() % 100 == 0) {
					Logger.info("Processed " + dataset.size() + " users...");
				}
				Instance i = new SparseInstance();
				List<Tweet> tweets = u.getTweets();
				tweetCount += tweets.size();
				for (Tweet t : tweets) {
					List<String> words = TextProcessor.tweetToGram(t, gram);
					for (String w : words) {
						i.setValue(w, i.getValue(w) + 1);
					}
				}

				dataset.addInstance(i);
				i.setClassValue(cls);
			}
			IOUtils.saveObject(dataset, getObjectFilename(cls));
			Logger.info("Tweet Count = " + tweetCount);
			return dataset;
		} else {
			Logger.info("Loading Gram Feature Instance (Gram = " + gram + ") from " + getObjectFilename(cls));
			return (Dataset) IOUtils.loadObject(getObjectFilename(cls));
		}
	}

	private int gram;
}