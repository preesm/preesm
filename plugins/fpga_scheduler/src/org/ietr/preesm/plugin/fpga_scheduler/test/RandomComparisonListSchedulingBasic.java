package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.plotter.GanttPlotter;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingBasicWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingBasicWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingBasicWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingBasicWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingBasicWithStaticOrderByBottomLevelOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassic;
import org.jfree.ui.RefineryUtilities;

public class RandomComparisonListSchedulingBasic extends
		RandomComparisonListScheduling {
	private static String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";

	private static String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture1.xml";

	private static String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

	ListSchedulingBasicWithStaticOrderByBottomLevelComputation scheduler1 = null;

	ListSchedulingBasicWithStaticOrderByBottomLevel scheduler2 = null;

	ListSchedulingBasicWithStaticOrderByBottomLevelIn scheduler3 = null;

	ListSchedulingBasicWithStaticOrderByBottomLevelOut scheduler4 = null;

	ListSchedulingBasicWithStaticOrderByBottomLevelInOut scheduler5 = null;

	ListSchedulingClassic schedulerClassic = null;

	public RandomComparisonListSchedulingBasic(double ccr) {
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
				.println("\n***** Random Comparison List Scheduling Basic Begins! *****");
		RandomComparisonListSchedulingBasic randomCompare = new RandomComparisonListSchedulingBasic(
				1);
		randomCompare.drawDAG();
		randomCompare.compare();

		GanttPlotter plot1 = new GanttPlotter(
				randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingBasicWithStaticOrderByBottomLevelComputation()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelComputation());
		plot1.pack();
		RefineryUtilities.centerFrameOnScreen(plot1);
		plot1.setVisible(true);

		GanttPlotter plot2 = new GanttPlotter(randomCompare
				.getListSchedulingBasicWithStaticOrderByBottomLevel().getName()
				+ " -> Schedule Length="
				+ randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevel()
						.getScheduleLength(), randomCompare
				.getListSchedulingBasicWithStaticOrderByBottomLevel());
		plot2.pack();
		RefineryUtilities.centerFrameOnScreen(plot2);
		plot2.setVisible(true);

		GanttPlotter plot3 = new GanttPlotter(randomCompare
				.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
				.getName()
				+ " -> Schedule Length="
				+ randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelIn()
						.getScheduleLength(), randomCompare
				.getListSchedulingBasicWithStaticOrderByBottomLevelIn());
		plot3.pack();
		RefineryUtilities.centerFrameOnScreen(plot3);
		plot3.setVisible(true);

		GanttPlotter plot4 = new GanttPlotter(
				randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingBasicWithStaticOrderByBottomLevelOut()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelOut());
		plot4.pack();
		RefineryUtilities.centerFrameOnScreen(plot4);
		plot4.setVisible(true);

		GanttPlotter plot5 = new GanttPlotter(
				randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
						.getName()
						+ " -> Schedule Length="
						+ randomCompare
								.getListSchedulingBasicWithStaticOrderByBottomLevelInOut()
								.getScheduleLength(),
				randomCompare
						.getListSchedulingBasicWithStaticOrderByBottomLevelInOut());
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
				.println("\n\n***** Random Compare List Scheduling Basic Finishes!*****");
	}

	public int compare() {
		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler1 = new ListSchedulingBasicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler1, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler2 = new ListSchedulingBasicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler3 = new ListSchedulingBasicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler4 = new ListSchedulingBasicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler5 = new ListSchedulingBasicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler5, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		schedulerClassic = new ListSchedulingClassic(algorithm, architecture);
		testScheduler(schedulerClassic, algorithm, architecture);

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

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

		System.out.print("\n\t" + schedulerClassic.getName() + "\t\t\t\t\t\t\t"
				+ schedulerClassic.getScheduleLength() + "\t\t\t"
				+ schedulerClassic.getUsedOperators().size() + "\t\t\t");
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

	public ListSchedulingBasicWithStaticOrderByBottomLevelComputation getListSchedulingBasicWithStaticOrderByBottomLevelComputation() {
		return scheduler1;
	}

	public ListSchedulingBasicWithStaticOrderByBottomLevel getListSchedulingBasicWithStaticOrderByBottomLevel() {
		return scheduler2;
	}

	public ListSchedulingBasicWithStaticOrderByBottomLevelIn getListSchedulingBasicWithStaticOrderByBottomLevelIn() {
		return scheduler3;
	}

	public ListSchedulingBasicWithStaticOrderByBottomLevelOut getListSchedulingBasicWithStaticOrderByBottomLevelOut() {
		return scheduler4;
	}

	public ListSchedulingBasicWithStaticOrderByBottomLevelInOut getListSchedulingBasicWithStaticOrderByBottomLevelInOut() {
		return scheduler5;
	}

	public ListSchedulingClassic getListSchedulingClassic() {
		return schedulerClassic;
	}
}
