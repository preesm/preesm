package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor;

import java.util.HashMap;
import java.util.Vector;

public class LinkDescriptor extends ComponentDescriptor {

	protected double averageClockCyclesPerTransfer = 1.0;

	protected Vector<CommunicationDescriptor> communications;

	protected HashMap<String, TimeInterval> occupiedTimeIntervals;

	public LinkDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		communications = new Vector<CommunicationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	public LinkDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		communications = new Vector<CommunicationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	public void setAverageClockCyclesPerTransfer(
			double averageClockCyclesPerTransfer) {
		this.averageClockCyclesPerTransfer = averageClockCyclesPerTransfer;
	}

	public double getAverageClockCyclesPerTransfer() {
		return averageClockCyclesPerTransfer;
	}

	public void addCommunication(CommunicationDescriptor communication) {
		if (!communications.contains(communication)) {
			communications.add(communication);
			addOccupiedTimeInterval(communication.getName(), communication
					.getStartTimeOnLink(), communication.getFinishTimeOnLink());
		} else {
			addOccupiedTimeInterval(communication.getName(), communication
					.getStartTimeOnLink(), communication.getFinishTimeOnLink());
		}
	}

	public void updateCommunication(CommunicationDescriptor communication) {
		addOccupiedTimeInterval(communication.getName(), communication
				.getStartTimeOnLink(), communication.getFinishTimeOnLink());
	}

	public void addCommunication(int index,
			CommunicationDescriptor communication) {
		if (!communications.contains(communication)) {
			communications.add(index, communication);
			addOccupiedTimeInterval(communication.getName(), communication
					.getStartTimeOnLink(), communication.getFinishTimeOnLink());
		} else {
			addOccupiedTimeInterval(communication.getName(), communication
					.getStartTimeOnLink(), communication.getFinishTimeOnLink());
		}
	}

	public void removeCommunication(CommunicationDescriptor communication) {
		communications.remove(communication);
		occupiedTimeIntervals.remove(communication.getName());
	}

	public Vector<CommunicationDescriptor> getCommunications() {
		return communications;
	}

	public CommunicationDescriptor getCommunication(int index) {
		return communications.get(index);
	}

	public HashMap<String, TimeInterval> getOccupiedTimeIntervals() {
		return occupiedTimeIntervals;
	}

	public TimeInterval getOccupiedTimeInterval(String communicationName) {
		return occupiedTimeIntervals.get(communicationName);
	}

	public void addOccupiedTimeInterval(String communicationName,
			int startTime, int finishTime) {
		if (!occupiedTimeIntervals.containsKey(communicationName)) {
			occupiedTimeIntervals.put(communicationName, new TimeInterval(
					startTime, finishTime));
		} else {
			occupiedTimeIntervals.get(communicationName)
					.setStartTime(startTime);
			occupiedTimeIntervals.get(communicationName).setFinishTime(
					finishTime);
		}
	}
}
