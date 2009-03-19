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
 * a medium interface is contained by an architecture component.
 * Containing a medium interface of type M and multiplicity N means that
 * this operator can be connected to N media of type M
 *         
 * @author mpelcat
 */
public class ArchitectureInterface {

	/**
	 * type of the corresponding bus
	 */
	private BusReference busReference;

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
	public ArchitectureInterface(BusReference busReference,
			ArchitectureComponent owner) {
		this.busReference = busReference;

		this.usedSlots = 0;

		this.owner = owner;
	}

	public ArchitectureInterface clone(BusReference busRef,
			ArchitectureComponent newOwner) {

		// The interface definition is cloned and references the given medium
		// definition
		ArchitectureInterface newintf = new ArchitectureInterface(
				(BusReference)this.busReference.clone(), newOwner);

		newintf.usedSlots = 0; // usedSlot will be incremented when
		// interconnexions are added

		return newintf;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ArchitectureInterface) {
			ArchitectureInterface intf = (ArchitectureInterface) obj;
			return owner.equals(intf.owner)
					&& busReference.equals(intf.busReference);
		}
		return false;
	}

	public BusReference getBusReference() {
		return busReference;
	}

	/**
	 * increments the number of used slots
	 */
	public void incrementUsedSlots() {
		usedSlots += 1;
	}

	public ArchitectureComponent getOwner() {
		return owner;
	}
	
	

}
