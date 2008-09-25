/**
 * 
 */
package org.ietr.preesm.plugin.mapper.model.implementation;

import java.util.Iterator;
import java.util.List;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.plugin.abc.order.SchedulingOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddPrecedenceEdgeTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

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
	public void addPrecedenceEdges(MapperDAG implementation, TransactionManager transactionManager) {

		Iterator<ArchitectureComponent> schedIt = orderManager.getArchitectureComponents()
				.iterator();

		// Iterates the schedules
		while (schedIt.hasNext()) {
			List<MapperDAGVertex> schedule = orderManager
					.getScheduleList(schedIt.next());

			Iterator<MapperDAGVertex> schedit = schedule.iterator();

			MapperDAGVertex src;

			// Iterates all vertices in each schedule
			if (schedit.hasNext()) {
				MapperDAGVertex dst = schedit.next();

				while (schedit.hasNext()) {

					src = dst;
					dst = schedit.next();

					if (implementation.getAllEdges(src, dst).isEmpty()) {
						// Adds a transaction
						Transaction transaction = new AddPrecedenceEdgeTransaction(implementation,src,dst);
						transactionManager.add(transaction);
					}
				}
			}
		}

		// Executes the transactions
		transactionManager.executeTransactionList();
	}

}
