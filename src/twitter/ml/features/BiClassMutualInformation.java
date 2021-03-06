package twitter.ml.features;

import java.util.ArrayList;
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

public class BiClassMutualInformation implements FeatureSelector {

	public BiClassMutualInformation() {
		scoreMap = new HashMap<>();
	}

	@Override
	public void train(Dataset dataset) {
		Logger.info("Training Mutual Information Feature Selector");
		Map<String, FeatureScore> featureMatrix = new HashMap<>();
		Set<Double> clsSet = new HashSet<>();

		for (Instance i : dataset) {
			clsSet.add(i.getClassValue());
			if (clsSet.size() > 2) {
				throw new RuntimeException("Cannot handle multiply classes");
			}
			for (Entry<String, Double> e : i) {
				if (!e.getKey().equals(dataset.getClassLabel())) {
					featureMatrix.put(e.getKey(), new FeatureScore());
				}
			}
		}

		Double[] classes = clsSet.toArray(new Double[0]);
		double cls = classes[0];

		Logger.info("Feature Count: " + featureMatrix.size());

		int featureCount = 0;
		for (Entry<String, FeatureScore> e : featureMatrix.entrySet()) {
			if (featureCount % 1000 == 0) {
				Logger.info("Processed " + featureCount + " Features");
			}
			String f = e.getKey();
			for (Instance instance : dataset) {
				if (instance.hasAttribute(f)) {
					if (cls == instance.getClassValue()) {
						featureMatrix.get(f).score[0] += 1;
					} else {
						featureMatrix.get(f).score[1] += 1;
					}
				} else {
					if (cls == instance.getClassValue()) {
						featureMatrix.get(f).score[2] += 1;
					} else {
						featureMatrix.get(f).score[3] += 1;
					}
				}
			}
			featureCount++;
		}

		for (Entry<String, FeatureScore> e : featureMatrix.entrySet()) {
			String f = e.getKey();
			double[] fm = e.getValue().score;
			int N = dataset.size();
			double score = fm[0] / N
					* (Math.log(N) + Math.log(fm[0]) - Math.log(fm[0] + fm[1]) - Math.log(fm[0] + fm[2])) + fm[1] / N
					* (Math.log(N) + Math.log(fm[1]) - Math.log(fm[0] + fm[1]) - Math.log(fm[1] + fm[3])) + fm[2] / N
					* (Math.log(N) + Math.log(fm[2]) - Math.log(fm[2] + fm[3]) - Math.log(fm[0] + fm[2])) + fm[3] / N
					* (Math.log(N) + Math.log(fm[3]) - Math.log(fm[2] + fm[3]) - Math.log(fm[1] + fm[3]));
			scoreMap.put(f, score);
		}
	}

	@Override
	public Map<String, Double> getResult(double cls) {
		return scoreMap;
	}

	@Override
	public List<String> getTop(double cls, int n) {
		return getAll(cls).subList(0, n);
	}

	@Override
	public List<String> getAll(double cls) {
		List<String> features = new ArrayList<>(scoreMap.keySet());
		Collections.sort(features, new Comparator<String>() {

			@Override
			public int compare(String f1, String f2) {
				double s1 = scoreMap.get(f1);
				double s2 = scoreMap.get(f2);
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
		for (String f : getTop(0.0, top)) {
			filteredInstance.setValue(f, instance.getValue(f));
		}
		filteredInstance.setValue(instance.getDataset().getClassLabel(), instance.getClassValue());

		return filteredInstance;
	}

	@Override
	public Dataset filterDataset(Dataset dataset, int top) {
		Dataset filteredDataset = new Dataset();
		filteredDataset.setClassLabel(dataset.getClassLabel());

		List<String> features = getTop(0.0, top);
		for (Instance i : dataset) {
			Instance filteredInstance = new SparseInstance();
			for (String f : features) {
				filteredInstance.setValue(f, i.getValue(f));
			}
			filteredDataset.addInstance(filteredInstance);
			filteredInstance.setClassValue(i.getClassValue());
		}

		return filteredDataset;
	}

	private Map<String, Double> scoreMap;
	private static final long serialVersionUID = -6400259403350439161L;
}
