package twitter.analysis;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import twitter.analysis.testors.BasicStatisticTestor;
import twitter.analysis.testors.UserVectorClassificationTestor;
import twitter.analysis.testors.Word2VecTestor;
import twitter.data.NewDatasetLoader;
import twitter.data.Sampler;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.utils.Logger;

public class Test {

	public static void main(String[] args) {
		try {
			Map<Long, User> spammers = NewDatasetLoader.loadAllSuspendedUsers();
			Map<Long, User> nonSpammers = NewDatasetLoader.loadSampledUsers();
			
			new BasicStatisticTestor().test(spammers, nonSpammers);
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}

}
