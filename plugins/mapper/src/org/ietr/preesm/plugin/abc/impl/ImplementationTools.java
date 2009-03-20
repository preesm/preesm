/**
 * 
 */
package org.ietr.preesm.plugin.abc.impl;

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Useful graph manipulation methods
 * 
 * @author mpelcat
 */
public class ImplementationTools {


	/**
	 * Gets all transfers from routes coming from or going to vertex. Do not
	 * execute if overheads are present
	 */
	public static Set<DAGVertex> getAllTransfers(MapperDAGVertex vertex) {

		Set<DAGVertex> transfers = new HashSet<DAGVertex>();

		transfers.addAll(getPrecedingTransfers(vertex));
		transfers.addAll(getFollowingTransfers(vertex));

		return transfers;
	}

	/**
	 * Gets all transfers preceding vertex. Recursive function
	 */
	private static Set<DAGVertex> getPrecedingTransfers(MapperDAGVertex vertex) {

		Set<DAGVertex> transfers = new HashSet<DAGVertex>();

		for (DAGEdge edge : vertex.incomingEdges()) {
			if (!(edge instanceof PrecedenceEdge)) {
				MapperDAGVertex v = (MapperDAGVertex) edge.getSource();
				if (v instanceof TransferVertex) {
					transfers.add(v);
					transfers.addAll(getPrecedingTransfers(v));
				}
			}
		}

		return transfers;
	}

	/**
	 * Gets all transfers following vertex. Recursive function
	 */
	private static Set<DAGVertex> getFollowingTransfers(MapperDAGVertex vertex) {

		Set<DAGVertex> transfers = new HashSet<DAGVertex>();

		for (DAGEdge edge : vertex.outgoingEdges()) {
			if (!(edge instanceof PrecedenceEdge)) {
				MapperDAGVertex v = (MapperDAGVertex) edge.getTarget();
				if (v instanceof TransferVertex) {
					transfers.add(v);
					transfers.addAll(getFollowingTransfers(v));
				}
			}
		}

		return transfers;
	}
}
