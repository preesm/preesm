package org.ietr.preesm.plugin.fpga_scheduler.scheduler;

import java.util.Collections;
import java.util.Vector;

import org.ietr.preesm.plugin.fpga_scheduler.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.fpga_scheduler.descriptor.ProcessorDescriptor;

public class DcpUncScheduler extends AbstractScheduler {

	private String processorName = "processor";
	private ProcessorDescriptor newProcessor = null;
	private Vector<ProcessorDescriptor> processorsInUse = null;
	private int nbProcessorInUse = 0;

	private ArchitectureDescriptor architecture;

	public DcpUncScheduler(AlgorithmDescriptor algorithm,
			ArchitectureDescriptor architecture) {
		// super(algorithm, architecture);
		super(algorithm);
		this.architecture = architecture;
		processorsInUse = new Vector<ProcessorDescriptor>();
		newProcessor = new ProcessorDescriptor("newProcessor", processorName,
				architecture.getComponents());
		newProcessor.addComputation(topComputation);
		topComputation.setOperator(newProcessor);
		newProcessor.addComputation(bottomComputation);
		bottomComputation.setOperator(newProcessor);
		unscheduledComputations = new Vector<ComputationDescriptor>(algorithm
				.getComputations().values());
		topComputation.setScheduled();
		unscheduledComputations.remove(topComputation);
		bottomComputation.setScheduled();
		unscheduledComputations.remove(bottomComputation);
		ProcessorDescriptor processor = new ProcessorDescriptor(processorName,
				processorName, architecture.getComponents());
		for (ComputationDescriptor computationIndex : algorithm
				.getComputations().values()) {
			computationIndex.addComputationDuration(processor, computationIndex
					.getTime());
		}
	}

	public boolean schedule() {
		System.out.println("\n*****schedule*****");
		algorithm.computeTopLevel();
		algorithm.computeBottomLevel();

		while (unscheduledComputations.size() != 0) {
			ComputationDescriptor highest = findHighest(unscheduledComputations);
			if (highest.getTopLevel() + highest.getBottomLevel() == topComputation
					.getTopLevel()
					+ topComputation.getBottomLevel()) {
				System.out.println("On DCP");
				selectProcessor(highest, true);
			} else {
				System.out.println("Not On DCP");
				selectProcessor(highest, false);
			}
			algorithm.computeTopLevel();
			algorithm.computeBottomLevel();
			System.out.println("  AEST after schedule: " + highest.getAEST());
		}
		for (ComputationDescriptor computationIndex : algorithm
				.getComputations().values()) {
			computationIndex.setStartTime(computationIndex.getAEST());
		}
		scheduleLength = bottomComputation.getTopLevel();
		return true;
	}

	public ComputationDescriptor findHighest(
			Vector<ComputationDescriptor> computations) {
		ComputationDescriptor highest = null;

		System.out.println("\n*****findHighest*****");
		Collections.sort(computations);
		highest = computations.get(0);
		for (int i = 0; i < computations.size(); i++) {
			if (computations.get(i).getTopLevel()
					+ computations.get(i).getBottomLevel() == highest
					.getTopLevel()
					+ highest.getBottomLevel()) {
				if (computations.get(i).getAEST() < highest.getAEST()) {
					highest = computations.get(i);
				}
			} else {
				break;
			}
		}
		System.out.println("schedule length=" + bottomComputation.getTopLevel()
				+ "\nhighest computation: " + highest.getName() + "\n  AEST="
				+ highest.getAEST() + "  ALST=" + highest.getALST());
		return highest;
	}

	public void selectProcessor(ComputationDescriptor computation, boolean OnDcp) {
		Vector<ProcessorDescriptor> processorList = new Vector<ProcessorDescriptor>();
		ProcessorDescriptor bestProcessor = null;
		ProcessorDescriptor processor = null;
		Vector<ComputationDescriptor> childList = new Vector<ComputationDescriptor>();
		ComputationDescriptor criticalChild = null;
		int bestCompositeAEST = Integer.MAX_VALUE;
		int bestAEST = Integer.MAX_VALUE;
		int thisAEST = 0;
		int childAEST = 0;

		System.out.println("*****selectProcessor*****");
		System.out.println("processorsInUse:");
		for (ProcessorDescriptor processorIndex : processorsInUse) {
			System.out.println("  " + processorIndex.getId());
		}
		if (OnDcp) {
			for (CommunicationDescriptor communicationIndex : computation
					.getPrecedingCommunications()) {
				if ((!communicationIndex.getSource().equals("top"))
						&& (algorithm.getComputations().get(
								communicationIndex.getSource()).getOperator() != null)) {
					System.out.println("preceding computation: "
							+ communicationIndex.getSource()
							+ "->"
							+ algorithm.getComputations().get(
									communicationIndex.getSource())
									.getOperator().getId());
					if (!processorList.contains((ProcessorDescriptor) algorithm
							.getComputations().get(
									communicationIndex.getSource())
							.getOperator())) {
						processorList.add((ProcessorDescriptor) algorithm
								.getComputations().get(
										communicationIndex.getSource())
								.getOperator());
					}

				}
			}
			for (CommunicationDescriptor communicationIndex : computation
					.getFollowingCommunications()) {
				if ((!communicationIndex.getDestination().equals("bottom"))
						&& (algorithm.getComputations().get(
								communicationIndex.getDestination())
								.getOperator() != null)) {
					System.out.println("following computation: "
							+ communicationIndex.getDestination()
							+ "->"
							+ algorithm.getComputations().get(
									communicationIndex.getDestination())
									.getOperator().getId());
					if (!processorList.contains((ProcessorDescriptor) algorithm
							.getComputations().get(
									communicationIndex.getDestination())
							.getOperator())) {
						processorList.add((ProcessorDescriptor) algorithm
								.getComputations().get(
										communicationIndex.getDestination())
								.getOperator());
					}
				}
			}
			if (!processorList.contains(newProcessor)) {
				processorList.add(newProcessor);
			}
		} else {
			processorList.addAll(processorsInUse);
		}
		System.out.println("processorList:");
		for (ProcessorDescriptor processorIndex : processorList) {
			System.out.println("  " + processorIndex.getId());
		}
		for (CommunicationDescriptor communicationIndex : computation
				.getFollowingCommunications()) {
			if (algorithm.getComputations().get(
					communicationIndex.getDestination()) != bottomComputation) {
				childList.add(algorithm.getComputations().get(
						communicationIndex.getDestination()));
				System.out.println("child: "
						+ communicationIndex.getDestination());
			}
		}
		if (childList.size() != 0) {
			Collections.sort(childList);
			criticalChild = childList.firstElement();
			System.out.println("critical child: " + criticalChild.getName());
			if (!criticalChild.isScheduled()) {
				while (processorList.size() != 0) {
					processor = processorList.firstElement();
					processorList.remove(0);
					thisAEST = findSlot(computation, processor, true);
					if ((thisAEST == Integer.MAX_VALUE) && OnDcp) {
						thisAEST = findSlot(computation, processor, false);
					}
					if (thisAEST != Integer.MAX_VALUE) {
						computation.setOperator(processor);
						childAEST = findSlot(criticalChild, processor, true);
						if (childAEST != Integer.MAX_VALUE) {
							if (childAEST + thisAEST < bestCompositeAEST) {
								bestProcessor = processor;
								bestCompositeAEST = childAEST + thisAEST;
								bestAEST = thisAEST;
							}
						}
					}
				}
				criticalChild.setOperator(null);
			} else {
				thisAEST = findSlot(computation,
						(ProcessorDescriptor) criticalChild.getOperator(), true);
				bestProcessor = (ProcessorDescriptor) criticalChild
						.getOperator();
				bestAEST = thisAEST;
				if (thisAEST == Integer.MAX_VALUE) {
					while (processorList.size() != 0) {
						processor = processorList.firstElement();
						processorList.remove(0);
						thisAEST = findSlot(computation, processor, true);
						if ((thisAEST == Integer.MAX_VALUE) && OnDcp) {
							thisAEST = findSlot(computation, processor, false);
						}
						if (thisAEST != Integer.MAX_VALUE) {
							computation.setOperator(processor);
							System.out.println("childProcessor is: "
									+ criticalChild.getOperator().getId());
							criticalChild.getOperator().getComputations()
									.remove(criticalChild);
							childAEST = findSlot(criticalChild,
									(ProcessorDescriptor) criticalChild
											.getOperator(), true);
							if (childAEST == Integer.MAX_VALUE) {
								childAEST = findSlot(criticalChild,
										(ProcessorDescriptor) criticalChild
												.getOperator(), false);
							}
							System.out.println("childAEST is: " + childAEST);
							criticalChild
									.getOperator()
									.getComputations()
									.add(
											criticalChild
													.getOperator()
													.getComputations()
													.indexOf(
															criticalChild
																	.getNextComputation()),
											criticalChild);
							if (childAEST != Integer.MAX_VALUE) {
								if (childAEST + thisAEST < bestCompositeAEST) {
									bestProcessor = processor;
									bestCompositeAEST = childAEST + thisAEST;
									bestAEST = thisAEST;
								}
							}
						}
					}
				}
			}
		} else {
			System.out.println("no child!");
			while (processorList.size() != 0) {
				processor = processorList.firstElement();
				processorList.remove(0);
				thisAEST = findSlot(computation, processor, true);
				if ((thisAEST == Integer.MAX_VALUE) && OnDcp) {
					thisAEST = findSlot(computation, processor, false);
				}
				if (thisAEST != Integer.MAX_VALUE) {
					if (thisAEST < bestAEST) {
						bestProcessor = processor;
						bestAEST = thisAEST;
					}
				}
			}
		}
		if (bestAEST == Integer.MAX_VALUE) {
			System.out.println("Failure! Use a new processor.");
			thisAEST = findSlot(computation, newProcessor, true);
			bestProcessor = newProcessor;
			bestAEST = thisAEST;
		}
		if (bestProcessor == newProcessor) {
			bestProcessor = new ProcessorDescriptor(processorName
					.concat(Integer.toString(nbProcessorInUse)), processorName,
					architecture.getComponents());
			bestProcessor.addComputation(topComputation);
			bestProcessor.addComputation(bottomComputation);
			processorsInUse.add(bestProcessor);
			nbProcessorInUse++;
		}
		System.out.println("bestProcessor is: " + bestProcessor.getId());
		System.out.println("bestAEST is: " + bestAEST);
		if (mapComputationToProcessor(computation, bestProcessor, OnDcp)) {
			computation.setScheduled();
			unscheduledComputations.remove(computation);
		} else {
			System.out.println("Error! Cannot add computation to processor!");
		}
	}

	public int findSlot(ComputationDescriptor computation,
			ProcessorDescriptor processor, boolean DontPush) {
		int thisAEST = Integer.MAX_VALUE;
		int max = 0;
		int min = 0;

		// System.out.println("\n*****findSlot*****");
		// System.out.println("computation: " + computation.getId() + "
		// processor: "
		// + processor.getId());
		computation.setOperator(processor);
		algorithm.computeTopLevel();
		algorithm.computeBottomLevel();
		if (DontPush) {
			for (int i = 0; i < processor.getComputations().size() - 1; i++) {
				max = computation.getAEST() > processor.getComputations()
						.get(i).getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration() ? computation
						.getAEST() : processor.getComputations().get(i)
						.getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration();
				min = computation.getALST()
						+ computation.getComputationDuration() < processor
						.getComputations().get(i + 1).getAEST() ? computation
						.getALST()
						+ computation.getComputationDuration() : processor
						.getComputations().get(i + 1).getAEST();
				if (min - max >= computation.getComputationDuration()) {
					thisAEST = max;
					break;
				}
			}
		} else {
			for (int i = 0; i < processor.getComputations().size() - 1; i++) {
				max = computation.getAEST() > processor.getComputations()
						.get(i).getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration() ? computation
						.getAEST() : processor.getComputations().get(i)
						.getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration();
				min = computation.getALST()
						+ computation.getComputationDuration() < processor
						.getComputations().get(i + 1).getALST() ? computation
						.getALST()
						+ computation.getComputationDuration() : processor
						.getComputations().get(i + 1).getALST();
				if (min - max >= computation.getComputationDuration()) {
					thisAEST = max;
					break;
				}
			}
		}
		return thisAEST;
	}

	public boolean mapComputationToProcessor(ComputationDescriptor computation,
			ProcessorDescriptor processor, boolean OnDcp) {
		int thisAEST = Integer.MAX_VALUE;
		thisAEST = findSlot(computation, processor, true);
		if (thisAEST != Integer.MAX_VALUE) {
			for (int i = 0; i < processor.getComputations().size() - 1; i++) {
				int max = computation.getAEST() > processor.getComputations()
						.get(i).getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration() ? computation
						.getAEST() : processor.getComputations().get(i)
						.getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration();
				int min = computation.getALST()
						+ computation.getComputationDuration() < processor
						.getComputations().get(i + 1).getAEST() ? computation
						.getALST()
						+ computation.getComputationDuration() : processor
						.getComputations().get(i + 1).getAEST();
				if (min - max >= computation.getComputationDuration()) {
					computation.setNextComputation(processor.getComputations()
							.get(i + 1));
					processor.getComputations().get(i).setNextComputation(
							computation);
					if (!computation
							.isChild(processor.getComputations().get(i))) {
						communicationName = (new String("communication_middle"))
								.concat(Integer.toString(nbCommunicationMiddle));
						new CommunicationDescriptor(communicationName,
								algorithm.getCommunications(), processor
										.getComputations().get(i).getName(),
								computation.getName(), 0);
						computation.addPrecedingCommunication(algorithm
								.getCommunications().get(communicationName));
						processor.getComputations().get(i)
								.addFollowingCommunication(
										algorithm.getCommunications().get(
												communicationName));
						nbCommunicationMiddle++;
						System.out.println(communicationName + " : "
								+ processor.getComputations().get(i).getName()
								+ " -> " + computation.getName());
					}
					if (!processor.getComputations().get(i + 1).isChild(
							computation)) {
						communicationName = (new String("communication_middle"))
								.concat(Integer.toString(nbCommunicationMiddle));
						new CommunicationDescriptor(communicationName,
								algorithm.getCommunications(), computation
										.getName(), processor.getComputations()
										.get(i + 1).getName(), 0);
						computation.addFollowingCommunication(algorithm
								.getCommunications().get(communicationName));
						processor.getComputations().get(i + 1)
								.addPrecedingCommunication(
										algorithm.getCommunications().get(
												communicationName));
						nbCommunicationMiddle++;
						System.out.println(communicationName
								+ " : "
								+ computation.getName()
								+ " -> "
								+ processor.getComputations().get(i + 1)
										.getName());
					}
					computation.setOperator(processor);
					processor.getComputations().add(i + 1, computation);
					break;
				}
			}
		} else if (OnDcp) {
			thisAEST = findSlot(computation, processor, false);
			for (int i = 0; i < processor.getComputations().size() - 1; i++) {
				int max = computation.getAEST() > processor.getComputations()
						.get(i).getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration() ? computation
						.getAEST() : processor.getComputations().get(i)
						.getAEST()
						+ processor.getComputations().get(i)
								.getComputationDuration();
				int min = computation.getALST()
						+ computation.getComputationDuration() < processor
						.getComputations().get(i + 1).getALST() ? computation
						.getALST()
						+ computation.getComputationDuration() : processor
						.getComputations().get(i + 1).getALST();
				if (min - max >= computation.getComputationDuration()) {
					computation.setNextComputation(processor.getComputations()
							.get(i + 1));
					processor.getComputations().get(i).setNextComputation(
							computation);
					if (!computation
							.isChild(processor.getComputations().get(i))) {
						communicationName = (new String("communication_middle"))
								.concat(Integer.toString(nbCommunicationMiddle));
						new CommunicationDescriptor(communicationName,
								algorithm.getCommunications(), processor
										.getComputations().get(i).getName(),
								computation.getName(), 0);
						computation.addPrecedingCommunication(algorithm
								.getCommunications().get(communicationName));
						processor.getComputations().get(i)
								.addFollowingCommunication(
										algorithm.getCommunications().get(
												communicationName));
						nbCommunicationMiddle++;
					}
					if (!processor.getComputations().get(i + 1).isChild(
							computation)) {
						communicationName = (new String("communication_middle"))
								.concat(Integer.toString(nbCommunicationMiddle));
						new CommunicationDescriptor(communicationName,
								algorithm.getCommunications(), computation
										.getName(), processor.getComputations()
										.get(i + 1).getName(), 0);
						computation.addFollowingCommunication(algorithm
								.getCommunications().get(communicationName));
						processor.getComputations().get(i + 1)
								.addPrecedingCommunication(
										algorithm.getCommunications().get(
												communicationName));
						nbCommunicationMiddle++;
					}
					computation.setOperator(processor);
					processor.getComputations().add(i + 1, computation);
					break;
				}
			}
		}
		if (thisAEST != Integer.MAX_VALUE) {
			return true;
		} else {
			return false;
		}
	}

	public Vector<ProcessorDescriptor> getProcessorsInUse() {
		return processorsInUse;
	}

	public int getNbProcessorInUse() {
		return nbProcessorInUse;
	}

}
