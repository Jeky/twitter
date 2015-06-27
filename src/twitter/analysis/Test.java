package twitter.analysis;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.nlp.TextProcessor;
import twitter.utils.Counter;
import twitter.utils.IOUtils;
import twitter.utils.Counter.CounterEntry;
import twitter.utils.Logger;

public class Test {

	public static void main(String[] args) {
		try {
			Logger.setOutput(new PrintStream("log.txt"));
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}

}
