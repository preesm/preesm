package org.ietr.preesm.mapper.abc.route.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.sf.dftools.architecture.slam.ComponentInstance;
import net.sf.dftools.architecture.slam.component.Dma;
import net.sf.dftools.workflow.tools.WorkflowLogger;

import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.edgescheduling.SimpleEdgeSched;
import org.ietr.preesm.mapper.abc.impl.ImplementationCleaner;
import org.ietr.preesm.mapper.abc.order.IScheduleElement;
import org.ietr.preesm.mapper.abc.route.AbstractCommunicationRouter;
import org.ietr.preesm.mapper.abc.route.CommunicationRouter;
import org.ietr.preesm.mapper.abc.route.CommunicationRouterImplementer;
import org.ietr.preesm.mapper.abc.transaction.AddOverheadVertexTransaction;
import org.ietr.preesm.mapper.abc.transaction.AddSendReceiveTransaction;
import org.ietr.preesm.mapper.abc.transaction.AddTransferVertexTransaction;
import org.ietr.preesm.mapper.abc.transaction.Transaction;
import org.ietr.preesm.mapper.abc.transaction.TransactionManager;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.impl.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.model.impl.TransferVertex;

/**
 * Class responsible to generate the suited vertices while simulating a dma
 * communication
 * 
 * @author mpelcat
 */
public class DmaComRouterImplementer extends CommunicationRouterImplementer {

	public DmaComRouterImplementer(AbstractCommunicationRouter user) {
		super(user);
	}

	@Override
	public void removeVertices(MapperDAGEdge edge,
			TransactionManager transactions) {

	}

	/**
	 * Careful!!! Only simple edge scheduler allowed for synchronized edges
	 */
	@Override
	public IEdgeSched getEdgeScheduler() {
		return new SimpleEdgeSched(getOrderManager());
	}

	/**
	 * Adds the simulation vertices for a given edge and a given route step
	 */
	@Override
	public Transaction addVertices(AbstractRouteStep routeStep,
			MapperDAGEdge edge, TransactionManager transactions, int type,
			int routeStepIndex, Transaction lastTransaction,
			List<Object> alreadyCreatedVertices) {

		if (routeStep instanceof DmaRouteStep) {
			// Adding the transfers
			DmaRouteStep dmaStep = ((DmaRouteStep) routeStep);

			// Adding the transfers of a dma route step
			if (type == CommunicationRouter.transferType) {
				// All the transfers along the path have the same time: the time
				// to transfer the data on the slowest contention node
				long transferTime = dmaStep.getWorstTransferTime(edge
						.getInitialEdgeProperty().getDataSize());
				List<ComponentInstance> nodes = dmaStep.getContentionNodes();
				AddTransferVertexTransaction transaction = null;

				for (ComponentInstance node : nodes) {
					int nodeIndex = nodes.indexOf(node);
					transaction = new AddTransferVertexTransaction("transfer",
							lastTransaction, getEdgeScheduler(), edge,
							getImplementation(), getOrderManager(),
							routeStepIndex, nodeIndex, routeStep, transferTime,
							node, true);
					transactions.add(transaction);
				}

				return transaction;
			} else if (type == CommunicationRouter.overheadType) {
				// Adding the overhead
				MapperDAGEdge incomingEdge = null;

				for (Object o : alreadyCreatedVertices) {
					if (o instanceof TransferVertex) {
						TransferVertex v = (TransferVertex) o;
						if (v.getSource().equals(edge.getSource())
								&& v.getTarget().equals(edge.getTarget())
								&& v.getRouteStep() == routeStep
								&& v.getNodeIndex() == 0) {
							// Finding the edge where to add an overhead
							incomingEdge = (MapperDAGEdge) v.incomingEdges()
									.toArray()[0];
						}

					}
				}

				Dma dmaDef = dmaStep.getDma();
				long overheadTime = dmaDef.getSetupTime();
				if (incomingEdge != null) {
					transactions.add(new AddOverheadVertexTransaction(
							incomingEdge, getImplementation(), routeStep,
							overheadTime, getOrderManager()));
				} else {
					WorkflowLogger
							.getLogger()
							.log(Level.FINE,
									"The transfer following vertex"
											+ edge.getSource()
											+ "was not found. We could not add overhead.");
				}

			} else if (type == CommunicationRouter.synchroType) {

				// Synchronizing the previously created transfers
				List<MapperDAGVertex> toSynchronize = new ArrayList<MapperDAGVertex>();

				for (Object o : alreadyCreatedVertices) {
					if (o instanceof TransferVertex) {
						TransferVertex v = (TransferVertex) o;
						if (v.getSource().equals(edge.getSource())
								&& v.getTarget().equals(edge.getTarget())
								&& v.getRouteStep() == routeStep) {
							toSynchronize.add(v);
						}

					}
				}

				// Synchronizing the vertices in order manager (they will all
				// have the same total order).
				if (toSynchronize.size() > 1) {
					ImplementationCleaner cleaner = new ImplementationCleaner(
							getOrderManager(), getImplementation());
					PrecedenceEdgeAdder adder = new PrecedenceEdgeAdder(
							getOrderManager(), getImplementation());
					IScheduleElement last = null;
					last = null;

					for (MapperDAGVertex v : toSynchronize) {
						cleaner.unscheduleVertex(v);
						last = getOrderManager().synchronize(last, v);
						adder.scheduleVertex(v);
					}

				}
			} else if (type == CommunicationRouter.sendReceiveType) {

				Transaction transaction = new AddSendReceiveTransaction(
						lastTransaction, edge, getImplementation(),
						getOrderManager(), routeStepIndex, routeStep,
						TransferVertex.SEND_RECEIVE_COST);

				transactions.add(transaction);
				return transaction;
			}
		}
		return null;
	}

}
