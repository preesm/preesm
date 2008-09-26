/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/


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
