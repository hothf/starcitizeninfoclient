package de.kauz.starcitizen.informer.model;

import de.kauz.starcitizen.informer.R;

/**
 * A FanSite object with various information about user created content filled
 * websites.
 * 
 * @author MadKauz
 * 
 */
public class FanSite {

	/**
	 * Can be a header or content
	 */
	public static enum SITETYPE {
		TYPE_HEADER, TYPE_CONTENT
	}

	/**
	 * 0 = wikis, 1 = sites, 2 = tools, 3 = others
	 */
	public static String[] CATEGORIES = { "Wikis", "Sites", "Tools", "Other" };

	private String url, name, category, id;

	private SITETYPE siteType;

	/**
	 * A FanSite has a url and a name, a type and category.
	 * 
	 * @param name
	 * @param url
	 * @param type
	 * @param category
	 */
	public FanSite(String name, String url, SITETYPE type, String category) {
		this.url = url;
		this.name = name;
		this.siteType = type;
		this.category = category;
	}

	/**
	 * 
	 * @param type
	 *            the site tyoe to set
	 */
	public void setSiteType(SITETYPE type) {
		this.siteType = type;
	}

	/**
	 * @return the site type
	 */
	public SITETYPE getSiteType() {
		return this.siteType;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the imageResId
	 */
	public int getImageResId() {
		return R.drawable.rsi_circle_grey;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
