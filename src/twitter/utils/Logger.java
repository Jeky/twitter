package twitter.utils;

import java.io.PrintStream;
import java.text.NumberFormat;
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

	public static void printMemUsage() {
		Runtime runtime = Runtime.getRuntime();
		NumberFormat format = NumberFormat.getInstance();

		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();

		info("Free Memory: " + format.format(freeMemory / 1024));
		info("Allocated Memory: " + format.format(allocatedMemory / 1024));
		info("Max Memory: " + format.format(maxMemory / 1024));
		info("Total Free Memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
	}

	public static void setOutput(PrintStream os) {
		out = os;
	}

	public static void setDebug(boolean d) {
		debug = d;
	}

	private static boolean debug = true;
	private static PrintStream out = System.out;
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
}
