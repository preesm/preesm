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
 
package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;

public class ProcessorDescriptor extends OperatorDescriptor {

	private int sendOverhead = 0;

	private int receiveOverhead = 0;

	private int sendInvolvementFactor = 0;

	private int receiveInvolvementFactor = 0;

	public ProcessorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		this.type = ComponentType.Processor;
	}

	public ProcessorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Processor;
	}

	public int getSendOverhead() {
		return sendOverhead;
	}

	public void setSendOverhead(int sendOverhead) {
		this.sendOverhead = sendOverhead;
	}

	public int getReceiveOverhead() {
		return receiveOverhead;
	}

	public void setReceiveOverhead(int receiveOverhead) {
		this.receiveOverhead = receiveOverhead;
	}

	public int getSendInvolvementFactor() {
		return sendInvolvementFactor;
	}

	public void setSendInvolvementFactor(int sendInvolvementFactor) {
		this.sendInvolvementFactor = sendInvolvementFactor;
	}

	public int getReceiveInvolvementFactor() {
		return receiveInvolvementFactor;
	}

	public void setReceiveInvolvementFactor(int receiveInvolvementFactor) {
		this.receiveInvolvementFactor = receiveInvolvementFactor;
	}

}
