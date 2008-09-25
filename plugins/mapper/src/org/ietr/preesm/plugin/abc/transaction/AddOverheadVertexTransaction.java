/**
 * 
 */
package org.ietr.preesm.plugin.abc.transaction;

import java.util.logging.Level;

import org.ietr.preesm.core.architecture.MediumDefinition;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.abc.SchedulingOrderManager;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.OverheadVertex;
import org.ietr.preesm.plugin.mapper.model.implementation.PrecedenceEdge;

/**
 * Transaction executing the addition of an overhead (or set-up) vertex.
 * 
 * @author mpelcat
 */
public class AddOverheadVertexTransaction extends Transaction {
	
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
	
	// Generated objects
	/**
	 * overhead vertex added
	 */
	private OverheadVertex oVertex = null;
	
	/**
	 * edges added
	 */
	private MapperDAGEdge newInEdge = null;
	private MapperDAGEdge newOutEdge = null;
	
	
	public AddOverheadVertexTransaction(MapperDAGEdge edge,
			MapperDAG implementation, RouteStep step,SchedulingOrderManager orderManager) {
		super();
		this.edge = edge;
		this.implementation = implementation;
		this.step = step;
		this.orderManager = orderManager;
	}

	@Override
	public void execute() {

		super.execute();
		
		MapperDAGVertex currentSource = (MapperDAGVertex)edge.getSource();
		MapperDAGVertex currentTarget = (MapperDAGVertex)edge.getTarget();

		MediumDefinition mediumDef = (MediumDefinition) step.getMedium()
				.getDefinition();

		if (edge instanceof PrecedenceEdge) {
			PreesmLogger.getLogger().log(Level.INFO,
					"no overhead vertex corresponding to a schedule edge");
			return;
		}

		String overtexID = "__overhead (" + currentSource.getName() + ","
				+ currentTarget.getName() + ")";

		if (mediumDef.hasMediumProperty()) {
			oVertex = new OverheadVertex(overtexID, implementation);

			oVertex.getTimingVertexProperty().setCost(
					mediumDef.getMediumProperty().getOverhead());

			oVertex.getImplementationVertexProperty().setEffectiveOperator(
					step.getSender());

			orderManager.insertVertexAfter(currentSource, oVertex);

			implementation.addVertex(oVertex);
			
			newInEdge = (MapperDAGEdge)implementation.addEdge(currentSource, oVertex);
			newOutEdge = (MapperDAGEdge)implementation.addEdge(oVertex, currentTarget);

			newInEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
			newOutEdge.setInitialEdgeProperty(edge.getInitialEdgeProperty().clone());
			
			newInEdge.getTimingEdgeProperty().setCost(0);
			newOutEdge.getTimingEdgeProperty().setCost(0);
		}
		
	}

	@Override
	public void undo() {
		super.undo();
		
		implementation.removeEdge(newInEdge);
		implementation.removeEdge(newOutEdge);
		implementation.removeVertex(oVertex);
		orderManager.removeVertex(oVertex, true);
	}

}
