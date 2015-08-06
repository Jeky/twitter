package twitter.analysis.testors;

import java.util.Map;

import twitter.analysis.converters.DatasetConverter;
import twitter.data.Sampler;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.ml.Dataset;
import twitter.ml.Evaluation;
import twitter.ml.Instance;
import twitter.ml.classifications.NaiveBayes;
import twitter.utils.Logger;

public class ClassificationTestor extends FileLoggerTestor {

	public ClassificationTestor(DatasetConverter<Map<Long, User>> converter) {
		this.converter = converter;
	}

	@Override
	public void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception {
		Dataset spammerDataset = converter.convert(spammers, DatasetConverter.SPAMMER_CLASS_LABEL);
		Dataset nonSpammerDataset = converter.convert(nonSpammers, DatasetConverter.NONSPAMMER_CLASS_LABEL);

		int[][] matrix = Evaluation.crossValidation(new NaiveBayes(), spammerDataset, nonSpammerDataset, 10);
		Evaluation.showResult(matrix);
	}

	protected DatasetConverter<Map<Long, User>> converter;
}
