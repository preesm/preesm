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
package org.ietr.preesm.plugin.mapper.listsched.scheduler;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.advancedmodel.RouteStep;
import org.ietr.preesm.core.architecture.advancedmodel.NodeLinkTuple;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperationType;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperatorDescriptor;

/**
 * This class gives a classic static list scheduling method.
 * 
 * @author pmu
 */
public class CSListSched extends AbstractScheduler {

	/**
	 * Constructs the scheduler with algorithm and architecture.
	 * 
	 * @param algorithm
	 *            Algorithm descriptor
	 * @param architecture
	 *            Architecture descriptor
	 */
	public CSListSched(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		super(algorithm);
		// TODO Auto-generated constructor stub
		this.architecture = architecture;
		this.name = "Classic Static List Scheduling";
	}

	@Override
	public boolean schedule() {
		// TODO Auto-generated method stub
		System.out.println("\n***** " + name + " *****");
		algorithm.computeTopLevel();
		algorithm.computeBottomLevel();
		staOrder = algorithm.sortComputationsByBottomLevel();
		OperatorDescriptor bestOperator = null;
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			indexOperator.addReceiveCommunication(topCommunication);
			indexOperator.addSendCommunication(topCommunication);
			indexOperator.addOperation(topCommunication);
			indexOperator.addReceiveCommunication(bottomCommunication);
			indexOperator.addSendCommunication(bottomCommunication);
			indexOperator.addOperation(bottomCommunication);
			// for (LinkDescriptor indexLink : indexOperator.getInputLinks()) {
			// indexLink.addCommunication(topCommunication);
			// indexLink.addCommunication(bottomCommunication);
			// }
			// for (LinkDescriptor indexLink : indexOperator.getOutputLinks()) {
			// indexLink.addCommunication(topCommunication);
			// indexLink.addCommunication(bottomCommunication);
			// }
		}
		for (LinkDescriptor indexLink : architecture.getAllLinks().values()) {
			indexLink.addCommunication(topCommunication);
			indexLink.addCommunication(bottomCommunication);
		}

		for (int i = 0; i < staOrder.size(); i++) {
			System.out.println(i + ": schedule " + staOrder.get(i).getName()
					+ " (bottom level=" + staOrder.get(i).getBottomLevel()
					+ ")");
			bestOperator = selectOperator(staOrder.get(i));

			scheduleComputation(staOrder.get(i), bestOperator);
			// schedulingOrder.get(i).setOperator(bestOperator);
			updateTimes();
			// System.out.println(" bestOperator" + "->" +
			// bestOperator.getId());
			// System.out.println(" startTime" + "="
			// + schedulingOrder.get(i).getStartTime() + "; finishTime"
			// + "=" + schedulingOrder.get(i).getFinishTime());
			// for (CommunicationDescriptor indexCommunication : schedulingOrder
			// .get(i).getPrecedingCommunications()) {
			// System.out.println(" preceding communication:"
			// + indexCommunication.getName() + " startTimeOnLink="
			// + indexCommunication.getStartTimeOnLink()
			// + "; finishTimeOnLink="
			// + indexCommunication.getFinishTimeOnLink() + "; ALAP="
			// + indexCommunication.getALAP());
			// }
		}
		for (int i = 0; i < staOrder.size(); i++) {
			scheduleLength = max(scheduleLength, staOrder.get(i)
					.getFinishTime());
		}
		for (OperatorDescriptor indexOperator : architecture.getAllOperators()
				.values()) {
			if (indexOperator.getOperations().size() > 2) {
				usedOperators.add(indexOperator);
				indexOperator.setFinishTime(indexOperator
						.getOccupiedTimeInterval(
								indexOperator
										.getOperation(
												indexOperator.getOperations()
														.size() - 2).getName())
						.getFinishTime());
			}
		}
		return true;
	}

	/**
	 * Selects the best operator to executes the given computation.
	 * 
	 * @param computation
	 *            An computation
	 * @return The best operator
	 */
	protected OperatorDescriptor selectOperator(
			ComputationDescriptor computation) {
		int bestOperatorFinishTime = Integer.MAX_VALUE;
		OperatorDescriptor bestOperator = null;
		System.out.println(" * select operator for " + computation.getName());
		if (computation.getOperator() == null) {
			// for (OperatorDescriptor indexOperator : architecture
			// .getAllOperators().values()) {
			for (String indexOperatorId : computation.getOperatorSet()) {
				int minOperatorFinishTime = scheduleComputation(computation,
						architecture.getOperator(indexOperatorId));
				if (bestOperatorFinishTime > minOperatorFinishTime) {
					bestOperatorFinishTime = minOperatorFinishTime;
					bestOperator = architecture.getOperator(indexOperatorId);
				}
				// System.out.println(" minOperatorFinishTime="
				// + minOperatorFinishTime);

				for (OperatorDescriptor indexOperator2 : architecture
						.getAllOperators().values()) {
					for (CommunicationDescriptor indexCommunication : computation
							.getInputCommunications()) {
						indexOperator2
								.removeSendCommunication(indexCommunication);
						indexOperator2
								.removeReceiveCommunication(indexCommunication);
						indexOperator2.removeOperation(indexCommunication);
						if (indexCommunication.getSendLink() != null) {
							indexCommunication.getSendLink()
									.removeCommunication(indexCommunication);
							indexCommunication.setSendLink(null);
						}
						if (indexCommunication.getReceiveLink() != null) {
							indexCommunication.getReceiveLink()
									.removeCommunication(indexCommunication);
							indexCommunication.setReceiveLink(null);
						}
						if (indexCommunication.getLinkList() != null) {
							for (int i = 0; i < indexCommunication
									.getLinkList().size(); i++) {
								LinkDescriptor link = indexCommunication
										.getLinkList().get(i);
								link.removeCommunication(indexCommunication);
							}
							indexCommunication.setLinkList(null);
						}
						indexCommunication.setExist();
						indexCommunication.clearScheduled();
					}
				}

				architecture.getOperator(indexOperatorId).removeComputation(
						computation);
				architecture.getOperator(indexOperatorId).removeOperation(
						computation);
				restoreTimes();
				computation.clearScheduled();
				computation.setOperator(null);
			}
			// System.out.println(" bestOperatorFinishTime="
			// + bestOperatorFinishTime);
		} else {
			bestOperator = computation.getOperator();
		}
		return bestOperator;
	}

	/**
	 * Schedules a computation on an operator.
	 * 
	 * @param computation
	 *            A computation
	 * @param operator
	 *            An operator
	 * @return The finish time of this computation on this operator
	 */
	protected int scheduleComputation(ComputationDescriptor computation,
			OperatorDescriptor operator) {
		int dataReadyTime = 0;
		int maxOperatorFinishTime = 0;
		// System.out.println(" ** schedule computation: " +
		// computation.getName()
		// + " on: " + operator.getId());
		if (computation.getComputationDurations().containsKey(
				operator.getName())
				&& computation.getOperatorSet().contains(operator.getId())) {
			// schedule preceding communications
			for (CommunicationDescriptor indexCommunication : computation
					.getInputCommunications()) {
				if (!indexCommunication.getOrigin().equalsIgnoreCase(
						topComputation.getName())) {
					scheduleCommunication(indexCommunication, operator);
				}
			}
			// calculate data ready time
			for (CommunicationDescriptor indexCommunication : computation
					.getInputCommunications()) {
				if (indexCommunication.isScheduled()) {
					if (dataReadyTime < indexCommunication
							.getFinishTimeOnReceiveOperator()) {
						dataReadyTime = indexCommunication
								.getFinishTimeOnReceiveOperator();
					}
				}
			}
			computation.setDataReadyTime(dataReadyTime);
			// find time interval in operator
			int maxTime = 0;
			for (int i = 0; i < operator.getOperations().size() - 1; i++) {
				maxTime = max(operator.getOccupiedTimeInterval(
						operator.getOperation(i).getName()).getFinishTime(),
						dataReadyTime);
				if (operator.getOccupiedTimeInterval(
						operator.getOperation(i + 1).getName()).getStartTime()
						- maxTime >= computation
						.getTotalComputationDuration(operator)) {
					computation.setStartTime(maxTime);
					computation
							.setFinishTime(maxTime
									+ computation
											.getTotalComputationDuration(operator));
					operator.addComputation(computation);
					operator.addOperation(i + 1, computation);
					operator.addOccupiedTimeInterval(computation.getName(),
							computation.getStartTime(), computation
									.getFinishTime());
					break;
				}
			}

			// Examine the latest finish time of all operators
			// for (OperatorDescriptor indexOperator : architecture
			// .getAllOperators().values()) {
			// if (maxOperatorFinishTime < indexOperator
			// .getOccupiedTimeInterval(
			// indexOperator
			// .getOperation(
			// indexOperator.getOperations()
			// .size() - 2).getName())
			// .getFinishTime()) {
			// maxOperatorFinishTime = indexOperator
			// .getOccupiedTimeInterval(
			// indexOperator.getOperation(
			// indexOperator.getOperations()
			// .size() - 2).getName())
			// .getFinishTime();
			// }
			// }
			//
			// give the finish time of the computation on this operator
			maxOperatorFinishTime = computation.getFinishTime();
			computation.setScheduled();
			computation.setOperator(operator);
		} else {
			maxOperatorFinishTime = Integer.MAX_VALUE;
		}
		return maxOperatorFinishTime;
	}

	/**
	 * Schedules a communication with its destination operator.
	 * 
	 * @param communication
	 *            An communication
	 * @param destinationOperator
	 *            The destination operator
	 */
	protected void scheduleCommunication(CommunicationDescriptor communication,
			OperatorDescriptor destinationOperator) {
		ComputationDescriptor sourceComputation = algorithm
				.getComputation(communication.getOrigin());
		// System.out.println(" *** schedule communication: "
		// + communication.getName());
		if (sourceComputation.isScheduled()) {
			OperatorDescriptor sourceOperator = sourceComputation.getOperator();
			if (sourceOperator == destinationOperator) {
				// System.out
				// .println(" sourceOperator == destinationOperator");
				communication.clearExist();
				communication.setStartTimeOnSendOperator(sourceComputation
						.getFinishTime());
				communication.setFinishTimeOnSendOperator();
				communication.setStartTimeOnLink(sourceComputation
						.getFinishTime());
				communication.setFinishTimeOnLink(sourceComputation
						.getFinishTime());
				communication.setStartTimeOnReceiveOperator(sourceComputation
						.getFinishTime());
				communication.setFinishTimeOnReceiveOperator();
			} else {
				// System.out
				// .println(" sourceOperator != destinationOperator");

				List<LinkDescriptor> linkList = findLinkList(sourceOperator,
						destinationOperator);

				int indexCommunicationOnLink[] = new int[linkList.size()];
				int infCommunicationTimeOnLink[] = new int[linkList.size()];
				int supCommunicationTimeOnLink[] = new int[linkList.size()];
				int infCommunicationTime = 0;
				int supCommunicationTime = Integer.MAX_VALUE;
				for (int i = 0; i < linkList.size(); i++) {
					indexCommunicationOnLink[i] = 0;
					infCommunicationTimeOnLink[i] = 0;
					supCommunicationTimeOnLink[i] = Integer.MAX_VALUE;
				}
				while (true) {
					infCommunicationTime = 0;
					supCommunicationTime = Integer.MAX_VALUE;
					for (int i = 0; i < linkList.size(); i++) {
						List<CommunicationDescriptor> communicationList = linkList
								.get(i).getCommunications();
						if (sourceOperator.getSendCommunications().contains(
								communicationList
										.get(indexCommunicationOnLink[i]))) {
							// a send communication on sourceOperator
							infCommunicationTimeOnLink[i] = max(algorithm
									.getComputation(communication.getOrigin())
									.getFinishTime(), communicationList.get(
									indexCommunicationOnLink[i])
									.getFinishTimeOnSendOperator(),
									communicationList.get(
											indexCommunicationOnLink[i])
											.getFinishTimeOnLink());
						} else if (sourceOperator
								.getReceiveCommunications()
								.contains(
										communicationList
												.get(indexCommunicationOnLink[i]))) {
							// a receive communication on sourceOperator
							infCommunicationTimeOnLink[i] = max(algorithm
									.getComputation(communication.getOrigin())
									.getFinishTime(), communicationList.get(
									indexCommunicationOnLink[i])
									.getFinishTimeOnReceiveOperator(),
									communicationList.get(
											indexCommunicationOnLink[i])
											.getFinishTimeOnLink());
						} else {
							// neither a send nor a receive on
							// sourceOperator
							infCommunicationTimeOnLink[i] = max(algorithm
									.getComputation(communication.getOrigin())
									.getFinishTime()
									+ communication.getSendOverhead(),
									communicationList.get(
											indexCommunicationOnLink[i])
											.getFinishTimeOnLink());
						}
						supCommunicationTimeOnLink[i] = communicationList.get(
								indexCommunicationOnLink[i] + 1)
								.getStartTimeOnLink();

						infCommunicationTime = max(infCommunicationTime,
								infCommunicationTimeOnLink[i]);
						supCommunicationTime = min(supCommunicationTime,
								supCommunicationTimeOnLink[i]);
					}
					if (infCommunicationTime
							+ communication.getCommunicationDuration() <= supCommunicationTime) {
						break;
					} else {
						boolean allReady = true;
						for (int i = 0; i < linkList.size(); i++) {

							if (infCommunicationTimeOnLink[i]
									+ communication.getCommunicationDuration() > supCommunicationTimeOnLink[i]) {
								indexCommunicationOnLink[i]++;
								allReady = false;
								break;
							}
						}
						if (allReady) {
							for (int i = 0; i < linkList.size(); i++) {
								if (supCommunicationTimeOnLink[i] == supCommunicationTime) {
									indexCommunicationOnLink[i]++;
								}
							}
						}
					}
				}

				// set new start time on source operator
				int indexOperationOnSourceOperator = sourceOperator
						.getOperations().indexOf(
								algorithm.getComputation(communication
										.getOrigin()));
				for (int i = 0; i < linkList.size(); i++) {
					CommunicationDescriptor previousCommunication = linkList
							.get(i).getCommunication(
									indexCommunicationOnLink[i]);
					indexOperationOnSourceOperator = max(
							indexOperationOnSourceOperator, sourceOperator
									.getOperations().indexOf(
											previousCommunication));
				}
				if (sourceOperator.getOperation(indexOperationOnSourceOperator)
						.getType() == OperationType.Computation) {
					communication
							.setStartTimeOnSendOperator(((ComputationDescriptor) sourceOperator
									.getOperation(indexOperationOnSourceOperator))
									.getFinishTime());
				} else {
					if (sourceOperator
							.getSendCommunications()
							.contains(
									(CommunicationDescriptor) sourceOperator
											.getOperation(indexOperationOnSourceOperator))) {
						communication
								.setStartTimeOnSendOperator(((CommunicationDescriptor) sourceOperator
										.getOperation(indexOperationOnSourceOperator))
										.getFinishTimeOnSendOperator());
					} else {
						communication
								.setStartTimeOnSendOperator(((CommunicationDescriptor) sourceOperator
										.getOperation(indexOperationOnSourceOperator))
										.getFinishTimeOnReceiveOperator());
					}
				}

				// set new start times on links
				communication.setStartTimeOnLink(infCommunicationTime);
				for (int i = 0; i < linkList.size(); i++) {
					CommunicationDescriptor nextCommunication = linkList.get(i)
							.getCommunication(indexCommunicationOnLink[i] + 1);
					nextCommunication.setStartTimeOnLink(max(communication
							.getFinishTimeOnLink(), nextCommunication
							.getStartTimeOnLink()));
				}

				// find slot in destination operator
				int indexOperationOnDestinationOperator = 0;
				for (int i = 0; i < linkList.size(); i++) {
					CommunicationDescriptor previousCommunication = linkList
							.get(i).getCommunication(
									indexCommunicationOnLink[i]);
					indexOperationOnDestinationOperator = max(
							indexOperationOnDestinationOperator,
							destinationOperator.getOperations().indexOf(
									previousCommunication));
				}

				int maxTime = 0;
				for (int i = indexOperationOnDestinationOperator; i < destinationOperator
						.getOperations().size() - 1; i++) {
					maxTime = max(destinationOperator.getOccupiedTimeInterval(
							destinationOperator.getOperation(i).getName())
							.getFinishTime(), communication
							.getFinishTimeOnLink());
					if (destinationOperator.getOccupiedTimeInterval(
							destinationOperator.getOperation(i + 1).getName())
							.getStartTime() >= maxTime) {
						communication.setStartTimeOnReceiveOperator(maxTime
								- communication.getReceiveInvolvement());
						indexOperationOnDestinationOperator = i;
						break;
					}
				}

				// insert communication
				// if (!sourceOperator.getSendCommunications().contains(
				// communication)) {
				sourceOperator.addSendCommunication(communication);
				sourceOperator.addOperation(indexOperationOnSourceOperator + 1,
						communication);
				sourceOperator.addOccupiedTimeInterval(communication.getName(),
						communication.getStartTimeOnSendOperator(),
						communication.getFinishTimeOnSendOperator());
				// }
				destinationOperator.addReceiveCommunication(communication);
				destinationOperator.addOperation(
						indexOperationOnDestinationOperator + 1, communication);
				destinationOperator.addOccupiedTimeInterval(communication
						.getName(), communication
						.getStartTimeOnReceiveOperator(), communication
						.getFinishTimeOnReceiveOperator());
				for (int i = 0; i < linkList.size(); i++) {
					linkList.get(i).addCommunication(
							indexCommunicationOnLink[i] + 1, communication);
				}
				communication.setSendLink(linkList.get(0));
				communication.setReceiveLink(linkList.get(linkList.size() - 1));
				communication.setLinkList(linkList);
			}
			communication.setScheduled();
		} else {
			System.out.println(" communication name: "
					+ communication.getName());
			System.out.println(" source computation has not been scheduled");
		}
	}

	protected List<LinkDescriptor> findLinkList(
			OperatorDescriptor sourceOperator,
			OperatorDescriptor destinationOperator) {
		MultiCoreArchitecture archi = this.architecture.getArchi();
		List<LinkDescriptor> linkList = new ArrayList<LinkDescriptor>();
		if (archi != null) {
			// schedule a communication on a route step
			RouteStep rs = archi.getRouteStepTable().getRouteStepList(
					sourceOperator.getId(), destinationOperator.getId())
					.getRouteStep();

			linkList.add(architecture.getLink(rs.getFirstLinkName()));
			for (NodeLinkTuple indexNLT : rs.getNodeLinkTuples()) {
				linkList.add(architecture.getLink(indexNLT.getLinkName()));
			}
		} else {
			linkList.add(sourceOperator.getOutputLink(0));
			linkList.add(destinationOperator.getInputLink(0));
		}

		return linkList;
	}

}
