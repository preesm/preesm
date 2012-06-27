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

package org.ietr.preesm.mapper.abc.taskscheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import net.sf.dftools.algorithm.model.dag.DAGVertex;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Scheduling the tasks in topological order and alphabetical order
 * 
 * @author mpelcat
 */
public class TopologicalTaskSched extends AbstractTaskSched {

	private VertexOrderList initList = null;
	private List<MapperDAGVertex> topolist = null;

	private static class InitListComparator implements
			Comparator<MapperDAGVertex> {

		private VertexOrderList initList = null;

		public InitListComparator(VertexOrderList initlist) {
			super();
			this.initList = initlist;
		}

		@Override
		public int compare(MapperDAGVertex v0, MapperDAGVertex v1) {
			int compare;

			compare = initList.orderOf(v0.getName())
					- initList.orderOf(v1.getName());

			return compare;
		}

	}

	public TopologicalTaskSched(VertexOrderList initlist) {
		this.initList = initlist;
	}

	/**
	 * Reuse infinite homogeneous order
	 */

	public List<MapperDAGVertex> createTopology(MapperDAG dag) {
		topolist = new ArrayList<MapperDAGVertex>();

		for (DAGVertex v : dag.vertexSet()) {
			topolist.add((MapperDAGVertex) v);

			if (!initList.contains(v.getName())) {
				WorkflowLogger.getLogger().log(Level.SEVERE,
						"problem with topological ordering.");
			}
		}

		Collections.sort(topolist, new InitListComparator(initList));

		return topolist;
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
				if (totalOrder >= 0) {
					orderManager.insertAtIndex(totalOrder + 1, vertex);
					inserted = true;
					break;
				}
				topoOrder--;
			}

			if (!inserted && vertex.getPredecessors(false).isEmpty()) {
				orderManager.addFirst(vertex);
			}
		} else {
			orderManager.addLast(vertex);
		}
	}
}
