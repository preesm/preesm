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
 * A fifo is a directed edge used to connect two nodes and transfer data from
 * the origin node to the destination node.
 * 
 * @author pmu
 */
public class Fifo extends AbstractLink {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * communication vertex
	 */
	public static final String propertyBeanName = "fifo";

	private SpiritInterface inputInterface;

	private SpiritInterface outputInterface;

	public Fifo(String name, FifoDefinition definition) {
		super(name, definition);
	}

	public Fifo clone() {
		// A new fifo is created with the same definition as this one.
		Fifo newFifo = new Fifo(this.getName(), this.getDefinition());
		newFifo.setDataRate(this.getDataRate());
		// Clone input and output interfaces.
		SpiritInterface newInputIntf = inputInterface.clone();
		newFifo.setInputInterface(newInputIntf);
		newInputIntf.setOwner(newFifo);
		SpiritInterface newOutputIntf = outputInterface.clone();
		newFifo.setOutputInterface(newOutputIntf);
		newOutputIntf.setOwner(newFifo);
		return newFifo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Fifo) {
			Fifo fifo = (Fifo) obj;
			return this.getName().compareToIgnoreCase(fifo.getName()) == 0;
		}
		return false;
	}

	@Override
	public FifoDefinition getDefinition() {
		return (FifoDefinition) definition;
	}

	public SpiritInterface getInputInterface() {
		return inputInterface;
	}

	public SpiritInterface getOutputInterface() {
		return outputInterface;
	}

	public boolean setInputInterface(SpiritInterface inputInterface) {
		if (addInterface(inputInterface)) {
			this.inputInterface = inputInterface;
			return true;
		} else {
			return false;
		}
	}

	public boolean setOutputInterface(SpiritInterface outputInterface) {
		if (addInterface(outputInterface)) {
			this.outputInterface = outputInterface;
			return true;
		} else {
			return false;
		}
	}
}
