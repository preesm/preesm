package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor;

import java.util.HashMap;
import java.util.Vector;

public class OperatorDescriptor extends TGVertexDescriptor {

	protected Vector<ComputationDescriptor> computations;

	protected Vector<CommunicationDescriptor> sendCommunications;

	protected Vector<CommunicationDescriptor> receiveCommunications;

	protected Vector<OperationDescriptor> operations;

	protected HashMap<String, TimeInterval> occupiedTimeIntervals;

	protected int finishTime = 0;

	public OperatorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		computations = new Vector<ComputationDescriptor>();
		sendCommunications = new Vector<CommunicationDescriptor>();
		receiveCommunications = new Vector<CommunicationDescriptor>();
		operations = new Vector<OperationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	public OperatorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		computations = new Vector<ComputationDescriptor>();
		sendCommunications = new Vector<CommunicationDescriptor>();
		receiveCommunications = new Vector<CommunicationDescriptor>();
		operations = new Vector<OperationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	// sendCommunication
	public void addSendCommunication(CommunicationDescriptor communication) {
		sendCommunications.add(communication);
		occupiedTimeIntervals.put(communication.getName(), new TimeInterval(
				communication.getStartTimeOnSendOperator(), communication
						.getFinishTimeOnSendOperator()));
	}

	public void removeSendCommunication(CommunicationDescriptor communication) {
		sendCommunications.remove(communication);
		occupiedTimeIntervals.remove(communication.getName());
	}

	public Vector<CommunicationDescriptor> getSendCommunications() {
		return sendCommunications;
	}

	// receiveCommunication
	public void addReceiveCommunication(CommunicationDescriptor communication) {
		receiveCommunications.add(communication);
		occupiedTimeIntervals.put(communication.getName(), new TimeInterval(
				communication.getStartTimeOnReceiveOperator(), communication
						.getFinishTimeOnReceiveOperator()));
	}

	public void removeReceiveCommunication(CommunicationDescriptor communication) {
		receiveCommunications.remove(communication);
		occupiedTimeIntervals.remove(communication.getName());
	}

	public Vector<CommunicationDescriptor> getReceiveCommunications() {
		return receiveCommunications;
	}

	// Computation
	public void addComputation(ComputationDescriptor computation) {
		computations.add(computation);
		occupiedTimeIntervals.put(computation.getName(), new TimeInterval(
				computation.getStartTime(), computation.getFinishTime()));
	}

	public void removeComputation(ComputationDescriptor computation) {
		computations.remove(computation);
		occupiedTimeIntervals.remove(computation.getName());
	}

	public Vector<ComputationDescriptor> getComputations() {
		return computations;
	}

	// Operation
	public void addOperation(OperationDescriptor operation) {
		operations.add(operation);
	}

	public void addOperation(int index, OperationDescriptor operation) {
		operations.add(index, operation);
	}

	public OperationDescriptor getOperation(int index) {
		return operations.get(index);
	}

	public Vector<OperationDescriptor> getOperations() {
		return operations;
	}

	public void removeOperation(OperationDescriptor operation) {
		operations.remove(operation);
	}

	public void addOccupiedTimeInterval(String operationName, int startTime,
			int finishTime) {
		if (!occupiedTimeIntervals.containsKey(operationName)) {
			occupiedTimeIntervals.put(operationName, new TimeInterval(
					startTime, finishTime));
		} else {
			occupiedTimeIntervals.get(operationName).setStartTime(startTime);
			occupiedTimeIntervals.get(operationName).setFinishTime(finishTime);
		}
	}

	public HashMap<String, TimeInterval> getOccupiedTimeIntervals() {
		return occupiedTimeIntervals;
	}

	public TimeInterval getOccupiedTimeInterval(String operationName) {
		return occupiedTimeIntervals.get(operationName);
	}

	public void removeOccupiedTimeInterval(String operationName) {
		occupiedTimeIntervals.remove(operationName);
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
}
