package org.ietr.preesm.core.architecture;

/**
 * The medium definition describes the medium capabilities
 *         
 * @author mpelcat
 */
public class MediumDefinition extends ArchitectureComponentDefinition {

	/**
	 * Properties used by architecture simulator
	 */
	private MediumProperty mediumProperty;

	public MediumDefinition(MediumDefinition origin) {
		super(origin.getId(), "medium");

	}

	public MediumDefinition(String id) {
		super(id, "medium");

		mediumProperty = null;

	}

	@Override
	public MediumDefinition clone() {

		MediumDefinition newdef = new MediumDefinition(this.getId());

		newdef.mediumProperty = this.mediumProperty.clone();

		return newdef;
	}

	public MediumProperty getMediumProperty() {
		return mediumProperty;
	}

	public boolean hasMediumProperty() {
		return (this.mediumProperty != null);
	}

	public void setMediumProperty(MediumProperty approximatelyTimedProperty) {
		this.mediumProperty = approximatelyTimedProperty;
	}
}
