package de.kauz.starcitizen.informer.model;

import android.graphics.Bitmap;

/**
 * An RSS item containing a title, link and imageUrl.
 * 
 * @author MadKauz
 * 
 */
public class RssItem {

	private String title;
	private String link;
	private String imgUrl;
	private Bitmap img;
	private String description;

	private boolean isAlreadyDownloading = false;

	/**
	 * A new RSS item which consists of a title, outgoing link and image url.
	 * @param title
	 * @param link
	 * @param imgUrl
	 */
	public RssItem(String title, String link, String imgUrl) {
		this.title = title;
		this.link = link;
		this.imgUrl = imgUrl;
	}

	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * 
	 * @return
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * 
	 * @return
	 */
	public String getImgUrl() {
		return this.imgUrl;
	}

	/**
	 * 
	 * @return
	 */
	public Bitmap getImg() {
		return this.img;
	}

	/**
	 * 
	 * @param imgUrl
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	/**
	 * 
	 * @param result
	 */
	public void setImg(Bitmap result) {
		this.img = result;
	}

	/**
	 * @return the isAlreadyDownloading
	 */
	public boolean isAlreadyDownloading() {
		return isAlreadyDownloading;
	}

	/**
	 * @param isAlreadyDownloading
	 *            the isAlreadyDownloading to set
	 */
	public void setAlreadyDownloading(boolean isAlreadyDownloading) {
		this.isAlreadyDownloading = isAlreadyDownloading;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
