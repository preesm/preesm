package org.ietr.preesm.core.architecture;

/**
 * a medium interface is contained by an architecture component.
 * Containing a medium interface of type M and multiplicity N means that
 * this operator can be connected to N media of type M
 *         
 * @author mpelcat
 */
public class ArchitectureInterface {

	/**
	 * type of the corresponding medium
	 */
	private ArchitectureInterfaceDefinition interfacedefinition;

	/**
	 * owner of the corresponding interface
	 */
	private ArchitectureComponent owner;

	/**
	 * Number of connected slots
	 */
	private int usedSlots;

	/**
	 * 
	 * Constructor
	 */
	public ArchitectureInterface(ArchitectureInterfaceDefinition interfacedef,
			ArchitectureComponent owner) {
		this.interfacedefinition = interfacedef;

		this.usedSlots = 0;

		this.owner = owner;
	}

	public ArchitectureInterface clone(MediumDefinition mediumDef,
			ArchitectureComponent newOwner) {

		// The interface definition is cloned and references the given medium
		// definition
		ArchitectureInterface newintf = new ArchitectureInterface(
				this.interfacedefinition.clone(mediumDef), newOwner);

		newintf.usedSlots = 0; // usedSlot will be incremented when
		// interconnexions are added

		return newintf;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ArchitectureInterface) {
			ArchitectureInterface intf = (ArchitectureInterface) obj;
			return owner.equals(intf.owner)
					&& interfacedefinition.equals(intf.interfacedefinition);
		}
		return false;
	}

	public ArchitectureInterfaceDefinition getInterfaceDefinition() {
		return interfacedefinition;
	}

	public MediumDefinition getMediumDefinition() {
		return interfacedefinition.getMediumDefinition();
	}

	/**
	 * increments the number of used slots
	 */
	public void incrementUsedSlots() {
		usedSlots += 1;
	}

	/**
	 * @return true if the interface has reached its maximal number of
	 *         interconnections
	 */
	public boolean isFull() {
		return (usedSlots == interfacedefinition.getMultiplicity());
	}

}
