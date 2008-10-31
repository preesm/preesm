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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Common features of components in an architecture. Components include nodes
 * (processor, memory, communicator and communicationNode) and links (bus and
 * fifo).
 * 
 * @author pmu
 */
public abstract class Component {

	public static final Component NO_COMPONENT = null;

	/**
	 * Name of the component instance
	 */
	protected String name;

	/**
	 * The type contains the category and the name
	 */
	protected ComponentDefinition definition;

	/**
	 * Available interfaces in this component. Interfaces are connected by
	 * interconnections
	 */
	protected Set<SpiritInterface> availableInterfaces;

	/**
	 * Constructor from a type and a name
	 */

	public Component(String name, ComponentDefinition definition) {
		this.name = new String(name);
		this.definition = definition;
		availableInterfaces = new HashSet<SpiritInterface>();
	}

	/**
	 * Adds an interface to the node
	 */
	public abstract boolean addInterface(SpiritInterface intf);

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Component) {
			Component compnt = (Component) obj;
			return this.getName().compareToIgnoreCase(compnt.getName()) == 0;
		}
		return false;
	}

	public Set<SpiritInterface> getAvailableInterfaces() {
		return availableInterfaces;
	}

	public ComponentDefinition getDefinition() {
		return definition;
	}

	/**
	 * Get the interface for the given name
	 * 
	 * @return the interface or null if it does not exist
	 */
	public SpiritInterface getInterface(String name) {
		Iterator<SpiritInterface> it = getAvailableInterfaces().iterator();
		while (it.hasNext()) {
			SpiritInterface intf = it.next();
			if (intf.getName().equalsIgnoreCase(name)) {
				return intf;
			}
		}
		return null;
	}

	/**
	 * Get the interfaces for the given link definition
	 * 
	 * @return the set of interfaces
	 */
	public Set<SpiritInterface> getInterfaces(AbstractLinkDefinition linkDef) {
		HashSet<SpiritInterface> intfSet = new HashSet<SpiritInterface>();
		Iterator<SpiritInterface> it = getAvailableInterfaces().iterator();
		while (it.hasNext()) {
			SpiritInterface intf = it.next();
			if (linkDef.sameName(intf.getLinkDefinition())) {
				intfSet.add(intf);
			}
		}
		return intfSet;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
