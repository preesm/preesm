package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.plotter.GanttPlotter;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassic;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut;
import org.jfree.ui.RefineryUtilities;

public class RandomComparisonListSchedulingCommunicationDelayBasic extends
		RandomComparisonListScheduling {

	private static String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";

	private static String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture1.xml";

	private static String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

	ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation scheduler1 = null;

	ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel scheduler2 = null;

	ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn scheduler3 = null;

	ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut scheduler4 = null;

	ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut scheduler5 = null;

	ListSchedulingClassic schedulerClassic = null;

	public RandomComparisonListSchedulingCommunicationDelayBasic(double ccr) {
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
				.println("\n***** Random Comparison List Scheduling CommunicationDelayBasic Begins! *****");
		RandomComparisonListSchedulingCommunicationDelayBasic randomCompare = new RandomComparisonListSchedulingCommunicationDelayBasic(
				1);
		randomCompare.drawDAG();
		randomCompare.compare();

		GanttPlotter plot1 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation());
		plot1.pack();
		RefineryUtilities.centerFrameOnScreen(plot1);
		plot1.setVisible(true);

		GanttPlotter plot2 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel());
		plot2.pack();
		RefineryUtilities.centerFrameOnScreen(plot2);
		plot2.setVisible(true);

		GanttPlotter plot3 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn());
		plot3.pack();
		RefineryUtilities.centerFrameOnScreen(plot3);
		plot3.setVisible(true);

		GanttPlotter plot4 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut());
		plot4.pack();
		RefineryUtilities.centerFrameOnScreen(plot4);
		plot4.setVisible(true);

		GanttPlotter plot5 = new GanttPlotter(
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut());
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
				.println("\n\n***** Random Compare List Scheduling CommunicationDelayBasic Finishes!*****");
	}

	public int compare() {
		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);

		scheduler1 = new ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler1, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler2 = new ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler3 = new ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler4 = new ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler5 = new ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut(
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

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t"
				+ scheduler2.getScheduleLength() + "\t\t\t"
				+ scheduler2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t"
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

	public ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelComputation() {
		return scheduler1;
	}

	public ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevel() {
		return scheduler2;
	}

	public ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelIn() {
		return scheduler3;
	}

	public ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelOut() {
		return scheduler4;
	}

	public ListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut getListSchedulingCommunicationDelayBasicWithStaticOrderByBottomLevelInOut() {
		return scheduler5;
	}

	public ListSchedulingClassic getListSchedulingClassic() {
		return schedulerClassic;
	}

}
