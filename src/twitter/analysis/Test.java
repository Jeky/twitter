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
import twitter.nlp.TweetsGramLoader;
import twitter.nlp.UserVector;
import twitter.nlp.Word2Vec;
import twitter.utils.IOUtils;
import twitter.utils.Logger;
import twitter.utils.Utils;
import twitter.utils.Counter.CounterEntry;

public class Test {

	public static void main(String[] args) {
		try {
			Logger.info("Job Finished");
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}

}
