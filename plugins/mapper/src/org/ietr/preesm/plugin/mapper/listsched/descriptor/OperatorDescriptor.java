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
import java.util.Vector;

/**
 * This class gives the description of an operator, which could be a processor
 * or an Ip
 * 
 * @author pmu
 * 
 */
public class OperatorDescriptor extends TGVertexDescriptor {
	/**
	 * Computations scheduled onto this operator
	 */
	protected Vector<ComputationDescriptor> computations;

	/**
	 * Send communications on this operator
	 */
	protected Vector<CommunicationDescriptor> sendCommunications;

	/**
	 * Receive communications on this operator
	 */
	protected Vector<CommunicationDescriptor> receiveCommunications;

	/**
	 * All the operations scheduled on this operator
	 */
	protected Vector<OperationDescriptor> operations;

	/**
	 * All the occupied time intervals on this operator
	 */
	protected HashMap<String, TimeInterval> occupiedTimeIntervals;

	/**
	 * The finish time of the operator, that is the finish time of the last
	 * operation on this operator
	 */
	protected int finishTime = 0;

	/**
	 * Construct an OperatorDescriptor with the given id, name and component
	 * buffer
	 * 
	 * @param id
	 *            Operator Id
	 * @param name
	 *            Operator name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 */
	public OperatorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		computations = new Vector<ComputationDescriptor>();
		sendCommunications = new Vector<CommunicationDescriptor>();
		receiveCommunications = new Vector<CommunicationDescriptor>();
		operations = new Vector<OperationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	/**
	 * Construct an OperatorDescriptor with the given id, name, component
	 * buffer, clock period, data width and surface
	 * 
	 * @param id
	 *            Operator Id
	 * @param name
	 *            Operator name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 * @param clockPeriod
	 *            Clock period
	 * @param dataWidth
	 *            Data width
	 * @param surface
	 *            Surface
	 */
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

	/**
	 * Add a computation
	 * 
	 * @param computation
	 *            A computation
	 */
	public void addComputation(ComputationDescriptor computation) {
		computations.add(computation);
		occupiedTimeIntervals.put(computation.getName(), new TimeInterval(
				computation.getStartTime(), computation.getFinishTime()));
	}

	/**
	 * Add an time interval occupied by an operation
	 * 
	 * @param operationName
	 *            Operation name
	 * @param startTime
	 *            Start time of the operation
	 * @param finishTime
	 *            Finish time of the operation
	 */
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

	/**
	 * Add an operation at a specified location
	 * 
	 * @param index
	 *            The specified location
	 * @param operation
	 *            The operation
	 */
	public void addOperation(int index, OperationDescriptor operation) {
		operations.add(index, operation);
	}

	/**
	 * Add an operation
	 * 
	 * @param operation
	 *            An operation
	 */
	public void addOperation(OperationDescriptor operation) {
		operations.add(operation);
	}

	/**
	 * Add a receive communication on this operator
	 * 
	 * @param communication
	 *            A receive communication
	 */
	public void addReceiveCommunication(CommunicationDescriptor communication) {
		receiveCommunications.add(communication);
		occupiedTimeIntervals.put(communication.getName(), new TimeInterval(
				communication.getStartTimeOnReceiveOperator(), communication
						.getFinishTimeOnReceiveOperator()));
	}

	/**
	 * Add a send communication on this operator
	 * 
	 * @param communication
	 *            A send communication
	 */
	public void addSendCommunication(CommunicationDescriptor communication) {
		sendCommunications.add(communication);
		occupiedTimeIntervals.put(communication.getName(), new TimeInterval(
				communication.getStartTimeOnSendOperator(), communication
						.getFinishTimeOnSendOperator()));
	}

	/**
	 * Get all the computations
	 * 
	 * @return All the computations
	 */
	public Vector<ComputationDescriptor> getComputations() {
		return computations;
	}

	/**
	 * Get the finish time of the operator
	 * 
	 * @return The finish time of the operator
	 */
	public int getFinishTime() {
		return finishTime;
	}

	/**
	 * Get the time interval occupied by the given operation
	 * 
	 * @param operationName
	 *            Operation name
	 * @return The time interval
	 */
	public TimeInterval getOccupiedTimeInterval(String operationName) {
		return occupiedTimeIntervals.get(operationName);
	}

	/**
	 * Get all the occupied time intervals
	 * 
	 * @return A HashMap of all the time intervals
	 */
	public HashMap<String, TimeInterval> getOccupiedTimeIntervals() {
		return occupiedTimeIntervals;
	}

	/**
	 * Get the operation at a specified location
	 * 
	 * @param index
	 *            The specified location
	 * @return The operation
	 */
	public OperationDescriptor getOperation(int index) {
		return operations.get(index);
	}

	/**
	 * Get all the operations
	 * 
	 * @return All the operations
	 */
	public Vector<OperationDescriptor> getOperations() {
		return operations;
	}

	/**
	 * Get all the receive communications
	 * 
	 * @return All the receive communications
	 */
	public Vector<CommunicationDescriptor> getReceiveCommunications() {
		return receiveCommunications;
	}

	/**
	 * Get all the send communications
	 * 
	 * @return All the send communications
	 */
	public Vector<CommunicationDescriptor> getSendCommunications() {
		return sendCommunications;
	}

	/**
	 * Remove a computation
	 * 
	 * @param computation
	 *            A computation
	 */
	public void removeComputation(ComputationDescriptor computation) {
		computations.remove(computation);
		occupiedTimeIntervals.remove(computation.getName());
	}

	/**
	 * Remove an time interval occupied by an given operation
	 * 
	 * @param operationName
	 *            Operation name
	 */
	public void removeOccupiedTimeInterval(String operationName) {
		occupiedTimeIntervals.remove(operationName);
	}

	/**
	 * Remove an operation
	 * 
	 * @param operation
	 *            The operation to be removed
	 */
	public void removeOperation(OperationDescriptor operation) {
		operations.remove(operation);
	}

	/**
	 * Remove a receive communication
	 * 
	 * @param communication
	 *            A receive communication
	 */
	public void removeReceiveCommunication(CommunicationDescriptor communication) {
		receiveCommunications.remove(communication);
		occupiedTimeIntervals.remove(communication.getName());
	}

	/**
	 * Remove a send communication
	 * 
	 * @param communication
	 *            A send communication
	 */
	public void removeSendCommunication(CommunicationDescriptor communication) {
		sendCommunications.remove(communication);
		occupiedTimeIntervals.remove(communication.getName());
	}

	/**
	 * Set the finish time of the operator
	 * 
	 * @param finishTime
	 *            The finish time of the operator
	 */
	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}
}
