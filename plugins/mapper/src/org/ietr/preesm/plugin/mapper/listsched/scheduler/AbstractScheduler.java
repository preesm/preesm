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

import java.util.HashMap;
import java.util.Vector;

import org.ietr.preesm.plugin.mapper.listsched.descriptor.AlgorithmDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ArchitectureDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.CommunicationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.ComputationDescriptor;
import org.ietr.preesm.plugin.mapper.listsched.descriptor.OperatorDescriptor;

/**
 * This is an abstract class for all the communication contentious list
 * scheduling methods.
 * 
 * @author pmu
 */
public abstract class AbstractScheduler {

	/**
	 * Scheduler name
	 */
	protected String name = "Abstract Scheduler";

	/**
	 * Sorted computations
	 */
	protected Vector<ComputationDescriptor> schedulingOrder = new Vector<ComputationDescriptor>();

	/**
	 * Computation buffer
	 */
	protected HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer;

	/**
	 * Communication buffer
	 */
	protected HashMap<String, CommunicationDescriptor> CommunicationDescriptorBuffer;

	/**
	 * Algorithm descriptor
	 */
	protected AlgorithmDescriptor algorithm;

	/**
	 * Architecture descriptor
	 */
	protected ArchitectureDescriptor architecture;

	/**
	 * The virtual top communication
	 */
	protected CommunicationDescriptor topCommunication;

	/**
	 * The virtual bottom communication
	 */
	protected CommunicationDescriptor bottomCommunication;

	/**
	 * The virtual top computation
	 */
	protected ComputationDescriptor topComputation;

	/**
	 * The virtual bottom computation
	 */
	protected ComputationDescriptor bottomComputation;

	/**
	 * Schedule length
	 */
	protected int scheduleLength = 0;

	/**
	 * All the used operators
	 */
	protected Vector<OperatorDescriptor> usedOperators = null;

	/**
	 * The name for additional communications
	 */
	protected String communicationName = null;

	/**
	 * Number of added communications out of top computation
	 */
	protected int nbCommunicationIn = 0;

	/**
	 * Number of added communications into bottom computation
	 */
	protected int nbCommunicationOut = 0;

	/**
	 * All the unscheduled computations
	 */
	protected Vector<ComputationDescriptor> unscheduledComputations = null;

	/**
	 * Constructs a scheduler with an algorithm descriptor.
	 * 
	 * @param algorithm
	 *            An algorithm descriptor
	 */
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

	/**
	 * Constructs a scheduler with computation and communication buffer.
	 * 
	 * @param ComputationDescriptorBuffer
	 *            Computation buffer
	 * @param CommunicationDescriptorBuffer
	 *            Communication buffer
	 */
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

	/**
	 * Adds an operator to the used operators.
	 * 
	 * @param operator
	 *            An operator
	 */
	public void addUsedOperators(OperatorDescriptor operator) {
		usedOperators.add(operator);
	}

	/**
	 * Backup all the times before the scheduling of the critical child.
	 */
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

	/**
	 * Gets the algorithm.
	 * 
	 * @return The algorithm
	 */
	public AlgorithmDescriptor getAlgorithm() {
		return algorithm;
	}

	/**
	 * Gets the architecture.
	 * 
	 * @return The architecture
	 */
	public ArchitectureDescriptor getArchitecture() {
		return architecture;
	}

	/**
	 * Gets the virtual bottom communication.
	 * 
	 * @return The virtual bottom communication
	 */
	public CommunicationDescriptor getBottomCommunication() {
		return bottomCommunication;
	}

	/**
	 * Gets the virtual bottom computation.
	 * 
	 * @return The virtual bottom computation
	 */
	public ComputationDescriptor getBottomComputation() {
		return bottomComputation;
	}

	/**
	 * Gets the name of the scheduler.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the schedule length.
	 * 
	 * @return The schedule length
	 */
	public int getScheduleLength() {
		return scheduleLength;
	}

	/**
	 * Gets the sorted computation list.
	 * 
	 * @return The sorted computation list
	 */
	public Vector<ComputationDescriptor> getSchedulingOrder() {
		return schedulingOrder;
	}

	/**
	 * Gets the virtual top communication.
	 * 
	 * @return The virtual top communication
	 */
	public CommunicationDescriptor getTopCommunication() {
		return topCommunication;
	}

	/**
	 * Gets the virtual top computation.
	 * 
	 * @return The virtual top computation
	 */
	public ComputationDescriptor getTopComputation() {
		return topComputation;
	}

	/**
	 * Gets all the used operators.
	 * 
	 * @return All the used operators
	 */
	public Vector<OperatorDescriptor> getUsedOperators() {
		return usedOperators;
	}

	/**
	 * Calculate the max of two integers.
	 * 
	 * @param a
	 *            First integer
	 * @param b
	 *            Second integer
	 * @return The max of two integers
	 */
	protected int max(int a, int b) {
		return a < b ? b : a;
	}

	/**
	 * Calculate the max of three integers.
	 * 
	 * @param a
	 *            First integer
	 * @param b
	 *            Second integer
	 * @param c
	 *            Third integer
	 * @return The max of three integers
	 */
	protected int max(int a, int b, int c) {
		int tmp = a < b ? b : a;
		return c < tmp ? tmp : c;
	}

	/**
	 * Calculate the min of two integers.
	 * 
	 * @param a
	 *            First integer
	 * @param b
	 *            Second integer
	 * @return The min of two integers
	 */
	protected int min(int a, int b) {
		return a > b ? b : a;
	}

	/**
	 * Calculate the min of three integers.
	 * 
	 * @param a
	 *            First integer
	 * @param b
	 *            Second integer
	 * @param c
	 *            Third integer
	 * @return The min of three integers
	 */
	protected int min(int a, int b, int c) {
		int tmp = a > b ? b : a;
		return c > tmp ? tmp : c;
	}

	/**
	 * Recover all the times after the scheduling of the critical child.
	 */
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

	/**
	 * Restore all the times when finishing the selecting processor.
	 */
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

	/**
	 * Does the scheduling.
	 * 
	 * @return true
	 */
	public abstract boolean schedule();

	/**
	 * Sets the algorithm.
	 * 
	 * @param algorithm
	 *            The algorithm
	 */
	public void setAlgorithm(AlgorithmDescriptor algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Sets the architecture.
	 * 
	 * @param architecture
	 *            The architecture
	 */
	public void setArchitecture(ArchitectureDescriptor architecture) {
		this.architecture = architecture;
	}

	/**
	 * Update all the times when finishing the real scheduling of a node.
	 */
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

}
