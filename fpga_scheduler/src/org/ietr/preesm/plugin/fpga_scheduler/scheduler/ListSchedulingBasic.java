package org.ietr.preesm.plugin.fpga_scheduler.scheduler;

import java.util.Vector;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.LinkDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.OperationType;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.OperatorDescriptor;

public class ListSchedulingBasic extends AbstractScheduler {

	public ListSchedulingBasic(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		super(algorithm);
		// TODO Auto-generated constructor stub
		this.architecture = architecture;
		this.name = "List Scheduling Basic";
	}

	@Override
	public boolean schedule() {
		// TODO Auto-generated method stub
		System.out.println("\n***** schedule *****");
		algorithm.computeTopLevel();
		algorithm.computeBottomLevel();
		schedulingOrder = algorithm.sortComputationsByBottomLevel();
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

		for (int i = 0; i < schedulingOrder.size(); i++) {
			System.out.println(i + ": schedule "
					+ schedulingOrder.get(i).getName() + " (bottom level="
					+ schedulingOrder.get(i).getBottomLevel() + ")");
			bestOperator = selectOperator(schedulingOrder.get(i));

			scheduleComputation(schedulingOrder.get(i), bestOperator);
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
		for (int i = 0; i < schedulingOrder.size(); i++) {
			scheduleLength = max(scheduleLength, schedulingOrder.get(i)
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

	protected OperatorDescriptor selectOperator(
			ComputationDescriptor computation) {
		int bestOperatorFinishTime = Integer.MAX_VALUE;
		OperatorDescriptor bestOperator = null;
		System.out.println(" * select operator for " + computation.getName());
		if (computation.getOperator() == null) {
			for (OperatorDescriptor indexOperator : architecture
					.getAllOperators().values()) {
				int minOperatorFinishTime = scheduleComputation(computation,
						indexOperator);
				if (bestOperatorFinishTime > minOperatorFinishTime) {
					bestOperatorFinishTime = minOperatorFinishTime;
					bestOperator = indexOperator;
				}
				// System.out.println(" minOperatorFinishTime="
				// + minOperatorFinishTime);
				// remove inserted communications(source!=destination) and
				// computations;
				for (OperatorDescriptor indexOperator2 : architecture
						.getAllOperators().values()) {
					for (CommunicationDescriptor indexCommunication : computation
							.getPrecedingCommunications()) {
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
				indexOperator.removeComputation(computation);
				indexOperator.removeOperation(computation);
				restoreTimes();
			}
			// System.out.println(" bestOperatorFinishTime="
			// + bestOperatorFinishTime);
		} else {
			bestOperator = computation.getOperator();
		}
		computation.clearScheduled();
		computation.setOperator(null);
		return bestOperator;
	}

	protected int scheduleComputation(ComputationDescriptor computation,
			OperatorDescriptor operator) {
		int dataReadyTime = 0;
		int maxOperatorFinishTime = 0;
		// System.out.println(" ** schedule computation: " +
		// computation.getName()
		// + " on: " + operator.getId());
		if (computation.getComputationDurations().containsKey(
				operator.getName())) {
			// schedule preceding communications
			for (CommunicationDescriptor indexCommunication : computation
					.getPrecedingCommunications()) {
				if (!indexCommunication.getSource().equalsIgnoreCase(
						topComputation.getName())) {
					scheduleCommunication(indexCommunication, operator);
				}
			}
			// calculate data ready time
			for (CommunicationDescriptor indexCommunication : computation
					.getPrecedingCommunications()) {
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
						.getComputationDuration(operator)) {
					computation.setStartTime(maxTime);
					computation.setFinishTime(maxTime
							+ computation.getComputationDuration(operator));
					operator.addComputation(computation);
					operator.addOperation(i + 1, computation);
					operator.addOccupiedTimeInterval(computation.getName(),
							computation.getStartTime(), computation
									.getFinishTime());
					break;
				}
			}
			// set ALAP for preceding communications
			Vector<CommunicationDescriptor> communicationList = new Vector<CommunicationDescriptor>();
			for (CommunicationDescriptor indexCommunication : computation
					.getPrecedingCommunications()) {
				if (indexCommunication.isExist()) {
					communicationList.add(indexCommunication);
				}
			}
			for (int i = communicationList.size() - 1; i >= 0; i--) {
				if (communicationList.get(i).isScheduled()) {
					communicationList.get(i).setALAP(
							communicationList.get(i).getStartTimeOnLink());
				}
			}
			// Examine the latest finish time of all operators
			for (OperatorDescriptor indexOperator : architecture
					.getAllOperators().values()) {
				if (maxOperatorFinishTime < indexOperator
						.getOccupiedTimeInterval(
								indexOperator
										.getOperation(
												indexOperator.getOperations()
														.size() - 2).getName())
						.getFinishTime()) {
					maxOperatorFinishTime = indexOperator
							.getOccupiedTimeInterval(
									indexOperator.getOperation(
											indexOperator.getOperations()
													.size() - 2).getName())
							.getFinishTime();
				}
			}
		} else {
			maxOperatorFinishTime = Integer.MAX_VALUE;
		}
		computation.setScheduled();
		computation.setOperator(operator);
		return maxOperatorFinishTime;
	}

	protected void scheduleCommunication(CommunicationDescriptor communication,
			OperatorDescriptor destinationOperator) {
		ComputationDescriptor sourceComputation = algorithm
				.getComputation(communication.getSource());
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
				communication.setALAP(sourceComputation.getFinishTime());
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
						infSendCommunicationTime = max(algorithm
								.getComputation(communication.getSource())
								.getFinishTime()
								+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnSendOperator()
										+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnLink());
					} else {
						infSendCommunicationTime = max(algorithm
								.getComputation(communication.getSource())
								.getFinishTime()
								+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnReceiveOperator()
										+ communication.getSendOverhead(),
								sendCommunicationList.get(
										indexCommunicationOnSendLink)
										.getFinishTimeOnLink());
					}
					supSendCommunicationTime = sendCommunicationList.get(
							indexCommunicationOnSendLink + 1).getALAP();
					infReceiveCommunicationTime = max(algorithm.getComputation(
							communication.getSource()).getFinishTime()
							+ communication.getSendOverhead(),
							receiveCommunicationList.get(
									indexCommunicationOnReceiveLink)
									.getFinishTimeOnLink());
					supReceiveCommunicationTime = receiveCommunicationList.get(
							indexCommunicationOnReceiveLink + 1).getALAP();
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

				// set new start times in source operator and links
				int indexOperationOnSourceOperator = max(sourceOperator
						.getOperations().indexOf(
								sendCommunicationList
										.get(indexCommunicationOnSendLink)),
						sourceOperator.getOperations().indexOf(
								algorithm.getComputation(communication
										.getSource())));
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
				receiveCommunicationList
						.get(indexCommunicationOnReceiveLink + 1)
						.setStartTimeOnLink(
								max(
										communication.getFinishTimeOnLink(),
										receiveCommunicationList
												.get(
														indexCommunicationOnReceiveLink + 1)
												.getStartTimeOnLink()));

				// Check delay
				int delayStartTime = 0;
				int delayDuration = 0;
				boolean needDelay = true;
				if (sourceOperator.getOperation(
						indexOperationOnSourceOperator + 1).getType() == OperationType.Computation) {
					if (((ComputationDescriptor) sourceOperator
							.getOperation(indexOperationOnSourceOperator + 1))
							.getStartTime() >= communication
							.getFinishTimeOnSendOperator()) {
						needDelay = false;
					} else {
						delayStartTime = ((ComputationDescriptor) sourceOperator
								.getOperation(indexOperationOnSourceOperator + 1))
								.getStartTime();
						delayDuration = communication
								.getFinishTimeOnSendOperator()
								- delayStartTime;
					}
				} else {
					if (sourceOperator
							.getSendCommunications()
							.contains(
									(CommunicationDescriptor) sourceOperator
											.getOperation(indexOperationOnSourceOperator + 1))) {
						if (((CommunicationDescriptor) sourceOperator
								.getOperation(indexOperationOnSourceOperator + 1))
								.getStartTimeOnSendOperator() >= communication
								.getFinishTimeOnSendOperator()) {
							needDelay = false;
						} else {
							delayStartTime = ((CommunicationDescriptor) sourceOperator
									.getOperation(indexOperationOnSourceOperator + 1))
									.getStartTimeOnSendOperator();
							delayDuration = communication
									.getFinishTimeOnSendOperator()
									- delayStartTime;
						}
					} else {
						if (((CommunicationDescriptor) sourceOperator
								.getOperation(indexOperationOnSourceOperator + 1))
								.getStartTimeOnReceiveOperator() >= communication
								.getFinishTimeOnSendOperator()) {
							needDelay = false;
						} else {
							delayStartTime = ((CommunicationDescriptor) sourceOperator
									.getOperation(indexOperationOnSourceOperator + 1))
									.getStartTimeOnReceiveOperator();
							delayDuration = communication
									.getFinishTimeOnSendOperator()
									- delayStartTime;
						}
					}
				}
				if (needDelay) {
					// delay operations
					// System.out.println(" delay operations for: "
					// + delayDuration + " after: " + delayStartTime);
					for (OperatorDescriptor indexOperator : architecture
							.getAllOperators().values()) {
						for (int i = 1; i < indexOperator.getOperations()
								.size() - 1; i++) {
							if (indexOperator.getOperation(i).getType() == OperationType.Computation) {
								if (((ComputationDescriptor) indexOperator
										.getOperation(i)).getStartTime() >= delayStartTime) {
									((ComputationDescriptor) indexOperator
											.getOperation(i))
											.setStartTime(((ComputationDescriptor) indexOperator
													.getOperation(i))
													.getStartTime()
													+ delayDuration);
								}
							} else {
								// check only the receive communication to avoid
								// duplicate checking
								if (indexOperator
										.getReceiveCommunications()
										.contains(
												(CommunicationDescriptor) indexOperator
														.getOperation(i))) {
									if (((CommunicationDescriptor) indexOperator
											.getOperation(i))
											.getStartTimeOnSendOperator() >= delayStartTime) {
										((CommunicationDescriptor) indexOperator
												.getOperation(i))
												.setStartTimeOnSendOperator(((CommunicationDescriptor) indexOperator
														.getOperation(i))
														.getStartTimeOnSendOperator()
														+ delayDuration);
									}
									if (((CommunicationDescriptor) indexOperator
											.getOperation(i))
											.getStartTimeOnLink() >= delayStartTime) {
										((CommunicationDescriptor) indexOperator
												.getOperation(i))
												.setStartTimeOnLink(((CommunicationDescriptor) indexOperator
														.getOperation(i))
														.getStartTimeOnLink()
														+ delayDuration);
									}
									if (((CommunicationDescriptor) indexOperator
											.getOperation(i))
											.getStartTimeOnReceiveOperator() >= delayStartTime) {
										((CommunicationDescriptor) indexOperator
												.getOperation(i))
												.setStartTimeOnReceiveOperator(((CommunicationDescriptor) indexOperator
														.getOperation(i))
														.getStartTimeOnReceiveOperator()
														+ delayDuration);
									}
								}
							}
						}
					}
				}

				// find slot in destination operator
				int indexOperationOnDestinationOperator = destinationOperator
						.getOperations().indexOf(
								receiveCommunicationList
										.get(indexCommunicationOnReceiveLink));
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

				// insert communication
				if (!sourceOperator.getSendCommunications().contains(
						communication)) {
					sourceOperator.addSendCommunication(communication);
					sourceOperator.addOperation(
							indexOperationOnSourceOperator + 1, communication);
					sourceOperator.addOccupiedTimeInterval(communication
							.getName(), communication
							.getStartTimeOnSendOperator(), communication
							.getFinishTimeOnSendOperator());
				}
				destinationOperator.addReceiveCommunication(communication);
				destinationOperator.addOperation(
						indexOperationOnDestinationOperator + 1, communication);
				destinationOperator.addOccupiedTimeInterval(communication
						.getName(), communication
						.getStartTimeOnReceiveOperator(), communication
						.getFinishTimeOnReceiveOperator());
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
				if (indexCommunicationOnSendLink == sendCommunicationList
						.size() - 2
						&& indexCommunicationOnReceiveLink == receiveCommunicationList
								.size() - 2) {
					communication.setALAP(communication.getStartTimeOnLink());
				} else {
					communication.setALAP(supCommunicationTime
							- communication.getCommunicationDuration());
				}
				sendLink.addCommunication(indexCommunicationOnSendLink + 1,
						communication);
				receiveLink.addCommunication(
						indexCommunicationOnReceiveLink + 1, communication);
				communication.setSendLink(sendLink);
				communication.setReceiveLink(receiveLink);
			}
			communication.setScheduled();
		} else {
			// System.out
			// .println(" source computation has not been scheduled");
		}
	}

	// protected RouteDescriptor findRoute(OperatorDescriptor sourceOperator,
	// OperatorDescriptor destinationOperator) {
	// RouteDescriptor route = new RouteDescriptor();
	// route.addLink(sourceOperator.getOutputLinks().get(0));
	// route.addLink(destinationOperator.getInputLinks().get(0));
	// return route;
	// }

}
