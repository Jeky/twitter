package twitter.data;

import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

public class User implements Serializable {

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOutDegree() {
		return outDegree;
	}

	public void setOutDegree(int outDegree) {
		this.outDegree = outDegree;
	}

	public int getInDegree() {
		return inDegree;
	}

	public void setInDegree(int inDegree) {
		this.inDegree = inDegree;
	}

	public int getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(int statusCount) {
		this.statusCount = statusCount;
	}

	public int getFavCount() {
		return favCount;
	}

	public void setFavCount(int favCount) {
		this.favCount = favCount;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<Tweet> getTweets() {
		return this.tweets;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getOldOutDegree() {
		return oldOutDegree;
	}

	public void setOldOutDegree(int oldOutDegree) {
		this.oldOutDegree = oldOutDegree;
	}

	public int getOldInDegree() {
		return oldInDegree;
	}

	public void setOldInDegree(int oldInDegree) {
		this.oldInDegree = oldInDegree;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", outDegree=" + outDegree
				+ ", inDegree=" + inDegree + ", oldOutDegree=" + oldOutDegree
				+ ", oldInDegree=" + oldInDegree + ", statusCount="
				+ statusCount + ", favCount=" + favCount + ", age=" + age
				+ ", location=" + location + "]";
	}

	private long id;
	private String name;
	private int outDegree;
	private int inDegree;
	private int oldOutDegree;
	private int oldInDegree;
	private int statusCount;
	private int favCount;
	private String age;
	private String location;
	private List<Tweet> tweets;
	private static final long serialVersionUID = 1L;
}
