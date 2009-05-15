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

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.ArchitectureInterface;

/**
 * A fifo is a directed edge used to connect two nodes and transfer data from
 * the origin node to the destination node.
 * 
 * @author pmu
 */
public class Fifo extends ArchitectureComponent implements ILink {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * communication vertex
	 */
	public static final String propertyBeanName = "fifo";

	// private ArchitectureComponent srcCmp;
	// private ArchitectureInterface srcIf;
	//
	// private ArchitectureComponent dstCmp;
	// private ArchitectureInterface dstIf;

	private ArchitectureInterface inputInterface;

	private ArchitectureInterface outputInterface;

	// public Fifo(ArchitectureComponent srcCmp, ArchitectureInterface srcIf,
	// ArchitectureComponent dstCmp, ArchitectureInterface dstIf) {
	// super("fifo", new FifoDefinition("fifo"));
	// this.srcCmp = srcCmp;
	// this.dstCmp = dstCmp;
	//
	// this.srcIf = srcIf;
	// this.dstIf = dstIf;
	//
	// dataRate = 0;
	// }

	public Fifo(String name, FifoDefinition type) {
		super(name, type);
	}

	// public Fifo(String name, FifoDefinition type, ArchitectureComponent
	// srcCmp,
	// ArchitectureInterface srcIf, ArchitectureComponent dstCmp,
	// ArchitectureInterface dstIf) {
	// super(name, type);
	// this.srcCmp = srcCmp;
	// this.dstCmp = dstCmp;
	//
	// this.srcIf = srcIf;
	// this.dstIf = dstIf;
	// }

	public double getDataRate() {
		return ((FifoDefinition) this.getDefinition()).getDataRate();
	}

	// @Override
	// public boolean equals(Object obj) {
	//
	// if (obj instanceof Fifo) {
	// Fifo intc = (Fifo) obj;
	// return intc.srcCmp.equals(this.srcCmp)
	// && intc.dstCmp.equals(this.dstCmp)
	// && intc.srcIf.equals(this.srcIf)
	// && intc.dstIf.equals(this.dstIf);
	// }
	// return false;
	// }
	//
	// public ArchitectureComponent getSrcCmp() {
	// return srcCmp;
	// }
	//
	// public ArchitectureComponent getDstCmp() {
	// return dstCmp;
	// }
	//
	// public ArchitectureInterface getSrcIf() {
	// return srcIf;
	// }
	//
	// public ArchitectureInterface getDstIf() {
	// return dstIf;
	// }
	//
	// public void setSrcCmp(ArchitectureComponent srcCmp) {
	// this.srcCmp = srcCmp;
	// }
	//
	// public void setSrcIf(ArchitectureInterface srcIf) {
	// this.srcIf = srcIf;
	// }
	//
	// public void setDstCmp(ArchitectureComponent dstCmp) {
	// this.dstCmp = dstCmp;
	// }
	//
	// public void setDstIf(ArchitectureInterface dstIf) {
	// this.dstIf = dstIf;
	// }
	//
	// public ArchitectureInterface getInterface(ArchitectureComponentType type)
	// {
	//
	// if (srcCmp.getType() == type)
	// return srcIf;
	// else if (dstCmp.getType() == type)
	// return dstIf;
	// else
	// return null;
	// }

	public ArchitectureInterface getInputInterface() {
		return inputInterface;
	}

	public ArchitectureInterface getOutputInterface() {
		return outputInterface;
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.fifo;
	}

	public void setDataRate(double dataRate) {
		((FifoDefinition) this.getDefinition()).setDataRate(dataRate);
	}

	public void setInputInterface(ArchitectureInterface inputInterface) {
		if (this.inputInterface != null) {
			availableInterfaces.remove(this.inputInterface);
		}
		this.inputInterface = inputInterface;
		this.addInterface(inputInterface);
	}

	public void setOutputInterface(ArchitectureInterface outputInterface) {
		if (this.outputInterface != null) {
			availableInterfaces.remove(this.outputInterface);
		}
		this.outputInterface = outputInterface;
		this.addInterface(outputInterface);
	}

	@Override
	public ArchitectureComponent clone() {
		return new Fifo(getName(),null);
	}
	
	public boolean isNode(){
		return true;
	}
}
