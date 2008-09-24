package org.ietr.preesm.core.architecture;

/**
 * a medium interface is contained by an architecture component.
 * Containing a medium interface of type M and multiplicity N means that
 * this operator can be connected to N media of type M
 *         
 * @author mpelcat
 */
public class ArchitectureInterfaceDefinition {

	/**
	 * Constant used to specify an infinite number of available slots
	 */
	public static final int INFINITE = -1;

	/**
	 * type of the corresponding medium
	 */
	private MediumDefinition mediumdef;

	/**
	 * Maximal number of interconnections on this interface
	 */
	private int multiplicity;

	/**
	 * Constructor
	 */
	public ArchitectureInterfaceDefinition(MediumDefinition mediumdef,
			int multiplicity) {
		this.mediumdef = mediumdef;

		this.multiplicity = multiplicity;
	}

	public ArchitectureInterfaceDefinition clone(MediumDefinition mediumDef) {

		// A new ArchitectureInterfaceDefinition is created which takes the
		// given medium definition
		// and retrieves the current multiplicity
		ArchitectureInterfaceDefinition newintf = new ArchitectureInterfaceDefinition(
				mediumDef, this.getMultiplicity());

		return newintf;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ArchitectureInterfaceDefinition) {
			ArchitectureInterfaceDefinition def = (ArchitectureInterfaceDefinition) obj;
			return this.mediumdef.equals(def.mediumdef)
					&& this.multiplicity == def.multiplicity;
		}
		return false;
	}

	public MediumDefinition getMediumDefinition() {
		return mediumdef;
	}

	public int getMultiplicity() {
		return multiplicity;
	}
}
