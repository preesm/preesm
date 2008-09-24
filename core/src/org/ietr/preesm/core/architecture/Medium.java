package org.ietr.preesm.core.architecture;

import java.util.Iterator;

/**
 * defines a communication medium between 2 operators. It represents a
 * bus and its drivers
 *         
 * @author mpelcat
 */
public class Medium extends ArchitectureComponent {

	/**
	 * ID used to reference the element in a property bean
	 */
	public static final String propertyBeanName = "medium";
	
	public Medium(String name, MediumDefinition type) {
		super(name, type);
	}

	public Medium(String name, MediumDefinition type,
			ArchitectureInterfaceDefinition def) {
		this(name, type);

		this.addInterface(new ArchitectureInterface(def, this));
	}

	public boolean addInterface(ArchitectureInterface intf) {

		MediumDefinition mediumtype = intf.getMediumDefinition();

		/**
		 * We can add an interface only if it does not exist and if its type is
		 * the mediumdefinition type
		 */
		if (getInterface(mediumtype) == null) {

			if (this.getDefinition().sameId(mediumtype)) {
				availableInterfaces.add(intf);
			}

			return true;
		}

		return false;
	}

	@Override
	public Medium clone() {

		// Definition is cloned
		Medium newmed = new Medium(this.getName(), ((MediumDefinition) this
				.getDefinition()).clone());

		// We iterate on interfaces
		Iterator<ArchitectureInterface> interIt = this.availableInterfaces
				.iterator();

		while (interIt.hasNext()) {
			// Each interface is cloned and added to the new medium.
			// The interface medium definition is set to the current definition
			newmed.availableInterfaces.add(interIt.next().clone(
					(MediumDefinition) newmed.getDefinition(), newmed));
		}

		return newmed;
	}

}
