package twitter.utils;

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
}
