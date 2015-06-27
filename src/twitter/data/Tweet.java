package twitter.data;

import java.io.Serializable;

public class Tweet implements Serializable {

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getRetCount() {
		return retCount;
	}

	public void setRetCount(int retCount) {
		this.retCount = retCount;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}

	public String getMentionedEntityIds() {
		return MentionedEntityIds;
	}

	public void setMentionedEntityIds(String mentionedEntityIds) {
		MentionedEntityIds = mentionedEntityIds;
	}
	
	@Override
	public String toString() {
		return text;
	}

	private String type;
	private String origin;
	private String text;
	private String url;
	private String id;
	private String time;
	private int retCount;
	private boolean favorite;
	private String MentionedEntityIds;
	private String hashtags;
	private static final long serialVersionUID = 1L;
}
