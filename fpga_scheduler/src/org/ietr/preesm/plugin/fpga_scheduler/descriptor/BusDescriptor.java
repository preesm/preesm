package org.ietr.preesm.plugin.fpga_scheduler.descriptor;

import java.util.HashMap;

public class BusDescriptor extends LinkDescriptor {

	private int portNumber = Integer.MAX_VALUE;

	private HashMap<String, TGVertexDescriptor> TGVertices;

	public BusDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer) {
		super(id, name, componentDescriptorBuffer);
		this.type = ComponentType.Bus;
		TGVertices = new HashMap<String, TGVertexDescriptor>();
	}

	public BusDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, componentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Bus;
		TGVertices = new HashMap<String, TGVertexDescriptor>();
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public boolean addTGVertex(TGVertexDescriptor TGVertex) {
		if (TGVertices.size() < portNumber) {
			TGVertices.put(TGVertex.getId(), TGVertex);
			return true;
		} else {
			System.out.println("Error: The bus doesn't have a port for "
					+ TGVertex.getId());
			return false;
		}
	}

	public TGVertexDescriptor getTGVertex(String name) {
		return TGVertices.get(name);
	}

	public HashMap<String, TGVertexDescriptor> getTGVertices() {
		return TGVertices;
	}

	public void setTGVertices(HashMap<String, TGVertexDescriptor> TGVertices) {
		this.TGVertices = TGVertices;
	}

}
