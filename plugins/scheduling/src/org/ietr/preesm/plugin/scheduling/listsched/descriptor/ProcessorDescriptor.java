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
package org.ietr.preesm.plugin.scheduling.listsched.descriptor;

import java.util.HashMap;

/**
 * This class gives the description of a processor
 * 
 * @author pmu
 * 
 */
public class ProcessorDescriptor extends OperatorDescriptor {
	/**
	 * Send overhead for a communication
	 */
	private int sendOverhead = 0;

	/**
	 * Receive overhead for a communication
	 */
	private int receiveOverhead = 0;

	/**
	 * Send involvement factor for a communication
	 */
	private int sendInvolvementFactor = 0;

	/**
	 * Receive involvement factor for a communicaiton
	 */
	private int receiveInvolvementFactor = 0;

	/**
	 * Construct a ProcessorDescriptor with the given id, name and component
	 * buffer
	 * 
	 * @param id
	 *            Processor id
	 * @param name
	 *            Processor name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 */
	public ProcessorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		this.type = ComponentType.Processor;
	}

	/**
	 * Construct a ProcessorDescriptor with the given id, name, component
	 * buffer, clock period, data width and surface
	 * 
	 * @param id
	 *            Processor id
	 * @param name
	 *            Processor name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period
	 * @param dataWidth
	 *            Data width
	 * @param surface
	 *            Surface
	 */
	public ProcessorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Processor;
	}

	/**
	 * Get the receive involvement factor
	 * 
	 * @return The receive involvement factor
	 */
	public int getReceiveInvolvementFactor() {
		return receiveInvolvementFactor;
	}

	/**
	 * Get the receive overhead
	 * 
	 * @return The receive overhead
	 */
	public int getReceiveOverhead() {
		return receiveOverhead;
	}

	/**
	 * Get the send involvement factor
	 * 
	 * @return The send involvement factor
	 */
	public int getSendInvolvementFactor() {
		return sendInvolvementFactor;
	}

	/**
	 * Get the send overhead
	 * 
	 * @return The send overhead
	 */
	public int getSendOverhead() {
		return sendOverhead;
	}

	/**
	 * Set the receive involvement factor
	 * 
	 * @param receiveInvolvementFactor
	 *            The receive involvement factor
	 */
	public void setReceiveInvolvementFactor(int receiveInvolvementFactor) {
		this.receiveInvolvementFactor = receiveInvolvementFactor;
	}

	/**
	 * Set the receive overhead
	 * 
	 * @param receiveOverhead
	 *            The receive overhead
	 */
	public void setReceiveOverhead(int receiveOverhead) {
		this.receiveOverhead = receiveOverhead;
	}

	/**
	 * Set the send involvement factor
	 * 
	 * @param sendInvolvementFactor
	 *            The send involvement factor
	 */
	public void setSendInvolvementFactor(int sendInvolvementFactor) {
		this.sendInvolvementFactor = sendInvolvementFactor;
	}

	/**
	 * Set the send overhead
	 * 
	 * @param sendOverhead
	 *            The send overhead
	 */
	public void setSendOverhead(int sendOverhead) {
		this.sendOverhead = sendOverhead;
	}
}
