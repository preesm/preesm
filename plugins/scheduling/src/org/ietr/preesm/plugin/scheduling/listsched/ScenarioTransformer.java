/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.plugin.scheduling.listsched;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.advancedmodel.IpCoprocessor;
import org.ietr.preesm.core.architecture.advancedmodel.Processor;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.ConstraintGroup;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.scheduling.listsched.descriptor.SwitchDescriptor;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * 
 * The ScenarioTransformer parses scenario
 * 
 * @author pmu
 */
public class ScenarioTransformer {

	/**
	 * Construct the ScenarioTransformer
	 */
	public ScenarioTransformer() {
	}

	/**
	 * Parse the scenario
	 * 
	 * @param scenario
	 *            A scenario to be parsed
	 * @param algorithm
	 *            An algorithm associated with the scenario
	 * @param architecture
	 *            An architecture associated with the scenario
	 */
	public void parseScenario(IScenario scenario,
			AlgorithmDescriptor algorithm, ArchitectureDescriptor architecture) {
		// Parse ConstraintGroup
		System.out.println("Constraints in the scenario:");
		for (ConstraintGroup indexConstraint : scenario
				.getConstraintGroupManager().getConstraintGroups()) {
			for (IOperator indexIOperator : indexConstraint.getOperators()) {
				if (((ArchitectureComponent) indexIOperator).getType() == ArchitectureComponentType.operator) {
					Operator indexOperator = (Operator) indexIOperator;
					System.out.println(" Operator: " + indexOperator.getName());
					for (SDFAbstractVertex indexVertex : indexConstraint
							.getVertices()) {
						algorithm.getComputation(indexVertex.getName())
								.addOperator(
										architecture.getOperator(indexOperator
												.getName()));
						System.out
								.println("\tVertex: " + indexVertex.getName());
					}
				} else if (((ArchitectureComponent) indexIOperator).getType() == ArchitectureComponentType.processor) {
					Processor indexOperator = (Processor) indexIOperator;
					System.out
							.println(" Processor: " + indexOperator.getName());
					for (SDFAbstractVertex indexVertex : indexConstraint
							.getVertices()) {
						algorithm.getComputation(indexVertex.getName())
								.addOperator(
										architecture.getOperator(indexOperator
												.getName()));
						System.out
								.println("\tVertex: " + indexVertex.getName());
					}
				} else if (((ArchitectureComponent) indexIOperator).getType() == ArchitectureComponentType.ipCoprocessor) {
					IpCoprocessor indexOperator = (IpCoprocessor) indexIOperator;
					System.out.println(" IpCoprocessor: "
							+ indexOperator.getName());
					for (SDFAbstractVertex indexVertex : indexConstraint
							.getVertices()) {
						algorithm.getComputation(indexVertex.getName())
								.addOperator(
										architecture.getOperator(indexOperator
												.getName()));
						System.out
								.println("\tVertex: " + indexVertex.getName());
					}
				}
			}
		}
		// Parse Timing
		for (Timing indexTiming : scenario.getTimingManager().getTimings()) {
			algorithm.getComputation(indexTiming.getVertex().getName())
					.addComputationDuration(
							indexTiming.getOperatorDefinition().getVlnv().getName(),
							indexTiming.getTime());
			System.out
					.println(" Name="
							+ algorithm.getComputation(
									indexTiming.getVertex().getName())
									.getName()
							+ "; Operator="
							+ indexTiming.getOperatorDefinition().getVlnv().getName()
							+ "; computationDuration="
							+ algorithm.getComputation(
									indexTiming.getVertex().getName())
									.getComputationDuration(
											indexTiming.getOperatorDefinition()
													.getVlnv().getName())
							+ "; nbTotalRepeate="
							+ algorithm.getComputation(
									indexTiming.getVertex().getName())
									.getNbTotalRepeat());
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
