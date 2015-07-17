package twitter.ml;

import java.io.Serializable;
import java.util.*;

public class Dataset implements Serializable, Iterable<Instance> {

	public Dataset(){
		instances = new ArrayList<>();
	}
	
	public Instance getInstance(int index) {
		return instances.get(index);
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

	@Override
	public Iterator<Instance> iterator() {
		return instances.iterator();
	}

	private String classLabel;
	private List<Instance> instances;

	private static final long serialVersionUID = 8403747810699269508L;

}
