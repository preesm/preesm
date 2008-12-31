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

import java.util.Vector;

import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperationType;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperatorDescriptor;

/**
 * This class gives a classic static list scheduling method with Critical Child
 * and Communication Delay.
 * 
 * @author pmu
 */
public class CSListSchedCcCd extends AbstractScheduler {

	/**
	 * Constructs the scheduler with algorithm and architecture.
	 * 
	 * @param algorithm
	 *            Algorithm descriptor
	 * @param architecture
	 *            Architecture descriptor
	 */
	public CSListSchedCcCd(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		super(algorithm);
		this.architecture = architecture;
		this.name = "Classic Static List Scheduling With Critical Child And Communication Delay";
	}

	@Override
	public boolean schedule() {
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
			for (LinkDescriptor indexLink : indexOperator.getInputLinks()) {
				indexLink.addCommunication(topCommunication);
				indexLink.addCommunication(bottomCommunication);
			}
			for (LinkDescriptor indexLink : indexOperator.getOutputLinks()) {
				indexLink.addCommunication(topCommunication);
				indexLink.addCommunication(bottomCommunication);
			}
		}

		for (int i = 0; i < staOrder.size(); i++) {
			System.out.println(i + ": schedule " + staOrder.get(i).getName()
					+ " (bottom level=" + staOrder.get(i).getBottomLevel()
					+ ")");
			bestOperator = selectOperator(staOrder.get(i));

			scheduleComputation(staOrder.get(i), bestOperator, false);
			// schedulingOrder.get(i).setOperator(bestOperator);
			updateTimes();
			System.out.println(" bestOperator" + "->" + bestOperator.getId());
			System.out.println(" startTime" + "="
					+ staOrder.get(i).getStartTime() + "; finishTime" + "="
					+ staOrder.get(i).getFinishTime());
			for (CommunicationDescriptor indexCommunication : staOrder.get(i)
					.getInputCommunications()) {
				System.out.println(" preceding communication:"
						+ indexCommunication.getName() + " startTimeOnLink="
						+ indexCommunication.getStartTimeOnLink()
						+ "; finishTimeOnLink="
						+ indexCommunication.getFinishTimeOnLink() + "; ALAP="
						+ indexCommunication.getALAP());
			}
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
		ComputationDescriptor criticalChild = null;
		int bestFinishTime = Integer.MAX_VALUE;
		int bestChildFinishTime = Integer.MAX_VALUE;
		OperatorDescriptor bestOperator = null;
		System.out.println(" * select operator for " + computation.getName());
		if (computation.getOperator() == null) {
			boolean findChild = false;
			for (int i = staOrder.indexOf(computation); i < staOrder.size(); i++) {
				for (CommunicationDescriptor indexCommunication : computation
						.getOutputCommunications()) {
					if (algorithm.getComputation(indexCommunication
							.getDestination()) == staOrder.get(i)) {
						findChild = true;
						criticalChild = staOrder.get(i);
						break;
					}
				}
				if (findChild) {
					break;
				}
			}
			// for (OperatorDescriptor indexOperator : architecture
			// .getAllOperators().values()) {
			for (String indexOperatorId : computation.getOperatorSet()) {
				int finishTime = scheduleComputation(computation, architecture
						.getOperator(indexOperatorId), true);
				// TODO: need to back up times after schedule of computation
				backupTimes();
				int minChildFinishTime = Integer.MAX_VALUE;
				if (findChild) {
					// System.out.println(" critical child is "
					// + criticalChild.getName());
					Vector<OperatorDescriptor> childOperatorList = new Vector<OperatorDescriptor>();
					// schedule critical child
					// for (CommunicationDescriptor incomingCommunication :
					// criticalChild
					// .getPrecedingCommunications()) {
					for (String childOperatorId : criticalChild
							.getOperatorSet()) {
						{
							// if (algorithm.getComputation(
							// incomingCommunication.getSource())
							// .isScheduled()) {
							// OperatorDescriptor childOperator = algorithm
							// .getComputation(
							// incomingCommunication.getSource())
							// .getOperator();
							if (!childOperatorList.contains(architecture
									.getOperator(childOperatorId))) {
								childOperatorList.add(architecture
										.getOperator(childOperatorId));
								int childFinishTime = scheduleComputation(
										criticalChild, architecture
												.getOperator(childOperatorId),
										true);
								// System.out.println(" finishTime=" +
								// finishTime);
								if (childFinishTime < minChildFinishTime) {
									minChildFinishTime = childFinishTime;
								}
								for (OperatorDescriptor indexOperator2 : architecture
										.getAllOperators().values()) {
									for (CommunicationDescriptor indexCommunication : criticalChild
											.getInputCommunications()) {
										indexOperator2
												.removeSendCommunication(indexCommunication);
										indexOperator2
												.removeReceiveCommunication(indexCommunication);
										indexOperator2
												.removeOperation(indexCommunication);
										if (indexCommunication.getSendLink() != null) {
											indexCommunication.getSendLink()
													.removeCommunication(
															indexCommunication);
											indexCommunication
													.setSendLink(null);
										}
										if (indexCommunication.getReceiveLink() != null) {
											indexCommunication.getReceiveLink()
													.removeCommunication(
															indexCommunication);
											indexCommunication
													.setReceiveLink(null);
										}
										indexCommunication.setExist();
										indexCommunication.clearScheduled();
									}
								}
								architecture.getOperator(childOperatorId)
										.removeComputation(criticalChild);
								architecture.getOperator(childOperatorId)
										.removeOperation(criticalChild);
								// TODO: need to recover times after schedule of
								// criticalChild
								recoverTimes();
								criticalChild.clearScheduled();
								criticalChild.setOperator(null);
							}
						}
					}
					if (bestChildFinishTime > minChildFinishTime) {
						bestChildFinishTime = minChildFinishTime;
						bestFinishTime = finishTime;
						bestOperator = architecture
								.getOperator(indexOperatorId);
					} else if (bestChildFinishTime == minChildFinishTime) {
						if (bestFinishTime > finishTime) {
							bestFinishTime = finishTime;
							bestOperator = architecture
									.getOperator(indexOperatorId);
						}
					}
				} else {
					// System.out.println(" no child");
					if (bestFinishTime > finishTime) {
						bestFinishTime = finishTime;
						bestOperator = architecture
								.getOperator(indexOperatorId);
					}
				}
				// if (bestChildFinishTime > minChildFinishTime) {
				// bestChildFinishTime = minChildFinishTime;
				// bestOperator = indexOperator;
				// }
				// System.out.println(" minChildFinishTime="
				// + minChildFinishTime);
				// remove inserted communications(source!=destination) and
				// computations;
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
			OperatorDescriptor operator, boolean isTemporary) {
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
			// set ALAP for preceding communications if it is not a temporary
			// try
			if (!isTemporary) {
				Vector<CommunicationDescriptor> communicationList = new Vector<CommunicationDescriptor>();
				for (CommunicationDescriptor indexCommunication : computation
						.getInputCommunications()) {
					if (indexCommunication.isExist()) {
						communicationList.add(indexCommunication);
					} else {
						// set ALAP to source computation finish time
						indexCommunication
								.setALAP(algorithm.getComputation(
										indexCommunication.getOrigin())
										.getFinishTime());
					}
				}
				// Collections.sort(communicationList);
				for (int i = 0; i < communicationList.size(); i++) {
					if (communicationList.get(i).isScheduled()) {
						int indexOnSendLink = communicationList.get(i)
								.getSendLink().getCommunications().indexOf(
										communicationList.get(i));
						int indexOnReceiveLink = communicationList.get(i)
								.getReceiveLink().getCommunications().indexOf(
										communicationList.get(i));
						communicationList
								.get(i)
								.setALAP(
										min(
												communicationList
														.get(i)
														.getStartTimeOnReceiveOperator()
														+ communicationList
																.get(i)
																.getReceiveInvolvement()
														- communicationList
																.get(i)
																.getCommunicationDuration(),
												communicationList
														.get(i)
														.getReceiveLink()
														.getCommunications()
														.get(
																indexOnReceiveLink + 1)
														.getStartTimeOnLink()
														- communicationList
																.get(i)
																.getCommunicationDuration(),
												communicationList
														.get(i)
														.getSendLink()
														.getCommunications()
														.get(
																indexOnSendLink + 1)
														.getStartTimeOnLink()
														- communicationList
																.get(i)
																.getCommunicationDuration()));
					} else {
						System.out.println("communication name: "
								+ communicationList.get(i).getName());
						System.out
								.println("Error: comunication is not scheduled for ALAP!");
					}
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
				// communication.setALAP(sourceComputation.getFinishTime());
			} else {
				// System.out
				// .println(" sourceOperator != destinationOperator");
				// RouteDescriptor route = findRoute(sourceOperator,
				// destinationOperator);
				LinkDescriptor sendLink = sourceOperator.getOutputLink(0);
				LinkDescriptor receiveLink = destinationOperator
						.getInputLink(0);
				Vector<CommunicationDescriptor> sendCommunicationList = sendLink
						.getCommunications();
				Vector<CommunicationDescriptor> receiveCommunicationList = receiveLink
						.getCommunications();

				// find slot in links
				int indexCommunicationOnSendLink = 0;
				int indexCommunicationOnReceiveLink = 0;
				int infSendCommunicationTime = 0;
				int supSendCommunicationTime = 0;
				int infReceiveCommunicationTime = 0;
				int supReceiveCommunicationTime = 0;
				int infCommunicationTime = 0;
				int supCommunicationTime = 0;
				while (indexCommunicationOnSendLink < sendCommunicationList
						.size()
						&& indexCommunicationOnReceiveLink < receiveCommunicationList
								.size()) {
					if (sourceOperator.getSendCommunications().contains(
							sendCommunicationList
									.get(indexCommunicationOnSendLink))) {
						// a send of sourceOperator
						infSendCommunicationTime = max(algorithm
								.getComputation(communication.getOrigin())
								.getFinishTime()
								+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnSendOperator()
										+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnLink());
					} else if (sourceOperator.getReceiveCommunications()
							.contains(
									sendCommunicationList
											.get(indexCommunicationOnSendLink))) {
						// a receive of sourceOperator
						infSendCommunicationTime = max(algorithm
								.getComputation(communication.getOrigin())
								.getFinishTime()
								+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnReceiveOperator()
										+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnLink());
					} else {
						// neither a send nor a receive of sourceOperator
						infSendCommunicationTime = max(algorithm
								.getComputation(communication.getOrigin())
								.getFinishTime()
								+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnLink());
					}
					supSendCommunicationTime = max(sendCommunicationList.get(
							indexCommunicationOnSendLink + 1).getALAP(),
							sendCommunicationList.get(
									indexCommunicationOnSendLink + 1)
									.getStartTimeOnLink());
					infReceiveCommunicationTime = max(algorithm.getComputation(
							communication.getOrigin()).getFinishTime()
							+ communication.getSendOverhead(),
							receiveCommunicationList.get(
									indexCommunicationOnReceiveLink)
									.getFinishTimeOnLink());
					supReceiveCommunicationTime = max(
							receiveCommunicationList.get(
									indexCommunicationOnReceiveLink + 1)
									.getALAP(), receiveCommunicationList.get(
									indexCommunicationOnReceiveLink + 1)
									.getStartTimeOnLink());

					infCommunicationTime = max(infSendCommunicationTime,
							infReceiveCommunicationTime);
					supCommunicationTime = min(supSendCommunicationTime,
							supReceiveCommunicationTime);
					if (infCommunicationTime
							+ communication.getCommunicationDuration() <= supCommunicationTime) {
						break;
					} else {
						if (infSendCommunicationTime
								+ communication.getCommunicationDuration() > supSendCommunicationTime) {
							indexCommunicationOnSendLink++;
						} else {
							if (infReceiveCommunicationTime
									+ communication.getCommunicationDuration() > supReceiveCommunicationTime) {
								indexCommunicationOnReceiveLink++;
							} else {
								if (infSendCommunicationTime < infReceiveCommunicationTime) {
									indexCommunicationOnSendLink++;
								} else {
									indexCommunicationOnReceiveLink++;
								}
							}
						}
					}
				}

				// set new start times on source operator and links
				int indexOperationOnSourceOperator = max(sourceOperator
						.getOperations().indexOf(
								sendCommunicationList
										.get(indexCommunicationOnSendLink)),
						sourceOperator.getOperations().indexOf(
								algorithm.getComputation(communication
										.getOrigin())));
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
				communication.setStartTimeOnLink(infCommunicationTime);
				sendCommunicationList
						.get(indexCommunicationOnSendLink + 1)
						.setStartTimeOnLink(
								max(
										communication.getFinishTimeOnLink(),
										sendCommunicationList
												.get(
														indexCommunicationOnSendLink + 1)
												.getStartTimeOnLink()));
				sendLink.updateCommunication(sendCommunicationList
						.get(indexCommunicationOnSendLink + 1));
				receiveCommunicationList
						.get(indexCommunicationOnReceiveLink + 1)
						.setStartTimeOnLink(
								max(
										communication.getFinishTimeOnLink(),
										receiveCommunicationList
												.get(
														indexCommunicationOnReceiveLink + 1)
												.getStartTimeOnLink()));
				receiveLink.updateCommunication(receiveCommunicationList
						.get(indexCommunicationOnReceiveLink + 1));

				// change ALAP for communications
				sendCommunicationList
						.get(indexCommunicationOnSendLink)
						.setALAP(
								min(
										sendCommunicationList.get(
												indexCommunicationOnSendLink)
												.getALAP(),
										communication.getStartTimeOnLink()
												- sendCommunicationList
														.get(
																indexCommunicationOnSendLink)
														.getCommunicationDuration()));
				communication.setALAP(communication.getStartTimeOnLink());
				receiveCommunicationList
						.get(indexCommunicationOnReceiveLink)
						.setALAP(
								min(
										receiveCommunicationList
												.get(
														indexCommunicationOnReceiveLink)
												.getALAP(),
										communication.getStartTimeOnLink()
												- receiveCommunicationList
														.get(
																indexCommunicationOnReceiveLink)
														.getCommunicationDuration()));

				// // Check delay
				// int delayStartTime = 0;
				// int delayDuration = 0;
				// boolean needDelay = true;
				// if (sourceOperator.getOperation(
				// indexOperationOnSourceOperator + 1).getType() ==
				// OperationType.Computation) {
				// if (((ComputationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))
				// .getStartTime() >= communication
				// .getFinishTimeOnSendOperator()) {
				// needDelay = false;
				// } else {
				// delayStartTime = ((ComputationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))
				// .getStartTime();
				// delayDuration = communication
				// .getFinishTimeOnSendOperator()
				// - delayStartTime;
				// }
				// } else {
				// if (sourceOperator
				// .getSendCommunications()
				// .contains(
				// (CommunicationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))) {
				// if (((CommunicationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))
				// .getStartTimeOnSendOperator() >= communication
				// .getFinishTimeOnSendOperator()) {
				// needDelay = false;
				// } else {
				// delayStartTime = ((CommunicationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))
				// .getStartTimeOnSendOperator();
				// delayDuration = communication
				// .getFinishTimeOnSendOperator()
				// - delayStartTime;
				// }
				// } else {
				// if (((CommunicationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))
				// .getStartTimeOnReceiveOperator() >= communication
				// .getFinishTimeOnSendOperator()) {
				// needDelay = false;
				// } else {
				// delayStartTime = ((CommunicationDescriptor) sourceOperator
				// .getOperation(indexOperationOnSourceOperator + 1))
				// .getStartTimeOnReceiveOperator();
				// delayDuration = communication
				// .getFinishTimeOnSendOperator()
				// - delayStartTime;
				// }
				// }
				// }
				// if (needDelay) {
				// // delay operations
				// // System.out.println(" delay operations for: "
				// // + delayDuration + " after: " + delayStartTime);
				// for (OperatorDescriptor indexOperator : architecture
				// .getAllOperators().values()) {
				// for (int i = 1; i < indexOperator.getOperations()
				// .size() - 1; i++) {
				// if (indexOperator.getOperation(i).getType() ==
				// OperationType.Computation) {
				// if (((ComputationDescriptor) indexOperator
				// .getOperation(i)).getStartTime() >= delayStartTime) {
				// ((ComputationDescriptor) indexOperator
				// .getOperation(i))
				// .setStartTime(((ComputationDescriptor) indexOperator
				// .getOperation(i))
				// .getStartTime()
				// + delayDuration);
				// }
				// } else {
				// // check only the receive communication to avoid
				// // duplicate checking
				// if (indexOperator
				// .getReceiveCommunications()
				// .contains(
				// (CommunicationDescriptor) indexOperator
				// .getOperation(i))) {
				// if (((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .getStartTimeOnSendOperator() >= delayStartTime) {
				// ((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .setStartTimeOnSendOperator(((CommunicationDescriptor)
				// indexOperator
				// .getOperation(i))
				// .getStartTimeOnSendOperator()
				// + delayDuration);
				// }
				// if (((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .getStartTimeOnLink() >= delayStartTime) {
				// ((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .setStartTimeOnLink(((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .getStartTimeOnLink()
				// + delayDuration);
				// }
				// if (((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .getStartTimeOnReceiveOperator() >= delayStartTime) {
				// ((CommunicationDescriptor) indexOperator
				// .getOperation(i))
				// .setStartTimeOnReceiveOperator(((CommunicationDescriptor)
				// indexOperator
				// .getOperation(i))
				// .getStartTimeOnReceiveOperator()
				// + delayDuration);
				// }
				// }
				// }
				// }
				// }
				// }

				// find slot in destination operator
				int indexOperationOnDestinationOperator = 0;
				if (destinationOperator.getOperations()
						.contains(
								sendCommunicationList
										.get(indexCommunicationOnSendLink))) {
					indexOperationOnDestinationOperator = destinationOperator
							.getOperations().indexOf(
									sendCommunicationList
											.get(indexCommunicationOnSendLink));
				}
				int maxTime = 0;
				for (int i = indexOperationOnDestinationOperator; i < destinationOperator
						.getOperations().size() - 1; i++) {
					maxTime = max(destinationOperator.getOccupiedTimeInterval(
							destinationOperator.getOperation(i).getName())
							.getFinishTime()
							+ communication.getReceiveInvolvement(),
							communication.getFinishTimeOnLink());
					if (destinationOperator.getOccupiedTimeInterval(
							destinationOperator.getOperation(i + 1).getName())
							.getStartTime()
							- maxTime >= communication.getReceiveOverhead()) {
						communication.setStartTimeOnReceiveOperator(maxTime
								- communication.getReceiveInvolvement());
						indexOperationOnDestinationOperator = i;
						break;
					}
				}

				sourceOperator.addSendCommunication(communication);
				sourceOperator.addOperation(indexOperationOnSourceOperator + 1,
						communication);
				sourceOperator.addOccupiedTimeInterval(communication.getName(),
						communication.getStartTimeOnSendOperator(),
						communication.getFinishTimeOnSendOperator());

				destinationOperator.addReceiveCommunication(communication);
				destinationOperator.addOperation(
						indexOperationOnDestinationOperator + 1, communication);
				destinationOperator.addOccupiedTimeInterval(communication
						.getName(), communication
						.getStartTimeOnReceiveOperator(), communication
						.getFinishTimeOnReceiveOperator());

				sendLink.addCommunication(indexCommunicationOnSendLink + 1,
						communication);
				if (sendLink != receiveLink) {
					receiveLink.addCommunication(
							indexCommunicationOnReceiveLink + 1, communication);
				}
				communication.setSendLink(sendLink);
				communication.setReceiveLink(receiveLink);
			}
			communication.setScheduled();
		} else {
			System.out.println(" communication name: "
					+ communication.getName());
			System.out.println(" source computation has not been scheduled");
		}
	}

	// protected RouteDescriptor findRoute(OperatorDescriptor sourceOperator,
	// OperatorDescriptor destinationOperator) {
	// RouteDescriptor route = new RouteDescriptor();
	// route.addLink(sourceOperator.getOutputLinks().get(0));
	// route.addLink(destinationOperator.getInputLinks().get(0));
	// return route;
	//
	// }

}
