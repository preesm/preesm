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
 
package org.ietr.preesm.plugin.abc.taskscheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.tools.TopologicalDAGIterator;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Scheduling the tasks in topological order and alphabetical order
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
				int precedentLevel = 0;
				for(DAGEdge edge : v.incomingEdges()){
					MapperDAGVertex precVertex = (MapperDAGVertex) edge.getSource();
					precedentLevel = Math.max(precedentLevel, precVertex.getInitialVertexProperty()
							.getTopologicalLevel());
					v.getInitialVertexProperty().setTopologicalLevel(
							precedentLevel + 1);
				}
			}
		}
		
		Collections.sort(topolist, new TopoComparator());
	}

	@Override
	public void insertVertex(MapperDAGVertex vertex) {
		int topoOrder = topolist.indexOf(vertex);
		boolean inserted = false;
		
		if (topolist != null && topoOrder >= 0) {

			topoOrder--;
			while (topoOrder >= 0) {
				MapperDAGVertex previousCandidate = topolist.get(topoOrder);
				int totalOrder = orderManager.totalIndexOf(previousCandidate);
				if (orderManager.getTotalOrder().contains(previousCandidate)) {
					orderManager.insertVertexAfter(orderManager.getVertex(totalOrder), vertex);
					inserted = true;
					break;
				}
				topoOrder--;
			}
			
			if(!inserted && vertex.getInitialVertexProperty().getTopologicalLevel() == 0){
				orderManager.addFirst(vertex);
			}
		} else {
			orderManager.addLast(vertex);
		}
	}
}
