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

import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGEdgePropertyType;

/**
 * CommunicationDescriptor describes a communication edge
 * 
 * @author pmu
 * 
 */
public class CommunicationDescriptor extends OperationDescriptor implements
		Comparable<CommunicationDescriptor> {

	/**
	 * A DAGEdge
	 */
	private DAGEdge edge;

	/**
	 * Id of the origin computation
	 */
	private String originId;

	/**
	 * Id of the destination computation
	 */
	private String destinationId;

	/**
	 * 0 when origin and destination in the same component, otherwise 1
	 */
	private int exist = 1;

	/**
	 * Send link used for this communication
	 */
	private LinkDescriptor sendLink;

	/**
	 * Switch used for this communication
	 */
	private SwitchDescriptor sw;

	/**
	 * Receive link used for this communication
	 */
	private LinkDescriptor receiveLink;

	/**
	 * The communication durations on different links
	 */
	private HashMap<String, Integer> communicationDurations;

	/**
	 * The actual communication duration
	 */
	private int communicationDuration = 0;

	/**
	 * The send overheads on different processors
	 */
	private HashMap<String, Integer> sendOverheads;

	/**
	 * The receive overheads on different processors
	 */
	private HashMap<String, Integer> receiveOverheads;

	/**
	 * The actual send overhead
	 */
	private int sendOverhead = 0;

	/**
	 * The actual receive overhead
	 */
	private int receiveOverhead = 0;

	/**
	 * The send involvements on different processors
	 */
	private HashMap<String, Integer> sendInvolvements;

	/**
	 * The receive involvements on different processors
	 */
	private HashMap<String, Integer> receiveInvolvements;

	/**
	 * The actual send involvement
	 */
	private int sendInvolvement = 0;

	/**
	 * The actual receive involvement
	 */
	private int receiveInvolvement = 0;

	/**
	 * The start time on link
	 */
	private int startTimeOnLink = 0;

	/**
	 * The finish time on link
	 */
	private int finishTimeOnLink = 0;

	/**
	 * The start time on send operator
	 */
	private int startTimeOnSendOperator = 0;

	/**
	 * The finish time on send operator
	 */
	private int finishTimeOnSendOperator = 0;

	/**
	 * The start time on receive operator
	 */
	private int startTimeOnReceiveOperator = 0;

	/**
	 * The finish time on receive operator
	 */
	private int finishTimeOnReceiveOperator = 0;

	/**
	 * The start time on link before the step of select processor
	 */
	private int oldStartTimeOnLink = 0;

	/**
	 * The finish time on link before the step of select processor
	 */
	private int oldFinishTimeOnLink = 0;

	/**
	 * The start time on send operator before the step of select processor
	 */
	private int oldStartTimeOnSendOperator = 0;

	/**
	 * The finish time on send operator before the step of select processor
	 */
	private int oldFinishTimeOnSendOperator = 0;

	/**
	 * The start time on receive operator before the step of select processor
	 */
	private int oldStartTimeOnReceiveOperator = 0;

	/**
	 * The start time on receive operator before the step of select processor
	 */
	private int oldFinishTimeOnReceiveOperator = 0;

	/**
	 * The As Soon As Possible time before the step of select processor
	 */
	private int oldASAP = 0;

	/**
	 * The As Late As Possible time before the step of select processor
	 */
	private int oldALAP = 0;

	/**
	 * A temporary start time on link during the step of select processor
	 */
	private int temporaryStartTimeOnLink = 0;

	/**
	 * A temporary finish time on link during the step of select processor
	 */
	private int temporaryFinishTimeOnLink = 0;

	/**
	 * A temporary start time on send operator during the step of select
	 * processor
	 */
	private int temporaryStartTimeOnSendOperator = 0;

	/**
	 * A temporary finish time on send operator during the step of select
	 * processor
	 */
	private int temporaryFinishTimeOnSendOperator = 0;

	/**
	 * A temporary start time on receive operator during the step of select
	 * processor
	 */
	private int temporaryStartTimeOnReceiveOperator = 0;

	/**
	 * A temporary finish time on receive operator during the step of select
	 * processor
	 */
	private int temporaryFinishTimeOnReceiveOperator = 0;

	/**
	 * A temporary As Soon As Possible time during the step of select processor
	 */
	private int temporaryASAP = 0;

	/**
	 * A temporary As Late As Possible time during the step of select processor
	 */
	private int temporaryALAP = 0;

	/**
	 * Construct the CommunicationDescriptor with the given name and algorithm
	 * 
	 * @param name
	 *            Communication name
	 * @param algorithm
	 *            Algorithm containing this communication
	 */
	public CommunicationDescriptor(String name, AlgorithmDescriptor algorithm) {
		super(name);
		edge = new DAGEdge();
		// edge.setName(name);
		edge.setWeight(new DAGEdgePropertyType(0));
		this.algorithm = algorithm;
		algorithm.addCommunication(this);
		this.type = OperationType.Communication;
		communicationDurations = new HashMap<String, Integer>();
		sendOverheads = new HashMap<String, Integer>();
		receiveOverheads = new HashMap<String, Integer>();
		sendInvolvements = new HashMap<String, Integer>();
		receiveInvolvements = new HashMap<String, Integer>();
	}

	/**
	 * Construct the CommunicationDescriptor with the given name and a buffer of
	 * communication
	 * 
	 * @param name
	 *            Communication name
	 * @param CommunicationDescriptorBuffer
	 *            Buffer of communication
	 */
	public CommunicationDescriptor(
			String name,
			HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer) {
		super(name);
		edge = new DAGEdge();
		// edge.setName(name);
		edge.setWeight(new DAGEdgePropertyType(0));
		CommunicationDescriptorBuffer.put(this.name, this);
		this.type = OperationType.Communication;
		communicationDurations = new HashMap<String, Integer>();
		sendOverheads = new HashMap<String, Integer>();
		receiveOverheads = new HashMap<String, Integer>();
		sendInvolvements = new HashMap<String, Integer>();
		receiveInvolvements = new HashMap<String, Integer>();
	}

	/**
	 * Construct the CommunicationDescriptor with the given name, communication
	 * buffer, origin computation id, destination computation id and weight
	 * 
	 * @param name
	 *            Communication name
	 * @param CommunicationDescriptorBuffer
	 *            Communication buffer
	 * @param originId
	 *            Origin computation Id
	 * @param destinationId
	 *            Destination computation Id
	 * @param weight
	 *            Communication weight
	 */
	public CommunicationDescriptor(
			String name,
			HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer,
			String originId, String destinationId, int weight) {
		super(name);
		edge = new DAGEdge();
		// edge.setName(name);
		edge.setWeight(new DAGEdgePropertyType(weight));
		this.originId = originId;
		this.destinationId = destinationId;
		CommunicationDescriptorBuffer.put(this.name, this);
		this.type = OperationType.Communication;
		communicationDurations = new HashMap<String, Integer>();
		sendOverheads = new HashMap<String, Integer>();
		receiveOverheads = new HashMap<String, Integer>();
		sendInvolvements = new HashMap<String, Integer>();
		receiveInvolvements = new HashMap<String, Integer>();
	}

	/**
	 * Construct the CommunicationDescriptor with the given name, origin
	 * computation id, destination computation id, weight and algorithm
	 * 
	 * @param name
	 *            Communication name
	 * @param originId
	 *            Origin computation Id
	 * @param destinationId
	 *            Destination computation Id
	 * @param weight
	 *            Weight of the communication
	 * @param algorithm
	 *            Algorithm containing this communication
	 */
	public CommunicationDescriptor(String name, String originId,
			String destinationId, int weight, AlgorithmDescriptor algorithm) {
		super(name);
		edge = new DAGEdge();
		// edge.setName(name);
		edge.setWeight(new DAGEdgePropertyType(weight));
		this.originId = originId;
		this.destinationId = destinationId;
		this.algorithm = algorithm;
		algorithm.addCommunication(this);
		this.type = OperationType.Communication;
		communicationDurations = new HashMap<String, Integer>();
		sendOverheads = new HashMap<String, Integer>();
		receiveOverheads = new HashMap<String, Integer>();
		sendInvolvements = new HashMap<String, Integer>();
		receiveInvolvements = new HashMap<String, Integer>();
	}

	/**
	 * Add a communication duration for a link
	 * 
	 * @param link
	 *            A link
	 * @param time
	 *            Communication duration
	 */
	public void addCommunicationDuration(LinkDescriptor link, int time) {
		communicationDurations.put(link.getName(), time);
	}

	/**
	 * Add a communication duration
	 * 
	 * @param name
	 *            Name of link or switch
	 * @param time
	 *            Communication duration
	 */
	public void addCommunicationDuration(String name, int time) {
		communicationDurations.put(name, time);
	}

	/**
	 * Add a communication duration for a switch
	 * 
	 * @param sw
	 *            A switch
	 * @param time
	 *            Communication duration
	 */
	public void addCommunicationDuration(SwitchDescriptor sw, int time) {
		communicationDurations.put(sw.getName(), time);
	}

	/**
	 * Add receive involvement time with a receive link
	 * 
	 * @param receiveLink
	 *            A link
	 * @param time
	 *            The receive involvement time
	 */
	public void addReceiveInvolvement(LinkDescriptor receiveLink, int time) {
		if (!receiveInvolvements.containsKey(receiveLink.getName())) {
			receiveInvolvements.put(receiveLink.getName(), time);
		}
	}

	/**
	 * Add receive involvement time with the name of a receive link
	 * 
	 * @param receiveLinkName
	 *            The name of a receive link
	 * @param time
	 *            The receive involvement time
	 */
	public void addReceiveInvolvement(String receiveLinkName, int time) {
		if (!receiveInvolvements.containsKey(receiveLinkName)) {
			receiveInvolvements.put(receiveLinkName, time);
		}
	}

	/**
	 * Add receive overhead with given receive operator
	 * 
	 * @param receiveOperator
	 *            An operator
	 * @param time
	 *            Receive overhead
	 */
	public void addReceiveOverhead(OperatorDescriptor receiveOperator, int time) {
		if (!receiveOverheads.containsKey(receiveOperator.getName())) {
			receiveOverheads.put(receiveOperator.getName(), time);
		}
	}

	/**
	 * Add receive overhead with given name of receive operator
	 * 
	 * @param receiveOperatorName
	 *            Name of an operator
	 * @param time
	 *            Receive overhead
	 */
	public void addReceiveOverhead(String receiveOperatorName, int time) {
		if (!receiveOverheads.containsKey(receiveOperatorName)) {
			receiveOverheads.put(receiveOperatorName, time);
		}
	}

	/**
	 * Add send involvement time with a send link
	 * 
	 * @param sendLink
	 *            A link
	 * @param time
	 *            Send involvement time
	 */
	public void addSendInvolvement(LinkDescriptor sendLink, int time) {
		if (!sendInvolvements.containsKey(sendLink.getName())) {
			sendInvolvements.put(sendLink.getName(), time);
		}
	}

	/**
	 * Add send involvement time with the name of a send link
	 * 
	 * @param sendLinkName
	 *            The name of a send link
	 * @param time
	 *            Send involvement time
	 */
	public void addSendInvolvement(String sendLinkName, int time) {
		if (!sendInvolvements.containsKey(sendLinkName)) {
			sendInvolvements.put(sendLinkName, time);
		}
	}

	/**
	 * Add send overhead with a given operator
	 * 
	 * @param sendOperator
	 *            An operator
	 * @param time
	 *            Send overhead
	 */
	public void addSendOverhead(OperatorDescriptor sendOperator, int time) {
		if (!sendOverheads.containsKey(sendOperator.getName())) {
			sendOverheads.put(sendOperator.getName(), time);
		}
	}

	/**
	 * Add send overhead with a given name of operator
	 * 
	 * @param sendOperatorName
	 *            Name of operator
	 * @param time
	 *            Send overhead
	 */
	public void addSendOverhead(String sendOperatorName, int time) {
		if (!sendOverheads.containsKey(sendOperatorName)) {
			sendOverheads.put(sendOperatorName, time);
		}
	}

	/**
	 * Backup all the times before the scheduling of the critical child
	 */
	public void backupTimes() {
		temporaryStartTimeOnLink = startTimeOnLink;
		temporaryFinishTimeOnLink = finishTimeOnLink;
		temporaryStartTimeOnSendOperator = startTimeOnSendOperator;
		temporaryFinishTimeOnSendOperator = finishTimeOnSendOperator;
		temporaryStartTimeOnReceiveOperator = startTimeOnReceiveOperator;
		temporaryFinishTimeOnReceiveOperator = finishTimeOnReceiveOperator;
		temporaryASAP = ASAP;
		temporaryALAP = ALAP;
	}

	/**
	 * Clear the existence of the communication
	 */
	public void clearExist() {
		exist = 0;
	}

	@Override
	public int compareTo(CommunicationDescriptor arg0) {
		return (startTimeOnLink - arg0.getStartTimeOnLink());
	}

	/**
	 * Get the actual communication duration
	 * 
	 * @return Communication duration
	 */
	public int getCommunicationDuration() {
		setCommunicationDuration();
		return communicationDuration * exist;
	}

	/**
	 * Get the communication duration with a given link
	 * 
	 * @param link
	 *            A link
	 * @return Communication duration
	 */
	public int getCommunicationDuration(LinkDescriptor link) {
		if (communicationDurations.containsKey(link.getName())) {
			return communicationDurations.get(link.getName());
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Get the communication duration with a given name
	 * 
	 * @param name
	 *            A name of link or switch
	 * @return Communication duration
	 */
	public int getCommunicationDuration(String name) {
		if (communicationDurations.containsKey(name)) {
			return communicationDurations.get(name);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Get the communication duration with a given switch
	 * 
	 * @param sw
	 *            A Switch
	 * @return The communication duration with the switch
	 */
	public int getCommunicationDuration(SwitchDescriptor sw) {
		if (communicationDurations.containsKey(sw.getName())) {
			return communicationDurations.get(sw.getName());
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Get all the communication durations
	 * 
	 * @return A HashMap of all the communication durations
	 */
	public HashMap<String, Integer> getCommunicationDurations() {
		return communicationDurations;
	}

	/**
	 * Get destination computation Id
	 * 
	 * @return destination computation Id
	 */
	public String getDestination() {
		return destinationId;
	}

	/**
	 * Get the associated DAGEdge
	 * 
	 * @return A DAGEdge
	 */
	public DAGEdge getEdge() {
		return edge;
	}

	/**
	 * Get finish time on link
	 * 
	 * @return Finish time on link
	 */
	public int getFinishTimeOnLink() {
		setCommunicationDuration();
		finishTimeOnLink = startTimeOnLink + communicationDuration * exist;
		return finishTimeOnLink;
	}

	/**
	 * Get finish time on receive operator
	 * 
	 * @return Finish time on receive operator
	 */
	public int getFinishTimeOnReceiveOperator() {
		finishTimeOnReceiveOperator = startTimeOnReceiveOperator
				+ (receiveOverhead + receiveInvolvement) * exist;
		return finishTimeOnReceiveOperator;
	}

	/**
	 * Get finish time on send operator
	 * 
	 * @return Finish time on send operator
	 */
	public int getFinishTimeOnSendOperator() {
		finishTimeOnSendOperator = startTimeOnSendOperator
				+ (sendOverhead + sendInvolvement) * exist;
		return finishTimeOnSendOperator;
	}

	/**
	 * Get origin computation Id
	 * 
	 * @return Origin computation Id
	 */
	public String getOrigin() {
		return originId;
	}

	/**
	 * Get actual receive involvement time
	 * 
	 * @return Receive involvement time
	 */
	public int getReceiveInvolvement() {
		return receiveInvolvement * exist;
	}

	/**
	 * Get the receive involvement time with a receive link
	 * 
	 * @param receiveLink
	 *            A link
	 * @return Receive involvement time
	 */
	public int getReceiveInvolvement(LinkDescriptor receiveLink) {
		return receiveInvolvements.get(receiveLink.getName());
	}

	/**
	 * Get the receive involvement time with the name of a receive link
	 * 
	 * @param receiveLinkName
	 *            The name of a receive link
	 * @return Receive involvement time
	 */
	public int getReceiveInvolvement(String receiveLinkName) {
		return receiveInvolvements.get(receiveLinkName);
	}

	/**
	 * Get all the receive involvements time
	 * 
	 * @return A HashMap of all the receive involvements time
	 */
	public HashMap<String, Integer> getReceiveInvolvements() {
		return receiveInvolvements;
	}

	/**
	 * Get the receive link
	 * 
	 * @return The receive link
	 */
	public LinkDescriptor getReceiveLink() {
		return receiveLink;
	}

	/**
	 * Get the actual receive overhead
	 * 
	 * @return Receive overhead
	 */
	public int getReceiveOverhead() {
		return receiveOverhead * exist;
	}

	/**
	 * Get the receive overhead with a given receive operator
	 * 
	 * @param receiveOperator
	 *            An operator
	 * @return Receive overhead
	 */
	public int getReceiveOverhead(OperatorDescriptor receiveOperator) {
		return receiveOverheads.get(receiveOperator.getName());
	}

	/**
	 * Get the receive overhead with a given name of receive operator
	 * 
	 * @param receiveOperatorName
	 *            A name of an operator
	 * @return Receive overhead
	 */
	public int getReceiveOverhead(String receiveOperatorName) {
		return receiveOverheads.get(receiveOperatorName);
	}

	/**
	 * Get all the receive overheads
	 * 
	 * @return A HashMap of all the receive overheads
	 */
	public HashMap<String, Integer> getReceiveOverheads() {
		return receiveOverheads;
	}

	/**
	 * Get actual send involvement time
	 * 
	 * @return Send involvement time
	 */
	public int getSendInvolvement() {
		return sendInvolvement * exist;
	}

	/**
	 * Get the send involvement time with the given send link
	 * 
	 * @param sendLink
	 *            A send link
	 * @return Send involvement time
	 */
	public int getSendInvolvement(LinkDescriptor sendLink) {
		return sendInvolvements.get(sendLink.getName());
	}

	/**
	 * Get the send involvement time with the name of a given send link
	 * 
	 * @param sendLinkName
	 *            The name of a given send link
	 * @return Send involvement time
	 */
	public int getSendInvolvement(String sendLinkName) {
		return sendInvolvements.get(sendLinkName);
	}

	/**
	 * Get all the send involvements time
	 * 
	 * @return A HashMap of all the send involvements time
	 */
	public HashMap<String, Integer> getSendInvolvements() {
		return sendInvolvements;
	}

	/**
	 * Get the send link
	 * 
	 * @return The send link
	 */
	public LinkDescriptor getSendLink() {
		return sendLink;
	}

	/**
	 * Get the actual send overhead
	 * 
	 * @return Send overhead
	 */
	public int getSendOverhead() {
		return sendOverhead * exist;
	}

	/**
	 * Get the send overhead with the send operator
	 * 
	 * @param sendOperator
	 *            An operator
	 * @return Send overhead
	 */
	public int getSendOverhead(OperatorDescriptor sendOperator) {
		return sendOverheads.get(sendOperator.getName());
	}

	/**
	 * Get the send overhead with the name of send operator
	 * 
	 * @param sendOperatorName
	 *            Name of an operator
	 * @return Send overhead
	 */
	public int getSendOverhead(String sendOperatorName) {
		return sendOverheads.get(sendOperatorName);
	}

	/**
	 * Get all the send overheads
	 * 
	 * @return A HashMap of all the overheads
	 */
	public HashMap<String, Integer> getSendOverheads() {
		return sendOverheads;
	}

	/**
	 * Get start time on link
	 * 
	 * @return Start time on link
	 */
	public int getStartTimeOnLink() {
		return startTimeOnLink;
	}

	/**
	 * Get start time on receive operator
	 * 
	 * @return Start time on receive operator
	 */
	public int getStartTimeOnReceiveOperator() {
		return startTimeOnReceiveOperator;
	}

	/**
	 * Get start time on send operator
	 * 
	 * @return Start time on send operator
	 */
	public int getStartTimeOnSendOperator() {
		return startTimeOnSendOperator;
	}

	/**
	 * Get the switch
	 * 
	 * @return The switch
	 */
	public SwitchDescriptor getSwitch() {
		return sw;
	}

	/**
	 * Get communication weight
	 * 
	 * @return Communication weight
	 */
	public int getWeight() {
		return edge.getWeight().intValue();
	}

	/**
	 * Does the communication exist?
	 * 
	 * @return A boolean indicating the existence of the communication
	 */
	public boolean isExist() {
		if (exist == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Recover all the times after the scheduling of the critical child
	 */
	public void recoverTimes() {
		startTimeOnLink = temporaryStartTimeOnLink;
		finishTimeOnLink = temporaryFinishTimeOnLink;
		startTimeOnSendOperator = temporaryStartTimeOnSendOperator;
		finishTimeOnSendOperator = temporaryFinishTimeOnSendOperator;
		startTimeOnReceiveOperator = temporaryStartTimeOnReceiveOperator;
		finishTimeOnReceiveOperator = temporaryFinishTimeOnReceiveOperator;
		ASAP = temporaryASAP;
		ALAP = temporaryALAP;
	}

	/**
	 * Restore all the times when finishing the selecting processor
	 */
	public void restoreTimes() {
		startTimeOnLink = oldStartTimeOnLink;
		finishTimeOnLink = oldFinishTimeOnLink;
		startTimeOnSendOperator = oldStartTimeOnSendOperator;
		finishTimeOnSendOperator = oldFinishTimeOnSendOperator;
		startTimeOnReceiveOperator = oldStartTimeOnReceiveOperator;
		finishTimeOnReceiveOperator = oldFinishTimeOnReceiveOperator;
		ASAP = oldASAP;
		ALAP = oldALAP;
	}

	/**
	 * Set communication duration
	 */
	private void setCommunicationDuration() {
		// if (sw != null) {
		// communicationDuration = (int) (edge.getWeight().intValue() * 8
		// / sw.getDataWidth()
		// * sw.getAverageClockCyclesPerTransfer()
		// * sw.getClockPeriod() * exist);
		// } else {
		// communicationDuration = edge.getWeight().intValue() * exist;
		// }
		if (sw != null) {
			communicationDuration = communicationDurations.get(sw.getName());
		} else {
			communicationDuration = edge.getWeight().intValue();
		}
	}

	/**
	 * Set communication duration
	 * 
	 * @param communicationDuration
	 *            Communication duration
	 */
	public void setCommunicationDuration(int communicationDuration) {
		this.communicationDuration = communicationDuration;
	}

	/**
	 * Set destination computation Id
	 * 
	 * @param destinationId
	 *            Destination computation Id
	 */
	public void setDestination(String destinationId) {
		this.destinationId = destinationId;
	}

	/**
	 * Set the associated DAGEdge
	 * 
	 * @param edge
	 *            A DAGEdge
	 */
	public void setEdge(DAGEdge edge) {
		this.edge = edge;
	}

	/**
	 * Mark the existence of the communication
	 */
	public void setExist() {
		exist = 1;
	}

	/**
	 * Set finish time on link
	 */
	public void setFinishTimeOnLink() {
		setCommunicationDuration();
		finishTimeOnLink = startTimeOnLink + communicationDuration * exist;
	}

	/**
	 * Set finish time on link
	 * 
	 * @param finishTimeOnLink
	 *            Finish time on link
	 */
	public void setFinishTimeOnLink(int finishTimeOnLink) {
		this.finishTimeOnLink = finishTimeOnLink;
		startTimeOnLink = finishTimeOnLink - communicationDuration * exist;
	}

	/**
	 * Set finish time on receive operator
	 */
	public void setFinishTimeOnReceiveOperator() {
		finishTimeOnReceiveOperator = startTimeOnReceiveOperator
				+ (receiveOverhead + receiveInvolvement) * exist;
	}

	/**
	 * Set finish time on send operator
	 */
	public void setFinishTimeOnSendOperator() {
		finishTimeOnSendOperator = startTimeOnSendOperator
				+ (sendOverhead + sendInvolvement) * exist;
	}

	/**
	 * Set origin computation Id
	 * 
	 * @param originId
	 *            Origin computation Id
	 */
	public void setOrigin(String originId) {
		this.originId = originId;
	}

	/**
	 * Set receive involvement time
	 * 
	 * @param receiveInvolvement
	 *            Receive involvement time
	 */
	public void setReceiveInvolvement(int receiveInvolvement) {
		this.receiveInvolvement = receiveInvolvement;
	}

	/**
	 * Set the receive link
	 * 
	 * @param receiveLink
	 *            The receive link
	 */
	public void setReceiveLink(LinkDescriptor receiveLink) {
		this.receiveLink = receiveLink;
	}

	/**
	 * Set receive overhead
	 * 
	 * @param receiveOverhead
	 *            Receive overhead
	 */
	public void setReceiveOverhead(int receiveOverhead) {
		this.receiveOverhead = receiveOverhead;
	}

	/**
	 * Set send involvement time
	 * 
	 * @param sendInvolvement
	 *            Send involvement time
	 */
	public void setSendInvolvement(int sendInvolvement) {
		this.sendInvolvement = sendInvolvement;
	}

	/**
	 * Set the send link
	 * 
	 * @param sendLink
	 *            The send link
	 */
	public void setSendLink(LinkDescriptor sendLink) {
		this.sendLink = sendLink;
	}

	/**
	 * Set send overhead
	 * 
	 * @param sendOverhead
	 *            Send overhead
	 */
	public void setSendOverhead(int sendOverhead) {
		this.sendOverhead = sendOverhead;
	}

	/**
	 * Set start time on link
	 * 
	 * @param startTimeOnLink
	 *            Start time on link
	 */
	public void setStartTimeOnLink(int startTimeOnLink) {
		this.startTimeOnLink = startTimeOnLink;
		setFinishTimeOnLink();
	}

	/**
	 * Set start time on receive operator
	 * 
	 * @param startTimeOnReceiveOperator
	 *            Start time on receive operator
	 */
	public void setStartTimeOnReceiveOperator(int startTimeOnReceiveOperator) {
		this.startTimeOnReceiveOperator = startTimeOnReceiveOperator;
		setFinishTimeOnReceiveOperator();
	}

	/**
	 * Set start time on send operator
	 * 
	 * @param startTimeOnSendOperator
	 *            Start time on send operator
	 */
	public void setStartTimeOnSendOperator(int startTimeOnSendOperator) {
		this.startTimeOnSendOperator = startTimeOnSendOperator;
		setFinishTimeOnSendOperator();
	}

	/**
	 * Set the switch
	 * 
	 * @param sw
	 *            The switch
	 */
	public void setSwitch(SwitchDescriptor sw) {
		this.sw = sw;
	}

	/**
	 * Set communication weight
	 * 
	 * @param weight
	 *            Communication weight
	 */
	public void setWeight(int weight) {
		if (edge.getWeight() == null) {
			edge.setWeight(new DAGEdgePropertyType(weight));
		} else {
			((DAGEdgePropertyType) edge.getWeight()).setValue(weight);
		}
	}

	/**
	 * Update all the times when finishing the real scheduling of a node
	 */
	public void updateTimes() {
		oldStartTimeOnLink = startTimeOnLink;
		oldFinishTimeOnLink = finishTimeOnLink;
		oldStartTimeOnSendOperator = startTimeOnSendOperator;
		oldFinishTimeOnSendOperator = finishTimeOnSendOperator;
		oldStartTimeOnReceiveOperator = startTimeOnReceiveOperator;
		oldFinishTimeOnReceiveOperator = finishTimeOnReceiveOperator;
		oldASAP = ASAP;
		oldALAP = ALAP;
	}
}
