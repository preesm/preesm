package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentType;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.NetworkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.parser.ArchitectureParser;

public class TestArchitecture {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture.xml";

		int argc = 0;
		argc = Integer.parseInt(args[0]);
		System.out.println("Number of argument: " + argc);
		switch (argc) {
		case 1:
			architectureFileName = args[1];
			break;
		default: {
			System.out.println("Invalid command! Use default!");
		}
		}

		// Parse the architecture document
		ArchitectureDescriptor architecture = new ArchitectureParser(
				architectureFileName).parse();

		for (ComponentDescriptor indexComponent : architecture.getComponents()
				.values()) {
			if (indexComponent.getType() == ComponentType.Processor) {
				System.out.println("Processor: Id=" + indexComponent.getId()
						+ "; Name=" + indexComponent.getName() + "; Type="
						+ indexComponent.getType());
				for (LinkDescriptor indexLink : ((ProcessorDescriptor) indexComponent)
						.getInputLinks()) {
					System.out.println("  inputLink: Id=" + indexLink.getId()
							+ "; Name=" + indexLink.getName() + "; Type="
							+ indexLink.getType());
				}
				for (LinkDescriptor indexLink : ((ProcessorDescriptor) indexComponent)
						.getOutputLinks()) {
					System.out.println("  outputFifo: Id=" + indexLink.getId()
							+ "; Name=" + indexLink.getName() + "; Type="
							+ indexLink.getType());
				}
			} else if (indexComponent.getType() == ComponentType.Network) {
				System.out.println("Network: Id=" + indexComponent.getId()
						+ "; Name=" + indexComponent.getName() + "; Type="
						+ indexComponent.getType());
				for (LinkDescriptor indexLink : ((NetworkDescriptor) indexComponent)
						.getInputLinks()) {
					System.out.println("  inputLink: Id=" + indexLink.getId()
							+ "; Name=" + indexLink.getName() + "; Type="
							+ indexLink.getType());
				}
				for (LinkDescriptor indexLink : ((NetworkDescriptor) indexComponent)
						.getOutputLinks()) {
					System.out.println("  outputFifo: Id=" + indexLink.getId()
							+ "; Name=" + indexLink.getName() + "; Type="
							+ indexLink.getType());
				}
			}
		}
	}

}
