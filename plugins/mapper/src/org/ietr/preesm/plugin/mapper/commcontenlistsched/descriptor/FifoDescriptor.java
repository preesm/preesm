package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;

public class FifoDescriptor extends LinkDescriptor {

	private TGVertexDescriptor origin;

	private TGVertexDescriptor destination;

	public FifoDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer) {
		super(id, name, componentDescriptorBuffer);
		this.type = ComponentType.Fifo;
	}

	public FifoDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, componentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Fifo;
	}

	public TGVertexDescriptor getOrigin() {
		return origin;
	}

	public void setOrigin(TGVertexDescriptor origin) {
		this.origin = origin;
	}

	public TGVertexDescriptor getDestination() {
		return destination;
	}

	public void setDestination(TGVertexDescriptor destination) {
		this.destination = destination;
	}
}
