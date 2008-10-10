package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;

public class ProcessorDescriptor extends OperatorDescriptor {

	private int sendOverhead = 0;

	private int receiveOverhead = 0;

	private int sendInvolvementFactor = 0;

	private int receiveInvolvementFactor = 0;

	public ProcessorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		super(id, name, ComponentDescriptorBuffer);
		this.type = ComponentType.Processor;
	}

	public ProcessorDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, ComponentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Processor;
	}

	public int getSendOverhead() {
		return sendOverhead;
	}

	public void setSendOverhead(int sendOverhead) {
		this.sendOverhead = sendOverhead;
	}

	public int getReceiveOverhead() {
		return receiveOverhead;
	}

	public void setReceiveOverhead(int receiveOverhead) {
		this.receiveOverhead = receiveOverhead;
	}

	public int getSendInvolvementFactor() {
		return sendInvolvementFactor;
	}

	public void setSendInvolvementFactor(int sendInvolvementFactor) {
		this.sendInvolvementFactor = sendInvolvementFactor;
	}

	public int getReceiveInvolvementFactor() {
		return receiveInvolvementFactor;
	}

	public void setReceiveInvolvementFactor(int receiveInvolvementFactor) {
		this.receiveInvolvementFactor = receiveInvolvementFactor;
	}

}
