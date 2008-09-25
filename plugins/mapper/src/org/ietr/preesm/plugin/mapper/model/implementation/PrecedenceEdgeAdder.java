/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.implementation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The edge adder automatically generates edges between vertices
 * successive on a single operator. It can also remove all the edges of
 * type PrecedenceEdgeAdder from the graph
 * 
 * @author mpelcat   
 */
public class PrecedenceEdgeAdder {

	private SchedulingOrderManager orderManager;

	public PrecedenceEdgeAdder(SchedulingOrderManager orderManager) {
		super();

		this.orderManager = orderManager;
	}

	/**
	 * Adds all necessary schedule edges to an implementation respecting
	 * the order given by the scheduling order manager.
	 */
	public void addPrecedenceEdges(MapperDAG implementation) {

		Iterator<ArchitectureComponent> schedIt = orderManager.getArchitectureComponents()
				.iterator();

		while (schedIt.hasNext()) {
			List<MapperDAGVertex> schedule = orderManager
					.getScheduleList(schedIt.next());

			Iterator<MapperDAGVertex> schedit = schedule.iterator();

			MapperDAGVertex src;

			if (schedit.hasNext()) {
				MapperDAGVertex dst = schedit.next();

				while (schedit.hasNext()) {

					src = dst;
					dst = schedit.next();

					if (implementation.getAllEdges(src, dst).isEmpty()) {
						PrecedenceEdge sEdge = new PrecedenceEdge();
						sEdge.getTimingEdgeProperty().setCost(0);
						implementation.addEdge(src, dst, sEdge);
					}
				}
			}
		}

	}

	/**
	 * Deletes all the edges of implementation with type PrecedenceEdgeAdderEdge
	 */
	public void deletePrecedenceEdges(MapperDAG implementation) {

		Iterator<DAGEdge> iterator = implementation.edgeSet().iterator();
		Set<MapperDAGEdge> edgeset = new HashSet<MapperDAGEdge>();
		MapperDAGEdge currentEdge;

		while (iterator.hasNext()) {
			currentEdge = (MapperDAGEdge) iterator.next();
			if (currentEdge instanceof PrecedenceEdge)
				edgeset.add(currentEdge);
		}

		implementation.removeAllEdges(edgeset);
	}

	/**
	 * Deletes all the incoming edges of vertex with type PrecedenceEdgeAdderEdge
	 */
	public void deletePrecedenceIncomingEdges(MapperDAG implementation,
			MapperDAGVertex vertex) {

		Iterator<DAGEdge> iterator = vertex.incomingEdges().iterator();
		Set<MapperDAGEdge> edgeset = new HashSet<MapperDAGEdge>();
		MapperDAGEdge currentEdge;

		while (iterator.hasNext()) {
			currentEdge = (MapperDAGEdge) iterator.next();

			if (currentEdge instanceof PrecedenceEdge)
				edgeset.add(currentEdge);
		}

		implementation.removeAllEdges(edgeset);
	}

}
