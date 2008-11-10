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
 
package org.ietr.preesm.plugin.mapper.commcontenlistsched.scheduler;

import java.util.HashMap;
import java.util.Vector;

import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor.OperatorDescriptor;

/**
 * This is an abstract class for all the communication contentious list
 * scheduling methods.
 * 
 * @author pmu
 */
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

			if (indexComputation.getInputCommunications().isEmpty()
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
						.addOutputCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				indexComputation
						.addInputCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				nbCommunicationIn++;
				System.out.println(communicationName + " : topComputation -> "
						+ indexComputation.getName());
			}
			if (indexComputation.getOutputCommunications().isEmpty()
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
						.addInputCommunication(CommunicationDescriptorBuffer
								.get(communicationName));
				indexComputation
						.addOutputCommunication(CommunicationDescriptorBuffer
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
			if (indexComputation.getInputCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = new String("communication_in")
						.concat(Integer.toString(nbCommunicationIn));
				new CommunicationDescriptor(communicationName, topComputation
						.getName(), indexComputation.getName(), 0, algorithm);
				algorithm.addCommunication(algorithm
						.getCommunication(communicationName));
				algorithm.getCommunication(communicationName).clearExist();
				topComputation.addOutputCommunication(algorithm
						.getCommunication(communicationName));
				indexComputation.addInputCommunication(algorithm
						.getCommunication(communicationName));
				nbCommunicationIn++;
				System.out.println(communicationName + " : "
						+ topComputation.getName() + " -> "
						+ indexComputation.getName());
			}
			if (indexComputation.getOutputCommunications().isEmpty()
					&& indexComputation != topComputation
					&& indexComputation != bottomComputation) {
				communicationName = (new String("communication_out"))
						.concat(Integer.toString(nbCommunicationOut));
				new CommunicationDescriptor(communicationName, indexComputation
						.getName(), bottomComputation.getName(), 0, algorithm);
				algorithm.getCommunication(communicationName).clearExist();
				algorithm.addCommunication(algorithm
						.getCommunication(communicationName));
				bottomComputation.addInputCommunication(algorithm
						.getCommunication(communicationName));
				indexComputation.addOutputCommunication(algorithm
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
				.getOutputCommunications()) {
			computations.add(ComputationDescriptorBuffer.get(indexCommunication
					.getDestination()));
		}

		for (int i = 0; i < computations.size(); i++) {
			int time = 0;
			boolean skipComputation = false;
			for (CommunicationDescriptor indexCommunication : computations.get(
					i).getInputCommunications()) {
				if (!ComputationDescriptorBuffer.get(
						indexCommunication.getOrigin()).isReady()) {
					skipComputation = true;
					break;
				} else if (time < ComputationDescriptorBuffer.get(
						indexCommunication.getOrigin()).getTopLevel()
						+ ComputationDescriptorBuffer.get(
								indexCommunication.getOrigin())
								.getComputationDuration()
						+ indexCommunication.getCommunicationDuration()) {
					time = ComputationDescriptorBuffer.get(
							indexCommunication.getOrigin()).getTopLevel()
							+ ComputationDescriptorBuffer.get(
									indexCommunication.getOrigin())
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
						.get(i).getOutputCommunications()) {
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
				.getInputCommunications()) {
			if (time < ComputationDescriptorBuffer.get(
					indexCommunication.getOrigin()).getTopLevel()
					+ ComputationDescriptorBuffer.get(
							indexCommunication.getOrigin())
							.getComputationDuration()) {
				time = ComputationDescriptorBuffer.get(
						indexCommunication.getOrigin()).getTopLevel()
						+ ComputationDescriptorBuffer.get(
								indexCommunication.getOrigin())
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
				.getInputCommunications()) {
			computations.add(ComputationDescriptorBuffer.get(indexCommunication
					.getOrigin()));
		}

		for (int i = 0; i < computations.size(); i++) {
			int time = 0;
			boolean skipComputation = false;
			for (CommunicationDescriptor indexCommunication : computations.get(
					i).getOutputCommunications()) {
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
						.get(i).getInputCommunications()) {
					if (ComputationDescriptorBuffer.get(indexCommunication
							.getOrigin()) != topComputation) {
						computations.add(ComputationDescriptorBuffer
								.get(indexCommunication.getOrigin()));
					}
				}
			}
		}
		int time = 0;
		for (CommunicationDescriptor indexCommunication : topComputation
				.getOutputCommunications()) {
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
						.getInputCommunications()) {
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
						.getInputCommunications()) {
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
						.getInputCommunications()) {
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
						.getInputCommunications()) {
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
