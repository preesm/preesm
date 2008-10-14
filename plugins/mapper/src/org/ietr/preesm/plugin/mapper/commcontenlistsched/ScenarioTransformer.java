package org.ietr.preesm.plugin.mapper.commcontenlistsched;

import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.SwitchDescriptor;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * @author pmu
 * 
 *         The ScenarioTransformer parses scenario
 */
public class ScenarioTransformer {

	public ScenarioTransformer() {
	}

	public void parseScenario(IScenario scenario,
			AlgorithmDescriptor algorithm, ArchitectureDescriptor architecture) {
		// Parse ConstraintGroup
		System.out.println("Constraints in the scenario:");
		for (ConstraintGroup indexConstraint : scenario
				.getConstraintGroupManager().getConstraintGroups()) {
			for (Operator indexOperator : indexConstraint.getOperators()) {
				System.out.println(" Operator: " + indexOperator.getName());
				for (SDFAbstractVertex indexVertex : indexConstraint
						.getVertices()) {
					algorithm.getComputation(indexVertex.getName())
							.addOperator(
									architecture.getOperator(indexOperator
											.getName()));
					System.out.println("\tVertex: " + indexVertex.getName());
				}
			}
		}
		// Parse Timing
		for (Timing indexTiming : scenario.getTimingManager().getTimings()) {
			algorithm.getComputation(indexTiming.getVertex().getName())
					.addComputationDuration(
							indexTiming.getOperatorDefinition().getId(),
							indexTiming.getTime());
			// System.out
			// .println(" Name="
			// + algorithm.getComputation(
			// indexTiming.getVertex().getName())
			// .getName()
			// + "; Operator="
			// + indexTiming.getOperatorDefinition().getId()
			// + "; computationDuration="
			// + algorithm.getComputation(
			// indexTiming.getVertex().getName())
			// .getComputationDuration(
			// indexTiming.getOperatorDefinition()
			// .getId())
			// + "; nbTotalRepeate="
			// + algorithm.getComputation(
			// indexTiming.getVertex().getName())
			// .getNbTotalRepeat());
		}
		System.out.println("Computations in the algorithm:");
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation != algorithm.getTopComputation()
					&& indexComputation != algorithm.getBottomComputation()) {
				int time = 0;
				int operator = 0;
				System.out.println(" Name=" + indexComputation.getName());
				for (String indexOperator : indexComputation
						.getComputationDurations().keySet()) {
					operator += 1;
					time += indexComputation
							.getComputationDuration(indexOperator);
					System.out.println("\tOperator="
							+ indexOperator
							+ "; computationDuration="
							+ indexComputation
									.getComputationDuration(indexOperator)
							+ "; nbTotalRepeate="
							+ indexComputation.getNbTotalRepeat());
				}
				if (operator == 0) {
					// default time of 100
					time = 100;
					System.out
							.println("\tNo operator specified, set to a default time of "
									+ time);
				} else {
					time /= operator;
					System.out.println("\tAverage time=" + time);
				}
				indexComputation.setTime(time);
			}
		}
		System.out.println("Communications in the algorithm:");
		for (CommunicationDescriptor indexCommunication : algorithm
				.getCommunications().values()) {
			for (SwitchDescriptor indexSwitch : architecture.getAllSwitches()
					.values()) {
				if (!indexCommunication.getCommunicationDurations()
						.containsKey(indexSwitch.getName())) {
					indexCommunication.addCommunicationDuration(indexSwitch
							.getName(), indexCommunication.getWeight());
					System.out.println(" Name="
							+ indexCommunication.getName()
							+ "; Switch="
							+ indexSwitch.getName()
							+ "; default communicationDuration="
							+ indexCommunication
									.getCommunicationDuration(indexSwitch
											.getName()));
				}
			}
			for (LinkDescriptor indexLink : architecture.getAllLinks().values()) {
				if (!indexCommunication.getCommunicationDurations()
						.containsKey(indexLink.getName())) {
					indexCommunication.addCommunicationDuration(indexLink
							.getName(), indexCommunication.getWeight());
					System.out.println(" Name="
							+ indexCommunication.getName()
							+ "; Link="
							+ indexLink.getName()
							+ "; default communicationDuration="
							+ indexCommunication
									.getCommunicationDuration(indexLink
											.getName()));
				}
			}
		}
	}
}
