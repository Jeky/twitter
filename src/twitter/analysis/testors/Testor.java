package twitter.analysis.testors;

import java.util.Map;

import twitter.data.NewDatasetLoader;
import twitter.data.User;
import twitter.data.UserLoader;

public abstract class Testor {
	
	public static final String TEST_PATH = NewDatasetLoader.PATH + "test/";

	public abstract void doTest(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception;

	public void onStart() throws Exception{

	}

	public void onFinish() throws Exception{

	}

	public void test(Map<Long, User> spammers, Map<Long, User> nonSpammers) throws Exception{
		onStart();
		doTest(spammers, nonSpammers);
		onFinish();
	}
}
