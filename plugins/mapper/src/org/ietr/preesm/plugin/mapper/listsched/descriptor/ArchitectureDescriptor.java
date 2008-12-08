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

	public ArchitectureDescriptor clone() {
		ArchitectureDescriptor archi = new ArchitectureDescriptor();

		for (ComponentDescriptor indexComponent : this.getComponents().values()) {
			if (indexComponent.getType() == ComponentType.Processor) {
				ProcessorDescriptor newProcessor = new ProcessorDescriptor(
						indexComponent.getId(), indexComponent.getName(), archi
								.getComponents());
				newProcessor
						.setClockPeriod(((ProcessorDescriptor) indexComponent)
								.getClockPeriod());
				newProcessor
						.setDataWidth(((ProcessorDescriptor) indexComponent)
								.getDataWidth());
				newProcessor.setSurface(((ProcessorDescriptor) indexComponent)
						.getSurface());
			} else if (indexComponent.getType() == ComponentType.Ip) {
				IpDescriptor newIp = new IpDescriptor(indexComponent.getId(),
						indexComponent.getName(), archi.getComponents());
				newIp.setClockPeriod(((IpDescriptor) indexComponent)
						.getClockPeriod());
				newIp.setUserInterfaceType(((IpDescriptor) indexComponent)
						.getUserInterfaceType());
				newIp.setDataWidth(((IpDescriptor) indexComponent)
						.getDataWidth());
				newIp.setNbInputData(((IpDescriptor) indexComponent)
						.getNbInputData());
				newIp.setNbOutputData(((IpDescriptor) indexComponent)
						.getNbOutputData());
				newIp.setLatency(((IpDescriptor) indexComponent).getLatency());
				newIp.setCadence(((IpDescriptor) indexComponent).getCadence());
				newIp.setSurface(((IpDescriptor) indexComponent).getSurface());

			} else if (indexComponent.getType() == ComponentType.Bus) {
				BusDescriptor newBus = new BusDescriptor(
						indexComponent.getId(), indexComponent.getName(), archi
								.getComponents());
				newBus.setClockPeriod(((BusDescriptor) indexComponent)
						.getClockPeriod());
				newBus.setDataWidth(((BusDescriptor) indexComponent)
						.getDataWidth());
				newBus
						.setAverageClockCyclesPerTransfer(((BusDescriptor) indexComponent)
								.getAverageClockCyclesPerTransfer());
				newBus.setPortNumber(((BusDescriptor) indexComponent)
						.getPortNumber());
				newBus
						.setSurface(((BusDescriptor) indexComponent)
								.getSurface());
			} else if (indexComponent.getType() == ComponentType.Fifo) {
				FifoDescriptor newFifo = new FifoDescriptor(indexComponent
						.getId(), indexComponent.getName(), archi
						.getComponents());
				newFifo.setClockPeriod(((FifoDescriptor) indexComponent)
						.getClockPeriod());
				newFifo.setDataWidth(((FifoDescriptor) indexComponent)
						.getDataWidth());
				newFifo
						.setAverageClockCyclesPerTransfer(((FifoDescriptor) indexComponent)
								.getAverageClockCyclesPerTransfer());
				newFifo.setSurface(((FifoDescriptor) indexComponent)
						.getSurface());
			} else if (indexComponent.getType() == ComponentType.Switch) {
				SwitchDescriptor newSwitch = new SwitchDescriptor(
						indexComponent.getId(), indexComponent.getName(), archi
								.getComponents());
				newSwitch.setClockPeriod(((SwitchDescriptor) indexComponent)
						.getClockPeriod());
				newSwitch.setDataWidth(((SwitchDescriptor) indexComponent)
						.getDataWidth());
				newSwitch
						.setAverageClockCyclesPerTransfer(((SwitchDescriptor) indexComponent)
								.getAverageClockCyclesPerTransfer());
				newSwitch.setPortNumber(((SwitchDescriptor) indexComponent)
						.getPortNumber());
				newSwitch.setSurface(((SwitchDescriptor) indexComponent)
						.getSurface());
			}
		}

		for (ComponentDescriptor indexComponent : this.getComponents().values()) {
			if (indexComponent.getType() == ComponentType.Processor
					|| indexComponent.getType() == ComponentType.Ip
					|| indexComponent.getType() == ComponentType.Switch) {
				for (LinkDescriptor indexLink : ((TGVertexDescriptor) indexComponent)
						.getInputLinks()) {
					((TGVertexDescriptor) archi.getComponent(indexComponent
							.getId())).addInputLink(((LinkDescriptor) archi
							.getComponent(indexLink.getId())));
				}
				for (LinkDescriptor indexLink : ((TGVertexDescriptor) indexComponent)
						.getOutputLinks()) {
					((TGVertexDescriptor) archi.getComponent(indexComponent
							.getId())).addOutputLink(((LinkDescriptor) archi
							.getComponent(indexLink.getId())));
				}
			} else if (indexComponent.getType() == ComponentType.Bus) {
				for (TGVertexDescriptor indexVertex : ((BusDescriptor) indexComponent)
						.getTGVertices().values()) {
					((BusDescriptor) archi.getComponent(indexComponent.getId()))
							.addTGVertex(((TGVertexDescriptor) archi
									.getComponent(indexVertex.getId())));
				}
			} else if (indexComponent.getType() == ComponentType.Fifo) {
				((FifoDescriptor) archi.getComponent(indexComponent.getId()))
						.setOrigin(((TGVertexDescriptor) archi
								.getComponent(((FifoDescriptor) indexComponent)
										.getOrigin().getId())));
				((FifoDescriptor) archi.getComponent(indexComponent.getId()))
						.setDestination(((TGVertexDescriptor) archi
								.getComponent(((FifoDescriptor) indexComponent)
										.getDestination().getId())));
			}
		}
		return archi;
	}
}
