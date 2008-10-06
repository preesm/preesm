package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling;

import java.util.HashMap;

import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComponentType;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.NetworkDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.OperatorDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.parser.ArchitectureParser;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.parser.ParameterParser;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.plotter.GanttPlotter;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.scheduler.*;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.AlgorithmTransformer;
import org.jfree.ui.RefineryUtilities;

import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

public class CombinedListScheduling {

	private String architectureFileName = "D:\\Projets\\PreesmSourceForge\\trunk\\plugins\\mapper\\src\\org\\ietr\\preesm\\plugin\\mapper\\communicationcontentiouslistscheduling\\architecture2.xml";

	private String parameterFileName = "D:\\Projets\\PreesmSourceForge\\trunk\\plugins\\mapper\\src\\org\\ietr\\preesm\\plugin\\mapper\\communicationcontentiouslistscheduling\\parameter.xml";

	private AlgorithmTransformer transformer = null;

	private SDFGraph graph = null;

	private HashMap<String, Integer> computationWeights = null;

	private AlgorithmDescriptor algorithm = null;

	private ArchitectureDescriptor architecture = null;

	private AbstractScheduler bestScheduler = null;

	private int bestScheduleLength = Integer.MAX_VALUE;

	public CombinedListScheduling(SDFGraph graph) {
		transformer = new AlgorithmTransformer();
		this.graph = graph;
		generateNodeWeight(graph, 100);
	}

	public CombinedListScheduling(String parameterFileName,
			String architectureFileName) {
		transformer = new AlgorithmTransformer();

		// Generating random DAG-like SDF
		int nbVertex = 100, minInDegree = 1, maxInDegree = 3, minOutDegree = 1, maxOutDegree = 3;
		graph = transformer.randomSDF(nbVertex, minInDegree, maxInDegree,
				minOutDegree, maxOutDegree, 500, 1000);
		generateRandomNodeWeight(graph, 500, 1000);
	}

	private void parse(SDFGraph sdf, String architectureFileName,
			String parameterFileName) {

		for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
			algorithm.getComputation(indexVertex.getName()).setTime(
					computationWeights.get(indexVertex.getName()));
		}

		architecture = new ArchitectureDescriptor();
		new ArchitectureParser(architectureFileName, architecture).parse();
		// Parse the design parameter document
		new ParameterParser(parameterFileName, architecture, algorithm).parse();

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

	private void generateRandomNodeWeight(SDFGraph sdf, double minWeight,
			double maxWeight) {
		computationWeights = new HashMap<String, Integer>();

		for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
			Double taskSize = Math.random() * (maxWeight - minWeight)
					+ minWeight;
			computationWeights.put(indexVertex.getName(), taskSize.intValue());
			// System.out.println("name: " + indexVertex.getName() + "; weight:"
			// + taskSize.intValue());
		}
	}

	private void generateNodeWeight(SDFGraph sdf, int weight) {
		computationWeights = new HashMap<String, Integer>();

		for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
			if (indexVertex.getName().equalsIgnoreCase("copy")) {
				computationWeights.put(indexVertex.getName(), 10 * weight);
			} else {
				computationWeights.put(indexVertex.getName(), weight);
			}
		}
	}

	public void schedule() {
		System.out
				.println("\n***** Combined List Scheduling With Static Order Begins! *****");
		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelComputation scheduler1 = new ListSchedulingClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		scheduler1.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevel scheduler2 = new ListSchedulingClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		scheduler2.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelIn scheduler3 = new ListSchedulingClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		scheduler3.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelOut scheduler4 = new ListSchedulingClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		scheduler4.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingClassicWithStaticOrderByBottomLevelInOut scheduler5 = new ListSchedulingClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		scheduler5.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation scheduler6 = new ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		scheduler6.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel scheduler7 = new ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		scheduler7.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn scheduler8 = new ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		scheduler8.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut scheduler9 = new ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		scheduler9.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut scheduler10 = new ListSchedulingCriticalChildClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		scheduler10.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation scheduler11 = new ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		scheduler11.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel scheduler12 = new ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		scheduler12.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn scheduler13 = new ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		scheduler13.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut scheduler14 = new ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		scheduler14.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut scheduler15 = new ListSchedulingCommunicationDelayClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		scheduler15.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation scheduler16 = new ListSchedulingCcCdClassicWithStaticOrderByBottomLevelComputation(
				algorithm, architecture);
		scheduler16.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCcCdClassicWithStaticOrderByBottomLevel scheduler17 = new ListSchedulingCcCdClassicWithStaticOrderByBottomLevel(
				algorithm, architecture);
		scheduler17.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn scheduler18 = new ListSchedulingCcCdClassicWithStaticOrderByBottomLevelIn(
				algorithm, architecture);
		scheduler18.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut scheduler19 = new ListSchedulingCcCdClassicWithStaticOrderByBottomLevelOut(
				algorithm, architecture);
		scheduler19.schedule();

		algorithm = transformer.sdf2Algorithm(graph);
		parse(graph, architectureFileName, parameterFileName);
		ListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut scheduler20 = new ListSchedulingCcCdClassicWithStaticOrderByBottomLevelInOut(
				algorithm, architecture);
		scheduler20.schedule();

		chooseBestScheduler(scheduler1);
		chooseBestScheduler(scheduler2);
		chooseBestScheduler(scheduler3);
		chooseBestScheduler(scheduler4);
		chooseBestScheduler(scheduler5);
		chooseBestScheduler(scheduler6);
		chooseBestScheduler(scheduler7);
		chooseBestScheduler(scheduler8);
		chooseBestScheduler(scheduler9);
		chooseBestScheduler(scheduler10);
		chooseBestScheduler(scheduler11);
		chooseBestScheduler(scheduler12);
		chooseBestScheduler(scheduler13);
		chooseBestScheduler(scheduler14);
		chooseBestScheduler(scheduler15);
		chooseBestScheduler(scheduler16);
		chooseBestScheduler(scheduler17);
		chooseBestScheduler(scheduler18);
		chooseBestScheduler(scheduler19);
		chooseBestScheduler(scheduler20);

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

		System.out.print("\n11\t" + scheduler11.getName() + "\t"
				+ scheduler11.getScheduleLength() + "\t\t\t"
				+ scheduler11.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler11
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n12\t" + scheduler12.getName() + "\t\t"
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

		System.out.print("\n15\t" + scheduler15.getName() + "\t"
				+ scheduler15.getScheduleLength() + "\t\t\t"
				+ scheduler15.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler15
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n16\t" + scheduler16.getName() + "\t\t"
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

		System.out.print("\n18\t" + scheduler18.getName() + "\t\t\t"
				+ scheduler18.getScheduleLength() + "\t\t\t"
				+ scheduler18.getUsedOperators().size() + "\t\t\t");
		for (ComputationDescriptor indexComputation : scheduler18
				.getSchedulingOrder()) {
			System.out.print(indexComputation.getName() + " ");
		}

		System.out.print("\n19\t" + scheduler19.getName() + "\t\t\t"
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

		System.out.print("\n\nBest Scheduler:\t\t" + bestScheduler.getName()
				+ "\nSchedule Length:\t" + bestScheduler.getScheduleLength()
				+ "\nUsed Operators:\t\t"
				+ bestScheduler.getUsedOperators().size());

		plot(bestScheduler);
		System.out
				.println("\n\n***** Combined List Scheduling With Static Order Finishes!*****");
	}

	private void chooseBestScheduler(AbstractScheduler scheduler) {
		if (bestScheduleLength > scheduler.getScheduleLength()) {
			bestScheduler = scheduler;
			bestScheduleLength = scheduler.getScheduleLength();
		} else if (bestScheduleLength == scheduler.getScheduleLength()) {
			if (bestScheduler.getUsedOperators().size() > scheduler
					.getUsedOperators().size()) {
				bestScheduler = scheduler;
				bestScheduleLength = scheduler.getScheduleLength();
			}
		}
	}

	private void plot(AbstractScheduler scheduler) {
		GanttPlotter plot = new GanttPlotter(scheduler.getName()
				+ " -> Schedule Length=" + scheduler.getScheduleLength(),
				scheduler);
		plot.pack();
		RefineryUtilities.centerFrameOnScreen(plot);
		plot.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String architectureFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\communicationcontentiouslistscheduling\\architecture1.xml";
		String parameterFileName = "src\\org\\ietr\\preesm\\plugin\\mapper\\communicationcontentiouslistscheduling\\parameter.xml";

		CombinedListScheduling test = new CombinedListScheduling(
				parameterFileName, architectureFileName);
		test.schedule();
	}

}
