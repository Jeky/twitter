package twitter.nlp;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter.data.Tweet;
import twitter.utils.Counter;
import twitter.utils.Logger;

public class TextProcessor {

	public static List<String> toGram(List<String> words, int n) {
		if (n == 1) {
			return words;
		} else {
			List<String> grams = new ArrayList<>();
			if (words.size() < n) {
				return grams;
			}

			for (int i = 0; i < words.size() - 1; i++) {
				StringBuilder buf = new StringBuilder();
				for (int j = 0; j < n - 1; j++) {
					buf.append(words.get(i + j)).append(" ");
				}
				buf.append(words.get(n - 1));
				grams.add(buf.toString());
			}
			return grams;
		}
	}

	public static void countGramsInTweet(Tweet t, Counter<String> counter, int n) {
		List<String> grams = tweetToGram(t, n);
		for (String g : grams) {
			counter.put(g);
		}
	}

	public static List<String> tweetToGram(Tweet t, int n) {
		return toGram(stem(filterStopwords(tokenize(t.getText()))), n);
	}

	public static List<String> tokenize(String text) {
		Matcher m = WORD_PATTERN.matcher(text);
		List<String> words = new ArrayList<>();
		while (m.find()) {
			String word = m.group();
			if (!isDigit(word)) {
				words.add(word.toLowerCase());
			}
		}

		return words;
	}

	public static List<String> stem(List<String> words) {
		List<String> stemmedWords = new ArrayList<String>(words.size());
		for (String w : words) {
			stemmedWords.add(STEMMER.stem(w));
		}

		return stemmedWords;
	}

	public static List<String> filterStopwords(List<String> words, Set<String> stops) {
		List<String> filteredWords = new ArrayList<>();
		for (String w : words) {
			if (!stops.contains(w)) {
				filteredWords.add(w);
			}
		}

		return filteredWords;
	}

	public static List<String> filterStopwords(List<String> words) {
		return filterStopwords(words, DEFAULT_STOPS);
	}

	private static boolean isDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static final Stemmer STEMMER = new Stemmer();
	private static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
	private static final Set<String> DEFAULT_STOPS = new HashSet<String>() {
		{
			add("a");
			add("about");
			add("above");
			add("across");
			add("after");
			add("again");
			add("against");
			add("ain");
			add("all");
			add("almost");
			add("alone");
			add("along");
			add("already");
			add("also");
			add("although");
			add("always");
			add("among");
			add("an");
			add("and");
			add("another");
			add("any");
			add("anybody");
			add("anyone");
			add("anything");
			add("anywhere");
			add("are");
			add("area");
			add("areas");
			add("around");
			add("as");
			add("ask");
			add("asked");
			add("asking");
			add("asks");
			add("at");
			add("away");
			add("b");
			add("back");
			add("backed");
			add("backing");
			add("backs");
			add("be");
			add("became");
			add("because");
			add("become");
			add("becomes");
			add("been");
			add("before");
			add("began");
			add("behind");
			add("being");
			add("beings");
			add("best");
			add("better");
			add("between");
			add("big");
			add("both");
			add("but");
			add("by");
			add("c");
			add("came");
			add("can");
			add("cannot");
			add("case");
			add("cases");
			add("certain");
			add("certainly");
			add("clear");
			add("clearly");
			add("come");
			add("could");
			add("d");
			add("did");
			add("differ");
			add("different");
			add("differently");
			add("do");
			add("does");
			add("doesn");
			add("done");
			add("down");
			add("down");
			add("downed");
			add("downing");
			add("downs");
			add("during");
			add("e");
			add("each");
			add("early");
			add("either");
			add("end");
			add("ended");
			add("ending");
			add("ends");
			add("enough");
			add("even");
			add("evenly");
			add("ever");
			add("every");
			add("everybody");
			add("everyone");
			add("everything");
			add("everywhere");
			add("f");
			add("face");
			add("faces");
			add("fact");
			add("facts");
			add("far");
			add("felt");
			add("few");
			add("find");
			add("finds");
			add("first");
			add("for");
			add("four");
			add("from");
			add("full");
			add("fully");
			add("further");
			add("furthered");
			add("furthering");
			add("furthers");
			add("g");
			add("gave");
			add("general");
			add("generally");
			add("get");
			add("gets");
			add("give");
			add("given");
			add("gives");
			add("go");
			add("going");
			add("good");
			add("goods");
			add("got");
			add("great");
			add("greater");
			add("greatest");
			add("group");
			add("grouped");
			add("grouping");
			add("groups");
			add("h");
			add("had");
			add("has");
			add("hasn");
			add("have");
			add("having");
			add("he");
			add("her");
			add("here");
			add("herself");
			add("high");
			add("high");
			add("high");
			add("higher");
			add("highest");
			add("him");
			add("himself");
			add("his");
			add("how");
			add("however");
			add("i");
			add("if");
			add("important");
			add("in");
			add("interest");
			add("interested");
			add("interesting");
			add("interests");
			add("into");
			add("is");
			add("isn");
			add("it");
			add("its");
			add("itself");
			add("j");
			add("just");
			add("k");
			add("keep");
			add("keeps");
			add("kind");
			add("knew");
			add("know");
			add("known");
			add("knows");
			add("l");
			add("large");
			add("largely");
			add("last");
			add("later");
			add("latest");
			add("least");
			add("less");
			add("let");
			add("lets");
			add("like");
			add("likely");
			add("long");
			add("longer");
			add("longest");
			add("m");
			add("made");
			add("make");
			add("making");
			add("man");
			add("many");
			add("may");
			add("me");
			add("member");
			add("members");
			add("men");
			add("might");
			add("more");
			add("most");
			add("mostly");
			add("mr");
			add("mrs");
			add("much");
			add("must");
			add("my");
			add("myself");
			add("n");
			add("necessary");
			add("need");
			add("needed");
			add("needing");
			add("needs");
			add("never");
			add("new");
			add("new");
			add("newer");
			add("newest");
			add("next");
			add("no");
			add("nobody");
			add("non");
			add("noone");
			add("not");
			add("nothing");
			add("now");
			add("nowhere");
			add("number");
			add("numbers");
			add("o");
			add("of");
			add("off");
			add("often");
			add("old");
			add("older");
			add("oldest");
			add("on");
			add("once");
			add("one");
			add("only");
			add("open");
			add("opened");
			add("opening");
			add("opens");
			add("or");
			add("order");
			add("ordered");
			add("ordering");
			add("orders");
			add("other");
			add("others");
			add("our");
			add("out");
			add("over");
			add("p");
			add("part");
			add("parted");
			add("parting");
			add("parts");
			add("per");
			add("perhaps");
			add("place");
			add("places");
			add("point");
			add("pointed");
			add("pointing");
			add("points");
			add("possible");
			add("present");
			add("presented");
			add("presenting");
			add("presents");
			add("problem");
			add("problems");
			add("put");
			add("puts");
			add("q");
			add("quite");
			add("r");
			add("rather");
			add("really");
			add("right");
			add("right");
			add("room");
			add("rooms");
			add("s");
			add("said");
			add("same");
			add("saw");
			add("say");
			add("says");
			add("second");
			add("seconds");
			add("see");
			add("seem");
			add("seemed");
			add("seeming");
			add("seems");
			add("sees");
			add("several");
			add("shall");
			add("she");
			add("should");
			add("show");
			add("showed");
			add("showing");
			add("shows");
			add("side");
			add("sides");
			add("since");
			add("small");
			add("smaller");
			add("smallest");
			add("so");
			add("some");
			add("somebody");
			add("someone");
			add("something");
			add("somewhere");
			add("state");
			add("states");
			add("still");
			add("still");
			add("such");
			add("sure");
			add("t");
			add("take");
			add("taken");
			add("than");
			add("that");
			add("the");
			add("their");
			add("them");
			add("then");
			add("there");
			add("therefore");
			add("these");
			add("they");
			add("thing");
			add("things");
			add("think");
			add("thinks");
			add("this");
			add("those");
			add("though");
			add("thought");
			add("thoughts");
			add("three");
			add("through");
			add("thus");
			add("to");
			add("today");
			add("together");
			add("too");
			add("took");
			add("toward");
			add("turn");
			add("turned");
			add("turning");
			add("turns");
			add("two");
			add("u");
			add("under");
			add("until");
			add("up");
			add("upon");
			add("us");
			add("use");
			add("used");
			add("uses");
			add("v");
			add("ve");
			add("very");
			add("w");
			add("want");
			add("wanted");
			add("wanting");
			add("wants");
			add("was");
			add("wasn");
			add("way");
			add("ways");
			add("we");
			add("well");
			add("wells");
			add("went");
			add("were");
			add("what");
			add("when");
			add("where");
			add("whether");
			add("which");
			add("while");
			add("who");
			add("whole");
			add("whose");
			add("why");
			add("will");
			add("with");
			add("within");
			add("without");
			add("work");
			add("worked");
			add("working");
			add("works");
			add("would");
			add("x");
			add("y");
			add("year");
			add("years");
			add("yet");
			add("you");
			add("young");
			add("younger");
			add("youngest");
			add("your");
			add("yours");
			add("z");
		}
	};
}
