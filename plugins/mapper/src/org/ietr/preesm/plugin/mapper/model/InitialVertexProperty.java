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

package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.architecture.IOperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Properties of an implanted vertex set when converting dag to mapper dag
 * 
 * @author mpelcat
 */
public class InitialVertexProperty {

	/**
	 * Corresponding vertex
	 */
	private MapperDAGVertex parentVertex;

	/**
	 * Timings on available operators
	 */
	private List<Timing> timings;

	/**
	 * Available operators
	 */
	private List<Operator> operators;

	/**
	 * Number of repetitions that ponderates the timing
	 */
	private int nbRepeat;

	/**
	 * Level of the vertex in topological order
	 */
	private int topologicalLevel;

	public int getNbRepeat() {
		return nbRepeat;
	}

	public InitialVertexProperty() {
		super();
		timings = new ArrayList<Timing>();
		this.nbRepeat = 1;
		parentVertex = null;
		operators = new ArrayList<Operator>();
		this.topologicalLevel = -1;
	}

	public void setNbRepeat(int nbRepeat) {
		this.nbRepeat = nbRepeat;
	}

	public void addTiming(Timing timing) {
		if (getTiming(timing.getOperatorDefinition()) == null)
			this.timings.add(timing);
	}

	/**
	 * Enabling the current vertex on the given operator. The operation is
	 * straightforward for normal vertices. For special vertices, a test is done
	 * on the neighbors.
	 */
	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}

	/**
	 * 
	 */
	public void removeOperatorIfPredNotImplantable(Operator operator) {
		boolean predImplantable = isPredImplantable(operator);
		if (!predImplantable) {
			this.operators.remove(operator);
		}
	}

	/**
	 * 
	 */
	public void removeOperatorIfSuccNotImplantable(Operator operator) {
		boolean succImplantable = isSuccImplantable(operator);
		if (!succImplantable) {
			this.operators.remove(operator);
		}
	}

	public InitialVertexProperty clone(MapperDAGVertex parentVertex) {

		InitialVertexProperty property = new InitialVertexProperty();

		if (parentVertex != null)
			property.setParentVertex(parentVertex);

		Iterator<Timing> it = getTimings().iterator();
		while (it.hasNext()) {
			Timing next = it.next();
			property.addTiming(next);
		}

		Iterator<Operator> it2 = operators.iterator();
		while (it2.hasNext()) {
			Operator next = it2.next();
			property.addOperator(next);
		}

		property.setNbRepeat(nbRepeat);

		return property;

	}

	/**
	 * Returns all the operators that can execute the vertex. Special vertices
	 * are originally enabled on every operator but their status is updated
	 * depending on the implantation of their neighbors
	 */
	public List<Operator> getInitialOperatorList() {
		return operators;
	}

	public MapperDAGVertex getParentVertex() {
		return parentVertex;
	}

	/**
	 * Returns the timing of the operation = number of repetitions * scenario
	 * time
	 */
	public int getTime(Operator operator) {

		int time = 0;

		if (operator != Operator.NO_COMPONENT) {

			Timing returntiming = getTiming((OperatorDefinition) operator
					.getDefinition());

			if (returntiming != Timing.UNAVAILABLE) {

				if (!SpecialVertexManager.isSpecial(parentVertex)) {
					if (returntiming.getTime() != 0) {
						// The basic timing is multiplied by the number of
						// repetitions
						time = returntiming.getTime() * this.nbRepeat;
					} else {
						time = Timing.DEFAULT_TASK_TIME;
					}
				} else {
					if (SpecialVertexManager.isBroadCast(parentVertex)) {
						// Broadcast time is calculated from its output size
						// if a memory copy speed is set in the operator
						OperatorDefinition def = (OperatorDefinition) operator
								.getDefinition();
						time = Timing.DEFAULT_BROADCAST_TIME;

						float dataCopySpeed = def.getDataCopySpeed();
						if (dataCopySpeed > 0) {
							// Calculating the sum of output data sizes
							int inputDataSize = getVertexInputBuffersSize(parentVertex);
							int outputDataSize = getVertexOutputBuffersSize(parentVertex);

							// A broadcast with different sizes in inputs and
							// output creates a memory copy with a given speed
							if (inputDataSize < outputDataSize
									&& outputDataSize > 0) {
								time = (int) Math.ceil(outputDataSize
										/ dataCopySpeed);
							} else {
								time = Timing.DEFAULT_BROADCAST_TIME;
							}
						}
					} else if (SpecialVertexManager.isFork(parentVertex)) {
						time = Timing.DEFAULT_FORK_TIME;
					} else if (SpecialVertexManager.isJoin(parentVertex)) {
						// Join time is calculated from its input size
						// if a memory copy speed is set in the operator
						OperatorDefinition def = (OperatorDefinition) operator
								.getDefinition();
						time = Timing.DEFAULT_BROADCAST_TIME;

						float dataCopySpeed = def.getDataCopySpeed();
						if (dataCopySpeed > 0) {
							// Calculating the sum of input data sizes
							// A join creates a memory copy with a given speed
							int inputDataSize = getVertexInputBuffersSize(parentVertex);

							if (inputDataSize > 0) {
								time = (int) Math.ceil(inputDataSize
										/ dataCopySpeed);
							} else {
								time = Timing.DEFAULT_JOIN_TIME;
							}
						}
					} else if (SpecialVertexManager.isInit(parentVertex)) {
						time = Timing.DEFAULT_INIT_TIME;
					}
				}
			}
		}

		return time;
	}

	private int getVertexInputBuffersSize(MapperDAGVertex v) {
		int inputDataSize = 0;

		for (DAGEdge e : parentVertex.incomingEdges()) {
			MapperDAGEdge me = (MapperDAGEdge) e;
			if (!(me.getSource() instanceof TransferVertex)) {
				inputDataSize += me.getInitialEdgeProperty().getDataSize();
			}
		}

		return inputDataSize;
	}

	private int getVertexOutputBuffersSize(MapperDAGVertex v) {
		int outputDataSize = 0;

		for (DAGEdge e : v.outgoingEdges()) {
			MapperDAGEdge me = (MapperDAGEdge) e;
			if (!(me.getTarget() instanceof TransferVertex)) {
				outputDataSize += me.getInitialEdgeProperty().getDataSize();
			}
		}

		return outputDataSize;

	}

	public Timing getTiming(IOperatorDefinition operatordef) {

		Timing returntiming = Timing.UNAVAILABLE;

		Iterator<Timing> iterator = timings.iterator();

		while (iterator.hasNext()) {
			Timing currenttiming = iterator.next();

			if (operatordef.equals(currenttiming.getOperatorDefinition())) {
				returntiming = currenttiming;
				break;
			}
		}

		return returntiming;
	}

	public List<Timing> getTimings() {
		return timings;
	}

	/**
	 * Checks in the vertex initial properties if it can be implanted on the
	 * given operator. For special vertices, the predecessors and successor
	 * implantabilities are studied
	 */
	public boolean isImplantable(Operator operator) {

		for (Operator op : operators) {
			if (op.equals(operator))
				return true;
		}

		return false;
	}

	/**
	 * Checks if the vertex first non special predecessors can be implanted on
	 * the given operator.
	 */
	public boolean isPredImplantable(Operator operator) {

		boolean predImplantable = false;

		for (MapperDAGVertex pred : parentVertex.getPredecessorSet(true)) {
			if (pred == null) {
				return false;
			} else if (SpecialVertexManager.isSpecial(pred)) {
				predImplantable |= pred.getInitialVertexProperty()
						.isPredImplantable(operator);
			} else {
				predImplantable |= pred.getInitialVertexProperty()
						.isImplantable(operator);
			}
		}

		return predImplantable;
	}

	/**
	 * Checks if the vertex first non special successor can be implanted on the
	 * given operator.
	 */
	public boolean isSuccImplantable(Operator operator) {

		boolean succImplantable = false;

		for (MapperDAGVertex succ : parentVertex.getSuccessorSet(true)) {
			if (succ == null) {
				return false;
			} else if (SpecialVertexManager.isSpecial(succ)) {
				succImplantable |= succ.getInitialVertexProperty()
						.isSuccImplantable(operator);
			} else {
				succImplantable |= succ.getInitialVertexProperty()
						.isImplantable(operator);
			}
		}

		return succImplantable;
	}

	public void setParentVertex(MapperDAGVertex parentVertex) {
		this.parentVertex = parentVertex;
	}

	public int getTopologicalLevel() {
		return topologicalLevel;
	}

	public void setTopologicalLevel(int topologicalLevel) {
		this.topologicalLevel = topologicalLevel;
	}
}
