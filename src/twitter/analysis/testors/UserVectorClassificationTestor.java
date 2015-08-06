package twitter.analysis.testors;

import java.util.List;
import java.util.Map;

import twitter.analysis.converters.UserVectorConverter;
import twitter.utils.Logger;

public class UserVectorClassificationTestor extends ClassificationTestor {

	public static final String TWEET_WORD_VECTOR_PATH = TEST_PATH + "tweets.bin.txt";

	public UserVectorClassificationTestor() {
		super(null);
		Logger.info("Loading Word Vectors...");
		Map<String, List<Double>> wordVectors = UserVectorConverter.loadWordVector(TWEET_WORD_VECTOR_PATH);
		
		this.converter = new UserVectorConverter(wordVectors);
		Logger.info("Starting Classification Test on User Vectors");
	}

}
