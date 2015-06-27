package twitter.utils;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	public static void info(Object msg) {
		out.println("[" + FORMAT.format(new Date()) + "][INFO]  " + msg);
	}

	public static void debug(Object msg) {
		if (debug) {
			out.println("[" + FORMAT.format(new Date()) + "][DEBUG] " + msg);
		}
	}

	public static void error(Object msg) {
		out.println("[" + FORMAT.format(new Date()) + "][ERROR] " + msg);
		System.exit(-1);
	}

	public static void error(Object msg, Exception e) {
		out.println("[" + FORMAT.format(new Date()) + "][ERROR] " + msg);
		e.printStackTrace(out);
		System.exit(-1);
	}

	public static void setOutput(PrintStream os) {
		out = os;
	}

	public static void setDebug(boolean d) {
		debug = d;
	}

	private static boolean debug = true;
	private static PrintStream out = System.out;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"MM-dd-yyyy HH:mm:ss");
}
