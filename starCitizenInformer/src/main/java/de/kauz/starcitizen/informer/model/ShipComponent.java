package de.kauz.starcitizen.informer.model;

/**
 * A ship component consisting of a title, type, rating and possible further
 * optional specifications.
 * 
 * @author MadKauz
 * 
 */
public class ShipComponent {

	private String rating, name, type, maxSize, cclass, quantity, category;

	private ShipSubComponent subComponent;

	/**
	 * Creates a new ship component with the given specifications.
	 * 
	 * @param title
	 * @param type
	 * @param rating
	 * @param subComponent
	 * 
	 */
	public ShipComponent(String title, String type, String rating,
			ShipSubComponent subComponent) {

		this.name = title;
		this.type = type;
		this.rating = rating;

		this.subComponent = subComponent;
	}

	/**
	 * Creates a new ship component with the given specifications.
	 * 
	 * @param title
	 * @param cclass
	 * @param maxSize
	 * @param quantity
	 * @param rating
	 * @param subComponent
	 */
	public ShipComponent(String title, String cclass, String maxSize,
			String quantity, String rating, ShipSubComponent subComponent) {

		this.name = title;
		this.cclass = cclass;
		this.maxSize = maxSize;
		this.quantity = quantity;
		this.rating = rating;

		this.subComponent = subComponent;
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
	 * Creates a new ship component with the given specifications.
	 * 
	 * @param title
	 * @param maxSize
	 * @param category
	 * @param subComponent
	 */
	public ShipComponent(String title, String maxSize, String category,
			String empty, ShipSubComponent subComponent) {

		this.category = category;
		this.name = title;
		this.maxSize = maxSize;

		this.subComponent = subComponent;

	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
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
	 * @return the maxSize
	 */
	public String getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize
	 *            the maxSize to set
	 */
	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the cclass
	 */
	public String getCclass() {
		return cclass;
	}

	/**
	 * @param cclass
	 *            the cclass to set
	 */
	public void setCclass(String cclass) {
		this.cclass = cclass;
	}

	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the subComponents
	 */
	public ShipSubComponent getSubComponent() {
		return this.subComponent;
	}

	/**
	 * @param subComponents
	 *            the subComponents to set
	 */
	public void setSubComponent(ShipSubComponent subComponent) {
		this.subComponent = subComponent;
	}

}
