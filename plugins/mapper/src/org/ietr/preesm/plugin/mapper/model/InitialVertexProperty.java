/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Properties of a mapped vertex set when converting dag to mapper dag
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
	private List<ComponentInstance> operators;

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
		operators = new ArrayList<ComponentInstance>();
		this.topologicalLevel = -1;
	}

	public void setNbRepeat(int nbRepeat) {
		this.nbRepeat = nbRepeat;
	}

	public void addTiming(Timing timing) {
		if (getTiming(timing.getOperatorDefinitionId()) == null)
			this.timings.add(timing);
	}

	/**
	 * Enabling the current vertex on the given operator. The operation is
	 * straightforward for normal vertices. For special vertices, a test is done
	 * on the neighbors.
	 */
	public void addOperator(ComponentInstance operator) {
		this.operators.add(operator);
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

		Iterator<ComponentInstance> it2 = operators.iterator();
		while (it2.hasNext()) {
			ComponentInstance next = it2.next();
			property.addOperator(next);
		}

		property.setNbRepeat(nbRepeat);

		return property;

	}

	/**
	 * Returns all the operators that can execute the vertex. Special vertices
	 * are originally enabled on every operator but their status is updated
	 * depending on the mapping of their neighbors
	 */
	public List<ComponentInstance> getInitialOperatorList() {
		return operators;
	}

	/**
	 * Checks in the vertex initial properties if it can be mapped on the given
	 * operator. For special vertices, the predecessors and successor mapping
	 * possibilities are studied
	 */
	public boolean isMapable(ComponentInstance operator) {

		for (ComponentInstance op : operators) {
			if (op.getInstanceName().equals(operator.getInstanceName()))
				return true;
		}

		return false;
	}

	public MapperDAGVertex getParentVertex() {
		return parentVertex;
	}

	/**
	 * Returns the timing of the operation = number of repetitions * scenario
	 * time
	 */
	public int getTime(ComponentInstance operator) {

		int time = 0;

		if (operator != DesignTools.NO_COMPONENT_INSTANCE) {

			Timing returntiming = getTiming(operator.getComponent().getVlnv()
					.getName());

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
						time = Timing.DEFAULT_BROADCAST_TIME;

						String stringCopySpeed = DesignTools.getParameter(
								operator, DesignTools.OPERATOR_COPY_SPEED);
						float dataCopySpeed = 0;

						if (stringCopySpeed != null) {
							dataCopySpeed = Float.valueOf(stringCopySpeed);
						}

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
						time = Timing.DEFAULT_BROADCAST_TIME;

						String stringCopySpeed = DesignTools.getParameter(
								operator, DesignTools.OPERATOR_COPY_SPEED);
						float dataCopySpeed = 0;

						if (stringCopySpeed != null) {
							dataCopySpeed = Float.valueOf(stringCopySpeed);
						}

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

	public Timing getTiming(String operatordefId) {

		Timing returntiming = Timing.UNAVAILABLE;

		Iterator<Timing> iterator = timings.iterator();

		while (iterator.hasNext()) {
			Timing currenttiming = iterator.next();

			if (operatordefId.equals(currenttiming.getOperatorDefinitionId())) {
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
	 * Checks if the vertex first non special predecessors can be mapped on the
	 * given operator.
	 */
	public boolean isPredMapable(ComponentInstance operator) {

		boolean predMapable = false;

		for (MapperDAGVertex pred : parentVertex.getPredecessorSet(true)) {
			if (pred == null) {
				return false;
			} else if (SpecialVertexManager.isSpecial(pred)) {
				predMapable |= pred.getInitialVertexProperty().isPredMapable(
						operator);
			} else {
				predMapable |= pred.getInitialVertexProperty().isMapable(
						operator);
			}
		}

		return predMapable;
	}

	/**
	 * Checks if the vertex first non special successor can be mapped on the
	 * given operator.
	 */
	public boolean isSuccMapable(ComponentInstance operator) {

		boolean succMapable = false;

		for (MapperDAGVertex succ : parentVertex.getSuccessorSet(true)) {
			if (succ == null) {
				return false;
			} else if (SpecialVertexManager.isSpecial(succ)) {
				succMapable |= succ.getInitialVertexProperty().isSuccMapable(
						operator);
			} else {
				succMapable |= succ.getInitialVertexProperty().isMapable(
						operator);
			}
		}

		return succMapable;
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
