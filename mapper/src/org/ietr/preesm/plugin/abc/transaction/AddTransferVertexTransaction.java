/**
 * 
 */
package org.ietr.preesm.plugin.abc.transaction;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.MediumDefinition;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.ScheduleEdge;
import org.ietr.preesm.plugin.mapper.model.implementation.TransferVertex;

/**
 * A transaction that adds one transfer vertex in an implementation
 * 
 * @author mpelcat
 */
public class AddTransferVertexTransaction extends Transaction {
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
	private TransferVertex tVertex = null;
	
	/**
	 * edges added
	 */
	private MapperDAGEdge newInEdge = null;
	private MapperDAGEdge newOutEdge = null;
	
	
	public AddTransferVertexTransaction(MapperDAGEdge edge,
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

		String tvertexID = "__transfer" + routeIndex + " (" + currentSource.getName()
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
				tVertex = new TransferVertex(tvertexID, implementation);

				tVertex.setRouteStep(step);

				tVertex.getTimingVertexProperty().setCost(transferCost);

				tVertex.getImplementationVertexProperty().setEffectiveMedium(
						currentMedium);

				orderManager.insertVertexAfter(currentSource, tVertex);
				
				implementation.addVertex(tVertex);

				newInEdge = (MapperDAGEdge)implementation.addEdge(currentSource, tVertex);
				newOutEdge = (MapperDAGEdge)implementation.addEdge(tVertex, currentTarget);

				newInEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				newOutEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
				
				newInEdge.getTimingEdgeProperty().setCost(0);
				newOutEdge.getTimingEdgeProperty().setCost(0);

			}
		}
	}

	@Override
	public void undo() {
		super.undo();

		implementation.removeEdge(newInEdge);
		implementation.removeEdge(newOutEdge);
		implementation.removeVertex(tVertex);
		orderManager.removeVertex(tVertex, true);
	}

}
