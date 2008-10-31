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
 * A processor is a hardware calculation entity able to process data. Processors
 * are connected to communication links
 * 
 * @author pmu
 */
public class Memory extends AbstractNode {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * computation vertex
	 */
	public static final String propertyBeanName = "memory";

	/**
	 * Communication performers are communicators and processors that can access
	 * this processor.
	 */
	private Set<AbstractNode> commPerformers;

	public Memory(String name, MemoryDefinition definition) {
		super(name, definition);
		commPerformers = new HashSet<AbstractNode>();
	}

	public boolean addCommunicationPerformer(AbstractNode commPerformer) {
		if (commPerformer instanceof Communicator
				|| commPerformer instanceof Processor) {
			return commPerformers.add(commPerformer);
		}
		return false;
	}

	public Memory clone() {

		// A new memory is created  with the same definition.
		Memory newMem = new Memory(new String(this.getName()), this
				.getDefinition());
		// We iterate in interfaces
		Iterator<SpiritInterface> intfIt = this.availableInterfaces.iterator();
		while (intfIt.hasNext()) {
			SpiritInterface currentIntf = intfIt.next();
			SpiritInterface newIntf = currentIntf.clone();
			newIntf.setOwner(newMem);
			newMem.addInterface(newIntf);
		}
		return newMem;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Memory) {
			Memory mem = (Memory) obj;
			return this.getName().compareToIgnoreCase(mem.getName()) == 0;
		}
		return false;
	}

	public Set<AbstractNode> getCommunicationPerformers() {
		return commPerformers;
	}

	@Override
	public MemoryDefinition getDefinition() {
		return (MemoryDefinition) definition;
	}
}
