package org.ietr.preesm.plugin.fpga_scheduler;

import org.ietr.preesm.plugin.fpga_scheduler.scheduler.*;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.*;
import org.ietr.preesm.plugin.fpga_scheduler.parser.*;
import org.ietr.preesm.plugin.fpga_scheduler.plotter.GanttPlotter;
import org.jfree.ui.RefineryUtilities;
import org.sdf4j.factories.DAGEdgeFactory;

public class DemoListScheduling {

	private AlgorithmDescriptor algorithm = null;

	private ArchitectureDescriptor architecture = null;

	public DemoListScheduling(String algorithmFileName,
			String parameterFileName, String architectureFileName) {
		System.out.println("\n***** DemoListScheduling begins! *****");

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation scheduler4 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevel scheduler5 = new ListSchedulingAdvancedWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler5, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelIn scheduler6 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler6, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelOut scheduler7 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler7, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut scheduler8 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler8, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassic scheduler1 = new ListSchedulingClassic(algorithm,
				architecture);
		testScheduler(scheduler1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelay scheduler2 = new ListSchedulingCommunicationDelay(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingCriticalChild scheduler3 = new ListSchedulingCriticalChild(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t\t\t\t\t\t\t"
				+ scheduler1.getScheduleLength() + "\t\t\t"
				+ scheduler1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler1
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot1 = new GanttPlotter(scheduler1.getName()
				+ " -> Schedule Length=" + scheduler1.getScheduleLength(),
				scheduler1);
		plot1.pack();
		RefineryUtilities.centerFrameOnScreen(plot1);
		plot1.setVisible(true);

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t\t\t\t"
				+ scheduler2.getScheduleLength() + "\t\t\t"
				+ scheduler2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot2 = new GanttPlotter(scheduler2.getName()
				+ " -> Schedule Length=" + scheduler2.getScheduleLength(),
				scheduler2);
		plot2.pack();
		RefineryUtilities.centerFrameOnScreen(plot2);
		plot2.setVisible(true);

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t\t\t\t\t"
				+ scheduler3.getScheduleLength() + "\t\t\t"
				+ scheduler3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler3
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot3 = new GanttPlotter(scheduler3.getName()
				+ " -> Schedule Length=" + scheduler3.getScheduleLength(),
				scheduler3);
		plot3.pack();
		RefineryUtilities.centerFrameOnScreen(plot3);
		plot3.setVisible(true);

		System.out.print("\n4\t" + scheduler4.getName() + "\t"
				+ scheduler4.getScheduleLength() + "\t\t\t"
				+ scheduler4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler4
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot4 = new GanttPlotter(scheduler4.getName()
				+ " -> Schedule Length=" + scheduler4.getScheduleLength(),
				scheduler4);
		plot4.pack();
		RefineryUtilities.centerFrameOnScreen(plot4);
		plot4.setVisible(true);

		System.out.print("\n5\t" + scheduler5.getName() + "\t\t"
				+ scheduler5.getScheduleLength() + "\t\t\t"
				+ scheduler5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler5
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		GanttPlotter plot5 = new GanttPlotter(scheduler5.getName()
				+ " -> Schedule Length=" + scheduler5.getScheduleLength(),
				scheduler5);
		plot5.pack();
		RefineryUtilities.centerFrameOnScreen(plot5);
		plot5.setVisible(true);

		System.out.print("\n6\t" + scheduler6.getName() + "\t\t"
				+ scheduler6.getScheduleLength() + "\t\t\t"
				+ scheduler6.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler6
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot6 = new GanttPlotter(scheduler6.getName()
				+ " -> Schedule Length=" + scheduler6.getScheduleLength(),
				scheduler6);
		plot6.pack();
		RefineryUtilities.centerFrameOnScreen(plot6);
		plot6.setVisible(true);

		System.out.print("\n7\t" + scheduler7.getName() + "\t\t"
				+ scheduler7.getScheduleLength() + "\t\t\t"
				+ scheduler7.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler7
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot7 = new GanttPlotter(scheduler7.getName()
				+ " -> Schedule Length=" + scheduler7.getScheduleLength(),
				scheduler7);
		plot7.pack();
		RefineryUtilities.centerFrameOnScreen(plot7);
		plot7.setVisible(true);

		System.out.print("\n8\t" + scheduler8.getName() + "\t"
				+ scheduler8.getScheduleLength() + "\t\t\t"
				+ scheduler8.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler8
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}
		GanttPlotter plot8 = new GanttPlotter(scheduler8.getName()
				+ " -> Schedule Length=" + scheduler8.getScheduleLength(),
				scheduler8);
		plot8.pack();
		RefineryUtilities.centerFrameOnScreen(plot8);
		plot8.setVisible(true);

		System.out.println("\n\n*****DemoListScheduling finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm1.xml";
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

		new DemoListScheduling(algorithmFileName, parameterFileName,
				architectureFileName);
	}

	private void parse(String algorithmFileName, String architectureFileName,
			String parameterFileName) {
		algorithm = new AlgorithmDescriptor(new DAGEdgeFactory());
		architecture = new ArchitectureDescriptor();
		// Parse the design algorithm document
		new AlgorithmParser(algorithmFileName, algorithm).parse();
		// Parse the design parameter document
		new ParameterParser(parameterFileName, architecture, algorithm).parse();
		// Parse the architecture document
		new ArchitectureParser(architectureFileName, architecture).parse();

		OperatorDescriptor defaultOperator = null;
		NetworkDescriptor defaultNetwork = null;
		for (ComponentDescriptor indexComponent : architecture.getComponents()
				.values()) {
			if ((indexComponent.getType() == ComponentType.Ip || indexComponent
					.getType() == ComponentType.Processor)
					&& indexComponent.getId().equalsIgnoreCase(
							indexComponent.getName())) {
				defaultOperator = (OperatorDescriptor) indexComponent;
			} else if (indexComponent.getType() == ComponentType.Network
					&& indexComponent.getId().equalsIgnoreCase(
							indexComponent.getName())) {
				defaultNetwork = (NetworkDescriptor) indexComponent;
			}
		}

		System.out.println(" default operator: Id=" + defaultOperator.getId()
				+ "; Name=" + defaultOperator.getName());
		System.out.println(" default network: Id=" + defaultNetwork.getId()
				+ "; Name=" + defaultNetwork.getName());
		System.out.println("Computations in the algorithm:");
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (!indexComputation.getComputationDurations().containsKey(
					defaultOperator)) {
				indexComputation.addComputationDuration(defaultOperator,
						indexComputation.getTime());
				System.out.println(" Name="
						+ indexComputation.getName()
						+ "; default computationDuration="
						+ indexComputation
								.getComputationDuration(defaultOperator));
			}
		}
		System.out.println("Communications in the algorithm:");
		for (CommunicationDescriptor indexCommunication : algorithm
				.getCommunications().values()) {
			if (!indexCommunication.getCommunicationDurations().containsKey(
					defaultNetwork)) {
				indexCommunication.addCommunicationDuration(defaultNetwork,
						indexCommunication.getWeight());
				System.out.println(" Name="
						+ indexCommunication.getName()
						+ "; default communicationDuration="
						+ indexCommunication
								.getCommunicationDuration(defaultNetwork));
			}
		}
		System.out.println("Operators in the architecture:");
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			System.out.println(" Id=" + indexOperator.getId() + "; Name="
					+ indexOperator.getName());
		}
	}

	private void testScheduler(AbstractScheduler scheduler,
			AlgorithmDescriptor algorithm, ArchitectureDescriptor architecture) {

		System.out.println("\nSchedule method: " + scheduler.getName());
		scheduler.schedule();
		System.out.println("\n***** Schedule results *****");
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			System.out.println("\n Operator: Id=" + indexOperator.getId()
					+ "; Name=" + indexOperator.getName());
			for (OperationDescriptor indexOperation : indexOperator
					.getOperations()) {
				if (indexOperation != scheduler.getTopCommunication()
						&& indexOperation != scheduler.getBottomCommunication()) {
					if (indexOperator.getComputations()
							.contains(indexOperation)) {
						System.out.println("  computation: Name="
								+ indexOperation.getName()
								+ "\n   1> startTime="
								+ indexOperator.getOccupiedTimeInterval(
										indexOperation.getName())
										.getStartTime()
								+ "\n   2> finishTime="
								+ indexOperator.getOccupiedTimeInterval(
										indexOperation.getName())
										.getFinishTime());
					} else {
						if (indexOperator.getSendCommunications().contains(
								indexOperation)) {
							System.out
									.println("  sendCommunication: Name="
											+ indexOperation.getName()
											+ "\n   1> startTimeOnSendOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getStartTime()
											+ "\n   2> finishTimeOnSendOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getFinishTime()
											+ "\n   3> startTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getStartTimeOnLink()
											+ "\n   4> finishTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getFinishTimeOnLink());
						} else {
							System.out
									.println("  receiveCommunication: Name="
											+ indexOperation.getName()
											+ "\n   1> startTimeOnReceiveOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getStartTime()
											+ "\n   2> finishTimeOnReceiveOperator="
											+ indexOperator
													.getOccupiedTimeInterval(
															indexOperation
																	.getName())
													.getFinishTime()
											+ "\n   3> startTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getStartTimeOnLink()
											+ "\n   4> finishTimeOnLink="
											+ ((CommunicationDescriptor) indexOperation)
													.getFinishTimeOnLink());
						}
					}
				}
			}
			for (LinkDescriptor indexLink : indexOperator.getOutputLinks()) {
				System.out.println(" outputLink: Id=" + indexLink.getId()
						+ "; Name=" + indexLink.getName());
				for (CommunicationDescriptor indexCommunication : indexLink
						.getCommunications()) {
					if (indexCommunication.getSendLink() == indexLink) {
						System.out.println("  sendCommunication: Name="
								+ indexCommunication.getName()
								+ "\n   1> startTimeOnLink="
								+ indexCommunication.getStartTimeOnLink()
								+ "\n   2> finishTimeOnLink="
								+ indexCommunication.getFinishTimeOnLink());
					}
				}
			}
			for (LinkDescriptor indexLink : indexOperator.getInputLinks()) {
				System.out.println(" inputLink: Id=" + indexLink.getId()
						+ "; Name=" + indexLink.getName());
				for (CommunicationDescriptor indexCommunication : indexLink
						.getCommunications()) {
					if (indexCommunication.getReceiveLink() == indexLink) {
						System.out.println("  receiveCommunication: Name="
								+ indexCommunication.getName()
								+ "\n   1> startTimeOnLink="
								+ indexCommunication.getStartTimeOnLink()
								+ "\n   2> finishTimeOnLink="
								+ indexCommunication.getFinishTimeOnLink());
					}
				}
			}
		}
		System.out.println("\n***** Schedule Length="
				+ scheduler.getScheduleLength() + " *****\n");
	}
}
