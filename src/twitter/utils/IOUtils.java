package twitter.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class IOUtils {

	public static void saveObject(Object o, String filename) {
		Logger.info("Saving Object to " + filename);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(new File(filename)));
			oos.writeObject(o);
		} catch (IOException e) {
			Logger.error("Cannot find file: " + filename, e);
		} catch (Exception e) {
			Logger.error("Saving object error", e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					Logger.error("Error when closing file: " + filename, e);
				}
			}
		}
	}
	
	public static Object loadObject(String filename){
		Logger.info("Loading Object from " + filename);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(filename)));
			return ois.readObject();
		} catch (IOException e) {
			Logger.error("Cannot find file: " + filename, e);
		} catch (Exception e) {
			Logger.error("Loading users error", e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					Logger.error("Error when closing file: " + filename, e);
				}
			}
		}
		return null;
	}
}
