package twitter.analysis.testors;

import java.io.PrintStream;

import twitter.utils.Logger;

public abstract class FileLoggerTestor extends Testor {
	
	@Override
	public void onStart() throws Exception{
		out = new PrintStream(FILENAME);
		Logger.setOutput(out);
	}
	
	@Override
	public void onFinish() throws Exception {
		Logger.info("Job Finished!");
		out.close();
	}

	public static final String FILENAME = "log.txt";
	private PrintStream out;
}
