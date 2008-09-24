package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.scheduler.*;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.*;
import org.ietr.preesm.plugin.fpga_scheduler.plotter.GanttPlotter;
import org.jfree.ui.RefineryUtilities;

public class CompareListSchedulingAdvancedToClassic extends
		CompareListScheduling {

	public CompareListSchedulingAdvancedToClassic(String algorithmFileName,
			String parameterFileName, String architectureFileName) {
		super(algorithmFileName, parameterFileName, architectureFileName);
		System.out
				.println("\n***** Compare List Scheduling Advanced To Classic (ordered by bottom-level) Begins! *****");

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingClassic scheduler1 = new ListSchedulingClassic(algorithm,
				architecture);
		testScheduler(scheduler1, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingCriticalChild scheduler2 = new ListSchedulingCriticalChild(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		parse(algorithmFileName, architectureFileName, parameterFileName);
		ListSchedulingAdvanced scheduler3 = new ListSchedulingAdvanced(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t\t\t"
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

		System.out
				.println("\n\n***** Compare List Scheduling Advanced To Classic (ordered by bottom-level) Finishes!*****");
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
		new CompareListSchedulingAdvancedToClassic(algorithmFileName,
				parameterFileName, architectureFileName);
	}

}
