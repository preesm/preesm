package org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.scheduler;

import java.util.HashMap;
import java.util.Vector;

import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.communicationcontentiouslistscheduling.descriptor.OperatorDescriptor;

public abstract class AbstractScheduler {

	protected String name = "Abstract Scheduler";

	protected Vector<ComputationDescriptor> schedulingOrder = new Vector<ComputationDescriptor>();

	protected HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer;

	protected HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer;

	protected AlgorithmDescriptor algorithm;

	protected ArchitectureDescriptor architecture;

	protected CommunicationDescriptor topCommunication;

	protected CommunicationDescriptor bottomCommunication;

	protected ComputationDescriptor topComputation;

	protected ComputationDescriptor bottomComputation;

	protected int scheduleLength = 0;

	protected Vector<OperatorDescriptor> usedOperators = null;

	protected String communicationName = null;

	protected int nbCommunicationIn = 0;

	protected int nbCommunicationOut = 0;

	protected int nbCommunicationMiddle = 0;

	protected Vector<ComputationDescriptor> unscheduledComputations = null;

	public AbstractScheduler(
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer,
			HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer) {
		this.ComputationDescriptorBuffer = ComputationDescriptorBuffer;
		this.CommunicationDescriptorBuffer = CommunicationDescriptorBuffer;
		topCommunication = new CommunicationDescriptor("topCommunication",
				CommunicationDescriptorBuffer);
		bottomCommunication = new CommunicationDescriptor(
				"bottomCommunication", CommunicationDescriptorBuffer);
		topCommunication.setStartTimeOnSendOperator(0);
		topCommunication.setStartTimeOnLink(0);
		topCommunication.setStartTimeOnReceiveOperator(0);
		topCommunication.setASAP(0);
		topCommunication.setALAP(0);
		topCommunication.setScheduled();
		bottomCommunication.setStartTimeOnSendOperator(Integer.MAX_VALUE);
		bottomCommunication.setStartTimeOnLink(Integer.MAX_VALUE);
		bottomCommunication.setStartTimeOnReceiveOperator(Integer.MAX_VALUE);
		bottomCommunication.setASAP(Integer.MAX_VALUE);
		bottomCommunication.setALAP(Integer.MAX_VALUE);
		bottomCommunication.setScheduled();
		topComputation = new ComputationDescriptor("topComputation",
				ComputationDescriptorBuffer);
		bottomComputation = new ComputationDescriptor("bottomComputation",
				ComputationDescriptorBuffer);
		topComputation.setStartTime(0);
		bottomComputation.setStartTime(Integer.MAX_VALUE);
		usedOperators = new Vector<OperatorDescriptor>();
		System.out
				.println("Add topComputation, bottomComputation and additional in/out communications:");
		for (ComputationDescriptor indexComputation : ComputationDescriptorBuffer
				.values()) {

			if (indexComputation.getPrecedingCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = new String("communication_in")
						.concat(Integer.toString(nbCommunicationIn));
				new CommunicationDescriptor(communicationName,
						CommunicationDescriptorBuffer,
						topComputation.getName(), indexComputation.getName(), 0);
				CommunicationDescriptorBuffer.get(communicationName)
						.clearExist();
				topComputation
						.addFollowingCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				indexComputation
						.addPrecedingCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				nbCommunicationIn++;
				System.out.println(communicationName + " : topComputation -> "
						+ indexComputation.getName());
			}
			if (indexComputation.getFollowingCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = (new String("communication_out"))
						.concat(Integer.toString(nbCommunicationOut));
				new CommunicationDescriptor(communicationName,
						CommunicationDescriptorBuffer, indexComputation
								.getName(), bottomComputation.getName(), 0);
				CommunicationDescriptorBuffer.get(communicationName)
						.clearExist();
				bottomComputation
						.addPrecedingCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				indexComputation
						.addFollowingCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				nbCommunicationOut++;
				System.out.println(communicationName + " : "
						+ indexComputation.getName() + " -> bottomComputation");
			}
		}
	}

	public AbstractScheduler(AlgorithmDescriptor algorithm) {
		this.algorithm = algorithm;
		topCommunication = new CommunicationDescriptor("topCommunication",
				algorithm);
		bottomCommunication = new CommunicationDescriptor(
				"bottomCommunication", algorithm);
		topCommunication.setStartTimeOnSendOperator(0);
		topCommunication.setStartTimeOnLink(0);
		topCommunication.setStartTimeOnReceiveOperator(0);
		topCommunication.setASAP(0);
		topCommunication.setALAP(0);
		topCommunication.setScheduled();
		bottomCommunication.setStartTimeOnSendOperator(Integer.MAX_VALUE);
		bottomCommunication.setStartTimeOnLink(Integer.MAX_VALUE);
		bottomCommunication.setStartTimeOnReceiveOperator(Integer.MAX_VALUE);
		bottomCommunication.setASAP(Integer.MAX_VALUE);
		bottomCommunication.setALAP(Integer.MAX_VALUE);
		bottomCommunication.setScheduled();
		usedOperators = new Vector<OperatorDescriptor>();
		System.out
				.println("Add topComputation, bottomComputation and additional in/out communications:");
		this.topComputation = algorithm.getTopComputation();
		this.bottomComputation = algorithm.getBottomComputation();
		topComputation.setStartTime(0);
		bottomComputation.setStartTime(Integer.MAX_VALUE);
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation.getPrecedingCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = new String("communication_in")
						.concat(Integer.toString(nbCommunicationIn));
				new CommunicationDescriptor(communicationName, topComputation
						.getName(), indexComputation.getName(), 0, algorithm);
				algorithm.addCommunication(algorithm
						.getCommunication(communicationName));
				algorithm.getCommunication(communicationName).clearExist();
				topComputation.addFollowingCommunication(algorithm
						.getCommunication(communicationName));
				indexComputation.addPrecedingCommunication(algorithm
						.getCommunication(communicationName));
				nbCommunicationIn++;
				System.out.println(communicationName + " : "
						+ topComputation.getName() + " -> "
						+ indexComputation.getName());
			}
			if (indexComputation.getFollowingCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = (new String("communication_out"))
						.concat(Integer.toString(nbCommunicationOut));
				new CommunicationDescriptor(communicationName, indexComputation
						.getName(), bottomComputation.getName(), 0, algorithm);
				algorithm.getCommunication(communicationName).clearExist();
				algorithm.addCommunication(algorithm
						.getCommunication(communicationName));
				bottomComputation.addPrecedingCommunication(algorithm
						.getCommunication(communicationName));
				indexComputation.addFollowingCommunication(algorithm
						.getCommunication(communicationName));
				nbCommunicationOut++;
				System.out.println(communicationName + " : "
						+ indexComputation.getName() + " -> "
						+ bottomComputation.getName());
			}
		}
	}

	public AlgorithmDescriptor getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(AlgorithmDescriptor algorithm) {
		this.algorithm = algorithm;
	}

	public ArchitectureDescriptor getArchitecture() {
		return architecture;
	}

	public void setArchitecture(ArchitectureDescriptor architecture) {
		this.architecture = architecture;
	}

	public ComputationDescriptor getTopComputation() {
		return topComputation;
	}

	public ComputationDescriptor getBottomComputation() {
		return bottomComputation;
	}

	public int getScheduleLength() {
		return scheduleLength;
	}

	public CommunicationDescriptor getTopCommunication() {
		return topCommunication;
	}

	public CommunicationDescriptor getBottomCommunication() {
		return bottomCommunication;
	}

	public Vector<OperatorDescriptor> getUsedOperators() {
		return usedOperators;
	}

	public void addUsedOperators(OperatorDescriptor operator) {
		usedOperators.add(operator);
	}

	/**
	 * 
	 */
	public void computeTopLevel() {
		for (ComputationDescriptor indexComputation : ComputationDescriptorBuffer
				.values()) {
			indexComputation.clearReady();
		}
		topComputation.setTopLevel(0);
		topComputation.setReady();

		// Vector is very important to contain some computations multiple
		// times in it because they may be treated multiple times.
		Vector<ComputationDescriptor> computations = new Vector<ComputationDescriptor>();
		for (CommunicationDescriptor indexCommunication : topComputation
				.getFollowingCommunications()) {
			computations.add(ComputationDescriptorBuffer.get(indexCommunication
					.getDestination()));
		}

		for (int i = 0; i < computations.size(); i++) {
			int time = 0;
			boolean skipComputation = false;
			for (CommunicationDescriptor indexCommunication : computations.get(
					i).getPrecedingCommunications()) {
				if (!ComputationDescriptorBuffer.get(
						indexCommunication.getSource()).isReady()) {
					skipComputation = true;
					break;
				} else if (time < ComputationDescriptorBuffer.get(
						indexCommunication.getSource()).getTopLevel()
						+ ComputationDescriptorBuffer.get(
								indexCommunication.getSource())
								.getComputationDuration()
						+ indexCommunication.getCommunicationDuration()) {
					time = ComputationDescriptorBuffer.get(
							indexCommunication.getSource()).getTopLevel()
							+ ComputationDescriptorBuffer.get(
									indexCommunication.getSource())
									.getComputationDuration()
							+ indexCommunication.getCommunicationDuration();
				}
			}
			if (!skipComputation) {
				computations.get(i).setTopLevel(time);
				computations.get(i).setReady();
				// System.out.println("step " + i + ": computationId="
				// + computations.get(i).getId() + " --> t-level="
				// + computations.get(i).getTopLevel());
				for (CommunicationDescriptor indexCommunication : computations
						.get(i).getFollowingCommunications()) {
					if (ComputationDescriptorBuffer.get(indexCommunication
							.getDestination()) != bottomComputation) {
						computations.add(ComputationDescriptorBuffer
								.get(indexCommunication.getDestination()));
					}
				}
			}
		}
		int time = 0;
		for (CommunicationDescriptor indexCommunication : bottomComputation
				.getPrecedingCommunications()) {
			if (time < ComputationDescriptorBuffer.get(
					indexCommunication.getSource()).getTopLevel()
					+ ComputationDescriptorBuffer.get(
							indexCommunication.getSource())
							.getComputationDuration()) {
				time = ComputationDescriptorBuffer.get(
						indexCommunication.getSource()).getTopLevel()
						+ ComputationDescriptorBuffer.get(
								indexCommunication.getSource())
								.getComputationDuration();
			}
		}
		bottomComputation.setTopLevel(time);
		for (ComputationDescriptor indexComputation : ComputationDescriptorBuffer
				.values()) {
			indexComputation.setASAP(indexComputation.getTopLevel());
		}
	}

	/**
	 * 
	 */
	public void computeBottomLevel() {
		for (ComputationDescriptor indexComputation : ComputationDescriptorBuffer
				.values()) {
			indexComputation.clearReady();
		}
		bottomComputation.setBottomLevel(0);
		bottomComputation.setReady();

		// Vector is very important to contain some computations multiple
		// times in it because they should be treated multiple times.
		Vector<ComputationDescriptor> computations = new Vector<ComputationDescriptor>();
		for (CommunicationDescriptor indexCommunication : bottomComputation
				.getPrecedingCommunications()) {
			computations.add(ComputationDescriptorBuffer.get(indexCommunication
					.getSource()));
		}

		for (int i = 0; i < computations.size(); i++) {
			int time = 0;
			boolean skipComputation = false;
			for (CommunicationDescriptor indexCommunication : computations.get(
					i).getFollowingCommunications()) {
				if (!ComputationDescriptorBuffer.get(
						indexCommunication.getDestination()).isReady()) {
					skipComputation = true;
					break;
				} else if (time < ComputationDescriptorBuffer.get(
						indexCommunication.getDestination()).getBottomLevel()
						+ indexCommunication.getCommunicationDuration()) {
					time = ComputationDescriptorBuffer.get(
							indexCommunication.getDestination())
							.getBottomLevel()
							+ indexCommunication.getCommunicationDuration();
				}
			}
			if (!skipComputation) {
				computations.get(i).setBottomLevel(
						time + computations.get(i).getComputationDuration());
				computations.get(i).setReady();
				// System.out.println("step " + i + ": computationName="
				// + computations.get(i).getName() + " --> b-level="
				// + computations.get(i).getBottomLevel());
				for (CommunicationDescriptor indexCommunication : computations
						.get(i).getPrecedingCommunications()) {
					if (ComputationDescriptorBuffer.get(indexCommunication
							.getSource()) != topComputation) {
						computations.add(ComputationDescriptorBuffer
								.get(indexCommunication.getSource()));
					}
				}
			}
		}
		int time = 0;
		for (CommunicationDescriptor indexCommunication : topComputation
				.getFollowingCommunications()) {
			if (time < ComputationDescriptorBuffer.get(
					indexCommunication.getDestination()).getBottomLevel()) {
				time = ComputationDescriptorBuffer.get(
						indexCommunication.getDestination()).getBottomLevel();
			}
		}
		topComputation.setBottomLevel(time);
		for (ComputationDescriptor indexComputation : ComputationDescriptorBuffer
				.values()) {
			indexComputation.setALAP(topComputation.getBottomLevel()
					- indexComputation.getBottomLevel());
		}
	}

	public void sortComputationsByBottomLevel(
			Vector<ComputationDescriptor> computationList) {
		Vector<ComputationDescriptor> sortedComputations = new Vector<ComputationDescriptor>();
		sortedComputations.add(topComputation);
		for (ComputationDescriptor indexComputation : computationList) {
			if ((indexComputation != topComputation)
					&& (indexComputation != bottomComputation)) {
				for (int i = 0; i < sortedComputations.size(); i++) {
					if (indexComputation.getBottomLevel() > sortedComputations
							.get(i).getBottomLevel()) {
						sortedComputations.add(i, indexComputation);
						break;
					} else {
						if (indexComputation.getBottomLevel() == sortedComputations
								.get(i).getBottomLevel()) {
							if (indexComputation.getTopLevel() > sortedComputations
									.get(i).getTopLevel()) {
								sortedComputations.add(i, indexComputation);
								break;
							}
						}
						if (i == (sortedComputations.size() - 1)) {
							sortedComputations.add(indexComputation);
							break;
						}
					}
				}
			}
		}
		sortedComputations.remove(0);
		computationList = sortedComputations;
	}

	public String getName() {
		return name;
	}

	public Vector<ComputationDescriptor> getSchedulingOrder() {
		return schedulingOrder;
	}

	protected void updateTimes() {
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation.isScheduled()) {
				indexComputation.updateTimes();
				for (CommunicationDescriptor indexCommunication : indexComputation
						.getPrecedingCommunications()) {
					indexCommunication.updateTimes();
				}
			}
		}
	}

	protected void restoreTimes() {
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation.isScheduled()) {
				indexComputation.restoreTimes();
				for (CommunicationDescriptor indexCommunication : indexComputation
						.getPrecedingCommunications()) {
					indexCommunication.restoreTimes();
				}
			}
		}
	}

	protected void backupTimes() {
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation.isScheduled()) {
				indexComputation.backupTimes();
				for (CommunicationDescriptor indexCommunication : indexComputation
						.getPrecedingCommunications()) {
					indexCommunication.backupTimes();
				}
			}
		}
	}

	protected void recoverTimes() {
		for (ComputationDescriptor indexComputation : algorithm
				.getComputations().values()) {
			if (indexComputation.isScheduled()) {
				indexComputation.recoverTimes();
				for (CommunicationDescriptor indexCommunication : indexComputation
						.getPrecedingCommunications()) {
					indexCommunication.recoverTimes();
				}
			}
		}
	}

	public abstract boolean schedule();

	protected int max(int a, int b) {
		return a < b ? b : a;
	}

	protected int max(int a, int b, int c) {
		int tmp = a < b ? b : a;
		return c < tmp ? tmp : c;
	}

	protected int min(int a, int b) {
		return a > b ? b : a;
	}

	protected int min(int a, int b, int c) {
		int tmp = a > b ? b : a;
		return c > tmp ? tmp : c;
	}

}
