package de.kauz.starcitizen.informer.model;

/**
 * A News object contains various news information.
 * 
 * @author MadKauz
 * 
 */
public class News {
	
	public final static String TYPE_LOAD_MORE = "loadmore";
	public final static String TYPE_NEWS = "Transmission";

	public String url, title, type, postingTime, additionalInfo, status;

	public int commentCount;

	/**
	 * Creates a new News object and fills in various information.<br>
	 * <br>
	 * The <b>status</b> of a news item is automatically set to 1. This means
	 * that the newsitem is unread.
	 * 
	 * @param url
	 *            - the url of a website
	 * @param title
	 *            - the title of the website
	 * @param type
	 *            - the type of the website (e.g. video, post)
	 * @param commentCount
	 *            - the count of comments on that website
	 * @param postingTime
	 *            - the timestamp of the creation of the website
	 * @param additionalInfo
	 *            - additional information on that website
	 * 
	 */
	public News(String url, String title, String type, int commentCount,
			String postingTime, String additionalInfo) {
		this.url = url;
		this.title = title;
		this.type = type;
		this.commentCount = commentCount;
		this.postingTime = postingTime;
		this.additionalInfo = additionalInfo;
		this.status = "1";
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		if (type.equals("post")) {
			return "Transmission";
		}
		if (type.equals("video")) {
			return "Video";
		}
		if (type.equals("slideshow")) {
			return "Slideshow";
		}
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the postingTime
	 */
	public String getPostingTime() {
		return postingTime;
	}

	/**
	 * @param postingTime
	 *            the postingTime to set
	 */
	public void setPostingTime(String postingTime) {
		this.postingTime = postingTime;
	}

	/**
	 * @return the additionalInfo
	 */
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	/**
	 * @param additionalInfo
	 *            the additionalInfo to set
	 */
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	/**
	 * @return the commentCount
	 */
	public int getCommentCount() {
		return commentCount;
	}

	/**
	 * @param commentCount
	 *            the commentCount to set
	 */
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	/**
	 * 
	 * @return a hash as a string
	 */
	public String getHash() {
		int code = Integer
				.valueOf(getTitle().replaceAll("\\s+", "").hashCode());
		return String.valueOf(code);
	}

}
