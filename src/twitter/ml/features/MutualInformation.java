package twitter.ml.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.SparseInstance;
import twitter.utils.Logger;

public class MutualInformation implements FeatureSelector {

	public MutualInformation() {
		scoreMap = new HashMap<>();
	}

	@Override
	public void train(Dataset dataset) {
		Logger.info("Training Mutual Information Feature Selector");
		Map<Double, Map<String, Double[]>> featureMatrix = new HashMap<>();

		for (Instance i : dataset) {
			if (!featureMatrix.containsKey(i.getClassValue())) {
				featureMatrix.put(i.getClassValue(), new HashMap<String, Double[]>());
			}
			if (featureMatrix.size() == 2) {
				break;
			}
		}
		// init feature matrix
		Set<String> words = new HashSet<>();
		for (Instance i : dataset) {
			for (Map<String, Double[]> values : featureMatrix.values()) {
				for (Entry<String, Double> e : i) {
					words.add(e.getKey());
					values.put(e.getKey(), new Double[] { 1.0, 1.0, 1.0, 1.0 });
				}
			}
		}

		Logger.info("Feature Count: " + featureMatrix.values().iterator().next().size());

		for (Entry<Double, Map<String, Double[]>> clsFeatureMatrix : featureMatrix.entrySet()) {
			int featureCount = 0;
			double cls = clsFeatureMatrix.getKey();
			Map<String, Double[]> values = clsFeatureMatrix.getValue();

			Logger.info("Starting counting features in cls (value = " + cls + ")");
			for (Entry<String, Double[]> e : values.entrySet()) {
				if (featureCount % 1000 == 0) {
					Logger.info("Processed " + featureCount + " features");
				}
				String f = e.getKey();
				for (Instance instance : dataset) {
					if (instance.hasAttribute(f)) {
						if (cls == instance.getClassValue()) {
							values.get(f)[0] += 1;
						} else {
							values.get(f)[1] += 1;
						}
					} else {
						if (cls == instance.getClassValue()) {
							values.get(f)[2] += 1;
						} else {
							values.get(f)[3] += 1;
						}
					}
				}
				featureCount++;
			}
		}

		Double[] classes = featureMatrix.keySet().toArray(new Double[0]);
		Map<String, Double[]> m1 = featureMatrix.get(classes[0]);
		Map<String, Double[]> m2 = featureMatrix.get(classes[1]);
		for (String w : words) {
			if (m1.get(w)[0] != m2.get(w)[0]) {
				Logger.info(w + "\t" + Arrays.toString(m1.get(w)) + "\t" + Arrays.toString(m2.get(w)));
			}
		}

		for (Entry<Double, Map<String, Double[]>> clsFeatureMatrix : featureMatrix.entrySet()) {
			double cls = clsFeatureMatrix.getKey();
			Map<String, Double[]> values = clsFeatureMatrix.getValue();
			Map<String, Double> scores = new HashMap<>();

			for (Entry<String, Double[]> e : values.entrySet()) {
				String f = e.getKey();
				Double[] fm = e.getValue();
				int N = values.size();
				double score = 
						  fm[0] / N * (Math.log(N) + Math.log(fm[0]) - Math.log(fm[0] + fm[1]) - Math.log(fm[0] + fm[2])) 
						+ fm[1]	/ N * (Math.log(N) + Math.log(fm[1]) - Math.log(fm[0] + fm[1]) - Math.log(fm[1] + fm[3]))
						+ fm[2] / N * (Math.log(N) + Math.log(fm[2]) - Math.log(fm[2] + fm[3]) - Math.log(fm[0] + fm[2])) 
						+ fm[3] / N * (Math.log(N) + Math.log(fm[3]) - Math.log(fm[2] + fm[3]) - Math.log(fm[1] + fm[3]));
				scores.put(f, score);
			}
			scoreMap.put(cls, scores);
		}
	}

	@Override
	public Map<String, Double> getResult(double cls) {
		return scoreMap.get(cls);
	}
	
	@Override
	public List<String> getAll(final double cls) {
		List<String> features = new ArrayList<>(scoreMap.get(cls).keySet());
		Collections.sort(features, new Comparator<String>() {

			@Override
			public int compare(String f1, String f2) {
				double s1 = scoreMap.get(cls).get(f1);
				double s2 = scoreMap.get(cls).get(f2);
				if (s2 > s1) {
					return 1;
				} else if (s2 < s1) {
					return -1;
				} else {
					return 0;
				}
			}

		});

		return features;
	}

	@Override
	public Instance filterInstance(Instance instance, int top) {
		Instance filteredInstance = new SparseInstance();
		// for (String f : getTop(top)) {
		// filteredInstance.setValue(f, instance.getValue(f));
		// }
		// filteredInstance.setValue(instance.getDataset().getClassLabel(),
		// instance.getClassValue());

		return filteredInstance;
	}

	@Override
	public Dataset filterDataset(Dataset dataset, int top) {
		Dataset filteredDataset = new Dataset();
		// filteredDataset.setClassLabel(dataset.getClassLabel());
		//
		// List<String> features = getTop(top);
		// for(Instance i : dataset){
		// Instance filteredInstance = new SparseInstance();
		// for (String f : features) {
		// filteredInstance.setValue(f, i.getValue(f));
		// }
		// filteredDataset.addInstance(filteredInstance);
		// filteredInstance.setClassValue(i.getClassValue());
		// }

		return filteredDataset;
	}

	@Override
	public List<String> getTop(final double cls, int n) {
		return getAll(cls).subList(0, n);
	}

	private Map<Double, Map<String, Double>> scoreMap;
	private static final long serialVersionUID = -6400259403350439161L;
}
