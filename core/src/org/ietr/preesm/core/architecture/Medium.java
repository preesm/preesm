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
