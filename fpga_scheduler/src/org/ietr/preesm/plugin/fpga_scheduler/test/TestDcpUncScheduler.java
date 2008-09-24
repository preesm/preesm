package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.scheduler.DcpUncScheduler;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentType;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ProcessorDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.parser.AlgorithmParser;
import org.ietr.preesm.plugin.fpga_scheduler.parser.ArchitectureParser;
import org.ietr.preesm.plugin.fpga_scheduler.parser.ParameterParser;
import org.sdf4j.factories.DAGEdgeFactory;

public class TestDcpUncScheduler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			parameterFileName = args[2];
			break;
		}
		case 3: {
			algorithmFileName = args[1];
			parameterFileName = args[2];
			architectureFileName = args[3];
			break;
		}
		default: {
			System.out.println("Invalid command! Use default!");
		}
		}

		// Parse the algorithm document
		AlgorithmDescriptor algorithm = null;
		if (algorithmFileName != null) {
			algorithm = new AlgorithmParser(algorithmFileName).parse();
		} else {
			algorithm = new AlgorithmDescriptor(new DAGEdgeFactory());
		}

		// Parse the architecture
		ArchitectureDescriptor architecture = null;
		if (architectureFileName != null) {
			architecture = new ArchitectureParser(architectureFileName).parse();
		} else {
			architecture = new ArchitectureDescriptor();
		}

		// Parse the parameter document
		int timeConstraint = new ParameterParser(parameterFileName,
				architecture, algorithm).parse();

		for (ComponentDescriptor componentIndex : architecture.getComponents()
				.values()) {
			if (componentIndex.getType() == ComponentType.Processor) {
				architecture
						.setNewProcessor((ProcessorDescriptor) componentIndex);
				architecture.getNewProcessor().setId(
						new String("new_").concat(architecture
								.getNewProcessor().getId()));
				break;
			}
		}
		if (architecture.getNewProcessor() == null) {
			architecture.setNewProcessor(new ProcessorDescriptor(
					"newProcessor", "processor", architecture.getComponents()));
		}

		System.out.println("\n*****TestDcpUncScheduler begins!*****\n");

		DcpUncScheduler scheduler = new DcpUncScheduler(algorithm, architecture);
		scheduler.schedule();
		int scheduleLength = scheduler.getScheduleLength();

		for (ProcessorDescriptor processorIndex : scheduler
				.getProcessorsInUse()) {
			System.out.println("Processor: " + processorIndex.getId());
			for (ComputationDescriptor computationIndex : processorIndex
					.getComputations()) {
				System.out.println(" Computation: "
						+ computationIndex.getName() + " -> start time is: "
						+ computationIndex.getStartTime());
			}
		}

		System.out.println("\nResult:\n time constraint is: " + timeConstraint);
		System.out.println(" schedule length is: " + scheduleLength
				+ "\n number of processors in use is: "
				+ scheduler.getNbProcessorInUse());
		System.out.println("\n*****TestDcpUncScheduler ends!*****\n");
	} // main
}