package twitter.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import twitter.data.User;
import twitter.data.UserLoader;
import twitter.utils.FileLineReader;
import twitter.utils.Counter.CounterEntry;
import twitter.utils.FileLineReader.LineHandler;

public class FeatureSelectionTestor {

	public static final String FEATURE_PATH = UserLoader.PATH;
	public static final String MUTUAL_INFORMATION = "mi";
	public static final String CHI_SQUARE = "chi";

	public static String getSpammerFeatureList(boolean isSpam, int gramLen, String method) {
		return FEATURE_PATH + (isSpam ? "spammer" : "nonspammer") + "-feature-" + gramLen + "-" + method + ".txt";
	}

	public static FeatureSelectionTestor loadFeatures(int gramLen, String method) {
		List<Feature> spammerFeatures = new FileLineReader<>(getSpammerFeatureList(true, gramLen, method),
				new FeatureLineHandler()).read();
		List<Feature> nonSpammerFeatures = new FileLineReader<>(getSpammerFeatureList(false, gramLen, method),
				new FeatureLineHandler()).read();

		return new FeatureSelectionTestor(spammerFeatures, nonSpammerFeatures);
	}

//	public Instances filter(Map<Long, User> spammers, Map<Long, User> nonSpammers,
//			Map<Long, List<CounterEntry<String>>> spammerGramFeatures,
//			Map<Long, List<CounterEntry<String>>> nonSpammerGramFeatures, int featureCount) {
//		FastVector attInfo = new FastVector(featureCount + 1);
//		Map<String, Attribute> attributeMap = new HashMap<>();
//
//		for (int i = 0; i < featureCount / 2; i++) {
//			Attribute att = new Attribute(nonSpammerFeatures.get(i).feature);
//			attributeMap.put(nonSpammerFeatures.get(i).feature, att);
//			attInfo.addElement(att);
//		}
//		for (int i = featureCount / 2; i < featureCount; i++) {
//			Attribute att = new Attribute(spammerFeatures.get(i).feature);
//			attributeMap.put(spammerFeatures.get(i).feature, att);
//			attInfo.addElement(att);
//		}
//
//		Attribute classAttr = new Attribute(UserWordInstances.CLASS_KEY, new FastVector() {
//			{
//				addElement(UserWordInstances.SPAMMER_CLASS_LABEL);
//				addElement(UserWordInstances.NONSPAMMER_CLASS_LABEL);
//			}
//		});
//		attInfo.addElement(classAttr);
//
//		Instances dataset = new Instances("Twitter Features", attInfo, spammers.size() + nonSpammers.size());
//		dataset.setClass(classAttr);
//
//		for (User u : spammers.values()) {
//			Instance ins = new Instance(featureCount + 1);
//			dataset.add(ins);
//			ins.setDataset(dataset);
//			ins.setClassValue(UserWordInstances.SPAMMER_CLASS_LABEL);
//			
//			for(Entry<String, Attribute> e:attributeMap.entrySet()){
//				ins.setValue(e.getValue(), spammerGramFeatures.get(u.getId()));
//			}
//			ins.setValue(classAttr, UserWordInstances.SPAMMER_CLASS_LABEL);
//		}
//		return dataset;
//	}

	public FeatureSelectionTestor(List<Feature> spammerFeatures, List<Feature> nonSpammerFeatures) {
		this.spammerFeatures = spammerFeatures;
		this.nonSpammerFeatures = nonSpammerFeatures;
	}

	private static class FeatureLineHandler implements LineHandler<List<Feature>> {

		public FeatureLineHandler() {
			features = new ArrayList<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			String[] split = line.split("\t");
			features.add(new Feature(split[0], Double.parseDouble(split[1])));

			return true;
		}

		@Override
		public List<Feature> getResult() {
			return features;
		}

		private List<Feature> features;
	}

	private static class Feature {

		public Feature(String feature, double score) {
			this.feature = feature;
			this.score = score;
		}

		String feature;
		double score;
	}

	private List<Feature> spammerFeatures;
	private List<Feature> nonSpammerFeatures;
}
