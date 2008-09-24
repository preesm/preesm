package org.ietr.preesm.core.architecture;

import java.util.Iterator;

/**
 * an operator is a hardware calculation entity able to process data.
 * Operators are connected with media
 * 
 * @author mpelcat
 */
public class Operator extends ArchitectureComponent {

	/**
	 * ID used to reference the element in a property bean in case of a computation vertex
	 */
	public static final String propertyBeanName = "Operator";
	
	public Operator(String name, OperatorDefinition type) {
		super(name, type);

	}

	public boolean addInterface(ArchitectureInterface intf) {

		if (getInterface(intf.getMediumDefinition()) == null) {
			availableInterfaces.add(intf);

			return true;
		}

		return false;
	}

	public boolean canConnectTo(Medium medium) {
		boolean compatible = false;

		MediumDefinition mediumtype = (MediumDefinition) medium.getDefinition();

		ArchitectureInterface operatorIntf = this.getInterface(mediumtype);
		ArchitectureInterface mediumIntf = medium.getInterface(mediumtype);

		if (operatorIntf != null && mediumIntf != null) {
			if (!operatorIntf.isFull() && !mediumIntf.isFull()) {
				return true;
			}
		}

		return compatible;
	}

	public Operator clone(IArchitecture archi) {

		// A new operator is created with a cloned definition taking archi as
		// parameter
		Operator newOp = new Operator(this.getName(),
				((OperatorDefinition) this.getDefinition()).clone(archi));

		// We iterate in interfaces
		Iterator<ArchitectureInterface> interIt = this.availableInterfaces
				.iterator();

		while (interIt.hasNext()) {
			ArchitectureInterface currentItf = interIt.next();
			// The medium definition used to clone the interface is retrieved
			// from new architecture
			MediumDefinition newmeddef = archi.getMediumDefinition(currentItf
					.getMediumDefinition().getId());

			newOp.availableInterfaces.add(currentItf.clone(newmeddef, newOp));
		}

		return newOp;
	}

}
