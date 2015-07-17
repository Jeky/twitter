package twitter.ml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class SparseInstance implements Instance {

	public SparseInstance() {
		values = new HashMap<>();
	}

	@Override
	public void setValue(String attribute, double value) {
		values.put(attribute, value);
	}

	@Override
	public double getValue(String attribute) {
		return values.get(attribute);
	}

	@Override
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	@Override
	public double getClassValue() {
		return values.get(dataset.getClassLabel());
	}

	@Override
	public void setClassValue(double value) {
		values.put(dataset.getClassLabel(), value);
	}
	
	@Override
	public Iterator<Entry<String, Double>> iterator() {
		return values.entrySet().iterator();
	}

	private Dataset dataset;
	private Map<String, Double> values;
	private static final long serialVersionUID = -3515379384138036736L;
}
