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

	/**
	 * User interface type: Fifo or Ram
	 */
	private String userInterfaceType = null;

	/**
	 * Number of input data (unit: byte)
	 */
	private int nbInputData = 1;

	/**
	 * Number of output data (unit: byte)
	 */
	private int nbOutputData = 2;

	/**
	 * Duration for receiving input data
	 */
	private int receiveDuration = 0;

	/**
	 * Duration for sending output data
	 */
	private int sendDuration = 0;

	/**
	 * Duration for communication of sending and receiving
	 */
	private int communicationDuration = 0;

	/**
	 * Duration of the Ip execution
	 */
	private int executionDuration = 0;

	/**
	 * Latency
	 */
	private int latency = 1;

	/**
	 * Cadence
	 */
	private int cadence = 1;

	/**
	 * Maximum number of Ip instance
	 */
	private int maxNbInstance = 1;

	/**
	 * Number of Ip instance
	 */
	private int nbInstance = 1;

	/**
	 * True if the number of instance is fixed
	 */
	private boolean fixed = false;

	/**
	 * Construct a new IpDescriptor with the id, name and component buffer
	 * 
	 * @param id
	 *            Id of the Ip
	 * @param name
	 *            Name of the Ip
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 */
	public IpDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		this.type = ComponentType.Ip;
	}

	/**
	 * Construct a new IpDescriptor with the id, name, component buffer, clock
	 * period, data width and surface
	 * 
	 * @param id
	 *            Id of the Ip
	 * @param name
	 *            Name of the Ip
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period
	 * @param dataWidth
	 *            Data width
	 * @param surface
	 *            Surface
	 */
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
	 * @param id
	 *            Id
	 * @param name
	 *            Name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period
	 * @param dataWidth
	 *            Data width
	 * @param surface
	 *            surface
	 * @param userInterfaceType
	 *            User interface type
	 * @param inputDataNumber
	 *            Number of input data
	 * @param outputDataNumber
	 *            Number of output data
	 * @param latency
	 *            Latency
	 * @param cadence
	 *            Cadence
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

	/**
	 * Complete the behavior model of the Ip according to the type of the
	 * component connected to this Ip
	 * 
	 * @param id
	 *            Id of the component connected to this Ip
	 */
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
	 * Clear the fixed status
	 */
	public void clearFixed() {
		fixed = false;
	}

	/**
	 * Get cadence
	 * 
	 * @return Cadence
	 */
	public int getCadence() {
		return cadence;
	}

	/**
	 * Get communication duration
	 * 
	 * @return Communication duration
	 */
	public int getCommunicationDuration() {
		return communicationDuration;
	}

	/**
	 * Get execution duration
	 * 
	 * @return Execution duration
	 */
	public int getExecutionDuration() {
		return executionDuration;
	}

	/**
	 * Get latency
	 * 
	 * @return Latency
	 */
	public int getLatency() {
		return latency;
	}

	/**
	 * Get maximum number of instance
	 * 
	 * @return Maximum number of instance
	 */
	public int getMaxNbInstance() {
		return maxNbInstance;
	}

	/**
	 * Get number of input data
	 * 
	 * @return Number of input data
	 */
	public int getNbInputData() {
		return nbInputData;
	}

	/**
	 * Get number of instance
	 * 
	 * @return Number of instance
	 */
	public int getNbInstance() {
		return nbInstance;
	}

	/**
	 * Get number of output data
	 * 
	 * @return Number of output data
	 */
	public int getNbOutputData() {
		return nbOutputData;
	}

	/**
	 * Get receive duration
	 * 
	 * @return Receive duration
	 */
	public int getReceiveDuration() {
		return receiveDuration;
	}

	/**
	 * Get send duration
	 * 
	 * @return Send duration
	 */
	public int getSendDuration() {
		return sendDuration;
	}

	/**
	 * Get user interface type
	 * 
	 * @return user interface type
	 */
	public String getUserInterfaceType() {
		return userInterfaceType;
	}

	/**
	 * Is the number of instance fixed
	 * 
	 * @return true/false
	 */
	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Set cadence
	 * 
	 * @param cadence
	 *            Cadence
	 */
	public void setCadence(int cadence) {
		this.cadence = cadence;
	}

	/**
	 * Set communication duration
	 */
	public void setCommunicationDuration() {
		this.communicationDuration = receiveDuration + sendDuration;
	}

	/**
	 * Set execution duration
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
	 * Fix the number of instance
	 */
	public void setFixed() {
		fixed = true;
	}

	/**
	 * Set latency
	 * 
	 * @param latency
	 *            Latency
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}

	/**
	 * Set maximum number of instance
	 */
	public void setMaxNbInstance() {
		maxNbInstance = executionDuration / communicationDuration + 1;
	}

	/**
	 * Set number of input data
	 * 
	 * @param nbInputData
	 *            Number of input data
	 */
	public void setNbInputData(int nbInputData) {
		this.nbInputData = nbInputData;
	}

	/**
	 * Set number of instance
	 * 
	 * @param nbInstance
	 *            number of instance
	 */
	public void setNbInstance(int nbInstance) {
		if (nbInstance < maxNbInstance)
			this.nbInstance = nbInstance;
		else {
			this.nbInstance = maxNbInstance;
		}
	}

	/**
	 * Set number of output data
	 * 
	 * @param nbOutputData
	 *            Number of output data
	 */
	public void setNbOutputData(int nbOutputData) {
		this.nbOutputData = nbOutputData;
	}

	/**
	 * Set receive duration with the connected bus
	 * 
	 * @param bus
	 *            Bus connected to Ip
	 */
	public void setReceiveDuration(BusDescriptor bus) {
		receiveDuration = (int) (bus.getAverageClockCyclesPerTransfer() * 8
				* nbInputData * bus.getClockPeriod() / bus.getDataWidth());
	}

	/**
	 * Set receive duration
	 * 
	 * @param receiveDuration
	 *            Receive duration
	 */
	public void setReceiveDuration(int receiveDuration) {
		this.receiveDuration = receiveDuration;
	}

	/**
	 * Set send duration with the connected bus
	 * 
	 * @param bus
	 *            Bus connected to Ip
	 */
	public void setSendDuration(BusDescriptor bus) {
		sendDuration = (int) (bus.getAverageClockCyclesPerTransfer() * 8
				* nbOutputData * bus.getClockPeriod() / bus.getDataWidth());
	}

	/**
	 * Set send duration
	 * 
	 * @param sendDuration
	 *            Send duration
	 */
	public void setSendDuration(int sendDuration) {
		this.sendDuration = sendDuration;
	}

	/**
	 * Set user interface type
	 * 
	 * @param userInterfaceType
	 *            User interface type
	 */
	public void setUserInterfaceType(String userInterfaceType) {
		this.userInterfaceType = userInterfaceType;
	}
}
