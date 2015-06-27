package twitter.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLineReader<T> {

	public FileLineReader(String filename, LineHandler<T> handler) {
		this.filename = filename;
		this.handler = handler;
	}

	public T read(boolean output) {
		if (output) {
			Logger.info("Start reading file: " + filename);
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(filename)));
			String line = null;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				if (output && i % 1000000 == 0 && i != 0) {
					Logger.info("Read " + i + " lines...");
				}
				if (line.trim().equals("")) {
					continue;
				}
				if (!handler.readLine(i, line)) {
					break;
				}
				i++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Logger.error("Error when closing file: " + filename, e);
				}
			}
		}

		return handler.getResult();
	}
	
	public T read() {
		return read(true);
	}

	public static interface LineHandler<T> {

		boolean readLine(int i, String line);

		T getResult();
	}

	private LineHandler<T> handler;
	private String filename;
}
