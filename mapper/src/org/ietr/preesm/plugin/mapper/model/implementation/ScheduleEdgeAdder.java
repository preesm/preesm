/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.implementation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.abc.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The edge adder automatically generates edges between vertices
 * successive on a single operator. It can also remove all the edges of
 * type ScheduleEdge from the graph
 * 
 * @author mpelcat   
 */
public class ScheduleEdgeAdder {

	private SchedulingOrderManager orderManager;

	public ScheduleEdgeAdder(SchedulingOrderManager orderManager) {
		super();

		this.orderManager = orderManager;
	}

	/**
	 * Adds all necessary schedule edges to an implantation respecting
	 * the order given by the scheduling order manager.
	 */
	public void addScheduleEdges(MapperDAG implantation) {

		Iterator<ArchitectureComponent> schedIt = orderManager.getComponents()
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

					if (implantation.getAllEdges(src, dst).isEmpty()) {
						ScheduleEdge sEdge = new ScheduleEdge();
						sEdge.getTimingEdgeProperty().setCost(0);
						implantation.addEdge(src, dst, sEdge);
					}
				}
			}
		}

	}

	/**
	 * Deletes all the edges of implantation with type ScheduleEdge
	 */
	public void deleteScheduleEdges(MapperDAG implantation) {

		Iterator<DAGEdge> iterator = implantation.edgeSet().iterator();
		Set<MapperDAGEdge> edgeset = new HashSet<MapperDAGEdge>();
		MapperDAGEdge currentEdge;

		while (iterator.hasNext()) {
			currentEdge = (MapperDAGEdge) iterator.next();
			if (currentEdge instanceof ScheduleEdge)
				edgeset.add(currentEdge);
		}

		implantation.removeAllEdges(edgeset);
	}

	/**
	 * Deletes all the incoming edges of vertex with type ScheduleEdge
	 */
	public void deleteScheduleIncomingEdges(MapperDAG implantation,
			MapperDAGVertex vertex) {

		Iterator<DAGEdge> iterator = vertex.incomingEdges().iterator();
		Set<MapperDAGEdge> edgeset = new HashSet<MapperDAGEdge>();
		MapperDAGEdge currentEdge;

		while (iterator.hasNext()) {
			currentEdge = (MapperDAGEdge) iterator.next();

			if (currentEdge instanceof ScheduleEdge)
				edgeset.add(currentEdge);
		}

		implantation.removeAllEdges(edgeset);
	}

}
