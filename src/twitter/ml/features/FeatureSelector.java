package twitter.ml.features;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import twitter.ml.Dataset;
import twitter.ml.Instance;

public interface FeatureSelector extends Serializable{

	void train(Dataset dataset);

	Map<String, Double> getResult();
	
	List<String> getTop(int n);

	Instance filterInstance(Instance instance, int top);

	Dataset filterDataset(Dataset dataset, int top);
}
