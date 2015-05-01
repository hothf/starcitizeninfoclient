package de.kauz.starcitizen.informer.model;

/**
 * A favourite can either be a news, player or an organization.
 * 
 * @author MadKauz
 * 
 */
public class Favourite {

	public final static String TYPE_NEWS = "news";
	public final static String TYPE_PLAYERS = "players";
	public final static String TYPE_ORGS = "orgs";

	private String type, title, url, description, date, optional1;

	/**
	 * Creates a new favourite with the givenspecifications.
	 * 
	 * @param title
	 * @param type
	 * @param url
	 * @param description
	 * @param date
	 * @param optional1 for future use
	 */
	public Favourite(String title, String type, String url, String description,
			String date, String optional1) {
		this.title = title;
		this.type = type;
		this.url = url;
		this.description = description;
		this.date = date;
		this.optional1 = optional1;
	}

	/**
	 * @return the type
	 */
	public String getType() {
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the optional1
	 */
	public String getOptional1() {
		return optional1;
	}

	/**
	 * @param optional1
	 *            the optional1 to set
	 */
	public void setOptional1(String optional1) {
		this.optional1 = optional1;
	}

}
