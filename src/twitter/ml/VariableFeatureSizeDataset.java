package twitter.ml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A dataset that the feature size in it can be changed dynamicly. This dataset
 * is used for testing of relationship between the number of selected top
 * features and the evaluation result of classification.
 * 
 * @author jeky
 *
 */
public class VariableFeatureSizeDataset extends Dataset {

	public VariableFeatureSizeDataset(Dataset dataset, ArrayList<String> features) {
		this.instances = new ArrayList<>();
		for (Instance i : dataset) {
			this.instances.add(new VariableFeatureInstance(i));
		}
		this.classLabel = dataset.classLabel;
		this.features = features;
	}

	public void setFeatureSize(int featureSize) {
		this.featureSize = featureSize;
	}

	private class VariableFeatureInstance implements Instance {

		public VariableFeatureInstance(Instance instance) {
			this.instance = instance;
		}

		@Override
		public Iterator<Entry<String, Double>> iterator() {
			return new VariableFeatureInstanceIterator();
		}

		@Override
		public void setValue(String attribute, double value) {
			instance.setValue(attribute, value);
		}

		@Override
		public double getValue(String attribute) {
			return instance.getValue(attribute);
		}

		@Override
		public void setDataset(Dataset dataset) {
			instance.setDataset(dataset);
		}

		@Override
		public Dataset getDataset() {
			return instance.getDataset();
		}

		@Override
		public double getClassValue() {
			return instance.getClassValue();
		}

		@Override
		public void setClassValue(double value) {
			instance.setClassValue(value);
		}

		@Override
		public boolean hasAttribute(String attribute) {
			return instance.hasAttribute(attribute);
		}

		class VariableFeatureInstanceIterator implements Iterator<Map.Entry<String, Double>> {

			@Override
			public boolean hasNext() {
				return count < featureSize;
			}

			@Override
			public Entry<String, Double> next() {
				entry.setIndex(count);
				count++;
				return entry;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove instance from VariableFeatureSizeDataset");
			}

			int count = 0;
			ReusableEntry entry = new ReusableEntry();

			private class ReusableEntry implements Entry<String, Double> {

				@Override
				public Double setValue(Double value) {
					throw new UnsupportedOperationException("Cannot set value of entry from VariableFeatureSizeDataset");
				}

				public void setIndex(int count) {
					this.index = count;
				}

				@Override
				public Double getValue() {
					return instance.getValue(features.get(index));
				}

				@Override
				public String getKey() {
					return features.get(index);
				}

				int index = 0;

			}
		};

		private Instance instance;
		private static final long serialVersionUID = -8351334175812788969L;
	}

	private List<String> features;
	private int featureSize;
	private static final long serialVersionUID = 5772192129553874685L;

}
