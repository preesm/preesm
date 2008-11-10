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
