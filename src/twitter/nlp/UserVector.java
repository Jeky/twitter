package twitter.nlp;

import java.io.Serializable;

public class UserVector implements Serializable{

	public UserVector(long id, boolean spammer, double[] vector) {
		super();
		this.id = id;
		this.spammer = spammer;
		this.vector = vector;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isSpammer() {
		return spammer;
	}

	public void setSpammer(boolean spammer) {
		this.spammer = spammer;
	}

	public double[] getVector() {
		return vector;
	}

	public void setVector(double[] vector) {
		this.vector = vector;
	}

	private long id;
	private boolean spammer;
	private double[] vector;
}
