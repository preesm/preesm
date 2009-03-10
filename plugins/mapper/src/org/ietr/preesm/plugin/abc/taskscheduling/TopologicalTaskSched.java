/**
 * 
 */
package org.ietr.preesm.plugin.abc.taskscheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Scheduling the tasks in topological order
 * 
 * @author mpelcat
 */
public class TopologicalTaskSched extends AbstractTaskSched {

	private static class TopoComparator implements Comparator<MapperDAGVertex>{

		@Override
		public int compare(MapperDAGVertex v0, MapperDAGVertex v1) {
			int compare;
			
			compare = v0.getInitialVertexProperty()
			.getTopologicalLevel() - v1.getInitialVertexProperty()
			.getTopologicalLevel();
			
			if(compare == 0){
				compare = v0.getName().compareTo(v1.getName());
			}
			
			return compare;
		}
		
	}
	
	private List<MapperDAGVertex> topolist = null;

	public TopologicalTaskSched(SchedOrderManager orderManager) {
		super(orderManager);
	}

	public void createTopology(MapperDAG dag) {
		topolist = new ArrayList<MapperDAGVertex>();

		TopologicalDAGIterator topoDAGIterator = new TopologicalDAGIterator(dag);

		while (topoDAGIterator.hasNext()) {
			MapperDAGVertex v = (MapperDAGVertex) topoDAGIterator.next();
			topolist.add(v);
			if (v.incomingEdges().isEmpty()) {
				v.getInitialVertexProperty().setTopologicalLevel(0);
			} else {
				MapperDAGVertex vertex = (MapperDAGVertex) ((DAGEdge) v
						.incomingEdges().toArray()[0]).getSource();
				int precedentLevel = vertex.getInitialVertexProperty()
						.getTopologicalLevel();
				v.getInitialVertexProperty().setTopologicalLevel(
						precedentLevel + 1);
			}
		}
		
		Collections.sort(topolist, new TopoComparator());
	}

	@Override
	public void insertVertex(MapperDAGVertex vertex) {
		int topoOrder = topolist.indexOf(vertex);
		if (topolist != null && topoOrder > 0) {

			topoOrder--;
			while (topoOrder >= 0) {
				MapperDAGVertex previousCandidate = topolist.get(topoOrder);
				int totalOrder = orderManager.totalIndexOf(previousCandidate);
				if (orderManager.getTotalOrder().contains(previousCandidate)) {
					orderManager.insertVertexAfter(orderManager.getVertex(totalOrder), vertex);
					break;
				}
				topoOrder--;
			}
		} else {
			orderManager.addLast(vertex);
		}
	}
}
