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

import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.mapper.model.ImplementationVertexProperty;
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
public class SchedOrderManager extends Observable {

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private Map<ComponentInstance, Schedule> schedules = null;

	/**
	 * total order of the vertices in the implementation
	 */
	Schedule totalOrder = null;

	public SchedOrderManager(Design archi) {

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
		for (IScheduleElement current : currentSched.getList()) {

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

		ImplementationVertexProperty currImpProp = vertex
				.getImplementationVertexProperty();

		if (currImpProp.hasEffectiveComponent()) {

			ComponentInstance cmp = currImpProp.getEffectiveComponent();
			int newSchedulingTotalOrder = totalIndexOf(vertex);
			int maxPrec = findLastestPredIndexForOp(
					currImpProp.getEffectiveComponent(),
					newSchedulingTotalOrder);
			// Testing a possible synchronized vertex
			IScheduleElement elt = get(newSchedulingTotalOrder);
			if (elt == null || elt.equals(vertex)) {
				elt = vertex;
			} else {
				if (elt instanceof SynchronizedVertices) {
					((SynchronizedVertices) elt).add(vertex);
				} else {
					WorkflowLogger.getLogger().log(Level.SEVERE,
							"Error in sched order!!");
				}
			}

			// Adds vertex or synchro vertices after its chosen predecessor
			if (maxPrec >= 0) {
				IScheduleElement previous = totalOrder.get(maxPrec);
				getSchedule(cmp).insertAfter(previous, elt);
			} else {
				getSchedule(cmp).addFirst(elt);
			}

		}

		// Notifies the time keeper that it should update the successors
		Set<IScheduleElement> vSet = totalOrder.getSuccessors(vertex);
		if (vSet == null || vSet.isEmpty()) {
			vSet = new HashSet<IScheduleElement>();
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
	public void addLast(IScheduleElement elt) {

		if (elt instanceof MapperDAGVertex) {
			MapperDAGVertex vertex = (MapperDAGVertex) elt;
			if (vertex.getImplementationVertexProperty()
					.hasEffectiveComponent()) {
				ComponentInstance effectiveCmp = vertex
						.getImplementationVertexProperty()
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
		} else if (elt instanceof SynchronizedVertices) {
			SynchronizedVertices synchros = (SynchronizedVertices) elt;

			for (MapperDAGVertex vertex : synchros.vertices()) {
				if (vertex.getImplementationVertexProperty()
						.hasEffectiveComponent()) {
					ComponentInstance effectiveCmp = vertex
							.getImplementationVertexProperty()
							.getEffectiveComponent();

					// Gets the schedule of vertex
					Schedule currentSchedule = getSchedule(effectiveCmp);

					currentSchedule.addLast(synchros);

				}
			}

			totalOrder.addLast(synchros);

			// Notifies the time keeper that it should update the vertex
			setChanged();
			notifyObservers(synchros);
		}
	}

	/**
	 * Appends the vertex at the beginning of a schedule and at the end of total
	 * order
	 */
	public void addFirst(MapperDAGVertex vertex) {

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {
			ComponentInstance effectiveCmp = vertex
					.getImplementationVertexProperty().getEffectiveComponent();

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
		notifyObservers(new HashSet<IScheduleElement>(totalOrder.getList()));
	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertAfter(MapperDAGVertex previous, MapperDAGVertex vertex) {

		if (previous == null) {
			addLast(vertex);
		} else {

			ImplementationVertexProperty prevImpProp = previous
					.getImplementationVertexProperty();
			ImplementationVertexProperty currImpProp = vertex
					.getImplementationVertexProperty();

			if (prevImpProp.hasEffectiveComponent()
					&& currImpProp.hasEffectiveComponent()) {

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
	public void insertBefore(IScheduleElement next, MapperDAGVertex vertex) {

		if (next == null) {
			addFirst(vertex);
		} else {

			ImplementationVertexProperty prevImpProp = next
					.getImplementationVertexProperty();
			ImplementationVertexProperty currImpProp = vertex
					.getImplementationVertexProperty();

			if (prevImpProp.hasEffectiveComponent()
					&& currImpProp.hasEffectiveComponent()) {

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
			IScheduleElement ref = totalOrder.get(index);
			insertBefore(ref, vertex);
		} else {
			addLast(vertex);
		}
	}

	/**
	 * Synchronizes vertex with refElt
	 */
	public SynchronizedVertices synchronize(IScheduleElement refElt,
			MapperDAGVertex vertex) {

		SynchronizedVertices synchroVs = null;
		ComponentInstance nCmp = vertex.getImplementationVertexProperty()
				.getEffectiveComponent();
		Schedule sched = getSchedule(nCmp);

		if (refElt == null) {
			// Replacing the vertex in schedule by a synchronized object
			synchroVs = new SynchronizedVertices();
			synchroVs.add(vertex);
			int vIndex = sched.indexOf(vertex);
			int vTIndex = totalOrder.indexOf(vertex);
			remove(vertex, true);
			sched.insertAtIndex(synchroVs, vIndex);
			totalOrder.insertAtIndex(synchroVs, vTIndex);
		} else if (refElt instanceof SynchronizedVertices) {
			synchroVs = (SynchronizedVertices) refElt;
			synchroVs.add(vertex);
			remove(vertex, true);
			int predIndex = findLastestPredIndexForOp(nCmp,
					totalIndexOf(synchroVs));
			if (predIndex == -1) {
				sched.addFirst(synchroVs);
			} else {
				sched.insertAfter(get(predIndex), synchroVs);
			}
		}

		return synchroVs;
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
	public int totalIndexOf(IScheduleElement vertex) {

		return totalOrder.indexOf(vertex);
	}

	/**
	 * Gets the vertex with the given total scheduling order
	 */
	public IScheduleElement get(int totalOrderIndex) {
		IScheduleElement elt = totalOrder.get(totalOrderIndex);
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
		Set<IScheduleElement> successors = totalOrder.getSuccessors(vertex);
		if (successors == null) {
			successors = new HashSet<IScheduleElement>();
		}
		successors.add(vertex);
		setChanged();
		notifyObservers(successors);

		// If the vertex has an effective component,
		// removes it from the corresponding scheduling
		Schedule sch = null;
		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {

			ComponentInstance cmp = vertex.getImplementationVertexProperty()
					.getEffectiveComponent();
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
			IScheduleElement elt = sch.getScheduleElt(vertex);
			if (elt != null) {
				if (elt.equals(vertex)) {
					sch.remove(elt);
				} else if (elt instanceof SynchronizedVertices) {
					sch.remove(elt);
					if (((SynchronizedVertices) elt).vertices().size() == 1) {
						totalOrder.remove(elt);
					}
					((SynchronizedVertices) elt).remove(vertex);
				}
			}
		}

		if (removeFromTotalOrder) {
			IScheduleElement elt = totalOrder.getScheduleElt(vertex);

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

		int currentOrder = ((MapperDAGVertex) newTotalOrder.get(0))
				.getImplementationVertexProperty().getSchedTotalOrder();
		List<MapperDAGVertex> verticesToSynchro = new ArrayList<MapperDAGVertex>();

		// If the current vertex has a greater order than its predecessor, we
		// add its predecessor in the schedules and we store the new vertex. If
		// the current vertex has the same order as its predecessor, we add it
		// to the vertices to synchro.
		for (DAGVertex vertex : newTotalOrder) {
			MapperDAGVertex mVertex = (MapperDAGVertex) vertex;
			int mVOrder = mVertex.getImplementationVertexProperty()
					.getSchedTotalOrder();
			if (mVOrder > currentOrder) {
				// Adding the preceding element
				if (verticesToSynchro.size() == 1) {
					addLast(verticesToSynchro.get(0));
				} else if (verticesToSynchro.size() > 1) {
					addLast(new SynchronizedVertices(verticesToSynchro));
				}
				verticesToSynchro.clear();
				currentOrder = mVOrder;
			}

			verticesToSynchro.add((MapperDAGVertex) vertex);
		}

		// Adding the last element
		if (verticesToSynchro.size() == 1) {
			addLast(verticesToSynchro.get(0));
		} else if (verticesToSynchro.size() > 1) {
			// addLast(new SynchronizedVertices(verticesToSynchro));
		}
	}

	/**
	 * Sets the total order of each implementation property in DAG
	 */
	public void tagDAG(MapperDAG dag) {

		for (IScheduleElement internalVertex : totalOrder.getList()) {
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
	 * Gets the previous vertex in the same schedule. Searches in the
	 * synchronized vertices if any
	 */
	public MapperDAGVertex getPrevious(MapperDAGVertex vertex) {

		IScheduleElement prevElt = null;
		MapperDAGVertex prevVertex = null;
		ComponentInstance cmp = vertex.getImplementationVertexProperty()
				.getEffectiveComponent();
		Schedule schedule = getSchedule(cmp);

		if (schedule != null) {
			prevElt = schedule.getPrevious(vertex);

			if (prevElt instanceof MapperDAGVertex) {
				prevVertex = (MapperDAGVertex) prevElt;
			} else if (prevElt instanceof SynchronizedVertices) {
				prevVertex = ((SynchronizedVertices) prevElt).getVertex(cmp);
			}
		}

		return prevVertex;
	}

	/**
	 * Gets the next vertex in the same schedule
	 */
	public MapperDAGVertex getNext(MapperDAGVertex vertex) {
		IScheduleElement nextElement = null;
		MapperDAGVertex nextVertex = null;

		ComponentInstance cmp = vertex.getImplementationVertexProperty()
				.getEffectiveComponent();
		Schedule schedule = getSchedule(cmp);

		if (schedule != null) {
			nextElement = schedule.getNext(vertex);

			if (nextElement instanceof MapperDAGVertex) {
				nextVertex = (MapperDAGVertex) nextElement;
			} else if (nextElement instanceof SynchronizedVertices) {
				nextVertex = ((SynchronizedVertices) nextElement)
						.getVertex(cmp);
			}
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
			for (IScheduleElement elt : s.getList()) {
				if (elt instanceof MapperDAGVertex) {
					vList.add((MapperDAGVertex) elt);
				} else if (elt instanceof SynchronizedVertices) {
					for (MapperDAGVertex sVertex : ((SynchronizedVertices) elt)
							.vertices()) {
						if (sVertex.getImplementationVertexProperty()
								.getEffectiveComponent().getInstanceName()
								.equals(cmp.getInstanceName())) {
							vList.add(sVertex);
						}
					}
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
