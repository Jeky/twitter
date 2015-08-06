package twitter.ml;

import java.io.Serializable;
import java.util.*;

import twitter.utils.Logger;

public class Dataset implements Serializable, Iterable<Instance> {

	public Dataset() {
		instances = new ArrayList<>();
	}

	public static Dataset merge(Dataset d1, Dataset d2) {
		Dataset d = new Dataset();
		d.setClassLabel(d1.getClassLabel());
		if (!d1.getClassLabel().equals(d2.getClassLabel())) {
			Logger.error("These 2 datasets used different class label: " + d1.getClassLabel() + ","
					+ d2.getClassLabel());
		}

		d.addDataset(d1);
		d.addDataset(d2);

		return d;
	}

	public Instance getInstance(int index) {
		return instances.get(index);
	}

	public void addDataset(Dataset dataset) {
		for (Instance i : dataset) {
			this.addInstance(i);
		}
	}

	public void addInstance(Instance instance) {
		instances.add(instance);
		instance.setDataset(this);
	}

	public String getClassLabel() {
		return classLabel;
	}

	public void setClassLabel(String classLabel) {
		this.classLabel = classLabel;
	}

	public int size() {
		return instances.size();
	}

	public void shuffle() {
		Collections.shuffle(instances);
	}

	@Override
	public Iterator<Instance> iterator() {
		return instances.iterator();
	}

	protected String classLabel;
	protected List<Instance> instances;

	private static final long serialVersionUID = 8403747810699269508L;

}
