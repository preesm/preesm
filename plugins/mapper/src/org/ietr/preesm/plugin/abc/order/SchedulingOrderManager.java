/**
 * 
 */
package org.ietr.preesm.plugin.abc.order;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * The scheduling order manager keeps a total order of the vertices and a
 * partial order in each schedule. It is used by the schedule edge adder to
 * insert schedule edges
 * 
 * @author mpelcat
 */
public class SchedulingOrderManager {

	/**
	 * Contains the rank list of all the vertices in an implementation
	 */
	private Map<ArchitectureComponent, Schedule> schedules = null;

	/**
	 * total order of the vertices in the implementation
	 */
	Schedule totalOrder = null;

	public SchedulingOrderManager() {

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

			int newSchedulingTotalOrder = getSchedulingTotalOrder(vertex);

			// Iterates the schedule
			Iterator<MapperDAGVertex> it = currentSched.iterator();
			int maxPrec = -1;

			while (it.hasNext()) {
				MapperDAGVertex current = it.next();

				// Looking for the preceding vertex with maximum total order
				int currentTotalOrder = getSchedulingTotalOrder(current);

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

		}

	}

	/**
	 * Appends the vertex at the end of a schedule and at the end of total order
	 */
	public void addVertex(MapperDAGVertex vertex) {

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

		}
	}

	/**
	 * Inserts vertex after previous
	 */
	public void insertVertexAfter(MapperDAGVertex previous,
			MapperDAGVertex vertex) {

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

	/**
	 * Gets the local scheduling order
	 */
	public int getSchedulingOrder(MapperDAGVertex vertex) {

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {

			Schedule sch = getSchedule(vertex.getImplementationVertexProperty()
					.getEffectiveComponent());
			if (sch != null){
				return sch.indexOf(vertex);
			}
		}

		return -1;
	}

	/**
	 * Gets the total scheduling order
	 */
	public int getSchedulingTotalOrder(MapperDAGVertex vertex) {
		return totalOrder.indexOf(vertex);
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

		return schedules.get(cmp);
	}

	/**
	 * Removes a given vertex
	 */
	public void remove(MapperDAGVertex vertex,
			boolean removeFromTotalOrder) {

		// If the vertex has an effective component,
		// removes it from the corresponding scheduling
		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {
			ArchitectureComponent cmp = vertex
					.getImplementationVertexProperty().getEffectiveComponent();
			Schedule sch = getSchedule(cmp);

			if (sch != null)
				sch.remove(vertex);
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
	}

	/**
	 * Adds the schedule corresponding to the vertex effective component if not
	 * present
	 */
	public void AddScheduleIfNotPresent(MapperDAGVertex vertex) {

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
	public void reconstructTotalOrderFromDAG(MapperDAG dag,
			MapperDAG implementation) {

		resetTotalOrder();
		
		ConcurrentSkipListSet<MapperDAGVertex> newTotalOrder = new ConcurrentSkipListSet<MapperDAGVertex>(
				new SchedulingOrderComparator());
		
		totalOrder.addAll(newTotalOrder);
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
	public void tagVertex(MapperDAGVertex vertex) {

		vertex.getImplementationVertexProperty().setSchedulingTotalOrder(
				totalOrder.indexOf(vertex));
	}
}
