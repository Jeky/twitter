package twitter.ml.classifications;

import twitter.ml.Dataset;
import twitter.ml.Instance;

public interface Classifier {
	
	void reset();

	void train(Dataset dataset);

	double classify(Instance instance);
}
