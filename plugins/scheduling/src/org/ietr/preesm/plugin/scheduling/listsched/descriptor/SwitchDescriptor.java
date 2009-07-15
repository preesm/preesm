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
 * This class describes a switch
 * 
 * @author pmu
 * 
 */
public class SwitchDescriptor extends TGVertexDescriptor {
	/**
	 * Average clock cycles per transfer (unit: clock cycle)
	 */
	private double averageClockCyclesPerTransfer;

	/**
	 * Number of ports
	 */
	private int portNumber;

	/**
	 * Construct a SwitchDescriptor with the given id, name and component buffer
	 * 
	 * @param id
	 *            Switch Id
	 * @param name
	 *            Switch name
	 * @param componentDescriptorBuffer
	 *            Component buffer
	 */
	public SwitchDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer) {
		super(id, name, componentDescriptorBuffer);
		this.type = ComponentType.Switch;
	}

	/**
	 * Construct a SwitchDescriptor with the given id, name, component buffer,
	 * clock period, data width and surface
	 * 
	 * @param id
	 *            Switch Id
	 * @param name
	 *            Switch name
	 * @param componentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period
	 * @param dataWidth
	 *            Data width
	 * @param surface
	 *            Surface
	 */
	public SwitchDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, componentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Switch;
	}

	/**
	 * Get the average clock cycles per transfer
	 * 
	 * @return The average clock cycles per transfer
	 */
	public double getAverageClockCyclesPerTransfer() {
		return averageClockCyclesPerTransfer;
	}

	/**
	 * Get the number of ports
	 * 
	 * @return The number of ports
	 */
	public int getPortNumber() {
		return portNumber;
	}

	/**
	 * Set the average clock cycles per transfer
	 * 
	 * @param cycles
	 *            The average clock cycles per transfer
	 */
	public void setAverageClockCyclesPerTransfer(double cycles) {
		this.averageClockCyclesPerTransfer = cycles;
	}

	/**
	 * Set the number of ports
	 * 
	 * @param portNumber
	 *            The number of ports
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
}
