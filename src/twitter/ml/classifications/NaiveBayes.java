package twitter.ml.classifications;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import twitter.ml.Dataset;
import twitter.ml.Instance;

public class NaiveBayes implements Classifier {

	@Override
	public void train(Dataset dataset) {
		clsProb = new HashMap<>();
		clsFeatureProb = new HashMap<>();
		HashMap<Double, Double> clsWordCount = new HashMap<>();

		Set<String> featureSet = new HashSet<>();

		for (Instance instance : dataset) {
			double cls = instance.getClassValue();
			if (!clsProb.containsKey(cls)) {
				clsFeatureProb.put(cls, new HashMap<String, Double>());
			}

			add(clsProb, cls, 1.0);

			for (Entry<String, Double> features : instance) {
				String n = features.getKey();
				double v = features.getValue();

				if (!n.equals(dataset.getClassLabel())) {
					add(clsFeatureProb.get(cls), n, v);
					add(clsWordCount, cls, v);
					featureSet.add(n);
				}
			}
		}

		for (Entry<Double, Double> e : clsProb.entrySet()) {
			double cls = e.getKey();
			double v = e.getValue();
			clsProb.put(cls, Math.log(v / dataset.size()));
		}

		for (Entry<Double, Map<String, Double>> e : clsFeatureProb.entrySet()) {
			double cls = e.getKey();
			Map<String, Double> featureProb = e.getValue();

			for (String n : featureSet) {
				double v = 0.0;
				if (featureProb.containsKey(n)) {
					v = featureProb.get(n);
				}

				featureProb.put(n, Math.log((v + 1) / (clsWordCount.get(cls) + featureSet.size())));
			}
		}
	}

	@Override
	public double classify(Instance instance) {
		if (clsProb == null) {
			throw new RuntimeException("Train Classifier First!");
		}
		double cls = 0.0;
		double prob = -1;

		for (Entry<Double, Map<String, Double>> e : clsFeatureProb.entrySet()) {
			double thisCls = e.getKey();
			Map<String, Double> features = e.getValue();
			double thisProb = clsProb.get(thisCls);

			for (Entry<String, Double> fe : features.entrySet()) {
				String n = fe.getKey();
				double v = fe.getValue();

				if (clsFeatureProb.get(thisCls).containsKey(n)) {
					thisProb += clsFeatureProb.get(thisCls).get(n) * v;
				}
			}

			if (thisProb > prob || prob == -1) {
				cls = thisCls;
				prob = thisProb;
			}
			System.out.println(thisCls + "\t" + prob);
		}
		return cls;
	}

	private <K> void add(Map<K, Double> map, K key, double v) {
		if (!map.containsKey(key)) {
			map.put(key, v);
		} else {
			map.put(key, map.get(key) + v);
		}
	}

	private Map<Double, Double> clsProb;
	private Map<Double, Map<String, Double>> clsFeatureProb;
}
