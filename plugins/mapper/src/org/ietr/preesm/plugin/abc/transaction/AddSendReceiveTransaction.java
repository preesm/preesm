/**
 * 
 */
package org.ietr.preesm.plugin.abc.transaction;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.MediumDefinition;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.ReceiveVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.ScheduleEdge;
import org.ietr.preesm.plugin.mapper.model.implementation.SendVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertex;

/**
 * A transaction that adds a send and a receive vertex in an implentation.
 * 
 * @author mpelcat
 */
public class AddSendReceiveTransaction extends Transaction {
	// Inputs
	/**
	 * Implementation DAG to which the vertex is added
	 */
	private MapperDAG implementation = null;

	/**
	 * Route step corresponding to this overhead
	 */
	private RouteStep step = null;

	/**
	 * Original edge corresponding to this overhead
	 */
	private MapperDAGEdge edge = null;

	/**
	 * manager keeping scheduling orders
	 */
	private SchedulingOrderManager orderManager = null;

	/**
	 * Cost of the transfer to give to the transfer vertex
	 */
	private int transferCost = 0;

	/**
	 * Index of the route step within its route
	 */
	private int routeIndex = 0;
	
	// Generated objects
	/**
	 * overhead vertex added
	 */
	private TransferVertex sendVertex = null;
	private TransferVertex receiveVertex = null;
	
	/**
	 * edges added
	 */
	private MapperDAGEdge newEdge1 = null;
	private MapperDAGEdge newEdge2 = null;
	private MapperDAGEdge newEdge3 = null;
	
	
	public AddSendReceiveTransaction(MapperDAGEdge edge,
			MapperDAG implementation, SchedulingOrderManager orderManager,
			int routeIndex, RouteStep step, int transferCost) {
		super();
		this.edge = edge;
		this.implementation = implementation;
		this.orderManager = orderManager;
		this.routeIndex = routeIndex;
		this.step = step;
		this.transferCost = transferCost;
	}

	@Override
	public void execute() {
		super.execute();

		MapperDAGVertex currentSource = (MapperDAGVertex)edge.getSource();
		MapperDAGVertex currentTarget = (MapperDAGVertex)edge.getTarget();

		String sendVertexID = "__send" + routeIndex + " (" + currentSource.getName()
				+ "," + currentTarget.getName() + ")";

		String receiveVertexID = "__receive" + routeIndex + " (" + currentSource.getName()
				+ "," + currentTarget.getName() + ")";
		
		Medium currentMedium = step.getMedium();

		if (edge instanceof ScheduleEdge) {
			PreesmLogger.getLogger().log(Level.INFO,
					"no transfer vertex corresponding to a schedule edge");
			return;
		}
		
		if (currentMedium != null) {

			MediumDefinition def = (MediumDefinition) currentMedium
					.getDefinition();

			if (def.hasMediumProperty()) {
				
				Operator senderOperator = step.getSender();
				Operator receiverOperator = step.getReceiver();
				
				sendVertex = new SendVertex(sendVertexID, implementation);
				sendVertex.setRouteStep(step);
				sendVertex.getTimingVertexProperty().setCost(transferCost);
				sendVertex.getImplementationVertexProperty().setEffectiveOperator(senderOperator);
				orderManager.insertVertexAfter(currentSource, sendVertex);
				implementation.addVertex(sendVertex);
				
				receiveVertex = new ReceiveVertex(receiveVertexID, implementation);
				receiveVertex.setRouteStep(step);
				receiveVertex.getTimingVertexProperty().setCost(transferCost);
				receiveVertex.getImplementationVertexProperty().setEffectiveOperator(receiverOperator);
				orderManager.insertVertexAfter(sendVertex, receiveVertex);
				implementation.addVertex(receiveVertex);

				newEdge1 = (MapperDAGEdge)implementation.addEdge(currentSource, sendVertex);
				newEdge2 = (MapperDAGEdge)implementation.addEdge(sendVertex, receiveVertex);
				newEdge3 = (MapperDAGEdge)implementation.addEdge(receiveVertex, currentTarget);

				newEdge1.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				newEdge2.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				newEdge3.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				
				newEdge1.getTimingEdgeProperty().setCost(0);
				newEdge2.getTimingEdgeProperty().setCost(0);
				newEdge3.getTimingEdgeProperty().setCost(0);

			}
		}
	}

	@Override
	public void undo() {
		super.undo();

		implementation.removeEdge(newEdge1);
		implementation.removeEdge(newEdge2);
		implementation.removeEdge(newEdge3);
		implementation.removeVertex(sendVertex);
		orderManager.removeVertex(sendVertex, true);
		implementation.removeVertex(receiveVertex);
		orderManager.removeVertex(receiveVertex, true);
	}


}
