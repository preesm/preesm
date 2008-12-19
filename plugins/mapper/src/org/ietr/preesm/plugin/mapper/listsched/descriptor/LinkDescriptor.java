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
 * This class describes a link
 * 
 * @author pmu
 * 
 */
public class LinkDescriptor extends ComponentDescriptor {

	/**
	 * Average clock cycles per transfer
	 */
	protected double averageClockCyclesPerTransfer = 1.0;

	/**
	 * Communications scheduled on this link
	 */
	protected Vector<CommunicationDescriptor> communications;

	/**
	 * Occupied time intervals on this link
	 */
	protected HashMap<String, TimeInterval> occupiedTimeIntervals;

	/**
	 * Construct the LinkDescriptor with the id, name and component buffer
	 * 
	 * @param id
	 *            Id
	 * @param name
	 *            Name
	 * @param ComponentDescriptorBuffer
	 *            Component buffer
	 */
	public LinkDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		communications = new Vector<CommunicationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	/**
	 * Construct the LinkDescriptor with the id, name, component buffer, clock
	 * period, data width and surface
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
	 *            Surface
	 */
	public LinkDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		communications = new Vector<CommunicationDescriptor>();
		occupiedTimeIntervals = new HashMap<String, TimeInterval>();
	}

	/**
	 * Add a communication on the link
	 * 
	 * @param communication
	 *            Communication
	 */
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

	/**
	 * Add the communication at a specified location
	 * 
	 * @param index
	 *            The specified location
	 * @param communication
	 *            Communication
	 */
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

	/**
	 * Add a time interval on the link
	 * 
	 * @param communicationName
	 *            Name of communication
	 * @param startTime
	 *            Start time of the communication
	 * @param finishTime
	 *            Finish time of the communication
	 */
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

	/**
	 * Get average clock cycles per transfer
	 * 
	 * @return Average clock cycles per transfer
	 */
	public double getAverageClockCyclesPerTransfer() {
		return averageClockCyclesPerTransfer;
	}

	/**
	 * Get the communication at a specified location
	 * 
	 * @param index
	 *            The specified location
	 * @return Communication
	 */
	public CommunicationDescriptor getCommunication(int index) {
		return communications.get(index);
	}

	/**
	 * Get all the communications on this link
	 * 
	 * @return A Vector of all the communications
	 */
	public Vector<CommunicationDescriptor> getCommunications() {
		return communications;
	}

	/**
	 * Get the time interval with a communication name
	 * 
	 * @param communicationName
	 *            Name of communication
	 * @return Time interval
	 */
	public TimeInterval getOccupiedTimeInterval(String communicationName) {
		return occupiedTimeIntervals.get(communicationName);
	}

	/**
	 * Get all the occupied time intervals
	 * 
	 * @return A HashMap of all the occupied time intervals
	 */
	public HashMap<String, TimeInterval> getOccupiedTimeIntervals() {
		return occupiedTimeIntervals;
	}

	/**
	 * Remove a communication from the link
	 * 
	 * @param communication
	 *            Removed communication
	 */
	public void removeCommunication(CommunicationDescriptor communication) {
		communications.remove(communication);
		occupiedTimeIntervals.remove(communication.getName());
	}

	/**
	 * Set average clock cycles per transfer
	 * 
	 * @param cycles
	 *            Average clock cycles per transfer
	 */
	public void setAverageClockCyclesPerTransfer(double cycles) {
		this.averageClockCyclesPerTransfer = cycles;
	}

	/**
	 * Update the time interval with the communication
	 * 
	 * @param communication
	 *            Communication
	 */
	public void updateCommunication(CommunicationDescriptor communication) {
		addOccupiedTimeInterval(communication.getName(), communication
				.getStartTimeOnLink(), communication.getFinishTimeOnLink());
	}
}
