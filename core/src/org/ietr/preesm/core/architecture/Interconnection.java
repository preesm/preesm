package org.ietr.preesm.core.architecture;

/**
 * An interconnection joins one interface of a medium to one interface of an
 * operator
 * 
 * @author mpelcat
 */
public class Interconnection {

	/**
	 * Type of the medium connectable with this interconnection
	 */
	private MediumDefinition mediumdef;

	private ArchitectureInterface mediumInterface;

	private ArchitectureInterface operatorInterface;

	public Interconnection(Operator op, Medium med) {
		mediumdef = new MediumDefinition((MediumDefinition) med.getDefinition());

		if (op.canConnectTo(med)) {
			operatorInterface = op.getInterface(mediumdef);
			operatorInterface.incrementUsedSlots();
			mediumInterface = med.getInterface(mediumdef);
			mediumInterface.incrementUsedSlots();
		}
	}

	public ArchitectureInterface getMediumInterface() {
		return mediumInterface;
	}

	public ArchitectureInterface getOperatorInterface() {
		return operatorInterface;
	}
}
