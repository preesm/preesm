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
