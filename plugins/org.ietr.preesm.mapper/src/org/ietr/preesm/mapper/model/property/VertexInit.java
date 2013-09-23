/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

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

package org.ietr.preesm.mapper.model.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.dftools.architecture.slam.ComponentInstance;

import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.mapper.abc.SpecialVertexManager;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Properties of a mapped vertex set when converting dag to mapper dag
 * 
 * @author mpelcat
 */
public class VertexInit {

	/**
	 * Corresponding vertex
	 */
	private MapperDAGVertex parentVertex;

	/**
	 * Timings for the given vertex on available operators
	 */
	private List<Timing> timings;

	/**
	 * Available operators
	 */
	private List<ComponentInstance> operators;

	/**
	 * Number of repetitions that may ponderate the timing
	 */
	private int nbRepeat;

	public int getNbRepeat() {
		return nbRepeat;
	}

	public VertexInit() {
		super();
		timings = new ArrayList<Timing>();
		this.nbRepeat = 1;
		parentVertex = null;
		operators = new ArrayList<ComponentInstance>();
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
		if (operator != null) {
			this.operators.add(operator);
		}
	}

	public VertexInit clone(MapperDAGVertex parentVertex) {

		VertexInit property = new VertexInit();

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
	 * time. Special vertices have specific time computation
	 */
	public long getTime(ComponentInstance operator) {

		long time = 0;

		if (operator != DesignTools.NO_COMPONENT_INSTANCE) {

			// Non special vertex timings are retrieved from scenario
			// Special vertex timings were computed from scenario
			Timing returntiming = getTiming(operator.getComponent().getVlnv()
					.getName());

			if (!SpecialVertexManager.isSpecial(parentVertex)) {

				if (returntiming != Timing.UNAVAILABLE) {
					if (returntiming.getTime() != 0) {
						// The basic timing is multiplied by the number of
						// repetitions
						time = returntiming.getTime() * this.nbRepeat;
					} else {
						time = Timing.DEFAULT_TASK_TIME;
					}
				}
			} else {
				// Special vertex timings are retrieved
				if (returntiming != Timing.UNAVAILABLE) {
					if (returntiming.getTime() != 0) {
						time = returntiming.getTime();
					} else {
						time = Timing.DEFAULT_SPECIAL_VERTEX_TIME;
					}
				} else {
					time = Timing.DEFAULT_SPECIAL_VERTEX_TIME;
				}
			}
		}

		return time;
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

		for (MapperDAGVertex pred : parentVertex.getPredecessors(true).keySet()) {
			if (pred == null) {
				return false;
			} else if (SpecialVertexManager.isSpecial(pred)) {
				predMapable |= pred.getInit().isPredMapable(operator);
			} else {
				predMapable |= pred.getInit().isMapable(operator);
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

		for (MapperDAGVertex succ : parentVertex.getSuccessors(true).keySet()) {
			if (succ == null) {
				return false;
			} else if (SpecialVertexManager.isSpecial(succ)) {
				succMapable |= succ.getInit().isSuccMapable(operator);
			} else {
				succMapable |= succ.getInit().isMapable(operator);
			}
		}

		return succMapable;
	}

	public void setParentVertex(MapperDAGVertex parentVertex) {
		this.parentVertex = parentVertex;
	}
}
