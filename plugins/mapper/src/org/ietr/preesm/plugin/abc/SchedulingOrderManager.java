/**
 * 
 */
package org.ietr.preesm.plugin.abc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;

/**
 * The scheduling order manager keeps a total order of the vertices and
 * a partial order in each schedule. It is used by the schedule edge
 * adder to insert schedule edges
 *         
 * @author mpelcat
 */
public class SchedulingOrderManager {

	/**
	 * A schedule is a list of successive vertices executed on a single
	 * processor
	 */
	private class Schedule {

		/**
		 * List of the vertices in schedule
		 */
		private LinkedList<MapperDAGVertex> vertices;

		public Schedule() {

			vertices = new LinkedList<MapperDAGVertex>();
		}

		/**
		 * Test if the vertex belongs to this schedule
		 */
		public boolean hasVertex(MapperDAGVertex vertex) {
			return (vertices.contains(vertex));
		}

		/**
		 * Gets the previous vertex in the current schedule
		 */
		public MapperDAGVertex getPreviousVertex(MapperDAGVertex vertex) {
			if (vertices.indexOf(vertex) <= 0)
				return null;
			return (vertices.get(vertices.indexOf(vertex) - 1));
		}

		/**
		 * Appends a vertex at the end of the schedule
		 */
		public void addVertex(MapperDAGVertex vertex) {
			if (!vertices.contains(vertex))
				vertices.addLast(vertex);
		}

		/**
		 * Inserts a vertex at the beginning of the schedule
		 */
		public void addVertexFirst(MapperDAGVertex vertex) {
			if (!vertices.contains(vertex))
				vertices.addFirst(vertex);
		}

		/**
		 * Inserts a vertex after the given one
		 */
		public void insertVertexAfter(MapperDAGVertex previous,
				MapperDAGVertex vertex) {

			if (!vertices.contains(vertex))
				if (vertices.indexOf(previous) >= 0) {
					if (vertices.indexOf(previous) + 1 < vertices.size())
						vertices.add(vertices.indexOf(previous) + 1, vertex);
					else
						vertices.addLast(vertex);
				}
		}

		/**
		 * Returns the order of the given vertex
		 */
		public int orderOf(MapperDAGVertex vertex) {

			return vertices.indexOf(vertex);
		}

		/**
		 * Removes the given vertex
		 */
		public void removeVertex(MapperDAGVertex vertex) {

			vertices.remove(vertex);

		}

		@Override
		public String toString() {
			return "{" + vertices.toString() + "}";
		}
	}

	/**
	 * Contains the rank list of all the vertices in an implantation
	 */
	private Map<ArchitectureComponent, Schedule> schedules = null;

	/**
	 * total order of the vertices in the implantation
	 */
	private LinkedList<MapperDAGVertex> totalOrder = null;

	public SchedulingOrderManager() {

		schedules = new HashMap<ArchitectureComponent, Schedule>();
		totalOrder = new LinkedList<MapperDAGVertex>();
	}

	/**
	 * Considering that vertex already has a total order, inserts it at the
	 * appropriate position
	 */
	public void insertVertexInTotalOrder(MapperDAGVertex vertex) {

		AddScheduleIfNotPresent(vertex);

		ImplementationVertexProperty currImpProp = vertex
				.getImplementationVertexProperty();

		if (currImpProp.hasEffectiveComponent()) {

			Schedule currentSched = getSchedule(currImpProp
					.getEffectiveComponent());

			Iterator<MapperDAGVertex> it = currentSched.vertices.iterator();
			int maxPrec = -1;

			while (it.hasNext()) {
				MapperDAGVertex current = it.next();

				int currentTotalOrder = getSchedulingTotalOrder(current);

				if (currentTotalOrder >= 0
						&& currentTotalOrder < getSchedulingTotalOrder(vertex)
						&& currentTotalOrder > maxPrec)
					maxPrec = currentTotalOrder;
			}

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

		if (prevImpProp.hasEffectiveComponent()) {
			if (currImpProp.hasEffectiveComponent()) {

				if (!totalOrder.contains(vertex))
					if (totalOrder.indexOf(previous) >= 0) {
						if (totalOrder.indexOf(previous) + 1 < totalOrder
								.size())
							totalOrder.add(totalOrder.indexOf(previous) + 1,
									vertex);
						else
							totalOrder.addLast(vertex);
					}

				insertVertexInTotalOrder(vertex);

			}
		}

	}

	/**
	 * Gets the local scheduling order
	 */
	public int getSchedulingOrder(MapperDAGVertex vertex) {

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {

			Schedule sch = getSchedule(vertex.getImplementationVertexProperty()
					.getEffectiveComponent());
			if (sch != null)
				return sch.orderOf(vertex);
			else
				return -1;
		} else
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
	public Set<ArchitectureComponent> getComponents() {

		return schedules.keySet();

	}

	/**
	 * Gets the schedule of a given component
	 */
	public List<MapperDAGVertex> getScheduleList(ArchitectureComponent cmp) {

		return getSchedule(cmp).vertices;

	}

	/**
	 * Gets the schedule of a given component
	 */
	public Schedule getSchedule(ArchitectureComponent cmp) {

		Iterator<ArchitectureComponent> it = schedules.keySet().iterator();

		while (it.hasNext()) {
			ArchitectureComponent currentCmp = it.next();
			if (currentCmp.equals(cmp))
				return schedules.get(currentCmp);
		}

		return null;
	}

	/**
	 * Removes a given vertex
	 */
	public void removeVertex(MapperDAGVertex vertex,
			boolean removeFromTotalOrder) {

		if (vertex.getImplementationVertexProperty().hasEffectiveComponent()) {
			ArchitectureComponent cmp = vertex.getImplementationVertexProperty()
					.getEffectiveComponent();
			Schedule sch = getSchedule(cmp);

			if (sch != null)
				sch.removeVertex(vertex);
		} else {
			Iterator<Schedule> it = schedules.values().iterator();

			while (it.hasNext()) {
				it.next().removeVertex(vertex);
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

		if (currImpProp.hasEffectiveComponent()) {
			ArchitectureComponent effectiveCmp = currImpProp
					.getEffectiveComponent();

			if (getSchedule(effectiveCmp) == null)
				schedules.put(effectiveCmp, new Schedule());
		}

	}

	/**
	 * Reconstructs the total order using the total order stored in DAG
	 */
	public void reconstructTotalOrderFromDAG(MapperDAG dag,
			MapperDAG implantation) {

		resetTotalOrder();

		int currentOrder = 0;
		int numberOfRemainingVertices = dag.vertexSet().size();

		while (true) {
			TopologicalDAGIterator it = new TopologicalDAGIterator(dag);

			while (it.hasNext()) {
				MapperDAGVertex v = (MapperDAGVertex)it.next();

				if (v.getImplementationVertexProperty().getSchedulingTotalOrder() == currentOrder) {

					MapperDAGVertex internalVertex = implantation.getMapperDAGVertex(v
							.getName());

					addVertex(internalVertex);
					numberOfRemainingVertices -= 1;
					break;
				}
			}

			if (numberOfRemainingVertices == 0)
				break;

			currentOrder++;
		}

	}

	/**
	 * Returns the vertex of the given schedule that immediately precedes vertex
	 */
	public MapperDAGVertex previousInTotalOrder(Schedule currentSched,
			MapperDAGVertex vertex) {
		MapperDAGVertex previous = null;

		while (previous != null && !currentSched.hasVertex(previous)) {
			previous = currentSched.getPreviousVertex(previous);
		}

		return previous;
	}

	/**
	 * Sets the total order of each implantation property in DAG
	 */
	public void tagDAG(MapperDAG dag) {
		Iterator<MapperDAGVertex> iterator = totalOrder.iterator();

		while (iterator.hasNext()) {
			MapperDAGVertex internalVertex = iterator.next();
			MapperDAGVertex vertex = dag.getMapperDAGVertex(internalVertex.getName());

			if (vertex != null) {
				tagVertex(vertex);
			}
		}
	}

	/**
	 * Sets the total order of vertex implantation property in DAG
	 */
	public void tagVertex(MapperDAGVertex vertex) {
		
		vertex.getImplementationVertexProperty().setSchedulingTotalOrder(totalOrder.indexOf(vertex));
	}
}
