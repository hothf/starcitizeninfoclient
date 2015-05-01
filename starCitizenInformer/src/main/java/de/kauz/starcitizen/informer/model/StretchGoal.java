package de.kauz.starcitizen.informer.model;

import java.io.Serializable;

/**
 * A stretch goal has a title, a date, a status and a description.
 * 
 * @author MadKauz
 * 
 */
public class StretchGoal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4678719988191664296L;
	private String title, description, date;
	private boolean status;

	/**
	 * Creates a new stretch goal.
	 * 
	 * @param title
	 *            of the goal
	 * @param status
	 *            of the goal (true is accomplished)
	 * @param description
	 *            of the goal
	 * @param date
	 *            of the goal
	 */
	public StretchGoal(String title, boolean status, String description,
			String date) {
		this.title = title;
		this.status = status;
		this.description = description;
		this.date = date;
	}

	/**
	 * Creates am empty stretch goal with dummy content;
	 */
	public StretchGoal() {
		this.title = "Empty";
		this.status = false;
		this.description = "Empty";
		this.date = "Empty";
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
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

}
