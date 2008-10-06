package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.scheduler;

import java.util.Vector;

import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.OperatorDescriptor;

public class ListSchedulingCriticalChildClassic extends ListSchedulingClassic {

	public ListSchedulingCriticalChildClassic(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		super(algorithm, architecture);
		this.name = "List Scheduling CriticalChildClassic";
	}

	protected OperatorDescriptor selectOperator(
			ComputationDescriptor computation) {
		ComputationDescriptor criticalChild = null;
		int bestOperatorFinishTime = Integer.MAX_VALUE;
		OperatorDescriptor bestOperator = null;
		System.out.println(" * select operator for " + computation.getName());
		if (computation.getOperator() == null) {
			boolean findChild = false;
			for (int i = schedulingOrder.indexOf(computation); i < schedulingOrder
					.size(); i++) {
				for (CommunicationDescriptor indexCommunication : computation
						.getFollowingCommunications()) {
					if (algorithm.getComputation(indexCommunication
							.getDestination()) == schedulingOrder.get(i)) {
						findChild = true;
						criticalChild = schedulingOrder.get(i);
						break;
					}
				}
				if (findChild) {
					break;
				}
			}
			for (OperatorDescriptor indexOperator : architecture
					.getAllOperators().values()) {
				int finishTime = scheduleComputation(computation, indexOperator);
				// TODO: need to back up times after schedule of computation
				backupTimes();
				int minOperatorFinishTime = Integer.MAX_VALUE;
				if (findChild) {
					// System.out.println(" critical child is "
					// + criticalChild.getName());
					Vector<OperatorDescriptor> childOperatorList = new Vector<OperatorDescriptor>();
					// schedule critical child
					for (CommunicationDescriptor incomingCommunication : criticalChild
							.getPrecedingCommunications()) {
						if (algorithm.getComputation(
								incomingCommunication.getSource())
								.isScheduled()) {
							OperatorDescriptor childOperator = algorithm
									.getComputation(
											incomingCommunication.getSource())
									.getOperator();
							if (!childOperatorList.contains(childOperator)) {
								childOperatorList.add(childOperator);
								finishTime = scheduleComputation(criticalChild,
										childOperator);
								// System.out.println(" finishTime=" +
								// finishTime);
								if (finishTime < minOperatorFinishTime) {
									minOperatorFinishTime = finishTime;
								}
								for (OperatorDescriptor indexOperator2 : architecture
										.getAllOperators().values()) {
									for (CommunicationDescriptor indexCommunication : criticalChild
											.getPrecedingCommunications()) {
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
								childOperator.removeComputation(criticalChild);
								childOperator.removeOperation(criticalChild);
								// TODO: need to recover times after schedule of
								// criticalChild
								recoverTimes();
								criticalChild.clearScheduled();
								criticalChild.setOperator(null);
							}
						}
					}
				} else {
					// System.out.println(" no child");
					minOperatorFinishTime = finishTime;
				}
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

}
