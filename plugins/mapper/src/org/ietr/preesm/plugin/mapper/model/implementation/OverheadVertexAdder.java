package org.ietr.preesm.plugin.mapper.model.implementation;

import java.util.Iterator;

import org.ietr.preesm.plugin.abc.SchedulingOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddOverheadVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Adds overheads and schedules them on cores
 * 
 * @author mpelcat
 */
public class OverheadVertexAdder {

	private SchedulingOrderManager orderManager;

	public OverheadVertexAdder(SchedulingOrderManager orderManager) {
		super();
		this.orderManager = orderManager;
	}

	/**
	 * Adds all necessary overhead vertices
	 */
	public void addOverheadVertices(MapperDAG implementation, TransactionManager transactionManager) {

		// We iterate the edges and process the ones with a transfer vertex as
		// destination
		Iterator<DAGEdge> iterator = implementation.edgeSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge)iterator.next();

			if (!(currentEdge instanceof ScheduleEdge)
					&& currentEdge.getTarget() instanceof TransferVertex) {

				TransferVertex tvertex = (TransferVertex) currentEdge
						.getTarget();

				transactionManager.add(new AddOverheadVertexTransaction(currentEdge,implementation, tvertex.getRouteStep(), orderManager));
				
			}
		}

		transactionManager.executeTransactionList();
	}
}
