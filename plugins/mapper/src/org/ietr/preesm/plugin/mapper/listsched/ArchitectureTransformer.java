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
package org.ietr.preesm.plugin.mapper.listsched;

import java.util.HashMap;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.advancedmodel.Bus;
import org.ietr.preesm.core.architecture.advancedmodel.Fifo;
import org.ietr.preesm.core.architecture.advancedmodel.RouteStep;
import org.ietr.preesm.core.architecture.advancedmodel.RouteStepList;
import org.ietr.preesm.core.architecture.advancedmodel.NodeLinkTuple;
import org.ietr.preesm.core.architecture.advancedmodel.ILink;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.BusDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.FifoDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.TGVertexDescriptor;

/**
 * The ArchitectureTransformer converts different architectures between
 * MultiCoreArchitecture and AlgorithmDescriptor
 * 
 * @author pmu
 * 
 */
public class ArchitectureTransformer {

	/**
	 * A buffer to contain all the ComponentDescriptors
	 */
	private HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer;

	/**
	 * Construct a new ArchitectureTransformer
	 * 
	 */
	public ArchitectureTransformer() {
	}

	/**
	 * Convert a MultiCoreArchitecture to an ArchitectureDescriptor
	 * 
	 * @param architecture
	 *            A MultiCoreArchitecture to be converted
	 * @return An ArchitectureDescriptor
	 */
	public ArchitectureDescriptor architecture2Descriptor(
			MultiCoreArchitecture architecture) {
		MultiCoreArchitecture archi = (MultiCoreArchitecture) architecture;
		ArchitectureDescriptor archiDescriptor = new ArchitectureDescriptor();

		this.ComponentDescriptorBuffer = archiDescriptor.getComponents();
		for (ArchitectureComponent indexOperator : archi
				.getComponents(ArchitectureComponentType.operator)) {
			new ProcessorDescriptor(indexOperator.getName(), indexOperator
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (ArchitectureComponent indexOperator : archi
				.getComponents(ArchitectureComponentType.processor)) {
			new ProcessorDescriptor(indexOperator.getName(), indexOperator
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (ArchitectureComponent indexOperator : archi
				.getComponents(ArchitectureComponentType.ipCoprocessor)) {
			new ProcessorDescriptor(indexOperator.getName(), indexOperator
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (ArchitectureComponent indexMedium : archi
				.getComponents(ArchitectureComponentType.medium)) {
			BusDescriptor bus = new BusDescriptor(indexMedium.getName(),
					indexMedium.getDefinition().getId(),
					ComponentDescriptorBuffer);
			bus
					.setAverageClockCyclesPerTransfer(1.0 / ((MediumDefinition) ((Medium) indexMedium)
							.getDefinition()).getDataRate());
		}
		for (ArchitectureComponent indexMedium : archi
				.getComponents(ArchitectureComponentType.bus)) {
			BusDescriptor bus = new BusDescriptor(indexMedium.getName(),
					indexMedium.getDefinition().getId(),
					ComponentDescriptorBuffer);
			bus.setAverageClockCyclesPerTransfer(1 / ((Bus) indexMedium)
					.getDataRate());
		}
		for (ArchitectureComponent indexMedium : archi
				.getComponents(ArchitectureComponentType.fifo)) {
			FifoDescriptor fifo = new FifoDescriptor(indexMedium.getName(),
					indexMedium.getDefinition().getId(),
					ComponentDescriptorBuffer);
			fifo.setAverageClockCyclesPerTransfer(1 / ((Fifo) indexMedium)
					.getDataRate());
		}
		for (ArchitectureComponent indexSwitch : archi
				.getComponents(ArchitectureComponentType.communicationNode)) {
			new SwitchDescriptor(indexSwitch.getName(), indexSwitch
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (Interconnection indexInterconnection : archi.getInterconnections()) {
			// transform two types of interconnections
			ArchitectureComponent srcComponent = indexInterconnection.getCp1();
			ArchitectureComponent dstComponent = indexInterconnection.getCp2();
			if (indexInterconnection.isDirected()) {
				if ((srcComponent.getType() == ArchitectureComponentType.operator)
						|| (srcComponent.getType() == ArchitectureComponentType.processor)
						|| (srcComponent.getType() == ArchitectureComponentType.ipCoprocessor)
						|| (srcComponent.getType() == ArchitectureComponentType.communicationNode)) {
					((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(srcComponent.getName()))
							.addOutputLink((FifoDescriptor) ComponentDescriptorBuffer
									.get(dstComponent.getName()));
					((FifoDescriptor) ComponentDescriptorBuffer
							.get(dstComponent.getName()))
							.setOrigin((TGVertexDescriptor) ComponentDescriptorBuffer
									.get(srcComponent.getName()));
				} else if ((dstComponent.getType() == ArchitectureComponentType.operator)
						|| (dstComponent.getType() == ArchitectureComponentType.processor)
						|| (dstComponent.getType() == ArchitectureComponentType.ipCoprocessor)
						|| (dstComponent.getType() == ArchitectureComponentType.communicationNode)) {
					((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(dstComponent.getName()))
							.addInputLink((FifoDescriptor) ComponentDescriptorBuffer
									.get(srcComponent.getName()));
					((FifoDescriptor) ComponentDescriptorBuffer
							.get(srcComponent.getName()))
							.setDestination((TGVertexDescriptor) ComponentDescriptorBuffer
									.get(dstComponent.getName()));
				}
			} else {
				if ((srcComponent.getType() == ArchitectureComponentType.operator)
						|| (srcComponent.getType() == ArchitectureComponentType.processor)
						|| (srcComponent.getType() == ArchitectureComponentType.ipCoprocessor)) {
					((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(srcComponent.getName()))
							.addInputLink((BusDescriptor) ComponentDescriptorBuffer
									.get(dstComponent.getName()));
					((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(srcComponent.getName()))
							.addOutputLink((BusDescriptor) ComponentDescriptorBuffer
									.get(dstComponent.getName()));
					((BusDescriptor) ComponentDescriptorBuffer.get(dstComponent
							.getName()))
							.addTGVertex((TGVertexDescriptor) ComponentDescriptorBuffer
									.get(srcComponent.getName()));
				} else if ((dstComponent.getType() == ArchitectureComponentType.operator)
						|| (dstComponent.getType() == ArchitectureComponentType.processor)
						|| (dstComponent.getType() == ArchitectureComponentType.ipCoprocessor)) {
					((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(dstComponent.getName()))
							.addInputLink((BusDescriptor) ComponentDescriptorBuffer
									.get(srcComponent.getName()));
					((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(dstComponent.getName()))
							.addOutputLink((BusDescriptor) ComponentDescriptorBuffer
									.get(srcComponent.getName()));
					((BusDescriptor) ComponentDescriptorBuffer.get(srcComponent
							.getName()))
							.addTGVertex((TGVertexDescriptor) ComponentDescriptorBuffer
									.get(dstComponent.getName()));
				}
			}
		}
		if (archi.getRouteStepTable().getRouteStepLists().size() != 0) {
			archiDescriptor.setArchi(archi);
			for (RouteStepList indexRSL : archi.getRouteStepTable()
					.getRouteStepLists()) {
				for (RouteStep indexRS : indexRSL.getRouteSteps()) {
					double minDataRate = ((ILink) archi.getComponent(indexRS
							.getFirstLinkName())).getDataRate();
					for (NodeLinkTuple indexNLT : indexRS.getNodeLinkTuples()) {
						if (minDataRate > ((ILink) archi.getComponent(indexNLT
								.getLinkName())).getDataRate()) {
							minDataRate = ((ILink) archi.getComponent(indexNLT
									.getLinkName())).getDataRate();
						}
					}
					indexRS.setDataRate(minDataRate);
				}
			}
		}
		return archiDescriptor;
	}

	/**
	 * Convert an ArchitectureDescriptor to a MultiCoreArchitecture
	 * 
	 * @param archiDescriptor
	 *            An architecture to be converted
	 * @return A MultiCoreArchitecture
	 */
	public MultiCoreArchitecture descriptor2Architecture(
			ArchitectureDescriptor archiDescriptor) {
		if (archiDescriptor.getArchi() != null) {
			return archiDescriptor.getArchi();
		} else {
			MultiCoreArchitecture architecture = new MultiCoreArchitecture(
					archiDescriptor.getName());
			// TODO:
			return architecture;
		}
	}

}
