package org.ietr.preesm.plugin.fpga_scheduler.descriptor;

import java.util.HashMap;

public class NetworkDescriptor extends TGVertexDescriptor {

	private double averageClockCyclesPerTransfer; // unit: clock cycle

	private int portNumber;

	public NetworkDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer) {
		super(id, name, componentDescriptorBuffer);
		this.type = ComponentType.Network;
	}

	public NetworkDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> componentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		super(id, name, componentDescriptorBuffer, clockPeriod, dataWidth,
				surface);
		this.type = ComponentType.Network;
	}

	public void setAverageClockCyclesPerTransfer(
			double averageClockCyclesPerTransfer) {
		this.averageClockCyclesPerTransfer = averageClockCyclesPerTransfer;
	}

	public double getAverageClockCyclesPerTransfer() {
		return averageClockCyclesPerTransfer;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

}
