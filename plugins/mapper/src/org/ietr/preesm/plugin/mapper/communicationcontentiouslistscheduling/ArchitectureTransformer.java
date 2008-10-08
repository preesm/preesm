package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling;

import java.util.HashMap;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.Switch;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.BusDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.TGVertexDescriptor;

/**
 * @author pmu
 * 
 *         The ArchitectureTransformer converts different architectures between
 *         IArchitecture and AlgorithmDescriptor
 */
public class ArchitectureTransformer {

	private HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer;

	public ArchitectureTransformer() {
	}

	public ArchitectureDescriptor architecture2Descriptor(
			IArchitecture architecture) {
		MultiCoreArchitecture archi = (MultiCoreArchitecture) architecture;
		ArchitectureDescriptor archiDescriptor = new ArchitectureDescriptor();
		this.ComponentDescriptorBuffer = archiDescriptor.getComponents();
		for (Operator indexOperator : archi.getOperators()) {
			new ProcessorDescriptor(indexOperator.getName(), indexOperator
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (Medium indexMedium : archi.getMedia()) {
			new BusDescriptor(indexMedium.getName(), indexMedium
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (Switch indexSwitch : archi.getSwitches()) {
			new SwitchDescriptor(indexSwitch.getName(), indexSwitch
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (Interconnection indexInterconnection : archi.getInterconnections()) {
			((TGVertexDescriptor) ComponentDescriptorBuffer
					.get(indexInterconnection.getOperatorInterface().getOwner()
							.getName()))
					.addInputLink((BusDescriptor) ComponentDescriptorBuffer
							.get(indexInterconnection.getMediumInterface()
									.getOwner().getName()));
			((TGVertexDescriptor) ComponentDescriptorBuffer
					.get(indexInterconnection.getOperatorInterface().getOwner()
							.getName()))
					.addOutputLink((BusDescriptor) ComponentDescriptorBuffer
							.get(indexInterconnection.getMediumInterface()
									.getOwner().getName()));
			((BusDescriptor) ComponentDescriptorBuffer.get(indexInterconnection
					.getMediumInterface().getOwner().getName()))
					.addTGVertex((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(indexInterconnection.getOperatorInterface()
									.getOwner().getName()));
		}

		return archiDescriptor;
	}

	public IArchitecture descriptor2Architecture(
			ArchitectureDescriptor archiDescriptor) {
		MultiCoreArchitecture architecture = new MultiCoreArchitecture(
				archiDescriptor.getName());
		return architecture;
	}
}
