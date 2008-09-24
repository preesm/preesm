package org.ietr.preesm.core.architecture;

/**
 * The architecture component definition gives component specifications
 * 
 * @author mpelcat
 */
public abstract class ArchitectureComponentDefinition {

	/**
	 * Category of the component definition: "medium" or "operator"
	 */
	protected String category;

	/**
	 * ID of the architecture component definition (examples: TCP, C64x+...)
	 */
	private String id;

	/**
	 * Constructor with clone
	 */
	public ArchitectureComponentDefinition(
			ArchitectureComponentDefinition origin) {
		this.id = origin.id;

		this.category = origin.category;
	}

	/**
	 * Constructor
	 */
	public ArchitectureComponentDefinition(String id, String category) {
		this.id = new String(id);

		this.category = new String(category);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ArchitectureComponentDefinition) {
			ArchitectureComponentDefinition def = (ArchitectureComponentDefinition) obj;
			return id.equalsIgnoreCase(def.getId())
					&& category.equalsIgnoreCase(def.category);
		}
		return false;
	}

	public String getId() {
		return id;
	}

	/**
	 * Compares two definitions for category
	 */
	public boolean sameCategory(ArchitectureComponentDefinition othertype) {
		return (category.compareToIgnoreCase(othertype.category) == 0);
	}

	/**
	 * Compares two definitions for id
	 */
	public boolean sameId(ArchitectureComponentDefinition othertype) {
		return (id.compareToIgnoreCase(othertype.id) == 0);
	}

}
