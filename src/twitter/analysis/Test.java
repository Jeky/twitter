package twitter.analysis;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import twitter.data.User;
import twitter.data.UserLoader;
import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.SparseInstance;
import twitter.ml.classifications.Classifier;
import twitter.ml.classifications.NaiveBayes;
import twitter.ml.features.FeatureSelector;
import twitter.ml.features.MutualInformationSelector;
import twitter.nlp.TweetsGramLoader;
import twitter.nlp.UserVector;
import twitter.nlp.Word2Vec;
import twitter.utils.FileLineReader;
import twitter.utils.FileLineReader.LineHandler;
import twitter.utils.IOUtils;
import twitter.utils.Logger;
import twitter.utils.Utils;
import twitter.utils.Counter.CounterEntry;

public class Test {

	public static void main(String[] args) {
		try {
			Dataset dataset = new FileLineReader<>("/Users/jeky/Projects/twitter/data.txt", new DatasetReader()).read();
			FeatureSelector selector = new MutualInformationSelector();
			selector.train(dataset);
			List<String> top = selector.getTop(100);
			for (String f : top) {
				System.out.println(f + "\t" + selector.getResult().get(f));
			}

			Logger.info("Job Finished");
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}

	private static class DatasetReader implements LineHandler<Dataset> {

		public DatasetReader() {
			dataset = new Dataset();
			dataset.setClassLabel("cls");
		}

		@Override
		public boolean readLine(int index, String line) {
			String[] split = line.split("\t");
			Instance instance = new SparseInstance();
			dataset.addInstance(instance);

			for (int i = 0; i < split.length - 1; i++) {
				instance.setValue("" + i, Double.parseDouble(split[i]));
			}
			instance.setClassValue(Double.parseDouble(split[split.length - 1]));
			return true;
		}

		@Override
		public Dataset getResult() {
			return dataset;
		}

		private Dataset dataset;
	}

}
