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

package org.ietr.preesm.plugin.abc.order;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGVertex;

/**
 * The scheduling order manager keeps a total order of the vertices and a
 * partial order in each schedule. It is used by the schedule edge adder to
 * insert schedule edges
 * 
 * @author mpelcat
 */
public class SchedOrderManager extends Observable {

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private Map<ArchitectureComponent, Schedule> schedules = null;

	/**
	 * total order of the vertices in the implementation
	 */
	Schedule totalOrder = null;
	public SchedOrderManager() {

		schedules = new HashMap<ArchitectureComponent, Schedule>();
		totalOrder = new Schedule();
	}

	/**
	 * Considering that vertex already has a total order, inserts it at the
	 * appropriate position in its schedule
	 */
	public void insertVertexInTotalOrder(MapperDAGVertex vertex) {

		AddScheduleIfNotPresent(vertex);

		ImplementationVertexProperty currImpProp = vertex
				.getImplementationVertexProperty();

		if (currImpProp.hasEffectiveComponent()) {
			// Retrieves the schedule corresponding to the vertex
			Schedule currentSched = getSchedule(currImpProp
					.getEffectiveComponent());

			int newSchedulingTotalOrder = totalIndexOf(vertex);

			// Iterates the schedule
			Iterator<MapperDAGVertex> it = currentSched.iterator();
			int maxPrec = -1;

			while (it.hasNext()) {
				MapperDAGVertex current = it.next();

				// Looking for the preceding vertex with maximum total order
				int currentTotalOrder = totalIndexOf(current);

				if (currentTotalOrder < newSchedulingTotalOrder
						&& currentTotalOrder > maxPrec)
					maxPrec = currentTotalOrder;
			}

			// Adds vertex after its chosen predecessor
			if (maxPrec >= 0) {
				MapperDAGVertex previous = totalOrder.get(maxPrec);
				currentSched.insertVertexAfter(previous, vertex);
			} else {
				currentSched.addVertexFirst(vertex);
			}

			// Notifies the time keeper that it should update the successors
			setChanged();
			notifyObservers(vertex);
			MapperDAGVertex successor = totalOrder.getNextVertex(vertex);
			while(successor != null){
				setChanged();
				notifyObservers(successor);
				successor = totalOrder.getNextVertex(successor);
			}
		}

	}

	/**
	 * Appends the vertex at the end of a schedule and at the end of total order
	 */
	public void addLast(MapperDAGVertex vertex) {

		AddScheduleIfNotPresent(vertex);

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {
			ArchitectureComponent effectiveCmp = vertex
					.getImplementationVertexProperty().getEffectiveComponent();

			// Gets the schedule of vertex
			Schedule currentSchedule = getSchedule(effectiveCmp);

			currentSchedule.addVertex(vertex);

			if (totalOrder.contains(vertex))
				totalOrder.remove(vertex);

			totalOrder.addLast(vertex);
			setChanged();
			notifyObservers(vertex);

		}
	}

	/**
	 * Appends the vertex at the beginning of a schedule and at the end of total order
	 */
	public void addFirst(MapperDAGVertex vertex) {

		AddScheduleIfNotPresent(vertex);

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {
			ArchitectureComponent effectiveCmp = vertex
					.getImplementationVertexProperty().getEffectiveComponent();

			// Gets the schedule of vertex
			Schedule currentSchedule = getSchedule(effectiveCmp);

			currentSchedule.addFirst(vertex);

			if (totalOrder.contains(vertex))
				totalOrder.remove(vertex);

			totalOrder.addFirst(vertex);

			// Notifies the time keeper that it should update the successors
			for(MapperDAGVertex v : totalOrder){
				setChanged();
				notifyObservers(v);
			}
		}
	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertVertexAfter(MapperDAGVertex previous,
			MapperDAGVertex vertex) {

		if (previous == null) {
			addLast(vertex);
		} else {
			AddScheduleIfNotPresent(vertex);

			ImplementationVertexProperty prevImpProp = previous
					.getImplementationVertexProperty();
			ImplementationVertexProperty currImpProp = vertex
					.getImplementationVertexProperty();

			if (prevImpProp.hasEffectiveComponent()
					&& currImpProp.hasEffectiveComponent()) {

				if (!totalOrder.contains(vertex)) {
					if (totalOrder.indexOf(previous) >= 0) {
						totalOrder.insertVertexAfter(previous, vertex);
					}
				}
				insertVertexInTotalOrder(vertex);

			}
		}
	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertVertexBefore(MapperDAGVertex next, MapperDAGVertex vertex) {

		if (next == null) {
			addFirst(vertex);
		} else {
			AddScheduleIfNotPresent(vertex);

			ImplementationVertexProperty prevImpProp = next
					.getImplementationVertexProperty();
			ImplementationVertexProperty currImpProp = vertex
					.getImplementationVertexProperty();

			if (prevImpProp.hasEffectiveComponent()
					&& currImpProp.hasEffectiveComponent()) {

				if (!totalOrder.contains(vertex)) {
					if (totalOrder.indexOf(next) >= 0) {
						totalOrder.insertVertexBefore(next, vertex);
					}
				}
				insertVertexInTotalOrder(vertex);

			}
		}

	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertVertexAtIndex(int index, MapperDAGVertex vertex) {
		
		MapperDAGVertex ref = totalOrder.get(index);
		if (ref != null) {
			insertVertexBefore(ref, vertex);
		} else {
			addLast(vertex);
		}
	}

	/**
	 * Gets the local scheduling order, -1 if not present
	 */
	public int localIndexOf(MapperDAGVertex vertex) {

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {

			Schedule sch = getSchedule(vertex.getImplementationVertexProperty()
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
	public MapperDAGVertex getVertex(int totalOrderIndex) {
		return totalOrder.get(totalOrderIndex);
	}

	/**
	 * Gets the scheduling components
	 */
	public Set<ArchitectureComponent> getArchitectureComponents() {

		return schedules.keySet();

	}

	/**
	 * Gets the schedule of a given component
	 */
	public List<MapperDAGVertex> getScheduleList(ArchitectureComponent cmp) {

		return getSchedule(cmp);

	}

	/**
	 * Gets the schedule of a given component
	 */
	public Schedule getSchedule(ArchitectureComponent cmp) {

		// Preventing from creating several schedules with same name
		for(ArchitectureComponent o : schedules.keySet()){
			if(o.equals(cmp)){
				return schedules.get(o);
			}
		}
		return null;
	}

	/**
	 * Removes a given vertex
	 */
	public void remove(MapperDAGVertex vertex, boolean removeFromTotalOrder) {

		// If the vertex has an effective component,
		// removes it from the corresponding scheduling
		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {
			ArchitectureComponent cmp = vertex
					.getImplementationVertexProperty().getEffectiveComponent();
			Schedule sch = getSchedule(cmp);

			if (sch != null){
				// Notifies the time keeper that it should update the successors
				MapperDAGVertex successor = totalOrder.getNextVertex(vertex);
				while(successor != null){
					setChanged();
					notifyObservers(successor);
					successor = totalOrder.getNextVertex(successor);
				}
				
				sch.remove(vertex);
			}
		} else { // Looks for the right scheduling to remove the vertex
			Iterator<Schedule> it = schedules.values().iterator();

			while (it.hasNext()) {
				it.next().remove(vertex);
			}
		}

		if (removeFromTotalOrder) {
			totalOrder.remove(vertex);
		}
		
	}

	/**
	 * Resets Total Order
	 */
	public void resetTotalOrder() {
		totalOrder.clear();
		schedules.clear();
	}

	/**
	 * Adds the schedule corresponding to the vertex effective component if not
	 * present
	 */
	private void AddScheduleIfNotPresent(MapperDAGVertex vertex) {

		ImplementationVertexProperty currImpProp = vertex
				.getImplementationVertexProperty();

		// Gets the component corresponding to the vertex
		if (currImpProp.hasEffectiveComponent()) {
			ArchitectureComponent effectiveCmp = currImpProp
					.getEffectiveComponent();

			// If no schedule exists for this component,
			// adds a schedule for it
			if (getSchedule(effectiveCmp) == null)
				schedules.put(effectiveCmp, new Schedule());
		}

	}

	/**
	 * Reconstructs the total order using the total order stored in DAG
	 */
	public void reconstructTotalOrderFromDAG(MapperDAG dag) {

		resetTotalOrder();

		ConcurrentSkipListSet<DAGVertex> newTotalOrder = new ConcurrentSkipListSet<DAGVertex>(
				new SchedulingOrderComparator());

		newTotalOrder.addAll(dag.vertexSet());

		for (DAGVertex vertex : newTotalOrder) {
			addLast((MapperDAGVertex) vertex);
		}

	}

	/**
	 * Sets the total order of each implementation property in DAG
	 */
	public void tagDAG(MapperDAG dag) {
		Iterator<MapperDAGVertex> iterator = totalOrder.iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex internalVertex = iterator.next();
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

		vertex.getImplementationVertexProperty().setSchedTotalOrder(
				totalOrder.indexOf(vertex));
	}

	/**
	 * Gets the previous vertex in the same schedule
	 */
	public MapperDAGVertex getPreviousVertex(MapperDAGVertex vertex) {

		MapperDAGVertex prevVertex = null;

		Schedule schedule = getSchedule(vertex
				.getImplementationVertexProperty().getEffectiveComponent());

		if (schedule != null)
			prevVertex = schedule.getPreviousVertex(vertex);

		return prevVertex;
	}

	/**
	 * Gets the next vertex in the same schedule
	 */
	public MapperDAGVertex getNextVertex(MapperDAGVertex vertex) {
		MapperDAGVertex nextVertex = null;

		Schedule schedule = getSchedule(vertex
				.getImplementationVertexProperty().getEffectiveComponent());

		if (schedule != null)
			nextVertex = schedule.getNextVertex(vertex);

		return nextVertex;
	}

	public Schedule getTotalOrder() {
		return totalOrder;
	}

	@Override
	public String toString() {
		return totalOrder.toString();
	}

}
