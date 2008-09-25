/**
 * 
 */
package org.ietr.preesm.plugin.abc.order;

import java.util.LinkedList;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * A schedule represents the consecutive tasks mapped on a single
 * {@link ArchitectureComponent}
 * @author mpelcat
 */
public class Schedule extends LinkedList<MapperDAGVertex> {

	public Schedule() {

		super();
	}
	
	/**
	 * Gets the previous vertex in the current schedule
	 */
	public MapperDAGVertex getPreviousVertex(MapperDAGVertex vertex) {
		if (indexOf(vertex) <= 0)
			return null;
		return (get(indexOf(vertex) - 1));
	}

	/**
	 * Appends a vertex at the end of the schedule
	 */
	public void addVertex(MapperDAGVertex vertex) {
		if (!contains(vertex))
			addLast(vertex);
	}

	/**
	 * Inserts a vertex at the beginning of the schedule
	 */
	public void addVertexFirst(MapperDAGVertex vertex) {
		if (!contains(vertex))
			addFirst(vertex);
	}

	/**
	 * Inserts a vertex after the given one
	 */
	public void insertVertexAfter(MapperDAGVertex previous,
			MapperDAGVertex vertex) {

		if (!contains(vertex))
			if (indexOf(previous) >= 0) {
				if (indexOf(previous) + 1 < size())
					add(indexOf(previous) + 1, vertex);
				else
					addLast(vertex);
			}
	}

	@Override
	public String toString() {
		return "{" + super.toString() + "}";
	}
}
