package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.plotter.GanttPlotter;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassic;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut;
import org.jfree.ui.RefineryUtilities;

public class RandomComparisonListSchedulingCommunicationDelay extends
		RandomComparisonListScheduling {

	private static String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";

	private static String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture1.xml";

	private static String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation scheduler1 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel scheduler2 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn scheduler3 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut scheduler4 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut scheduler5 = null;

	ListSchedulingClassic schedulerClassic = null;

	public RandomComparisonListSchedulingCommunicationDelay(double ccr) {
		super(algorithmFileName, parameterFileName, architectureFileName);
		dagCreator = new DAGCreator();
		// Generating random sdf dag
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		graph = dagCreator
				.randomDAG(nbVertex, minInDegree, maxInDegree, minOutDegree,
						maxOutDegree,
						(int) (500 * ccr / ((minInDegree + maxInDegree
								+ minOutDegree + maxOutDegree) / 4)),
						(int) (1000 * ccr / ((minInDegree + maxInDegree
								+ minOutDegree + maxOutDegree) / 4)));
		generateRandomNodeWeight(graph, 500, 1000);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out
				.println("\n***** Random Comparison List Scheduling CommunicationDelay Begins! *****");
		RandomComparisonListSchedulingCommunicationDelay randomCompare = new RandomComparisonListSchedulingCommunicationDelay(
				1);
		randomCompare.drawDAG();
		randomCompare.compare();

		GanttPlotter plot1 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation());
		plot1.pack();
		RefineryUtilities.centerFrameOnScreen(plot1);
		plot1.setVisible(true);

		GanttPlotter plot2 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel());
		plot2.pack();
		RefineryUtilities.centerFrameOnScreen(plot2);
		plot2.setVisible(true);

		GanttPlotter plot3 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn());
		plot3.pack();
		RefineryUtilities.centerFrameOnScreen(plot3);
		plot3.setVisible(true);

		GanttPlotter plot4 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut());
		plot4.pack();
		RefineryUtilities.centerFrameOnScreen(plot4);
		plot4.setVisible(true);

		GanttPlotter plot5 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut());
		plot5.pack();
		RefineryUtilities.centerFrameOnScreen(plot5);
		plot5.setVisible(true);

		GanttPlotter plot = new GanttPlotter(randomCompare
				.getListSchedulingClassic().getName()
				+ " -> Schedule Length="
				+ randomCompare.getListSchedulingClassic().getScheduleLength(),
				randomCompare.getListSchedulingClassic());
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);

		System.out
				.println("\n\n***** Random Compare List Scheduling CommunicationDelay Finishes!*****");

	}

	public int compare() {
		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);

		scheduler1 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler1, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler2 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler3 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler4 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler5 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler5, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		schedulerClassic = new ListSchedulingClassic(algorithm, architecture);
		testScheduler(schedulerClassic, algorithm, architecture);

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t"
				+ scheduler1.getScheduleLength() + "\t\t\t"
				+ scheduler1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler1
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t\t"
				+ scheduler2.getScheduleLength() + "\t\t\t"
				+ scheduler2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t\t"
				+ scheduler3.getScheduleLength() + "\t\t\t"
				+ scheduler3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler3
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n4\t" + scheduler4.getName() + "\t\t"
				+ scheduler4.getScheduleLength() + "\t\t\t"
				+ scheduler4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler4
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n5\t" + scheduler5.getName() + "\t\t"
				+ scheduler5.getScheduleLength() + "\t\t\t"
				+ scheduler5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler5
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n\t" + schedulerClassic.getName()
				+ "\t\t\t\t\t\t\t\t\t" + schedulerClassic.getScheduleLength()
				+ "\t\t\t" + schedulerClassic.getUsedOperators().size()
				+ "\t\t\t");
		for (ComputationDescriptor indexComputation : schedulerClassic
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.println("\n********************");
		int minScheduleLength = Integer.MAX_VALUE;
		minScheduleLength = minScheduleLength > scheduler1.getScheduleLength() ? scheduler1
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler2.getScheduleLength() ? scheduler2
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler3.getScheduleLength() ? scheduler3
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler4.getScheduleLength() ? scheduler4
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler5.getScheduleLength() ? scheduler5
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > schedulerClassic
				.getScheduleLength() ? schedulerClassic.getScheduleLength()
				: minScheduleLength;
		if (minScheduleLength == scheduler1.getScheduleLength()
				|| minScheduleLength == scheduler2.getScheduleLength()
				|| minScheduleLength == scheduler3.getScheduleLength()
				|| minScheduleLength == scheduler4.getScheduleLength()
				|| minScheduleLength == scheduler5.getScheduleLength()) {
			return 1;
		} else {
			return 0;
		}
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation() {
		return scheduler1;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel() {
		return scheduler2;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn() {
		return scheduler3;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut() {
		return scheduler4;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut() {
		return scheduler5;
	}

	public ListSchedulingClassic getListSchedulingClassic() {
		return schedulerClassic;
	}

}
