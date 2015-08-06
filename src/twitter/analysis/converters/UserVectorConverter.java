package twitter.analysis.converters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter.analysis.testors.Testor;
import twitter.analysis.testors.Word2VecTestor;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.ml.Dataset;
import twitter.ml.Instance;
import twitter.ml.SparseInstance;
import twitter.nlp.TextProcessor;
import twitter.utils.FileLineReader;
import twitter.utils.IOUtils;
import twitter.utils.Logger;
import twitter.utils.FileLineReader.LineHandler;

public class UserVectorConverter implements DatasetConverter<Map<Long, User>> {

	public static final String USER_VECTOR_PATH = Testor.TEST_PATH + "user-vector.bin";
	
	public UserVectorConverter(Map<String, List<Double>> wordVectors) {
		this.wordVectors = wordVectors;
	}

	@Override
	public Dataset convert(Map<Long, User> origin, double cls) {
		if (!new File(USER_VECTOR_PATH).exists()) {
			Dataset dataset = new Dataset();

			int length = wordVectors.values().iterator().next().size();
			int count = 0;

			for (User u : origin.values()) {
				if (count % 1000 == 0) {
					Logger.info("Processed " + count + " users");
				}
				Instance instance = new SparseInstance();
				double[] values = new double[length];
				for (Tweet t : u.getTweets()) {
					List<String> grams = TextProcessor.tweetToGram(t, 1);
					for (String w : grams) {
						if(wordVectors.containsKey(w)){
							for (int i = 0; i < values.length; i++) {
								values[i] += wordVectors.get(w).get(i);
							}
						}
					}
				}
				for (int i = 0; i < values.length; i++) {
					instance.setValue("" + i, values[i]);
				}

				dataset.addInstance(instance);
				instance.setClassValue(cls);
				count++;
			}

			IOUtils.saveObject(dataset, USER_VECTOR_PATH);
			return dataset;
		} else {
			return (Dataset) IOUtils.loadObject(USER_VECTOR_PATH);
		}
	}

	public static Map<String, List<Double>> loadWordVector(String filename) {
		return new FileLineReader<>(filename, new VectorFileHandler()).read();
	}

	private static class VectorFileHandler implements LineHandler<Map<String, List<Double>>> {

		public VectorFileHandler() {
			vectorMap = new HashMap<>();
		}

		@Override
		public boolean readLine(int i, String line) {
			if (i != 0) {
				String[] split = line.split(" ");
				String word = split[0];
				List<Double> vector = new ArrayList<>();
				for (int j = 1; j < split.length; j++) {
					vector.add(Double.parseDouble(split[j]));
				}

				vectorMap.put(word, vector);
			}

			return true;
		}

		@Override
		public Map<String, List<Double>> getResult() {
			return vectorMap;
		}

		private Map<String, List<Double>> vectorMap;
	}

	private Map<String, List<Double>> wordVectors;
}
