package twitter.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Utils {

	public static boolean isEmptyStr(String s) {
		if (s == null) {
			return true;
		}
		if (s.trim().equals("")) {
			return true;
		}
		return false;
	}

	public static <K> void mapAdd(Map<K, Double> map, K key, double v) {
		if (!map.containsKey(key)) {
			map.put(key, v);
		} else {
			map.put(key, map.get(key) + v);
		}
	}

	public static String join(double[] arr, String joint) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < arr.length - 1; i++) {
			builder.append(arr[i]).append(joint);
		}

		builder.append(arr[arr.length - 1]);

		return builder.toString();
	}

	public static void runShell(String command) {
		Logger.info("Running Shell Command: " + command);
		BufferedReader reader = null;
		try {
			Process p = Runtime.getRuntime().exec(command);
			reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				Logger.info(line);
			}

			p.waitFor();
		} catch (Exception e) {
			Logger.error("Error when executing shell command", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Logger.error("Error when closing reader", e);
				}
			}
		}
	}
}
