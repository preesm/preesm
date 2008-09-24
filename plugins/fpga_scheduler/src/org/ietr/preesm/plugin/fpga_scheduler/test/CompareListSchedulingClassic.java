package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.scheduler.*;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.*;
import org.ietr.preesm.plugin.fpga_scheduler.plotter.GanttPlotter;
import org.jfree.ui.RefineryUtilities;

public class CompareListSchedulingClassic extends CompareListScheduling {

	public CompareListSchedulingClassic(String algorithmFileName,
			String parameterFileName, String architectureFileName) {
		super(algorithmFileName, parameterFileName, architectureFileName);
		System.out
				.println("\n***** Compare List Scheduling Classic Begins! *****");

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelComputation scheduler1 = new ListSchedulingClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevel scheduler2 = new ListSchedulingClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelIn scheduler3 = new ListSchedulingClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelOut scheduler4 = new ListSchedulingClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelInOut scheduler5 = new ListSchedulingClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler5, algorithm, architecture);

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t"
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

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t"
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

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t"
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

		System.out.print("\n4\t" + scheduler4.getName() + "\t\t"
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

		System.out
				.println("\n\n*****Compare List Scheduling Classic Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

		int argc = Integer.parseInt(args[0]);
		System.out.println("Number of argument: " + argc);

		switch (argc) {
		case 1: {
			algorithmFileName = args[1];
			System.out.println("algorithm: " + algorithmFileName);
			break;
		}
		case 2: {
			algorithmFileName = args[1];
			System.out.println("algorithm: " + algorithmFileName);
			architectureFileName = args[2];
			System.out.println("architecture: " + architectureFileName);
			break;
		}
		case 3: {
			algorithmFileName = args[1];
			System.out.println("algorithm: " + algorithmFileName);
			architectureFileName = args[2];
			System.out.println("architecture: " + architectureFileName);
			parameterFileName = args[3];
			System.out.println("parameter: " + parameterFileName);
			break;
		}
		default: {
			System.out.println("Invalid command! Use default arguments!");
			System.out.println("algorithm: " + algorithmFileName);
			System.out.println("architecture: " + architectureFileName);
			System.out.println("parameter: " + parameterFileName);
		}
		}
		new CompareListSchedulingClassic(algorithmFileName, parameterFileName,
				architectureFileName);
	}

}
