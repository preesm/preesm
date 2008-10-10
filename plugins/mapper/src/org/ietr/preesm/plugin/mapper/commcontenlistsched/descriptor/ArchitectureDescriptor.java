package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;
import java.util.Vector;

public class ArchitectureDescriptor {

	private String name = "architecture";

	private HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer;

	private HashMap<String, OperatorDescriptor> allOperators;

	private HashMap<String, SwitchDescriptor> allSwitches;

	private HashMap<String, LinkDescriptor> allLinks;

	private Vector<ProcessorDescriptor> processorsInUse = null;

	private ProcessorDescriptor newProcessor = null;

	private int nbProcessorInUse = 0;

	private int surfaceUsed = 0;

	public ArchitectureDescriptor() {
		ComponentDescriptorBuffer = new HashMap<String, ComponentDescriptor>();
		allOperators = new HashMap<String, OperatorDescriptor>();
		allSwitches = new HashMap<String, SwitchDescriptor>();
		allLinks = new HashMap<String, LinkDescriptor>();
		processorsInUse = new Vector<ProcessorDescriptor>();
	}

	public ArchitectureDescriptor(
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer) {
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		allOperators = new HashMap<String, OperatorDescriptor>();
		allSwitches = new HashMap<String, SwitchDescriptor>();
		allLinks = new HashMap<String, LinkDescriptor>();
		processorsInUse = new Vector<ProcessorDescriptor>();
	}

	public ArchitectureDescriptor(
			HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer,
			ProcessorDescriptor newProcessor) {
		this.ComponentDescriptorBuffer = ComponentDescriptorBuffer;
		allOperators = new HashMap<String, OperatorDescriptor>();
		allSwitches = new HashMap<String, SwitchDescriptor>();
		allLinks = new HashMap<String, LinkDescriptor>();
		processorsInUse = new Vector<ProcessorDescriptor>();
		this.newProcessor = newProcessor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, ComponentDescriptor> getComponents() {
		return ComponentDescriptorBuffer;
	}

	public ComponentDescriptor getComponent(String id) {
		return ComponentDescriptorBuffer.get(id);
	}

	public OperatorDescriptor getOperator(String id) {
		return (OperatorDescriptor) ComponentDescriptorBuffer.get(id);
	}

	public SwitchDescriptor getSwitch(String id) {
		return (SwitchDescriptor) ComponentDescriptorBuffer.get(id);
	}

	public LinkDescriptor getLink(String id) {
		return (LinkDescriptor) ComponentDescriptorBuffer.get(id);
	}

	public HashMap<String, OperatorDescriptor> getAllOperators() {
		if (allOperators.size() == 0) {
			for (ComponentDescriptor indexComponent : ComponentDescriptorBuffer
					.values()) {
				if ((indexComponent.getType() == ComponentType.Ip || indexComponent
						.getType() == ComponentType.Processor)
						&& !indexComponent.getId().equalsIgnoreCase(
								indexComponent.getName())) {
					allOperators.put(indexComponent.getId(),
							(OperatorDescriptor) indexComponent);
				}
			}
		}
		return allOperators;
	}

	public HashMap<String, SwitchDescriptor> getAllSwitches() {
		if (allSwitches.size() == 0) {
			for (ComponentDescriptor indexComponent : ComponentDescriptorBuffer
					.values()) {
				if ((indexComponent.getType() == ComponentType.Switch)
						&& !indexComponent.getId().equalsIgnoreCase(
								indexComponent.getName())) {
					allSwitches.put(indexComponent.getId(),
							(SwitchDescriptor) indexComponent);
				}
			}
		}
		return allSwitches;
	}

	public HashMap<String, LinkDescriptor> getAllLinks() {
		if (allLinks.size() == 0) {
			for (ComponentDescriptor indexComponent : ComponentDescriptorBuffer
					.values()) {
				if ((indexComponent.getType() == ComponentType.Bus || indexComponent
						.getType() == ComponentType.Fifo)
						&& !indexComponent.getId().equalsIgnoreCase(
								indexComponent.getName())) {
					allLinks.put(indexComponent.getId(),
							(LinkDescriptor) indexComponent);
				}
			}
		}
		return allLinks;
	}

	public Vector<ProcessorDescriptor> getProcessorsInUse() {
		return processorsInUse;
	}

	public void setProcessorsInUse(Vector<ProcessorDescriptor> processorsInUse) {
		this.processorsInUse = processorsInUse;
	}

	public ProcessorDescriptor getNewProcessor() {
		return newProcessor;
	}

	public void setNewProcessor(ProcessorDescriptor newProcessor) {
		this.newProcessor = newProcessor;
	}

	public int getNbProcessorInUse() {
		return nbProcessorInUse;
	}

	public void setNbProcessorInUse(int nbProcessorInUse) {
		this.nbProcessorInUse = nbProcessorInUse;
	}

	public void increaseNbProcessorInUse() {
		nbProcessorInUse++;
	}

	public int getSurfaceUsed() {
		return surfaceUsed;
	}

	public void setSurfaceUsed(int surfaceUsed) {
		this.surfaceUsed = surfaceUsed;
	}

}
