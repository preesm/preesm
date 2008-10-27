/**
 * PREESM
 * Copyright IETR 2007 under CeCILL license.
 *
 * Pengcheng MU <Pengcheng.Mu@ens.insa-rennes.fr>
 */
package org.ietr.preesm.plugin.mapper.commcontenlistsched.descriptor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.dag.DAGVertexPropertyType;

/**
 * This class is a descriptor of computation that provides necessary parameters.
 * 
 * @author P. Mu
 * 
 */
public class ComputationDescriptor extends OperationDescriptor implements
		Comparable<ComputationDescriptor> {

	private DAGVertex vertex = null;

	private int nbTotalRepeat = 1;

	private Vector<CommunicationDescriptor> inputCommunications;

	private Vector<CommunicationDescriptor> outputCommunications;

	private HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer = null;

	private OperatorDescriptor operator = null;

	private Set<String> operatorSet = null;

	private HashMap<String, Integer> computationDurations;

	// The computation is ready if and only if all its predecessors or
	// successors are ready.
	private boolean ready = false;

	private int nbAverageRepeat;

	private int dataReadyTime = 0;

	private int startTime = 0;

	private int finishTime = 0;

	private int oldStartTime = 0;

	private int oldFinishTime = 0;

	private int temporaryStartTime = 0;

	private int temporaryFinishTime = 0;

	private int computationDuration = 0;

	private int minComputationDuration = 0;

	// next computation in the same processor
	private ComputationDescriptor nextComputation = null;

	private Vector<CommunicationDescriptor> unscheduledInputCommunications = null;

	private Vector<CommunicationDescriptor> unscheduledOutputCommunications = null;

	private int topLevel = 0;

	private int bottomLevel = 0;

	private int topLevelComputation = 0;

	private int bottomLevelComputation = 0;

	private int topLevelIn = 0;

	private int bottomLevelIn = 0;

	private int topLevelOut = 0;

	private int bottomLevelOut = 0;

	private int topLevelInOut = 0;

	private int bottomLevelInOut = 0;

	/**
	 * Construct a new ComputationDescriptor with the identifier id and buffer.
	 * 
	 * @param id
	 * @param ComputationDescriptorBuffer
	 */
	public ComputationDescriptor(String name,
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer) {
		super(name);
		vertex = new DAGVertex();
		vertex.setName(name);
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

	public ComputationDescriptor(String name, AlgorithmDescriptor algorithm) {
		super(name);
		vertex = new DAGVertex();
		vertex.setName(name);
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

	public void setTime(int time) {
		if (vertex.getTime() == null) {
			vertex.setTime(new DAGVertexPropertyType(time));
		} else {
			((DAGVertexPropertyType) vertex.getTime()).setValue(time);
		}
	}

	public int getTime() {
		return vertex.getTime().intValue();
	}

	/**
	 * 
	 * @param n
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
	 * 
	 * @return
	 */
	public int getNbTotalRepeat() {
		return vertex.getNbRepeat().intValue();
	}

	public void addComputationDuration(OperatorDescriptor operator, int time) {
		if (!this.computationDurations.containsKey(operator.getName())) {
			this.computationDurations.put(operator.getName(), time);
		}
	}

	public void addComputationDuration(String operatorName, int time) {
		if (!this.computationDurations.containsKey(operatorName)) {
			this.computationDurations.put(operatorName, time);
		}
	}

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

	public int getComputationDuration(String operatorName) {
		if (computationDurations.containsKey(operatorName)) {
			return computationDurations.get(operatorName);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	public HashMap<String, Integer> getComputationDurations() {
		return computationDurations;
	}

	/**
	 * 
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
	 * 
	 * @return
	 */
	public int getComputationDuration() {
		setComputationDuration();
		return computationDuration;
	}

	/**
	 * 
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
	 * 
	 * @return
	 */
	public int getMinComputationDuration() {
		setMinComputationDuration();
		return minComputationDuration;
	}

	/**
	 * 
	 */
	public void addInputCommunication(CommunicationDescriptor communication) {
		inputCommunications.add(communication);
		unscheduledInputCommunications.add(communication);
	}

	/**
	 * 
	 * @return
	 */
	public Vector<CommunicationDescriptor> getInputCommunications() {
		return inputCommunications;
	}

	/**
	 * 
	 */
	public void addOutputCommunication(CommunicationDescriptor communication) {
		outputCommunications.add(communication);
		unscheduledOutputCommunications.add(communication);
	}

	/**
	 * 
	 * @return
	 */
	public Vector<CommunicationDescriptor> getOutputCommunications() {
		return outputCommunications;
	}

	/**
	 * 
	 * @param operator
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

	public void setOperatorSet(Set<String> operators) {
		operatorSet = operators;
	}

	public Set<String> getOperatorSet() {
		return operatorSet;
	}

	public void addOperator(OperatorDescriptor operator) {
		operatorSet.add(operator.getId());
	}

	public void addOperator(String operatorId) {
		operatorSet.add(operatorId);
	}

	/**
	 * 
	 * @return
	 */
	public OperatorDescriptor getOperator() {
		return operator;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isReady() {
		return ready;
	}

	/**
	 * 
	 */
	public void setReady() {
		ready = true;
	}

	/**
	 * 
	 */
	public void clearReady() {
		ready = false;
	}

	/**
	 * 
	 */
	public void setNbAverageRepeat(int n) {
		nbAverageRepeat = n;
	}

	/**
	 * 
	 * @return
	 */
	public int getNbAverageRepeat() {
		return nbAverageRepeat;
	}

	public void setDataReadyTime(int time) {
		dataReadyTime = time;
	}

	public int getDataReadyTime() {
		return dataReadyTime;
	}

	/**
	 * 
	 * @param time
	 */
	public void setStartTime(int time) {
		this.startTime = time;
	}

	/**
	 * 
	 * @return
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * 
	 */
	public void setFinishTime() {
		setComputationDuration();
		finishTime = startTime + computationDuration;
	}

	public void setFinishTime(int i) {
		finishTime = i;
		computationDuration = finishTime - startTime;
	}

	/**
	 * 
	 * @return
	 */
	public int getFinishTime() {
		finishTime = startTime + computationDuration;
		return finishTime;
	}

	public void setNextComputation(ComputationDescriptor computation) {
		nextComputation = computation;
	}

	public ComputationDescriptor getNextComputation() {
		return nextComputation;
	}

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

	public CommunicationDescriptor getUnscheduledInputCommunication(int index) {
		return unscheduledInputCommunications.get(index);
	}

	public Vector<CommunicationDescriptor> getUnscheduledInputCommunications() {
		return unscheduledInputCommunications;
	}

	public void addUnscheduledInputCommunication(
			CommunicationDescriptor unscheduledCommunication) {
		unscheduledInputCommunications.add(unscheduledCommunication);
	}

	public CommunicationDescriptor getUnscheduledOutputCommunication(int index) {
		return unscheduledOutputCommunications.get(index);
	}

	public Vector<CommunicationDescriptor> getUnscheduledOutputCommunications() {
		return unscheduledOutputCommunications;
	}

	public void addUnscheduledOutputCommunication(
			CommunicationDescriptor unscheduledCommunication) {
		unscheduledOutputCommunications.add(unscheduledCommunication);
	}

	public int compareTo(ComputationDescriptor computation) {
		return -((topLevel + bottomLevel) - (computation.getTopLevel() + computation
				.getBottomLevel()));
	}

	public HashMap<String, ComputationDescriptor> getComputationDescriptorBuffer() {
		return ComputationDescriptorBuffer;
	}

	public void setComputationDescriptorBuffer(
			HashMap<String, ComputationDescriptor> ComputationDescriptorBuffer) {
		this.ComputationDescriptorBuffer = ComputationDescriptorBuffer;
	}

	public DAGVertex getVertex() {
		return vertex;
	}

	public void setVertex(DAGVertex vertex) {
		this.vertex = vertex;
	}

	public void updateTimes() {
		oldStartTime = startTime;
		oldFinishTime = finishTime;
	}

	public void restoreTimes() {
		startTime = oldStartTime;
		finishTime = oldFinishTime;
	}

	public void backupTimes() {
		temporaryStartTime = startTime;
		temporaryFinishTime = finishTime;
	}

	public void recoverTimes() {
		startTime = temporaryStartTime;
		finishTime = temporaryFinishTime;
	}

	/**
	 * 
	 */
	public void setTopLevel(int topLevel) {
		this.topLevel = topLevel;
		ASAP = topLevel;
	}

	/**
	 * 
	 */
	public int getTopLevel() {
		return topLevel;
	}

	/**
	 * 
	 */
	public void setBottomLevel(int bottomLevel) {
		this.bottomLevel = bottomLevel;
	}

	/**
	 * 
	 */
	public int getBottomLevel() {
		return bottomLevel;
	}

	public int getTopLevelComputation() {
		return topLevelComputation;
	}

	public void setTopLevelComputation(int topLevelComputation) {
		this.topLevelComputation = topLevelComputation;
	}

	public int getBottomLevelComputation() {
		return bottomLevelComputation;
	}

	public void setBottomLevelComputation(int bottomLevelComputation) {
		this.bottomLevelComputation = bottomLevelComputation;
	}

	public int getTopLevelIn() {
		return topLevelIn;
	}

	public void setTopLevelIn(int topLevelIn) {
		this.topLevelIn = topLevelIn;
	}

	public int getBottomLevelIn() {
		return bottomLevelIn;
	}

	public void setBottomLevelIn(int bottomLevelIn) {
		this.bottomLevelIn = bottomLevelIn;
	}

	public int getTopLevelOut() {
		return topLevelOut;
	}

	public void setTopLevelOut(int topLevelOut) {
		this.topLevelOut = topLevelOut;
	}

	public int getBottomLevelOut() {
		return bottomLevelOut;
	}

	public void setBottomLevelOut(int bottomLevelOut) {
		this.bottomLevelOut = bottomLevelOut;
	}

	public int getTopLevelInOut() {
		return topLevelInOut;
	}

	public void setTopLevelInOut(int topLevelInOut) {
		this.topLevelInOut = topLevelInOut;
	}

	public int getBottomLevelInOut() {
		return bottomLevelInOut;
	}

	public void setBottomLevelInOut(int bottomLevelInOut) {
		this.bottomLevelInOut = bottomLevelInOut;
	}

}