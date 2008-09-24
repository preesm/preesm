package org.ietr.preesm.core.architecture;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Common features of components in an architecture. Media and Operators are
 * ArchitectureComponents
 * 
 * @author mpelcat
 */
public abstract class ArchitectureComponent {

	public static final ArchitectureComponent NO_COMPONENT = null;

	/**
	 * media interfaces available in this architecture component. Interfaces are
	 * be connected via interconnections
	 */
	protected List<ArchitectureInterface> availableInterfaces;

	/**
	 * The type contains the category (medium or operator) and the id (example:
	 * C64x+)
	 */
	private ArchitectureComponentDefinition definition;

	/**
	 * Name of the component instance
	 */
	private String name;

	/**
	 * Constructor from a type and a name
	 */
	public ArchitectureComponent(String name,
			ArchitectureComponentDefinition definition) {
		this.name = new String(name);
		this.definition = definition;

		availableInterfaces = new ArrayList<ArchitectureInterface>();
	}

	/**
	 * Adds an interface to the architecture component
	 */
	public abstract boolean addInterface(ArchitectureInterface intf);

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ArchitectureComponent) {
			ArchitectureComponent op = (ArchitectureComponent) obj;
			return this.getName().compareToIgnoreCase(op.getName()) == 0;
		}

		return false;
	}

	public List<ArchitectureInterface> getAvailableInterfaces() {
		return availableInterfaces;
	}

	public ArchitectureComponentDefinition getDefinition() {
		return definition;
	}

	/**
	 * Gets the interface for the given medium type
	 * 
	 * @return the interface or null if it does not exist
	 */
	public ArchitectureInterface getInterface(MediumDefinition mediumdef) {

		ListIterator<ArchitectureInterface> it = getAvailableInterfaces()
				.listIterator();

		while (it.hasNext()) {
			ArchitectureInterface intf = it.next();
			if (mediumdef.sameId(intf.getMediumDefinition())) {
				return intf;
			}
		}

		return null;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
