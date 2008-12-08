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
package org.ietr.preesm.plugin.mapper.listsched.descriptor;

import java.util.HashMap;

/**
 * This class is a descriptor of IP that provides necessary parameters.
 * 
 * @author Pengcheng MU
 * 
 */
public class IpDescriptor extends OperatorDescriptor {

	private String userInterfaceType = null; // Fifo or Ram

	private int nbInputData = 1; // unit: byte

	private int nbOutputData = 2;

	private int receiveDuration = 0;

	private int sendDuration = 0;

	private int communicationDuration = 0;

	private int executionDuration = 0;

	private int latency = 1;

	private int cadence = 1;

	private int maxNbInstance = 1;

	private int nbInstance = 1;

	private boolean fixed = false;

	/**
	 * Construct a new IpDescriptor with the identifier id.
	 * 
	 * 
	 */

	public IpDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		this.type = ComponentType.Ip;
	}

	public IpDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Ip;
	}

	/**
	 * Construct a new IpDescriptor with the identifier id, surface and timing
	 * parameters.
	 * 
	 */
	public IpDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface,
			String userInterfaceType, int inputDataNumber,
			int outputDataNumber, int latency, int cadence) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Ip;
		this.userInterfaceType = userInterfaceType;
		this.nbInputData = inputDataNumber;
		this.nbOutputData = outputDataNumber;
		this.latency = latency;
		this.cadence = cadence;
	}

	public void calculateBehaviorModel(String id) {
		if (ComponentDescriptorBuffer.get(id).getType() == ComponentType.Bus) {
			BusDescriptor bus = (BusDescriptor) ComponentDescriptorBuffer
					.get(id);
			receiveDuration = (int) (bus.getAverageClockCyclesPerTransfer() * 8
					* nbInputData * bus.getClockPeriod() / bus.getDataWidth());
			sendDuration = (int) (bus.getAverageClockCyclesPerTransfer() * 8
					* nbOutputData * bus.getClockPeriod() / bus.getDataWidth());
			if (userInterfaceType.equalsIgnoreCase("Fifo")) {
				executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
						* cadence)
						* clockPeriod - receiveDuration;
			} else if (userInterfaceType.equalsIgnoreCase("Ram")) {
				executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
						* cadence)
						* clockPeriod;
			}
			communicationDuration = receiveDuration + sendDuration;
			maxNbInstance = executionDuration
					/ (receiveDuration + sendDuration) + 1;
		} else if (ComponentDescriptorBuffer.get(id).getType() == ComponentType.Fifo) {
			FifoDescriptor fifo = (FifoDescriptor) ComponentDescriptorBuffer
					.get(id);
			receiveDuration = (int) (fifo.getAverageClockCyclesPerTransfer()
					* 8 * nbInputData * fifo.getClockPeriod() / fifo
					.getDataWidth());
			sendDuration = (int) (fifo.getAverageClockCyclesPerTransfer() * 8
					* nbOutputData * fifo.getClockPeriod() / fifo
					.getDataWidth());
			if (userInterfaceType.equalsIgnoreCase("Fifo")) {
				executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
						* cadence)
						* clockPeriod - receiveDuration;
			} else if (userInterfaceType.equalsIgnoreCase("Ram")) {
				executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
						* cadence)
						* clockPeriod;
			}
			communicationDuration = receiveDuration + sendDuration;
			maxNbInstance = executionDuration
					/ (receiveDuration + sendDuration) + 1;
		} else if (ComponentDescriptorBuffer.get(id).getType() == ComponentType.Switch) {
			SwitchDescriptor network = (SwitchDescriptor) ComponentDescriptorBuffer
					.get(id);
			receiveDuration = (int) (network.getAverageClockCyclesPerTransfer()
					* 8 * nbInputData * network.getClockPeriod() / network
					.getDataWidth());
			sendDuration = (int) (network.getAverageClockCyclesPerTransfer()
					* 8 * nbOutputData * network.getClockPeriod() / network
					.getDataWidth());
			if (userInterfaceType.equalsIgnoreCase("Fifo")) {
				executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
						* cadence)
						* clockPeriod - receiveDuration;
			} else if (userInterfaceType.equalsIgnoreCase("Ram")) {
				executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
						* cadence)
						* clockPeriod;
			}
			communicationDuration = receiveDuration + sendDuration;
			maxNbInstance = executionDuration
					/ (receiveDuration + sendDuration) + 1;
		}
	}

	/**
	 * 
	 * @param userInterfaceType
	 */
	public void setUserInterfaceType(String userInterfaceType) {
		this.userInterfaceType = userInterfaceType;
	}

	/**
	 * 
	 * @return
	 */
	public String getUserInterfaceType() {
		return userInterfaceType;
	}

	/**
	 * 
	 * @param nbInputData
	 */
	public void setNbInputData(int nbInputData) {
		this.nbInputData = nbInputData;
	}

	/**
	 * 
	 * @return
	 */
	public int getNbInputData() {
		return nbInputData;
	}

	/**
	 * 
	 * @param nbInputData
	 */
	public void setNbOutputData(int nbOutputData) {
		this.nbOutputData = nbOutputData;
	}

	/**
	 * 
	 * @return
	 */
	public int getNbOutputData() {
		return nbOutputData;
	}

	/**
	 * 
	 */
	public void setReceiveDuration(int receiveDuration) {
		this.receiveDuration = receiveDuration;
	}

	/**
	 * 
	 */
	public void setReceiveDuration(BusDescriptor bus) {
		receiveDuration = (int) (bus.getAverageClockCyclesPerTransfer() * 8
				* nbInputData * bus.getClockPeriod() / bus.getDataWidth());
	}

	/**
	 * 
	 * @return
	 */
	public int getReceiveDuration() {
		return receiveDuration;
	}

	/**
	 * 
	 */
	public void setSendDuration(int sendDuration) {
		this.sendDuration = sendDuration;
	}

	/**
	 * 
	 */
	public void setSendDuration(BusDescriptor bus) {
		sendDuration = (int) (bus.getAverageClockCyclesPerTransfer() * 8
				* nbOutputData * bus.getClockPeriod() / bus.getDataWidth());
	}

	/**
	 * 
	 * @return
	 */
	public int getSendDuration() {
		return sendDuration;
	}

	/**
	 * 
	 */
	public void setCommunicationDuration() {
		this.communicationDuration = receiveDuration + sendDuration;
	}

	/**
	 * 
	 * @return
	 */
	public int getCommunicationDuration() {
		return communicationDuration;
	}

	/**
	 * 
	 */
	public void setExecutionDuration() {
		if (userInterfaceType.equalsIgnoreCase("ram")) {
			executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
					* cadence)
					* clockPeriod;
		} else if (userInterfaceType.equalsIgnoreCase("fifo")) {
			executionDuration = (latency + (8 * nbOutputData / dataWidth - 1)
					* cadence)
					* clockPeriod - receiveDuration;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getExecutionDuration() {
		return executionDuration;
	}

	/**
	 * 
	 * @param latency
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}

	/**
	 * 
	 * @return
	 */
	public int getLatency() {
		return latency;
	}

	/**
	 * 
	 * @param cadence
	 */
	public void setCadence(int cadence) {
		this.cadence = cadence;
	}

	/**
	 * 
	 * @return
	 */
	public int getCadence() {
		return cadence;
	}

	/**
	 * 
	 * @return
	 */
	public void setMaxNbInstance() {
		maxNbInstance = executionDuration / communicationDuration + 1;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxNbInstance() {
		return maxNbInstance;
	}

	/**
	 * 
	 */
	public void setFixed() {
		fixed = true;
	}

	/**
	 * 
	 */
	public void clearFixed() {
		fixed = false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * 
	 * @param nbInstance
	 */
	public void setNbInstance(int nbInstance) {
		if (nbInstance < maxNbInstance)
			this.nbInstance = nbInstance;
		else {
			this.nbInstance = maxNbInstance;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int getNbInstance() {
		return nbInstance;
	}

}
