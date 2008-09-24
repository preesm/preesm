package org.ietr.preesm.plugin.fpga_scheduler;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevel;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelComputation;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelIn;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelInOut;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.ListSchedulingAdvancedWithStaticOrderByBottomLevelOut;
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
import org.ietr.preesm.plugin.fpga_scheduler.test.DAGCreator;
import org.ietr.preesm.plugin.fpga_scheduler.test.RandomComparisonListScheduling;

import org.sdf4j.model.sdf.SDFGraph;

public class DemoRandomComparisonListScheduling extends
		RandomComparisonListScheduling {

	public DemoRandomComparisonListScheduling(String algorithmFileName,
			String parameterFileName, String architectureFileName) {
		super(algorithmFileName, parameterFileName, architectureFileName);
		System.out
				.println("\n***** Random Comparison List Scheduling With Static Order Begins! *****");
		DAGCreator dagCreator = new DAGCreator();

		// Generating random sdf dag
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		SDFGraph graph = dagCreator.randomDAG(nbVertex, minInDegree,
				maxInDegree, minOutDegree, maxOutDegree, 1000);
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
				.println("\n\n***** Random Compare List Scheduling With Static Order Finishes!*****");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String algorithmFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\algorithm.xml";
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\architecture1.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\fpga_scheduler\\test\\parameter.xml";

		new DemoRandomComparisonListScheduling(algorithmFileName,
				parameterFileName, architectureFileName);
	}

}
