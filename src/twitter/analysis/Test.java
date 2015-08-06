package twitter.analysis;

import java.util.Map;

import twitter.analysis.testors.UserVectorClassificationTestor;
import twitter.analysis.testors.Word2VecTestor;
import twitter.data.NewDatasetLoader;
import twitter.data.User;
import twitter.utils.Logger;

public class Test {

	public static void main(String[] args) {
		try {
			Map<Long, User> spammers = NewDatasetLoader.loadAllSuspendedUsers();
			Map<Long, User> nonSpammers = NewDatasetLoader.loadSampledUsers();
//			new TSNETestor(BiClassMutualInformation.class, 1, 10000).test(spammers, nonSpammers);
//			new Word2VecTestor().test(spammers, nonSpammers);
			new UserVectorClassificationTestor().test(spammers, nonSpammers);
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}
}
