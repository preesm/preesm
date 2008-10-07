/**
 * PREESM
 * Copyright IETR 2007 under CeCILL license.
 *
 * Pengcheng MU <Pengcheng.Mu@ens.insa-rennes.fr>
 */
package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor;

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
