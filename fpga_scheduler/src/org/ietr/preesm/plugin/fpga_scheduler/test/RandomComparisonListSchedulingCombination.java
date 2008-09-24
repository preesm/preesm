package org.ietr.preesm.plugin.fpga_scheduler.test;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassic;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassicWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassicWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassicWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassicWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingClassicWithStaticOrderByBottomLevelOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCriticalChildWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCriticalChildWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingCriticalChildWithStaticOrderByBottomLevelOut;

import org.sdf4j.model.sdf.SDFGraph;

public class RandomComparisonListSchedulingCombination extends
		RandomComparisonListScheduling {

	private static String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";

	private static String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture1.xml";

	private static String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

	ListSchedulingClassicWithStaticOrderByBottomLevelComputation scheduler1 = null;

	ListSchedulingClassicWithStaticOrderByBottomLevel scheduler2 = null;

	ListSchedulingClassicWithStaticOrderByBottomLevelIn scheduler3 = null;

	ListSchedulingClassicWithStaticOrderByBottomLevelOut scheduler4 = null;

	ListSchedulingClassicWithStaticOrderByBottomLevelInOut scheduler5 = null;

	ListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation scheduler6 = null;

	ListSchedulingCriticalChildWithStaticOrderByBottomLevel scheduler7 = null;

	ListSchedulingCriticalChildWithStaticOrderByBottomLevelIn scheduler8 = null;

	ListSchedulingCriticalChildWithStaticOrderByBottomLevelOut scheduler9 = null;

	ListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut scheduler10 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation scheduler11 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel scheduler12 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn scheduler13 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut scheduler14 = null;

	ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut scheduler15 = null;

	ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation scheduler16 = null;

	ListSchedulingAdvancedWithStaticOrderByBottomLevel scheduler17 = null;

	ListSchedulingAdvancedWithStaticOrderByBottomLevelIn scheduler18 = null;

	ListSchedulingAdvancedWithStaticOrderByBottomLevelOut scheduler19 = null;

	ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut scheduler20 = null;

	ListSchedulingClassic schedulerClassic = null;

	public RandomComparisonListSchedulingCombination(double ccr) {
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

	public RandomComparisonListSchedulingCombination(String algorithmFileName,
			String parameterFileName, String architectureFileName) {
		super(algorithmFileName, parameterFileName, architectureFileName);
		System.out
				.println("\n***** Random Comparison List Scheduling Combination Begins! *****");
		DAGCreator dagCreator = new DAGCreator();

		// Generating random sdf dag
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = dagCreator.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 1500);
		generateRandomNodeWeight(graph);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelComputation scheduler1 = new ListSchedulingClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler1, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevel scheduler2 = new ListSchedulingClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelIn scheduler3 = new ListSchedulingClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelOut scheduler4 = new ListSchedulingClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelInOut scheduler5 = new ListSchedulingClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler5, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation scheduler6 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler6, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel scheduler7 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler7, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn scheduler8 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler8, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut scheduler9 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler9, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut scheduler10 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler10, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation scheduler11 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler11, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildWithStaticOrderByBottomLevel scheduler12 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler12, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildWithStaticOrderByBottomLevelIn scheduler13 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler13, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildWithStaticOrderByBottomLevelOut scheduler14 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler14, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut scheduler15 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler15, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation scheduler16 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler16, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevel scheduler17 = new ListSchedulingAdvancedWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler17, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelIn scheduler18 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler18, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelOut scheduler19 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler19, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut scheduler20 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler20, algorithm, architecture);

		System.out.println("***Compared Results***");

		System.out
				.print("No.\tScheduling Method\t\t\t\t\t\t\t\t\tSchedule Length\t\tUsed Operators\t\tScheduling Order");

		System.out.print("\n1\t" + scheduler1.getName() + "\t\t\t"
				+ scheduler1.getScheduleLength() + "\t\t\t"
				+ scheduler1.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler1
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n2\t" + scheduler2.getName() + "\t\t\t\t"
				+ scheduler2.getScheduleLength() + "\t\t\t"
				+ scheduler2.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler2
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n3\t" + scheduler3.getName() + "\t\t\t\t"
				+ scheduler3.getScheduleLength() + "\t\t\t"
				+ scheduler3.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler3
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n4\t" + scheduler4.getName() + "\t\t\t\t"
				+ scheduler4.getScheduleLength() + "\t\t\t"
				+ scheduler4.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler4
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n5\t" + scheduler5.getName() + "\t\t\t\t"
				+ scheduler5.getScheduleLength() + "\t\t\t"
				+ scheduler5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler5
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n6\t" + scheduler6.getName() + "\t"
				+ scheduler6.getScheduleLength() + "\t\t\t"
				+ scheduler6.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler6
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n7\t" + scheduler7.getName() + "\t\t\t"
				+ scheduler7.getScheduleLength() + "\t\t\t"
				+ scheduler7.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler7
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n8\t" + scheduler8.getName() + "\t\t"
				+ scheduler8.getScheduleLength() + "\t\t\t"
				+ scheduler8.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler8
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n9\t" + scheduler9.getName() + "\t\t"
				+ scheduler9.getScheduleLength() + "\t\t\t"
				+ scheduler9.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler9
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n10\t" + scheduler10.getName() + "\t\t"
				+ scheduler10.getScheduleLength() + "\t\t\t"
				+ scheduler10.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler10
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n11\t" + scheduler11.getName() + "\t\t"
				+ scheduler11.getScheduleLength() + "\t\t\t"
				+ scheduler11.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler11
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n12\t" + scheduler12.getName() + "\t\t\t"
				+ scheduler12.getScheduleLength() + "\t\t\t"
				+ scheduler12.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler12
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n13\t" + scheduler13.getName() + "\t\t\t"
				+ scheduler13.getScheduleLength() + "\t\t\t"
				+ scheduler13.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler13
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n14\t" + scheduler14.getName() + "\t\t\t"
				+ scheduler14.getScheduleLength() + "\t\t\t"
				+ scheduler14.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler14
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n15\t" + scheduler15.getName() + "\t\t\t"
				+ scheduler15.getScheduleLength() + "\t\t\t"
				+ scheduler15.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler15
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n16\t" + scheduler16.getName() + "\t\t\t"
				+ scheduler16.getScheduleLength() + "\t\t\t"
				+ scheduler16.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler16
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n17\t" + scheduler17.getName() + "\t\t\t\t"
				+ scheduler17.getScheduleLength() + "\t\t\t"
				+ scheduler17.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler17
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n18\t" + scheduler18.getName() + "\t\t\t\t"
				+ scheduler18.getScheduleLength() + "\t\t\t"
				+ scheduler18.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler18
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n19\t" + scheduler19.getName() + "\t\t\t\t"
				+ scheduler19.getScheduleLength() + "\t\t\t"
				+ scheduler19.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler19
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n20\t" + scheduler20.getName() + "\t\t\t"
				+ scheduler20.getScheduleLength() + "\t\t\t"
				+ scheduler20.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler20
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out
				.println("\n\n***** Random Compare List Scheduling Combination Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture1.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

		new RandomComparisonListSchedulingCombination(algorithmFileName,
				parameterFileName, architectureFileName);
	}

	public int compare() {
		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);

		scheduler1 = new ListSchedulingClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler1, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler2 = new ListSchedulingClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler2, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler3 = new ListSchedulingClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler3, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler4 = new ListSchedulingClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler4, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler5 = new ListSchedulingClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler5, algorithm, architecture);

		scheduler6 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler6, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler7 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler7, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler8 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler8, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler9 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler9, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler10 = new ListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler10, algorithm, architecture);

		scheduler11 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler11, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler12 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler12, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler13 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler13, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler14 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler14, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler15 = new ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler15, algorithm, architecture);

		scheduler16 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		testScheduler(scheduler16, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler17 = new ListSchedulingAdvancedWithStaticOrderByBottomLevel(
				algorithm, architecture);
		testScheduler(scheduler17, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler18 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		testScheduler(scheduler18, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler19 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		testScheduler(scheduler19, algorithm, architecture);

		algorithm = dagCreator.sdf2dag(graph);
		parse(graph, architectureFileName, parameterFileName);
		scheduler20 = new ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		testScheduler(scheduler20, algorithm, architecture);

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

		System.out.print("\n5\t" + scheduler5.getName() + "\t"
				+ scheduler5.getScheduleLength() + "\t\t\t"
				+ scheduler5.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler5
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n6\t" + scheduler6.getName() + "\t"
				+ scheduler6.getScheduleLength() + "\t\t\t"
				+ scheduler6.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler6
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n7\t" + scheduler7.getName() + "\t\t"
				+ scheduler7.getScheduleLength() + "\t\t\t"
				+ scheduler7.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler7
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n8\t" + scheduler8.getName() + "\t\t"
				+ scheduler8.getScheduleLength() + "\t\t\t"
				+ scheduler8.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler8
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n9\t" + scheduler9.getName() + "\t\t"
				+ scheduler9.getScheduleLength() + "\t\t\t"
				+ scheduler9.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler9
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n10\t" + scheduler10.getName() + "\t\t"
				+ scheduler10.getScheduleLength() + "\t\t\t"
				+ scheduler10.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler10
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n11\t" + scheduler11.getName() + "\t"
				+ scheduler11.getScheduleLength() + "\t\t\t"
				+ scheduler11.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler11
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n12\t" + scheduler12.getName() + "\t\t\t"
				+ scheduler12.getScheduleLength() + "\t\t\t"
				+ scheduler12.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler12
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n13\t" + scheduler13.getName() + "\t\t"
				+ scheduler13.getScheduleLength() + "\t\t\t"
				+ scheduler13.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler13
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n14\t" + scheduler14.getName() + "\t\t"
				+ scheduler14.getScheduleLength() + "\t\t\t"
				+ scheduler14.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler14
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n15\t" + scheduler15.getName() + "\t\t"
				+ scheduler15.getScheduleLength() + "\t\t\t"
				+ scheduler15.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler15
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n16\t" + scheduler16.getName() + "\t"
				+ scheduler16.getScheduleLength() + "\t\t\t"
				+ scheduler16.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler16
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n17\t" + scheduler17.getName() + "\t\t"
				+ scheduler17.getScheduleLength() + "\t\t\t"
				+ scheduler17.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler17
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n18\t" + scheduler18.getName() + "\t\t"
				+ scheduler18.getScheduleLength() + "\t\t\t"
				+ scheduler18.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler18
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n19\t" + scheduler19.getName() + "\t\t"
				+ scheduler19.getScheduleLength() + "\t\t\t"
				+ scheduler19.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler19
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n20\t" + scheduler5.getName() + "\t"
				+ scheduler20.getScheduleLength() + "\t\t\t"
				+ scheduler20.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler20
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
		minScheduleLength = minScheduleLength > scheduler6.getScheduleLength() ? scheduler6
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler7.getScheduleLength() ? scheduler7
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler8.getScheduleLength() ? scheduler8
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler9.getScheduleLength() ? scheduler9
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler10.getScheduleLength() ? scheduler10
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler11.getScheduleLength() ? scheduler11
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler12.getScheduleLength() ? scheduler12
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler13.getScheduleLength() ? scheduler13
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler14.getScheduleLength() ? scheduler14
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler15.getScheduleLength() ? scheduler15
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler16.getScheduleLength() ? scheduler16
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler17.getScheduleLength() ? scheduler17
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler18.getScheduleLength() ? scheduler18
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler19.getScheduleLength() ? scheduler19
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > scheduler20.getScheduleLength() ? scheduler20
				.getScheduleLength()
				: minScheduleLength;
		minScheduleLength = minScheduleLength > schedulerClassic
				.getScheduleLength() ? schedulerClassic.getScheduleLength()
				: minScheduleLength;
		if (minScheduleLength == scheduler1.getScheduleLength()
				|| minScheduleLength == scheduler2.getScheduleLength()
				|| minScheduleLength == scheduler3.getScheduleLength()
				|| minScheduleLength == scheduler4.getScheduleLength()
				|| minScheduleLength == scheduler5.getScheduleLength()
				|| minScheduleLength == scheduler6.getScheduleLength()
				|| minScheduleLength == scheduler7.getScheduleLength()
				|| minScheduleLength == scheduler8.getScheduleLength()
				|| minScheduleLength == scheduler9.getScheduleLength()
				|| minScheduleLength == scheduler10.getScheduleLength()
				|| minScheduleLength == scheduler11.getScheduleLength()
				|| minScheduleLength == scheduler12.getScheduleLength()
				|| minScheduleLength == scheduler13.getScheduleLength()
				|| minScheduleLength == scheduler14.getScheduleLength()
				|| minScheduleLength == scheduler15.getScheduleLength()
				|| minScheduleLength == scheduler16.getScheduleLength()
				|| minScheduleLength == scheduler17.getScheduleLength()
				|| minScheduleLength == scheduler18.getScheduleLength()
				|| minScheduleLength == scheduler19.getScheduleLength()
				|| minScheduleLength == scheduler20.getScheduleLength()) {
			return 1;
		} else {
			return 0;
		}
	}

	public ListSchedulingClassicWithStaticOrderByBottomLevelComputation getListSchedulingClassicWithStaticOrderByBottomLevelComputation() {
		return scheduler1;
	}

	public ListSchedulingClassicWithStaticOrderByBottomLevel getListSchedulingClassicWithStaticOrderByBottomLevel() {
		return scheduler2;
	}

	public ListSchedulingClassicWithStaticOrderByBottomLevelIn getListSchedulingClassicWithStaticOrderByBottomLevelIn() {
		return scheduler3;
	}

	public ListSchedulingClassicWithStaticOrderByBottomLevelOut getListSchedulingClassicWithStaticOrderByBottomLevelOut() {
		return scheduler4;
	}

	public ListSchedulingClassicWithStaticOrderByBottomLevelInOut getListSchedulingClassicWithStaticOrderByBottomLevelInOut() {
		return scheduler5;
	}

	public ListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation getListSchedulingCriticalChildWithStaticOrderByBottomLevelComputation() {
		return scheduler6;
	}

	public ListSchedulingCriticalChildWithStaticOrderByBottomLevel getListSchedulingCriticalChildWithStaticOrderByBottomLevel() {
		return scheduler7;
	}

	public ListSchedulingCriticalChildWithStaticOrderByBottomLevelIn getListSchedulingCriticalChildWithStaticOrderByBottomLevelIn() {
		return scheduler8;
	}

	public ListSchedulingCriticalChildWithStaticOrderByBottomLevelOut getListSchedulingCriticalChildWithStaticOrderByBottomLevelOut() {
		return scheduler9;
	}

	public ListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut getListSchedulingCriticalChildWithStaticOrderByBottomLevelInOut() {
		return scheduler10;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelComputation() {
		return scheduler11;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevel getListSchedulingCommunicationDelayWithStaticOrderByBottomLevel() {
		return scheduler12;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelIn() {
		return scheduler13;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelOut() {
		return scheduler14;
	}

	public ListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut getListSchedulingCommunicationDelayWithStaticOrderByBottomLevelInOut() {
		return scheduler15;
	}

	public ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation getListSchedulingAdvancedWithStaticOrderByBottomLevelComputation() {
		return scheduler16;
	}

	public ListSchedulingAdvancedWithStaticOrderByBottomLevel getListSchedulingAdvancedWithStaticOrderByBottomLevel() {
		return scheduler17;
	}

	public ListSchedulingAdvancedWithStaticOrderByBottomLevelIn getListSchedulingAdvancedWithStaticOrderByBottomLevelIn() {
		return scheduler18;
	}

	public ListSchedulingAdvancedWithStaticOrderByBottomLevelOut getListSchedulingAdvancedWithStaticOrderByBottomLevelOut() {
		return scheduler19;
	}

	public ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut getListSchedulingAdvancedWithStaticOrderByBottomLevelInOut() {
		return scheduler20;
	}

	public ListSchedulingClassic getListSchedulingClassic() {
		return schedulerClassic;
	}
}
