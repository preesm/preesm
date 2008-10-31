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

package org.ietr.preesm.core.architecture.advancedmodel;

/**
 * The component definition gives specifications of this component
 * 
 * @author pmu
 */
public abstract class ComponentDefinition {

	/**
	 * Name of the component definition (examples: TCP, C64x+...)
	 */
	protected String name;

	/**
	 * Category of the component definition: nodes of "Processor", "Memory",
	 * "Communicator" and "CommunicationNode"; links of "Bus" and "Fifo"
	 */
	protected String category;

	/**
	 * Constructor with clone
	 */
	public ComponentDefinition(ComponentDefinition origin) {
		this.name = origin.name;
		this.category = origin.category;
	}

	/**
	 * Constructor
	 */
	public ComponentDefinition(String name, String category) {
		this.name = new String(name);
		this.category = new String(category);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ComponentDefinition) {
			ComponentDefinition def = (ComponentDefinition) obj;
			return name.equalsIgnoreCase(def.getName())
					&& category.equalsIgnoreCase(def.category);
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	/**
	 * Compares two definitions for category
	 */
	public boolean sameCategory(ComponentDefinition othertype) {
		return (category.compareToIgnoreCase(othertype.getCategory()) == 0);
	}

	/**
	 * Compares two definitions for name
	 */
	public boolean sameName(ComponentDefinition othertype) {
		return (name.compareToIgnoreCase(othertype.getName()) == 0);
	}

}
