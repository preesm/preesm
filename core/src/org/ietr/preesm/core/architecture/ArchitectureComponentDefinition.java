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

	public abstract ArchitectureComponentType getType();
	
	public abstract ArchitectureComponentDefinition clone();
	
	public abstract void fill(ArchitectureComponentDefinition origin);
}
