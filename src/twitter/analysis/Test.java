package twitter.analysis;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import twitter.classification.UserInstances;
import twitter.data.Tweet;
import twitter.data.User;
import twitter.data.UserLoader;
import twitter.nlp.TextProcessor;
import twitter.utils.Logger;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;

public class Test {

	public static void main(String[] args) {
		try {
			collectTweets("spam-tweets.txt", UserLoader.loadSpammers());
			collectTweets("nonspam-tweets.txt", Sampler.loadSampledUsers());
			
			Logger.info("Job Finished");
		} catch (Exception e) {
			Logger.error("Error in main thread", e);
		}
	}
	
	public static void collectTweets(String filename, Map<Long, User> users) throws FileNotFoundException{
		PrintWriter writer = new PrintWriter(filename);
		for(User u: users.values()){
			for(Tweet t: u.getTweets()){
				List<String> words = TextProcessor.tokenize(t.getText());
				words = TextProcessor.stem(words);
				words = TextProcessor.filterStopwords(words);
				for(String w: words){
					writer.print(w + " ");
				}
				writer.println();
			}
		}
		
		writer.close();
	}

}
