package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;

import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGEdgePropertyType;

public class CommunicationDescriptor extends OperationDescriptor implements
		Comparable<CommunicationDescriptor> {

	private DAGEdge edge;

	private String sourceId;

	private String destinationId;

	private int exist = 1; // 0 when source and destination in the same
	// component, otherwise 1

	private LinkDescriptor sendLink;

	private SwitchDescriptor sw;

	private LinkDescriptor receiveLink;

	private HashMap<String, Integer> communicationDurations;

	private int communicationDuration = 0;

	private HashMap<String, Integer> sendOverheads;

	private HashMap<String, Integer> receiveOverheads;

	private int sendOverhead = 0;

	private int receiveOverhead = 0;

	private HashMap<String, Integer> sendInvolvements;

	private HashMap<String, Integer> receiveInvolvements;

	private int sendInvolvement = 0;

	private int receiveInvolvement = 0;

	private int startTimeOnLink = 0;

	private int finishTimeOnLink = 0;

	private int startTimeOnSendOperator = 0;

	private int finishTimeOnSendOperator = 0;

	private int startTimeOnReceiveOperator = 0;

	private int finishTimeOnReceiveOperator = 0;

	private int oldStartTimeOnLink = 0;

	private int oldFinishTimeOnLink = 0;

	private int oldStartTimeOnSendOperator = 0;

	private int oldFinishTimeOnSendOperator = 0;

	private int oldStartTimeOnReceiveOperator = 0;

	private int oldFinishTimeOnReceiveOperator = 0;

	private int oldASAP = 0;

	private int oldALAP = 0;

	private int temporaryStartTimeOnLink = 0;

	private int temporaryFinishTimeOnLink = 0;

	private int temporaryStartTimeOnSendOperator = 0;

	private int temporaryFinishTimeOnSendOperator = 0;

	private int temporaryStartTimeOnReceiveOperator = 0;

	private int temporaryFinishTimeOnReceiveOperator = 0;

	private int temporaryASAP = 0;

	private int temporaryALAP = 0;

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

	public CommunicationDescriptor(String name, String sourceId,
			String destinationId, int weight, AlgorithmDescriptor algorithm) {
		super(name);
		edge = new DAGEdge();
		// edge.setName(name);
		edge.setWeight(new DAGEdgePropertyType(weight));
		this.sourceId = sourceId;
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

	public CommunicationDescriptor(
			String name,
			HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer,
			String sourceId, String destinationId, int weight) {
		super(name);
		edge = new DAGEdge();
		// edge.setName(name);
		edge.setWeight(new DAGEdgePropertyType(weight));
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		CommunicationDescriptorBuffer.put(this.name, this);
		this.type = OperationType.Communication;
		communicationDurations = new HashMap<String, Integer>();
		sendOverheads = new HashMap<String, Integer>();
		receiveOverheads = new HashMap<String, Integer>();
		sendInvolvements = new HashMap<String, Integer>();
		receiveInvolvements = new HashMap<String, Integer>();
	}

	public DAGEdge getEdge() {
		return edge;
	}

	public void setEdge(DAGEdge edge) {
		this.edge = edge;
	}

	public void setWeight(int weight) {
		if (edge.getWeight() == null) {
			edge.setWeight(new DAGEdgePropertyType(weight));
		} else {
			((DAGEdgePropertyType) edge.getWeight()).setValue(weight);
		}
	}

	public int getWeight() {
		return edge.getWeight().intValue();
	}

	public void setSource(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSource() {
		return sourceId;
	}

	public void setDestination(String destinationId) {
		this.destinationId = destinationId;
	}

	public String getDestination() {
		return destinationId;
	}

	public void setExist() {
		exist = 1;
	}

	public void clearExist() {
		exist = 0;
	}

	public boolean isExist() {
		if (exist == 0) {
			return false;
		} else {
			return true;
		}
	}

	public LinkDescriptor getSendLink() {
		return sendLink;
	}

	public void setSendLink(LinkDescriptor sendLink) {
		this.sendLink = sendLink;
	}

	public SwitchDescriptor getSwitch() {
		return sw;
	}

	public void setSwitch(SwitchDescriptor sw) {
		this.sw = sw;
	}

	public LinkDescriptor getReceiveLink() {
		return receiveLink;
	}

	public void setReceiveLink(LinkDescriptor receiveLink) {
		this.receiveLink = receiveLink;
	}

	public void addCommunicationDuration(SwitchDescriptor sw, int time) {
		communicationDurations.put(sw.getName(), time);
	}

	public void addCommunicationDuration(String name, int time) {
		communicationDurations.put(name, time);
	}

	public void addCommunicationDuration(LinkDescriptor link, int time) {
		communicationDurations.put(link.getName(), time);
	}

	public HashMap<String, Integer> getCommunicationDurations() {
		return communicationDurations;
	}

	public int getCommunicationDuration(SwitchDescriptor sw) {
		if (communicationDurations.containsKey(sw.getName())) {
			return communicationDurations.get(sw.getName());
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public int getCommunicationDuration(String name) {
		if (communicationDurations.containsKey(name)) {
			return communicationDurations.get(name);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public int getCommunicationDuration(LinkDescriptor link) {
		if (communicationDurations.containsKey(link.getName())) {
			return communicationDurations.get(link.getName());
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public int getCommunicationDuration() {
		setCommunicationDuration();
		return communicationDuration * exist;
	}

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

	public void setCommunicationDuration(int communicationDuration) {
		this.communicationDuration = communicationDuration;
	}

	public void addSendOverhead(OperatorDescriptor sendOperator, int time) {
		if (!sendOverheads.containsKey(sendOperator.getName())) {
			sendOverheads.put(sendOperator.getName(), time);
		}
	}

	public void addSendOverhead(String sendOperatorName, int time) {
		if (!sendOverheads.containsKey(sendOperatorName)) {
			sendOverheads.put(sendOperatorName, time);
		}
	}

	public HashMap<String, Integer> getSendOverheads() {
		return sendOverheads;
	}

	public int getSendOverhead(OperatorDescriptor sendOperator) {
		return sendOverheads.get(sendOperator.getName());
	}

	public int getSendOverhead(String sendOperatorName) {
		return sendOverheads.get(sendOperatorName);
	}

	public void addReceiveOverhead(OperatorDescriptor receiveOperator, int time) {
		if (!receiveOverheads.containsKey(receiveOperator.getName())) {
			receiveOverheads.put(receiveOperator.getName(), time);
		}
	}

	public void addReceiveOverhead(String receiveOperatorName, int time) {
		if (!receiveOverheads.containsKey(receiveOperatorName)) {
			receiveOverheads.put(receiveOperatorName, time);
		}
	}

	public HashMap<String, Integer> getReceiveOverheads() {
		return receiveOverheads;
	}

	public int getReceiveOverhead(OperatorDescriptor receiveOperator) {
		return receiveOverheads.get(receiveOperator.getName());
	}

	public int getReceiveOverhead(String receiveOperatorName) {
		return receiveOverheads.get(receiveOperatorName);
	}

	public int getSendOverhead() {
		return sendOverhead * exist;
	}

	public void setSendOverhead(int sendOverhead) {
		this.sendOverhead = sendOverhead;
	}

	public int getReceiveOverhead() {
		return receiveOverhead * exist;
	}

	public void setReceiveOverhead(int receiveOverhead) {
		this.receiveOverhead = receiveOverhead;
	}

	public void addSendInvolvement(LinkDescriptor sendLink, int time) {
		if (!sendInvolvements.containsKey(sendLink.getName())) {
			sendInvolvements.put(sendLink.getName(), time);
		}
	}

	public void addSendInvolvement(String sendLinkName, int time) {
		if (!sendInvolvements.containsKey(sendLinkName)) {
			sendInvolvements.put(sendLinkName, time);
		}
	}

	public HashMap<String, Integer> getSendInvolvements() {
		return sendInvolvements;
	}

	public int getSendInvolvement(LinkDescriptor sendLink) {
		return sendInvolvements.get(sendLink.getName());
	}

	public int getSendInvolvement(String sendLinkName) {
		return sendInvolvements.get(sendLinkName);
	}

	public void addReceiveInvolvement(LinkDescriptor receiveLink, int time) {
		if (!receiveInvolvements.containsKey(receiveLink.getName())) {
			receiveInvolvements.put(receiveLink.getName(), time);
		}
	}

	public void addReceiveInvolvement(String receiveLinkName, int time) {
		if (!receiveInvolvements.containsKey(receiveLinkName)) {
			receiveInvolvements.put(receiveLinkName, time);
		}
	}

	public HashMap<String, Integer> getReceiveInvolvements() {
		return receiveInvolvements;
	}

	public int getReceiveInvolvement(LinkDescriptor receiveLink) {
		return receiveInvolvements.get(receiveLink.getName());
	}

	public int getReceiveInvolvement(String receiveLinkName) {
		return receiveInvolvements.get(receiveLinkName);
	}

	public int getSendInvolvement() {
		return sendInvolvement * exist;
	}

	public void setSendInvolvement(int sendInvolvement) {
		this.sendInvolvement = sendInvolvement;
	}

	public int getReceiveInvolvement() {
		return receiveInvolvement * exist;
	}

	public void setReceiveInvolvement(int receiveInvolvement) {
		this.receiveInvolvement = receiveInvolvement;
	}

	// OnSendOperator
	public int getStartTimeOnSendOperator() {
		return startTimeOnSendOperator;
	}

	public void setStartTimeOnSendOperator(int startTimeOnSendOperator) {
		this.startTimeOnSendOperator = startTimeOnSendOperator;
		setFinishTimeOnSendOperator();
	}

	public int getFinishTimeOnSendOperator() {
		finishTimeOnSendOperator = startTimeOnSendOperator
				+ (sendOverhead + sendInvolvement) * exist;
		return finishTimeOnSendOperator;
	}

	public void setFinishTimeOnSendOperator() {
		finishTimeOnSendOperator = startTimeOnSendOperator
				+ (sendOverhead + sendInvolvement) * exist;
	}

	// OnLink
	public int getStartTimeOnLink() {
		return startTimeOnLink;
	}

	public void setStartTimeOnLink(int startTimeOnLink) {
		this.startTimeOnLink = startTimeOnLink;
		setFinishTimeOnLink();
	}

	public int getFinishTimeOnLink() {
		setCommunicationDuration();
		finishTimeOnLink = startTimeOnLink + communicationDuration * exist;
		return finishTimeOnLink;
	}

	public void setFinishTimeOnLink() {
		setCommunicationDuration();
		finishTimeOnLink = startTimeOnLink + communicationDuration * exist;
	}

	public void setFinishTimeOnLink(int finishTimeOnLink) {
		this.finishTimeOnLink = finishTimeOnLink;
		startTimeOnLink = finishTimeOnLink - communicationDuration * exist;
	}

	// OnReceiveOperator
	public int getStartTimeOnReceiveOperator() {
		return startTimeOnReceiveOperator;
	}

	public void setStartTimeOnReceiveOperator(int startTimeOnReceiveOperator) {
		this.startTimeOnReceiveOperator = startTimeOnReceiveOperator;
		setFinishTimeOnReceiveOperator();
	}

	public int getFinishTimeOnReceiveOperator() {
		finishTimeOnReceiveOperator = startTimeOnReceiveOperator
				+ (receiveOverhead + receiveInvolvement) * exist;
		return finishTimeOnReceiveOperator;
	}

	public void setFinishTimeOnReceiveOperator() {
		finishTimeOnReceiveOperator = startTimeOnReceiveOperator
				+ (receiveOverhead + receiveInvolvement) * exist;
	}

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

	@Override
	public int compareTo(CommunicationDescriptor arg0) {
		return (startTimeOnLink - arg0.getStartTimeOnLink());
	}

}
