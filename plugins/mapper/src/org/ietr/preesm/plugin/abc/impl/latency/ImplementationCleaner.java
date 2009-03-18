/**
 * 
 */
package org.ietr.preesm.plugin.abc.impl.latency;

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.RemoveVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Class cleaning an implementation i.e. removing added transfers and edges
 * 
 * @author mpelcat
 */
public class ImplementationCleaner {

	private SchedOrderManager orderManager;
	private MapperDAG implementation;
	private TransactionManager transactionManager;

	public ImplementationCleaner(SchedOrderManager orderManager,MapperDAG implementation) {
		super();
		this.orderManager = orderManager;
		this.implementation = implementation;
		this.transactionManager = new TransactionManager();
	}
	
	/**
	 * Removes all transfers from routes coming from or going to vertex
	 */
	public void removeAllTransfers(MapperDAGVertex vertex) {

		for (DAGVertex v : getAllTransfers(vertex)) {
			if (v instanceof TransferVertex) {
				transactionManager.add(new RemoveVertexTransaction(
						(MapperDAGVertex) v, implementation, orderManager),
						null);

			}
		}

		transactionManager.execute();
		transactionManager.clear();
	}

	/**
	 * Removes all overheads from routes coming from or going to vertex
	 */
	public void removeAllOverheads(MapperDAGVertex vertex) {

		for (DAGVertex v : getAllTransfers(vertex)) {
			if (v instanceof TransferVertex) {
				MapperDAGVertex o = ((TransferVertex) v).getPrecedingOverhead();
				if (o != null && o instanceof OverheadVertex) {
					transactionManager.add(new RemoveVertexTransaction(o,
							implementation, orderManager), null);
				}
			}
		}

		transactionManager.execute();
		transactionManager.clear();
	}

	/**
	 * Gets all transfers from routes coming from or going to vertex. Do not
	 * execute if overheads are present
	 */
	public Set<DAGVertex> getAllTransfers(MapperDAGVertex vertex) {

		Set<DAGVertex> transfers = new HashSet<DAGVertex>();

		transfers.addAll(getPrecedingTransfers(vertex));
		transfers.addAll(getFollowingTransfers(vertex));

		return transfers;
	}

	/**
	 * Gets all transfers preceding vertex. Recursive function
	 */
	public Set<DAGVertex> getPrecedingTransfers(MapperDAGVertex vertex) {

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
	public Set<DAGVertex> getFollowingTransfers(MapperDAGVertex vertex) {

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

	public void unscheduleVertex(MapperDAGVertex vertex) {

		MapperDAGVertex prev = orderManager.getPreviousVertex(vertex);
		MapperDAGVertex next = orderManager.getNextVertex(vertex);


		if(prev != null){
			PrecedenceEdgeAdder.removePrecedenceEdge(implementation, prev, vertex);
		}
		
		if(next != null){
			PrecedenceEdgeAdder.removePrecedenceEdge(implementation, vertex, next);
		}

		Set<DAGEdge> edges = implementation.getAllEdges(prev, next);

		if ((prev != null && next != null) && (edges == null || edges.isEmpty())){
			PrecedenceEdgeAdder.addPrecedenceEdge(implementation, prev, next);
		}

	}
}
