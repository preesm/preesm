package org.ietr.preesm.plugin.mapper.commcontenlistsched;

import java.util.HashMap;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.Interconnection;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.Switch;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.BusDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.SwitchDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.TGVertexDescriptor;

/**
 * @author pmu
 * 
 *         The ArchitectureTransformer converts different architectures between
 *         MultiCoreArchitecture and AlgorithmDescriptor
 */
public class ArchitectureTransformer {

	private HashMap<String, ComponentDescriptor> ComponentDescriptorBuffer;

	public ArchitectureTransformer() {
	}

	public ArchitectureDescriptor architecture2Descriptor(
			MultiCoreArchitecture architecture) {
		MultiCoreArchitecture archi = (MultiCoreArchitecture) architecture;
		ArchitectureDescriptor archiDescriptor = new ArchitectureDescriptor();
		this.ComponentDescriptorBuffer = archiDescriptor.getComponents();
		for (ArchitectureComponent indexOperator : archi.getComponents(ArchitectureComponentType.operator)) {
			new ProcessorDescriptor(indexOperator.getName(), indexOperator
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (ArchitectureComponent indexMedium : archi.getComponents(ArchitectureComponentType.medium)) {
			new BusDescriptor(indexMedium.getName(), indexMedium
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (Switch indexSwitch : archi.getSwitches()) {
			new SwitchDescriptor(indexSwitch.getName(), indexSwitch
					.getDefinition().getId(), ComponentDescriptorBuffer);
		}
		for (Interconnection indexInterconnection : archi.getInterconnections()) {
			((TGVertexDescriptor) ComponentDescriptorBuffer
					.get(indexInterconnection.getInterface(ArchitectureComponentType.operator).getOwner()
							.getName()))
					.addInputLink((BusDescriptor) ComponentDescriptorBuffer
							.get(indexInterconnection.getInterface(ArchitectureComponentType.medium)
									.getOwner().getName()));
			((TGVertexDescriptor) ComponentDescriptorBuffer
					.get(indexInterconnection.getInterface(ArchitectureComponentType.operator).getOwner()
							.getName()))
					.addOutputLink((BusDescriptor) ComponentDescriptorBuffer
							.get(indexInterconnection.getInterface(ArchitectureComponentType.medium)
									.getOwner().getName()));
			((BusDescriptor) ComponentDescriptorBuffer.get(indexInterconnection
					.getInterface(ArchitectureComponentType.medium).getOwner().getName()))
					.addTGVertex((TGVertexDescriptor) ComponentDescriptorBuffer
							.get(indexInterconnection.getInterface(ArchitectureComponentType.operator)
									.getOwner().getName()));
		}

		return archiDescriptor;
	}

	public MultiCoreArchitecture descriptor2Architecture(
			ArchitectureDescriptor archiDescriptor) {
		MultiCoreArchitecture architecture = new MultiCoreArchitecture(
				archiDescriptor.getName());
		return architecture;
	}
}
