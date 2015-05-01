package de.kauz.starcitizen.informer.model;

/**
 * A Tweet item containing a name, a username, url, url to an image, a messege,
 * a retweetcount and the data when the message was sent.
 * 
 * @author Thomas Hofmann
 * 
 */
public class Tweet {

	private String name;
	private String userName;
	private String urlImage;
	private String message;
	private String data;
	private String url;
	private String tweetcount;

	/**
	 * Creates an empty tweet.
	 */
	public Tweet() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String user) {
		this.userName = user;
	}

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String url) {
		this.urlImage = url;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the tweetcount
	 */
	public String getTweetcount() {
		return tweetcount;
	}

	/**
	 * @param tweetcount
	 *            the tweetcount to set
	 */
	public void setTweetcount(String tweetcount) {
		this.tweetcount = tweetcount;
	}

	public String getUrl() {
		return this.url;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
