/**
 * 
 */
package org.ietr.preesm.plugin.abc.impl;

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

		for (DAGVertex v : ImplementationTools.getAllTransfers(vertex)) {
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

		for (DAGVertex v : ImplementationTools.getAllTransfers(vertex)) {
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

	public void unscheduleVertex(MapperDAGVertex vertex) {

		MapperDAGVertex prev = orderManager.getPreviousVertex(vertex);
		MapperDAGVertex next = orderManager.getNextVertex(vertex);
		PrecedenceEdgeAdder precEdgeAdder = new PrecedenceEdgeAdder(orderManager);

		if(prev != null){
			precEdgeAdder.removePrecedenceEdge(implementation, prev, vertex);
		}
		
		if(next != null){
			precEdgeAdder.removePrecedenceEdge(implementation, vertex, next);
		}

		Set<DAGEdge> edges = implementation.getAllEdges(prev, next);

		if ((prev != null && next != null) && (edges == null || edges.isEmpty())){
			precEdgeAdder.addPrecedenceEdge(implementation, prev, next);
		}

	}
}
