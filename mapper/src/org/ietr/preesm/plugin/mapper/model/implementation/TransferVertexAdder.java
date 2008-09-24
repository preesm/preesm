package org.ietr.preesm.plugin.mapper.model.implementation;

import java.util.Iterator;

import org.ietr.preesm.core.architecture.Route;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.plugin.abc.CommunicationRouter;
import org.ietr.preesm.plugin.abc.SchedulingOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddSendReceiveTransaction;
import org.ietr.preesm.plugin.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.sdf4j.model.dag.DAGEdge;

/**
 * The TransferVertexAdder creates the vertices allowing edge scheduling
 * 
 * @author mpelcat   
 */
public class TransferVertexAdder {

	private CommunicationRouter router;

	private SchedulingOrderManager orderManager;

	/**
	 * True if we want to add a send and a receive operation,
	 * false if a simple transfer vertex should be added  
	 */
	private boolean sendReceive;

	public TransferVertexAdder(CommunicationRouter router,
			SchedulingOrderManager orderManager, boolean sendReceive) {
		super();
		this.router = router;
		this.orderManager = orderManager;
		this.sendReceive = sendReceive;
	}

	/**
	 * Adds all necessary transfer vertices
	 */
	public void addTransferVertices(MapperDAG implementation, TransactionManager transactionManager) {

		// We iterate the edges and process the ones with different allocations
		Iterator<DAGEdge> iterator = implementation.edgeSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge)iterator.next();

			if (!(currentEdge instanceof ScheduleEdge)) {
				ImplementationVertexProperty currentSourceProp = ((MapperDAGVertex)currentEdge
						.getSource()).getImplementationVertexProperty();
				ImplementationVertexProperty currentDestProp = ((MapperDAGVertex)currentEdge
						.getTarget()).getImplementationVertexProperty();

				if (currentSourceProp.hasEffectiveOperator()
						&& currentDestProp.hasEffectiveOperator()) {
					if (currentSourceProp.getEffectiveOperator() != currentDestProp
							.getEffectiveOperator()) {
						// Adds several transfers for one edge depending on the route steps
						addTransferVertices(currentEdge, implementation,
								 transactionManager);
					}
				}
			}
		}

		transactionManager.executeTransactionList();
	}

	/**
	 * Adds one transfer vertex per route step. It does not remove the original
	 * edge
	 */
	public void addTransferVertices(MapperDAGEdge edge, MapperDAG implementation,
			TransactionManager transactionManager) {

		MapperDAGVertex currentSource = (MapperDAGVertex)edge.getSource();
		MapperDAGVertex currentDest = (MapperDAGVertex)edge.getTarget();

		Route route = router.getRoute(currentSource
				.getImplementationVertexProperty().getEffectiveOperator(),
				currentDest.getImplementationVertexProperty()
						.getEffectiveOperator());


		Iterator<RouteStep> it = route.iterator();
		int i = 1;

		while (it.hasNext()) {
			RouteStep step = it.next();
			
			int transferCost = router.evaluateTransfer(edge, step.getSender(), step
					.getReceiver());
			
			Transaction transaction = null;
			
			if(sendReceive)
				transaction = new AddSendReceiveTransaction(edge,implementation,orderManager,i,step,transferCost);
			else
				transaction = new AddTransferVertexTransaction(edge,implementation,orderManager,i,step,transferCost);
				
			transactionManager.add(transaction);
			
			i++;
		}
	}
}
