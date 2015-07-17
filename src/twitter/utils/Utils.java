package twitter.utils;

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
}
