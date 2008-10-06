package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor;

import java.util.HashMap;

public class ComponentDescriptor {
	protected HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer = null;

	protected ComponentType type;

	protected String id;

	protected String name;

	protected int clockPeriod = 10; // unit: ns

	protected int dataWidth = 32;

	protected int surface;

	public ComponentDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		this.id = id;
		this.name = name;
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		ComponentDescriptorBuffer.put(this.id, this);
	}

	public ComponentDescriptor(String id, String name,
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			int clockPeriod, int dataWidth, int surface) {
		this.id = id;
		this.name = name;
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		ComponentDescriptorBuffer.put(this.id, this);
		this.clockPeriod = clockPeriod;
		this.dataWidth = dataWidth;
		this.surface = surface;
	}

	public ComponentType getType() {
		return type;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setClockPeriod(int clockPeriod) {
		this.clockPeriod = clockPeriod;
	}

	public int getClockPeriod() {
		return clockPeriod;
	}

	public void setDataWidth(int dataWidth) {
		this.dataWidth = dataWidth;
	}

	public int getDataWidth() {
		return dataWidth;
	}

	/**
	 * 
	 * @param surface
	 */
	public void setSurface(int surface) {
		this.surface = surface;
	}

	/**
	 * 
	 * @return
	 */
	public int getSurface() {
		return surface;
	}
}
