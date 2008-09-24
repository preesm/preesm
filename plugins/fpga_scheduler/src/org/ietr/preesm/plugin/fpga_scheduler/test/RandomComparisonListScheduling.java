package org.ietr.preesm.plugin.fpga_scheduler.test;

import java.util.HashMap;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComponentType;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.NetworkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.OperatorDescriptor;

import org.ietr.preesm.plugin.fpga_scheduler.parser.ArchitectureParser;
import org.ietr.preesm.plugin.fpga_scheduler.parser.ParameterParser;
import org.ietr.preesm.plugin.fpga_scheduler.scheduler.AbstractScheduler;

import org.sdf4j.demo.SDFAdapterDemo;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;

public class RandomComparisonListScheduling extends CompareListScheduling {

	protected DAGCreator dagCreator = null;

	protected SDFGraph graph = null;

	protected HashMap<String, Integer> computationWeights = null;

	public RandomComparisonListScheduling(String algorithmFileName,
			String parameterFileName, String architectureFileName) {
		super(algorithmFileName, parameterFileName, architectureFileName);
	}

	public void drawDAG() {
		SDFAdapterDemo applet = new SDFAdapterDemo();
		applet.init(graph);
	}

	protected void generateRandomNodeWeight(SDFGraph sdf) {
		generateRandomNodeWeight(sdf, 1, 1000);
	}

	protected void generateRandomNodeWeight(SDFGraph sdf, double weight) {
		generateRandomNodeWeight(sdf, weight, weight);
	}

	protected void generateRandomNodeWeight(SDFGraph sdf, double minWeight,
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

	protected void parse(SDFGraph sdf, String architectureFileName,
			String parameterFileName) {
		// Parse the design algorithm document
		// algorithm = new AlgorithmDescriptor(new DAGEdgeFactory());
		for (SDFAbstractVertex indexVertex : sdf.vertexSet()) {
			algorithm.getComputation(indexVertex.getName()).setTime(
					computationWeights.get(indexVertex.getName()));
		}
		// for (ComputationDescriptor indexComputation : algorithm
		// .getComputations().values()) {
		// indexComputation.setTime(computationWeights.get(indexComputation
		// .getName()));
		// }
		// Parse the architecture document
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

	protected void testScheduler(AbstractScheduler scheduler,
			AlgorithmDescriptor algorithm, ArchitectureDescriptor architecture) {

		System.out.println("\nSchedule method: " + scheduler.getName());
		scheduler.schedule();

		// System.out.println("\n***** Schedule results *****");
		// for (OperatorDescriptor indexOperator :
		// architecture.getAllOperators()
		// .values()) {
		// System.out.println("\n Operator: Id=" + indexOperator.getId()
		// + "; Name=" + indexOperator.getName());
		// for (OperationDescriptor indexOperation : indexOperator
		// .getOperations()) {
		// if (indexOperation != scheduler.getTopCommunication()
		// && indexOperation != scheduler.getBottomCommunication()) {
		// if (indexOperator.getComputations()
		// .contains(indexOperation)) {
		// System.out.println(" computation: Name="
		// + indexOperation.getName()
		// + "\n 1> startTime="
		// + indexOperator.getOccupiedTimeInterval(
		// indexOperation.getName())
		// .getStartTime()
		// + "\n 2> finishTime="
		// + indexOperator.getOccupiedTimeInterval(
		// indexOperation.getName())
		// .getFinishTime());
		// } else {
		// if (indexOperator.getSendCommunications().contains(
		// indexOperation)) {
		// System.out
		// .println(" sendCommunication: Name="
		// + indexOperation.getName()
		// + "\n 1> startTimeOnSendOperator="
		// + indexOperator
		// .getOccupiedTimeInterval(
		// indexOperation
		// .getName())
		// .getStartTime()
		// + "\n 2> finishTimeOnSendOperator="
		// + indexOperator
		// .getOccupiedTimeInterval(
		// indexOperation
		// .getName())
		// .getFinishTime()
		// + "\n 3> startTimeOnLink="
		// + ((CommunicationDescriptor) indexOperation)
		// .getStartTimeOnLink()
		// + "\n 4> finishTimeOnLink="
		// + ((CommunicationDescriptor) indexOperation)
		// .getFinishTimeOnLink());
		// } else {
		// System.out
		// .println(" receiveCommunication: Name="
		// + indexOperation.getName()
		// + "\n 1> startTimeOnReceiveOperator="
		// + indexOperator
		// .getOccupiedTimeInterval(
		// indexOperation
		// .getName())
		// .getStartTime()
		// + "\n 2> finishTimeOnReceiveOperator="
		// + indexOperator
		// .getOccupiedTimeInterval(
		// indexOperation
		// .getName())
		// .getFinishTime()
		// + "\n 3> startTimeOnLink="
		// + ((CommunicationDescriptor) indexOperation)
		// .getStartTimeOnLink()
		// + "\n 4> finishTimeOnLink="
		// + ((CommunicationDescriptor) indexOperation)
		// .getFinishTimeOnLink());
		// }
		// }
		// }
		// }
		// for (LinkDescriptor indexLink : indexOperator.getOutputLinks()) {
		// System.out.println(" outputLink: Id=" + indexLink.getId()
		// + "; Name=" + indexLink.getName());
		// for (CommunicationDescriptor indexCommunication : indexLink
		// .getCommunications()) {
		// if (indexCommunication.getSendLink() == indexLink) {
		// System.out.println(" sendCommunication: Name="
		// + indexCommunication.getName()
		// + "\n 1> startTimeOnLink="
		// + indexCommunication.getStartTimeOnLink()
		// + "\n 2> finishTimeOnLink="
		// + indexCommunication.getFinishTimeOnLink());
		// }
		// }
		// }
		// for (LinkDescriptor indexLink : indexOperator.getInputLinks()) {
		// System.out.println(" inputLink: Id=" + indexLink.getId()
		// + "; Name=" + indexLink.getName());
		// for (CommunicationDescriptor indexCommunication : indexLink
		// .getCommunications()) {
		// if (indexCommunication.getReceiveLink() == indexLink) {
		// System.out.println(" receiveCommunication: Name="
		// + indexCommunication.getName()
		// + "\n 1> startTimeOnLink="
		// + indexCommunication.getStartTimeOnLink()
		// + "\n 2> finishTimeOnLink="
		// + indexCommunication.getFinishTimeOnLink());
		// }
		// }
		// }
		// }

		System.out.println("\n***** Schedule Length="
				+ scheduler.getScheduleLength() + " *****\n");
	}

}
