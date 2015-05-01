package de.kauz.starcitizen.informer.model;

/**
 * A ship sub-component.
 * 
 * @author MadKauz
 * 
 */
public class ShipSubComponent {

	private String type, size, name;

	/**
	 * Creates a ship sub component with specified name, type and size.
	 * 
	 * @param subtitle
	 * @param subtype
	 * @param subsize
	 */
	public ShipSubComponent(String subtitle, String subtype, String subsize) {
		this.name = subtitle;
		this.type = subtype;
		this.size = subsize;
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
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size) {
		this.size = size;
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

}
