package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentType;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.NetworkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.parser.AlgorithmParser;
import org.ietr.preesm.plugin.fpga_scheduler.parser.ArchitectureParser;
import org.ietr.preesm.plugin.fpga_scheduler.parser.ParameterParser;
import org.sdf4j.factories.DAGEdgeFactory;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String args[]) {
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

		int argc = Integer.parseInt(args[0]);
		System.out.println("Number of argument: " + argc);
		switch (argc) {
		case 1: {
			algorithmFileName = args[1];
			break;
		}
		case 2: {
			algorithmFileName = args[1];
			architectureFileName = args[2];
			break;
		}
		case 3: {
			algorithmFileName = args[1];
			architectureFileName = args[2];
			parameterFileName = args[3];
			break;
		}
		default: {
			System.out.println("Invalid command! Use default arguments!");
		}
		}
		AlgorithmDescriptor algorithm = new AlgorithmDescriptor(
				new DAGEdgeFactory());
		ArchitectureDescriptor architecture = new ArchitectureDescriptor();
		// Parse the design algorithm document
		new AlgorithmParser(algorithmFileName, algorithm).parse();
		// Parse the design parameter document
		new ParameterParser(parameterFileName, architecture, algorithm).parse();
		// Parse the architecture document
		new ArchitectureParser(architectureFileName, architecture).parse();

		System.out.println("\n*****Test begins!*****\n");
		System.out.println("\n*****TestAlgorithm*****");
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			System.out.println("Computation: Name="
					+ indexComputation.getName() + "; Duration="
					+ indexComputation.getComputationDuration());
		}
		System.out.println("\n*****TestArchitecture*****");
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
		System.out.println("\n*****TestParameter*****");
		for (CommunicationDescriptor indexCommunication : algorithm
				.getCommunications().values()) {
			System.out.println("Communication: Name="
					+ indexCommunication.getName() + "; Duration="
					+ indexCommunication.getCommunicationDuration());
			for (String key : indexCommunication.getCommunicationDurations()
					.keySet()) {
				System.out.println(" networkName="
						+ key
						+ "; communicationDuration="
						+ indexCommunication.getCommunicationDurations().get(
								key));
			}
			for (String key : indexCommunication.getSendOverheads().keySet()) {
				System.out.println(" sendOperatorName=" + key
						+ "; sendOverhead="
						+ indexCommunication.getSendOverheads().get(key));
			}
			for (String key : indexCommunication.getReceiveOverheads().keySet()) {
				System.out.println(" receiveOperatorName=" + key
						+ "; receiveOverhead="
						+ indexCommunication.getReceiveOverheads().get(key));
			}
			for (String key : indexCommunication.getSendInvolvements().keySet()) {
				System.out.println(" sendLinkName=" + key
						+ "; sendInvolvement="
						+ indexCommunication.getSendInvolvements().get(key));
			}
			for (String key : indexCommunication.getReceiveInvolvements()
					.keySet()) {
				System.out.println(" receiveLinkName=" + key
						+ "; receiveInvolvement="
						+ indexCommunication.getReceiveInvolvements().get(key));
			}
		}

		System.out.println("\n*****Test ends!*****\n");
	} // main
}
