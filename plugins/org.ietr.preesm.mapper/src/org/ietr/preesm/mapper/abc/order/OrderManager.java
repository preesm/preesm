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

package org.ietr.preesm.mapper.abc.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * The scheduling order manager keeps a total order of the vertices and a
 * partial order in each schedule. It is used by the schedule edge adder to
 * insert schedule edges. The scheduling order manager is observed by the time
 * keeper and reports the vertices which timings need to be updated.
 * 
 * @author mpelcat
 */
public class OrderManager extends Observable {

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private Map<ComponentInstance, Schedule> schedules = null;

	/**
	 * total order of the vertices in the implementation
	 */
	Schedule totalOrder = null;

	public OrderManager(Design archi) {

		schedules = new HashMap<ComponentInstance, Schedule>();

		// Adding one schedule per component
		for (ComponentInstance cmp : DesignTools.getComponentInstances(archi)) {
			schedules.put(cmp, new Schedule());
		}

		totalOrder = new Schedule();
	}

	public int findLastestPredIndexForOp(ComponentInstance cmp, int refIndex) {

		// Retrieves the schedule corresponding to the component
		Schedule currentSched = getSchedule(cmp);

		// Iterates the schedule to find the latest predecessor
		int maxPrec = -1;
		for (MapperDAGVertex current : currentSched.getList()) {

			// Looking for the preceding vertex with maximum total order in
			// vertex schedule
			int currentTotalOrder = totalIndexOf(current);

			if (currentTotalOrder < refIndex) {
				maxPrec = currentTotalOrder;
			}
		}

		return maxPrec;
	}

	/**
	 * Considering that vertex already has a total order (is already in total
	 * order list), inserts it at the appropriate position in its schedule
	 */
	public void insertGivenTotalOrder(MapperDAGVertex vertex) {

		if (vertex.hasEffectiveComponent()) {

			ComponentInstance cmp = vertex.getEffectiveComponent();
			int newSchedulingTotalOrder = totalIndexOf(vertex);
			int maxPrec = findLastestPredIndexForOp(
					vertex.getEffectiveComponent(),
					newSchedulingTotalOrder);
			// Testing a possible synchronized vertex
			MapperDAGVertex elt = get(newSchedulingTotalOrder);
			if (elt == null || elt.equals(vertex)) {
				elt = vertex;
			} else {
				WorkflowLogger.getLogger().log(Level.SEVERE,
						"Error in sched order!!");
			}

			// Adds vertex or synchro vertices after its chosen predecessor
			if (maxPrec >= 0) {
				MapperDAGVertex previous = totalOrder.get(maxPrec);
				getSchedule(cmp).insertAfter(previous, elt);
			} else {
				getSchedule(cmp).addFirst(elt);
			}

		}

		// Notifies the time keeper that it should update the successors
		Set<MapperDAGVertex> vSet = totalOrder.getSuccessors(vertex);
		if (vSet == null || vSet.isEmpty()) {
			vSet = new HashSet<MapperDAGVertex>();
		}
		vSet.add(vertex);
		setChanged();
		notifyObservers(vSet);
	}

	/**
	 * If the input is a vertex, appends it at the end of one schedule and at
	 * the end of total order. If the input is synschronizedVertices, appends it
	 * at the end of all concerned schedules and at the end of total order.
	 */
	public void addLast(MapperDAGVertex elt) {

		if (elt instanceof MapperDAGVertex) {
			MapperDAGVertex vertex = (MapperDAGVertex) elt;
			if (vertex.hasEffectiveComponent()) {
				ComponentInstance effectiveCmp = vertex
						.getEffectiveComponent();

				// Gets the schedule of vertex
				Schedule currentSchedule = getSchedule(effectiveCmp);

				currentSchedule.addLast(vertex);

				if (totalOrder.contains(vertex)) {
					totalOrder.remove(vertex);
				}

				totalOrder.addLast(vertex);
			}

			// Notifies the time keeper that it should update the vertex
			setChanged();
			notifyObservers(vertex);
		}
	}

	/**
	 * Appends the vertex at the beginning of a schedule and at the end of total
	 * order
	 */
	public void addFirst(MapperDAGVertex vertex) {

		if (vertex.hasEffectiveComponent()) {
			ComponentInstance effectiveCmp = vertex
					.getEffectiveComponent();

			// Gets the schedule of vertex
			Schedule currentSchedule = getSchedule(effectiveCmp);

			currentSchedule.addFirst(vertex);

			if (totalOrder.contains(vertex)) {
				totalOrder.remove(vertex);
			}

			totalOrder.addFirst(vertex);
		}

		// Notifies the time keeper that it should update the successors
		setChanged();
		notifyObservers(new HashSet<MapperDAGVertex>(totalOrder.getList()));
	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertAfter(MapperDAGVertex previous, MapperDAGVertex vertex) {

		if (previous == null) {
			addLast(vertex);
		} else {

			if (previous.hasEffectiveComponent()
					&& vertex.hasEffectiveComponent()) {

				if (!totalOrder.contains(vertex)) {
					if (totalOrder.indexOf(previous) >= 0) {
						totalOrder.insertAfter(previous, vertex);
					}
				}
				insertGivenTotalOrder(vertex);

			}
		}
	}

	/**
	 * Inserts vertex before next
	 */
	public void insertBefore(MapperDAGVertex next, MapperDAGVertex vertex) {

		if (next == null) {
			addFirst(vertex);
		} else {
			if (next.hasEffectiveComponent()
					&& vertex.hasEffectiveComponent()) {

				if (!totalOrder.contains(vertex)) {
					if (totalOrder.indexOf(next) >= 0) {
						totalOrder.insertBefore(next, vertex);
					}
				}
				insertGivenTotalOrder(vertex);

			}
		}

	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertAtIndex(int index, MapperDAGVertex vertex) {

		if (index < totalOrder.size() && index >= 0) {
			MapperDAGVertex ref = totalOrder.get(index);
			insertBefore(ref, vertex);
		} else {
			addLast(vertex);
		}
	}

	/**
	 * Gets the local scheduling order, -1 if not present
	 */
	public int localIndexOf(MapperDAGVertex vertex) {

		if (vertex.hasEffectiveComponent()) {

			Schedule sch = getSchedule(vertex
					.getEffectiveComponent());
			if (sch != null) {
				return sch.indexOf(vertex);
			}
		}

		return -1;
	}

	/**
	 * Gets the total scheduling order
	 */
	public int totalIndexOf(MapperDAGVertex vertex) {

		return totalOrder.indexOf(vertex);
	}

	/**
	 * Gets the vertex with the given total scheduling order
	 */
	public MapperDAGVertex get(int totalOrderIndex) {
		MapperDAGVertex elt = totalOrder.get(totalOrderIndex);
		return elt;
	}

	/**
	 * Gets the scheduling components
	 */
	public Set<ComponentInstance> getArchitectureComponents() {

		return schedules.keySet();

	}

	/**
	 * Removes a given vertex
	 */
	public void remove(MapperDAGVertex vertex, boolean removeFromTotalOrder) {

		// Notifies the time keeper that it should update the successors
		Set<MapperDAGVertex> successors = totalOrder.getSuccessors(vertex);
		if (successors == null) {
			successors = new HashSet<MapperDAGVertex>();
		}
		successors.add(vertex);
		setChanged();
		notifyObservers(successors);

		// If the vertex has an effective component,
		// removes it from the corresponding scheduling
		Schedule sch = null;
		if (vertex.hasEffectiveComponent()) {

			ComponentInstance cmp = vertex.getEffectiveComponent();
			sch = getSchedule(cmp);
		} else { // Looks for the right scheduling to remove the vertex
			for (Schedule locSched : schedules.values()) {
				if (locSched.contains(vertex)) {
					sch = locSched;
					break;
				}
			}
		}

		if (sch != null) {
			MapperDAGVertex elt = sch.getScheduleElt(vertex);
			if (elt != null) {
				if (elt.equals(vertex)) {
					sch.remove(elt);
				}
			}
		}

		if (removeFromTotalOrder) {
			MapperDAGVertex elt = totalOrder.getScheduleElt(vertex);

			if (elt != null) {
				totalOrder.remove(elt);
			}
		}

	}

	/**
	 * Resets Total Order
	 */
	public void resetTotalOrder() {
		totalOrder.clear();

		for (Schedule s : schedules.values()) {
			s.clear();
		}
	}

	/**
	 * Reconstructs the total order using the total order stored in DAG. Creates
	 * synchronized vertices when several vertices have the same order
	 */
	public void reconstructTotalOrderFromDAG(MapperDAG dag) {

		resetTotalOrder();

		List<DAGVertex> newTotalOrder = new ArrayList<DAGVertex>(
				dag.vertexSet());

		Collections.sort(newTotalOrder, new SchedulingOrderComparator());

		for (DAGVertex vertex : newTotalOrder) {
			MapperDAGVertex mVertex = (MapperDAGVertex) vertex;
			addLast(mVertex);
		}
	}

	/**
	 * Sets the total order of each implementation property in DAG
	 */
	public void tagDAG(MapperDAG dag) {

		for (MapperDAGVertex internalVertex : totalOrder.getList()) {
			MapperDAGVertex vertex = dag.getMapperDAGVertex(internalVertex
					.getName());

			if (vertex != null) {
				tagVertex(vertex);
			}
		}
	}

	/**
	 * Sets the total order of vertex implementation property in DAG
	 */
	private void tagVertex(MapperDAGVertex vertex) {

		vertex.setTotalOrder(totalOrder.indexOf(vertex));
	}

	/**
	 * Gets the previous vertex in the same schedule. Searches in the
	 * synchronized vertices if any
	 */
	public MapperDAGVertex getPrevious(MapperDAGVertex vertex) {

		MapperDAGVertex prevElt = null;
		MapperDAGVertex prevVertex = null;
		ComponentInstance cmp = vertex.getEffectiveComponent();
		Schedule schedule = getSchedule(cmp);

		if (schedule != null) {
			prevElt = schedule.getPrevious(vertex);

			if (prevElt instanceof MapperDAGVertex) {
				prevVertex = (MapperDAGVertex) prevElt;
			}
		}

		return prevVertex;
	}

	/**
	 * Gets the next vertex in the same schedule
	 */
	public MapperDAGVertex getNext(MapperDAGVertex vertex) {
		MapperDAGVertex nextVertex = null;

		ComponentInstance cmp = vertex.getEffectiveComponent();
		Schedule schedule = getSchedule(cmp);

		if (schedule != null) {
			nextVertex = schedule.getNext(vertex);
		}

		return nextVertex;
	}

	public Schedule getTotalOrder() {
		return totalOrder;
	}

	@Override
	public String toString() {
		return totalOrder.toString();
	}

	/**
	 * Gets the schedule of a given component
	 */
	private Schedule getSchedule(ComponentInstance cmp) {

		// Preventing from creating several schedules with same name
		for (ComponentInstance o : schedules.keySet()) {
			if (o.getInstanceName().equals(cmp.getInstanceName())) {
				return schedules.get(o);
			}
		}
		return null;
	}

	/**
	 * Gets the mapperdag vertex list of a given component. Splits the
	 * synchronized vertices objects into their components
	 */
	public List<MapperDAGVertex> getVertexList(ComponentInstance cmp) {

		Schedule s = null;
		List<MapperDAGVertex> vList = new ArrayList<MapperDAGVertex>();

		// Preventing from creating several schedules with same name
		for (ComponentInstance o : schedules.keySet()) {
			if (o.getInstanceName().equals(cmp.getInstanceName())) {
				s = schedules.get(o);
			}
		}

		if (s != null) {
			for (MapperDAGVertex elt : s.getList()) {
				if (elt instanceof MapperDAGVertex) {
					vList.add((MapperDAGVertex) elt);
				}
			}
		}

		return vList;
	}

	public long getBusyTime(ComponentInstance c) {
		Schedule sched = getSchedule(c);
		if (sched != null) {
			return sched.getBusyTime();
		}

		return 0l;
	}
}
