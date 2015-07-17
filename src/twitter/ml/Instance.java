package twitter.ml;

import java.io.Serializable;
import java.util.Map.Entry;

public interface Instance extends Serializable, Iterable<Entry<String, Double>> {

	void setValue(String attribute, double value);

	double getValue(String attribute);

	void setDataset(Dataset dataset);

	double getClassValue();

	void setClassValue(double value);

}