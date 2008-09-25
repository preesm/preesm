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
public class Schedule {

	/**
	 * List of the vertices in schedule
	 */
	private LinkedList<MapperDAGVertex> vertices;

	public Schedule() {

		vertices = new LinkedList<MapperDAGVertex>();
	}


	public LinkedList<MapperDAGVertex> getVertices() {
		return vertices;
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
