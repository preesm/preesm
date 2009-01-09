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
package org.ietr.preesm.plugin.mapper.listsched.descriptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.types.DAGVertexPropertyType;

/**
 * This class is a descriptor of computation that provides necessary parameters.
 * 
 * @author pmu
 * 
 */
public class ComputationDescriptor extends OperationDescriptor implements
		Comparable<ComputationDescriptor> {

	/**
	 * The DAGVertex associated with the computation
	 */
	private DAGVertex vertex = null;

	/**
	 * Total repeat number
	 */
	private int nbTotalRepeat = 1;

	/**
	 * All the input communications
	 */
	private Vector<CommunicationDescriptor> inputCommunications;

	/**
	 * All the output communications
	 */
	private Vector<CommunicationDescriptor> outputCommunications;

	/**
	 * Computation buffer
	 */
	private HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer = null;

	/**
	 * Operator to which the computation is scheduled
	 */
	private OperatorDescriptor operator = null;

	/**
	 * All the operators that can execute this computation
	 */
	private Set<String> operatorSet = null;

	/**
	 * Computation durations on different operators
	 */
	private HashMap<String, Integer> computationDurations;

	/**
	 * The computation is ready if and only if all its predecessors or
	 * successors are ready
	 */
	private boolean ready = false;

	/**
	 * Average repeat number for an executing instance
	 */
	private int nbAverageRepeat;

	/**
	 * Data Ready Time
	 */
	private int dataReadyTime = 0;

	/**
	 * Actual start time
	 */
	private int startTime = 0;

	/**
	 * Actual finish time
	 */
	private int finishTime = 0;

	/**
	 * The start time before the step of select processor
	 */
	private int oldStartTime = 0;

	/**
	 * The finish time before the step of select processor
	 */
	private int oldFinishTime = 0;

	/**
	 * A temporary start time during the step of select processor
	 */
	private int temporaryStartTime = 0;

	/**
	 * A temporary finish time during the step of select processor
	 */
	private int temporaryFinishTime = 0;

	/**
	 * The computation duration
	 */
	private int computationDuration = 0;

	/**
	 * The minimum computation duration
	 */
	private int minComputationDuration = 0;

	/**
	 * The next computation in the same processor
	 */
	private ComputationDescriptor nextComputation = null;

	/**
	 * Unscheduled input communications
	 */
	private Vector<CommunicationDescriptor> unscheduledInputCommunications = null;

	/**
	 * Unscheduled output communications
	 */
	private Vector<CommunicationDescriptor> unscheduledOutputCommunications = null;

	/**
	 * Top level
	 */
	private int topLevel = 0;

	/**
	 * Bottom level
	 */
	private int bottomLevel = 0;

	/**
	 * Computation top level
	 */
	private int topLevelComputation = 0;

	/**
	 * Computation bottom level
	 */
	private int bottomLevelComputation = 0;

	/**
	 * Input top level
	 */
	private int topLevelIn = 0;

	/**
	 * Input bottom level
	 */
	private int bottomLevelIn = 0;

	/**
	 * Output top level
	 */
	private int topLevelOut = 0;

	/**
	 * Output bottom level
	 */
	private int bottomLevelOut = 0;

	/**
	 * Input/output top level
	 */
	private int topLevelInOut = 0;

	/**
	 * Input/output bottom level
	 */
	private int bottomLevelInOut = 0;

	/**
	 * Constructs a new ComputationDescriptor with the identifier id and
	 * algorithm.
	 * 
	 * @param id
	 *            Computation Id
	 * @param algorithm
	 *            Algorithm
	 */
	public ComputationDescriptor(String id, AlgorithmDescriptor algorithm) {
		super(id);
		vertex = new DAGVertex();
		vertex.setName(id);
		vertex.setNbRepeat(new DAGVertexPropertyType(1));
		vertex.setTime(new DAGVertexPropertyType(0));
		this.algorithm = algorithm;
		ComputationDescriptorBuffer = algorithm.getComputations();
		ComputationDescriptorBuffer.put(this.name, this);
		operatorSet = new HashSet<String>();
		inputCommunications = new Vector<CommunicationDescriptor>();
		outputCommunications = new Vector<CommunicationDescriptor>();
		computationDurations = new HashMap<String, Integer>();
		unscheduledInputCommunications = new Vector<CommunicationDescriptor>();
		unscheduledOutputCommunications = new Vector<CommunicationDescriptor>();
		this.type = OperationType.Computation;
	}

	/**
	 * Constructs a new ComputationDescriptor with the identifier id and buffer.
	 * 
	 * @param id
	 *            Computation Id
	 * @param ComputationDescriptorBuffer
	 *            Computation buffer
	 */
	public ComputationDescriptor(String id,
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer) {
		super(id);
		vertex = new DAGVertex();
		vertex.setName(id);
		vertex.setNbRepeat(new DAGVertexPropertyType(1));
		vertex.setTime(new DAGVertexPropertyType(0));
		operatorSet = new HashSet<String>();
		inputCommunications = new Vector<CommunicationDescriptor>();
		outputCommunications = new Vector<CommunicationDescriptor>();
		computationDurations = new HashMap<String, Integer>();
		ComputationDescriptorBuffer.put(this.name, this);
		this.ComputationDescriptorBuffer = ComputationDescriptorBuffer;
		unscheduledInputCommunications = new Vector<CommunicationDescriptor>();
		unscheduledOutputCommunications = new Vector<CommunicationDescriptor>();
		this.type = OperationType.Computation;
	}

	/**
	 * Adds the computation duration for a specified operator.
	 * 
	 * @param operator
	 *            An operator
	 * @param time
	 *            The corresponding computation duration
	 */
	public void addComputationDuration(OperatorDescriptor operator, int time) {
		if (!this.computationDurations.containsKey(operator.getName())) {
			this.computationDurations.put(operator.getName(), time);
		}
	}

	/**
	 * Adds the computation duration for a specified operator name.
	 * 
	 * @param operatorName
	 *            An operator name
	 * @param time
	 *            The corresponding computation duration
	 */
	public void addComputationDuration(String operatorName, int time) {
		if (!this.computationDurations.containsKey(operatorName)) {
			this.computationDurations.put(operatorName, time);
		}
	}

	/**
	 * Adds an input communication.
	 * 
	 * @param communication
	 *            Input communication
	 */
	public void addInputCommunication(CommunicationDescriptor communication) {
		inputCommunications.add(communication);
		unscheduledInputCommunications.add(communication);
	}

	/**
	 * Adds an operator to the operator set.
	 * 
	 * @param operator
	 *            An operator
	 */
	public void addOperator(OperatorDescriptor operator) {
		operatorSet.add(operator.getId());
	}

	/**
	 * Adds an operator to the operator set by its Id.
	 * 
	 * @param operatorId
	 *            Operator Id
	 */
	public void addOperator(String operatorId) {
		operatorSet.add(operatorId);
	}

	/**
	 * Adds an output communication.
	 * 
	 * @param communication
	 *            An output communication
	 */
	public void addOutputCommunication(CommunicationDescriptor communication) {
		outputCommunications.add(communication);
		unscheduledOutputCommunications.add(communication);
	}

	/**
	 * Adds a communication as unscheduled input communication.
	 * 
	 * @param unscheduledCommunication
	 *            Added communication
	 */
	public void addUnscheduledInputCommunication(
			CommunicationDescriptor unscheduledCommunication) {
		unscheduledInputCommunications.add(unscheduledCommunication);
	}

	/**
	 * Adds a communication as unscheduled output communication.
	 * 
	 * @param unscheduledCommunication
	 *            Added communication
	 */
	public void addUnscheduledOutputCommunication(
			CommunicationDescriptor unscheduledCommunication) {
		unscheduledOutputCommunications.add(unscheduledCommunication);
	}

	/**
	 * Backups all the times before the scheduling of the critical child.
	 */
	public void backupTimes() {
		temporaryStartTime = startTime;
		temporaryFinishTime = finishTime;
	}

	/**
	 * Clear the mark of ready.
	 */
	public void clearReady() {
		ready = false;
	}

	public int compareTo(ComputationDescriptor computation) {
		return -((topLevel + bottomLevel) - (computation.getTopLevel() + computation
				.getBottomLevel()));
	}

	/**
	 * Gets the bottom level of this computation.
	 * 
	 * @return The bottom level of this computation
	 */
	public int getBottomLevel() {
		return bottomLevel;
	}

	/**
	 * Gets the computation bottom level of this computation.
	 * 
	 * @return The computation bottom level of this computation
	 */
	public int getBottomLevelComputation() {
		return bottomLevelComputation;
	}

	/**
	 * Gets the input bottom level of this computation.
	 * 
	 * @return The input bottom level of this computation
	 */
	public int getBottomLevelIn() {
		return bottomLevelIn;
	}

	/**
	 * Gets the input/output bottom level of this computation.
	 * 
	 * @return The input/output bottom level of this computation
	 */
	public int getBottomLevelInOut() {
		return bottomLevelInOut;
	}

	/**
	 * Gets the output bottom level of this computation.
	 * 
	 * @return The output bottom level of this computation
	 */
	public int getBottomLevelOut() {
		return bottomLevelOut;
	}

	/**
	 * Gets the computation buffer containing all the computations.
	 * 
	 * @return The computation buffer
	 */
	public HashMap<String, ComputationDescriptor> getComputationDescriptorBuffer() {
		return ComputationDescriptorBuffer;
	}

	/**
	 * Gets the actual computation duration.
	 * 
	 * @return The actual computation duration
	 */
	public int getComputationDuration() {
		setComputationDuration();
		return computationDuration;
	}

	/**
	 * Gets the computation duration for a specified operator name.
	 * 
	 * @param operatorName
	 *            An operator name
	 * @return The corresponding computation duration
	 */
	public int getComputationDuration(String operatorName) {
		if (computationDurations.containsKey(operatorName)) {
			return computationDurations.get(operatorName);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Gets all the computation durations.
	 * 
	 * @return A HashMap containing all the computation durations
	 */
	public HashMap<String, Integer> getComputationDurations() {
		return computationDurations;
	}

	/**
	 * Gets the Data Ready Time.
	 * 
	 * @return Data Ready Time
	 */
	public int getDataReadyTime() {
		return dataReadyTime;
	}

	/**
	 * Gets the finish time.
	 * 
	 * @return Finish time
	 */
	public int getFinishTime() {
		finishTime = startTime + computationDuration;
		return finishTime;
	}

	/**
	 * Gets all the input communications.
	 * 
	 * @return All the input communications
	 */
	public Vector<CommunicationDescriptor> getInputCommunications() {
		return inputCommunications;
	}

	/**
	 * Gets the minimum computation duration.
	 * 
	 * @return
	 */
	public int getMinComputationDuration() {
		setMinComputationDuration();
		return minComputationDuration;
	}

	/**
	 * Gets the average number of repeat.
	 * 
	 * @return The average number of repeat
	 */
	public int getNbAverageRepeat() {
		return nbAverageRepeat;
	}

	/**
	 * Gets the total repeat number.
	 * 
	 * @return The total repeat number
	 */
	public int getNbTotalRepeat() {
		return vertex.getNbRepeat().intValue();
	}

	/**
	 * Gets the computation next to this one on the same operator.
	 * 
	 * @return The next computation
	 */
	public ComputationDescriptor getNextComputation() {
		return nextComputation;
	}

	/**
	 * Gets the actual operator.
	 * 
	 * @return
	 */
	public OperatorDescriptor getOperator() {
		return operator;
	}

	/**
	 * Gets the set of operators that can execute this computation.
	 * 
	 * @return The set of operators that can execute this computation
	 */
	public Set<String> getOperatorSet() {
		return operatorSet;
	}

	/**
	 * Gets all the output communications.
	 * 
	 * @return All the output communications
	 */
	public Vector<CommunicationDescriptor> getOutputCommunications() {
		return outputCommunications;
	}

	/**
	 * Gets the start time.
	 * 
	 * @return Start time
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Gets vertex time cost.
	 * 
	 * @return Time cost
	 */
	public int getTime() {
		return vertex.getTime().intValue();
	}

	/**
	 * Gets the top level of this computation.
	 * 
	 * @return The top level of this computation
	 */
	public int getTopLevel() {
		return topLevel;
	}

	/**
	 * Gets the computation top level of this computation.
	 * 
	 * @return The computation top level of this computation
	 */
	public int getTopLevelComputation() {
		return topLevelComputation;
	}

	/**
	 * Gets the input top level of this computation.
	 * 
	 * @return The input top level of this computation
	 */
	public int getTopLevelIn() {
		return topLevelIn;
	}

	/**
	 * Gets the input/output top level of this computation.
	 * 
	 * @return The input/output top level of this computation
	 */
	public int getTopLevelInOut() {
		return topLevelInOut;
	}

	/**
	 * Gets the output top level of this computation.
	 * 
	 * @return The output top level of this computation
	 */
	public int getTopLevelOut() {
		return topLevelOut;
	}

	/**
	 * Gets the computation duration for a specified operator.
	 * 
	 * @param operator
	 *            An operator
	 * @return The corresponding computation duration
	 */
	public int getTotalComputationDuration(OperatorDescriptor operator) {
		int duration = 0;
		if (computationDurations.containsKey(operator.getName())) {
			if (operator.getType() == ComponentType.Ip) {
				double op1 = new Integer(nbTotalRepeat).doubleValue();
				double op2 = new Integer(((IpDescriptor) operator)
						.getNbInstance()).doubleValue();
				nbAverageRepeat = (int) (Math.ceil(op1 / op2));
				duration = nbAverageRepeat
						* (((IpDescriptor) operator).getCommunicationDuration()
								+ ((IpDescriptor) operator)
										.getExecutionDuration() - ((IpDescriptor) operator)
								.getNbInstance()
								* ((IpDescriptor) operator)
										.getCommunicationDuration())
						+ (nbTotalRepeat
								+ ((IpDescriptor) operator).getNbInstance() - 1)
						* ((IpDescriptor) operator).getCommunicationDuration();
			} else if (operator.getType() == ComponentType.Processor) {
				duration = nbTotalRepeat
						* computationDurations.get(operator.getName());
			}
			return duration;
			// return computationDurations.get(operator.getName());
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Gets the unscheduled input communication at a specified position.
	 * 
	 * @param index
	 *            The specified position
	 * @return The requested input communication.
	 */
	public CommunicationDescriptor getUnscheduledInputCommunication(int index) {
		return unscheduledInputCommunications.get(index);
	}

	/**
	 * Gets all the unscheduled input communication.
	 * 
	 * @return All the unscheduled input communication
	 */
	public Vector<CommunicationDescriptor> getUnscheduledInputCommunications() {
		return unscheduledInputCommunications;
	}

	/**
	 * Gets the unscheduled output communication at a specified position.
	 * 
	 * @param index
	 *            The specified position
	 * @return The requested output communication.
	 */
	public CommunicationDescriptor getUnscheduledOutputCommunication(int index) {
		return unscheduledOutputCommunications.get(index);
	}

	/**
	 * Gets all the unscheduled output communication.
	 * 
	 * @return All the unscheduled input communication
	 */
	public Vector<CommunicationDescriptor> getUnscheduledOutputCommunications() {
		return unscheduledOutputCommunications;
	}

	/**
	 * Gets the associated DAGVertex.
	 * 
	 * @return The associated DAGVertex
	 */
	public DAGVertex getVertex() {
		return vertex;
	}

	/**
	 * Is this computation an ancestor of the given computation.
	 * 
	 * @param computation
	 *            A given computation
	 * @return true for yes; false for no
	 */
	public boolean isAncestor(ComputationDescriptor computation) {
		boolean result = false;
		Vector<ComputationDescriptor> computations = new Vector<ComputationDescriptor>();

		computations.add(this);
		for (int i = 0; i < computations.size(); i++) {
			for (CommunicationDescriptor edgeIndex : computations.get(i)
					.getOutputCommunications()) {
				if (edgeIndex.getDestination().equals(computation.getName())) {
					result = true;
					break;
				} else {
					computations.add(ComputationDescriptorBuffer.get(edgeIndex
							.getDestination()));
				}
			}
			if (result) {
				break;
			}
		}
		return result;
	}

	/**
	 * Is this computation a child of the given computation.
	 * 
	 * @param computation
	 *            A given computation
	 * @return true for yes; false for no
	 */
	public boolean isChild(ComputationDescriptor computation) {
		boolean result = false;

		for (CommunicationDescriptor edgeIndex : computation
				.getOutputCommunications()) {
			if (edgeIndex.getDestination().equals(this.getName())) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * Is this computation a descendant of the given computation.
	 * 
	 * @param computation
	 *            A given computation
	 * @return true for yes; false for no
	 */
	public boolean isDescendant(ComputationDescriptor computation) {
		boolean result = false;
		Vector<ComputationDescriptor> computations = new Vector<ComputationDescriptor>();

		computations.add(this);
		for (int i = 0; i < computations.size(); i++) {
			for (CommunicationDescriptor edgeIndex : computations.get(i)
					.getInputCommunications()) {
				if (edgeIndex.getOrigin().equals(computation.getName())) {
					result = true;
					break;
				} else {
					computations.add(ComputationDescriptorBuffer.get(edgeIndex
							.getOrigin()));
				}
			}
			if (result) {
				break;
			}
		}
		return result;
	}

	/**
	 * Is this computation ready?
	 * 
	 * @return true for yes; false for no
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * Recovers all the times after the scheduling of the critical child.
	 */
	public void recoverTimes() {
		startTime = temporaryStartTime;
		finishTime = temporaryFinishTime;
	}

	/**
	 * Restores all the times when finishing the selecting processor.
	 */
	public void restoreTimes() {
		startTime = oldStartTime;
		finishTime = oldFinishTime;
	}

	/**
	 * Sets the bottom level of this computation.
	 * 
	 * @param bottomLevel
	 *            The bottom level of this computation
	 */
	public void setBottomLevel(int bottomLevel) {
		this.bottomLevel = bottomLevel;
	}

	/**
	 * Sets the computation bottom level of this computation.
	 * 
	 * @param bottomLevelComputation
	 *            The computation bottom level of this computation
	 */
	public void setBottomLevelComputation(int bottomLevelComputation) {
		this.bottomLevelComputation = bottomLevelComputation;
	}

	/**
	 * Sets the input bottom level of this computation.
	 * 
	 * @param bottomLevelIn
	 *            The input bottom level of this computation
	 */
	public void setBottomLevelIn(int bottomLevelIn) {
		this.bottomLevelIn = bottomLevelIn;
	}

	/**
	 * Sets the input/output bottom level of this computation.
	 * 
	 * @param bottomLevelInOut
	 *            The input/output bottom level of this computation
	 */
	public void setBottomLevelInOut(int bottomLevelInOut) {
		this.bottomLevelInOut = bottomLevelInOut;
	}

	/**
	 * Sets the output bottom level of this computation.
	 * 
	 * @param bottomLevelOut
	 *            The output bottom level of this computation
	 */
	public void setBottomLevelOut(int bottomLevelOut) {
		this.bottomLevelOut = bottomLevelOut;
	}

	/**
	 * Sets the computation buffer containing all the computations.
	 * 
	 * @param ComputationDescriptorBuffer
	 *            The computation buffer
	 */
	public void setComputationDescriptorBuffer(
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer) {
		this.ComputationDescriptorBuffer = ComputationDescriptorBuffer;
	}

	/**
	 * Sets computation duration with the internal behavior model.
	 */
	private void setComputationDuration() {
		if (operator != null) {
			if (operator.getType() == ComponentType.Ip) {
				double op1 = new Integer(nbTotalRepeat).doubleValue();
				double op2 = new Integer(((IpDescriptor) operator)
						.getNbInstance()).doubleValue();
				nbAverageRepeat = (int) (Math.ceil(op1 / op2));
				computationDuration = nbAverageRepeat
						* (((IpDescriptor) operator).getCommunicationDuration()
								+ ((IpDescriptor) operator)
										.getExecutionDuration() - ((IpDescriptor) operator)
								.getNbInstance()
								* ((IpDescriptor) operator)
										.getCommunicationDuration())
						+ (nbTotalRepeat
								+ ((IpDescriptor) operator).getNbInstance() - 1)
						* ((IpDescriptor) operator).getCommunicationDuration();
			} else if (operator.getType() == ComponentType.Processor) {
				computationDuration = nbTotalRepeat
						* computationDurations.get(operator.getName());
			}
		} else {
			computationDuration = nbTotalRepeat * vertex.getTime().intValue();
		}
	}

	/**
	 * Sets the Data Ready Time.
	 * 
	 * @param time
	 *            Data Ready Time
	 */
	public void setDataReadyTime(int time) {
		dataReadyTime = time;
	}

	/**
	 * Sets up the finish time.
	 */
	public void setFinishTime() {
		setComputationDuration();
		finishTime = startTime + computationDuration;
	}

	/**
	 * Sets the finish time
	 * 
	 * @param i
	 *            Finish time
	 */
	public void setFinishTime(int i) {
		finishTime = i;
		computationDuration = finishTime - startTime;
	}

	/**
	 * Sets the minimum computation duration with the internal behavior model.
	 */
	private void setMinComputationDuration() {
		double op1 = new Integer(nbTotalRepeat).doubleValue();
		double op2 = new Integer(((IpDescriptor) operator).getMaxNbInstance())
				.doubleValue();
		int minNbAverageRepeat = (int) (Math.ceil(op1 / op2));
		minComputationDuration = minNbAverageRepeat
				* (((IpDescriptor) operator).getCommunicationDuration()
						+ ((IpDescriptor) operator).getExecutionDuration() - ((IpDescriptor) operator)
						.getMaxNbInstance()
						* ((IpDescriptor) operator).getCommunicationDuration())
				+ (nbTotalRepeat + ((IpDescriptor) operator).getMaxNbInstance() - 1)
				* ((IpDescriptor) operator).getCommunicationDuration();
	}

	/**
	 * Sets the average number of repeat.
	 * 
	 * @param n
	 *            The average number of repeat
	 */
	public void setNbAverageRepeat(int n) {
		nbAverageRepeat = n;
	}

	/**
	 * Sets the total repeat number.
	 * 
	 * @param n
	 *            The total repeat number
	 */
	public void setNbTotalRepeat(int n) {
		nbTotalRepeat = n;
		if (vertex.getNbRepeat() == null) {
			vertex.setNbRepeat(new DAGVertexPropertyType(n));
		} else {
			((DAGVertexPropertyType) vertex.getNbRepeat()).setValue(n);
		}
	}

	/**
	 * Sets the computation next to this one on the same operator.
	 * 
	 * @param computation
	 *            The next computation
	 */
	public void setNextComputation(ComputationDescriptor computation) {
		nextComputation = computation;
	}

	/**
	 * Sets operator for this computation.
	 * 
	 * @param operator
	 *            An operator
	 */
	public void setOperator(OperatorDescriptor operator) {
		if (operator == null) {
			this.operator = null;
		} else {
			this.operator = operator;
			for (CommunicationDescriptor indexCommunication : inputCommunications) {
				if (ComputationDescriptorBuffer != null) {
					if (ComputationDescriptorBuffer.get(
							indexCommunication.getOrigin()).getOperator() == operator) {
						indexCommunication.clearExist();
					} else {
						if (indexCommunication.getWeight() == 0) {
							indexCommunication.clearExist();
						} else {
							indexCommunication.setExist();
						}
					}
				} else {
					if (algorithm
							.getComputation(indexCommunication.getOrigin())
							.getOperator() == operator) {
						indexCommunication.clearExist();
					} else {
						if (indexCommunication.getWeight() == 0) {
							indexCommunication.clearExist();
						} else {
							indexCommunication.setExist();
						}
					}
				}
			}
			for (CommunicationDescriptor indexCommunication : outputCommunications) {
				if (ComputationDescriptorBuffer != null) {
					if (ComputationDescriptorBuffer.get(
							indexCommunication.getDestination()).getOperator() == operator) {
						indexCommunication.clearExist();
					} else {
						indexCommunication.setExist();
					}
				} else {
					if (algorithm.getComputation(
							indexCommunication.getDestination()).getOperator() == operator) {
						indexCommunication.clearExist();
					} else {
						indexCommunication.setExist();
					}
				}
			}
		}
	}

	/**
	 * Sets the set of operators that can execute this computation.
	 * 
	 * @param operators
	 *            A set of operators
	 */
	public void setOperatorSet(Set<String> operators) {
		operatorSet = operators;
	}

	/**
	 * Mark this computation as ready.
	 */
	public void setReady() {
		ready = true;
	}

	/**
	 * Sets the start time.
	 * 
	 * @param time
	 *            Start time
	 */
	public void setStartTime(int time) {
		this.startTime = time;
	}

	/**
	 * Sets vertex time cost.
	 * 
	 * @param time
	 *            Time cost
	 */
	public void setTime(int time) {
		if (vertex.getTime() == null) {
			vertex.setTime(new DAGVertexPropertyType(time));
		} else {
			((DAGVertexPropertyType) vertex.getTime()).setValue(time);
		}
	}

	/**
	 * Sets the top level of this computation.
	 * 
	 * @param topLevel
	 *            The top level of this computation
	 */
	public void setTopLevel(int topLevel) {
		this.topLevel = topLevel;
		ASAP = topLevel;
	}

	/**
	 * Sets the computation top level of this computation.
	 * 
	 * @param topLevelComputation
	 *            The computation top level of this computation
	 */
	public void setTopLevelComputation(int topLevelComputation) {
		this.topLevelComputation = topLevelComputation;
	}

	/**
	 * Sets the input top level of this computation.
	 * 
	 * @param topLevelIn
	 *            The input top level of this computation
	 */
	public void setTopLevelIn(int topLevelIn) {
		this.topLevelIn = topLevelIn;
	}

	/**
	 * Sets the input/output top level of this computation.
	 * 
	 * @param topLevelInOut
	 *            The input/output top level of this computation
	 */
	public void setTopLevelInOut(int topLevelInOut) {
		this.topLevelInOut = topLevelInOut;
	}

	/**
	 * Sets the output top level of this computation.
	 * 
	 * @param topLevelOut
	 *            The output top level of this computation
	 */
	public void setTopLevelOut(int topLevelOut) {
		this.topLevelOut = topLevelOut;
	}

	/**
	 * Sets the associated DAGVertex.
	 * 
	 * @param vertex
	 *            The associated DAGVertex
	 */
	public void setVertex(DAGVertex vertex) {
		this.vertex = vertex;
	}

	/**
	 * Updates all the times when finishing the real scheduling of a node.
	 */
	public void updateTimes() {
		oldStartTime = startTime;
		oldFinishTime = finishTime;
	}
}