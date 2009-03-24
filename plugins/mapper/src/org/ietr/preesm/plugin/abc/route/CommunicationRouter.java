/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.NodeRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.plugin.abc.edgescheduling.AbstractEdgeSched;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.impl.ImplementationTools;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.transaction.AddOverheadVertexTransaction;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.ietr.preesm.plugin.mapper.model.impl.TransferVertex;
import org.sdf4j.model.dag.DAGEdge;
import org.sdf4j.model.dag.DAGVertex;

/**
 * Routes the communications coming from or going to
 * 
 * Based on bridge design pattern
 * 
 * @author mpelcat
 */
public class CommunicationRouter extends AbstractCommunicationRouter {

	public static final int transferType = 0;
	public static final int overheadType = 1;

	private boolean handleOverheads = false;
	private RouteCalculator calculator = null;

	public CommunicationRouter(MultiCoreArchitecture archi,
			MapperDAG implementation, IEdgeSched edgeScheduler,
			SchedOrderManager orderManager, boolean handleOverheads) {
		super();
		this.handleOverheads = handleOverheads;
		this.calculator = new RouteCalculator(archi);

		// Initializing the available router implementers

		this.addImplementer(DmaRouteStep.id, new DmaComRouterImplementer(
				implementation, edgeScheduler, orderManager));
		this.addImplementer(MediumRouteStep.id, new MediumRouterImplementer(
				implementation, edgeScheduler, orderManager));
		this.addImplementer(NodeRouteStep.id, new MessageComRouterImplementer(
				implementation, edgeScheduler, orderManager));
	}

	public void routeNewVertex(MapperDAGVertex newVertex) {

		Map<MapperDAGEdge, Route> transferEdges = getRouteMap(newVertex);

		if (!transferEdges.isEmpty()) {
			// addVertices(transferEdges,"involvement");
			addVertices(transferEdges, transferType);
			if (handleOverheads) {
				addVertices(transferEdges, overheadType);
			}
		}
	}

	public Map<MapperDAGEdge, Route> getRouteMap(MapperDAGVertex newVertex) {
		Map<MapperDAGEdge, Route> transferEdges = new HashMap<MapperDAGEdge, Route>();

		Set<DAGEdge> edges = new HashSet<DAGEdge>();
		if (newVertex.incomingEdges() != null)
			edges.addAll(newVertex.incomingEdges());
		if (newVertex.outgoingEdges() != null)
			edges.addAll(newVertex.outgoingEdges());

		for (DAGEdge edge : edges) {

			if (!(edge instanceof PrecedenceEdge)) {
				ImplementationVertexProperty currentSourceProp = ((MapperDAGVertex) edge
						.getSource()).getImplementationVertexProperty();
				ImplementationVertexProperty currentDestProp = ((MapperDAGVertex) edge
						.getTarget()).getImplementationVertexProperty();

				if (currentSourceProp.hasEffectiveOperator()
						&& currentDestProp.hasEffectiveOperator()) {
					if (!currentSourceProp.getEffectiveOperator().equals(
							currentDestProp.getEffectiveOperator())) {
						MapperDAGEdge mapperEdge = (MapperDAGEdge) edge;
						transferEdges.put(mapperEdge, calculator
								.getRoute(mapperEdge));
					}
				}
			}
		}
		return transferEdges;
	}

	public void addVertices(Map<MapperDAGEdge, Route> transferEdges, int type) {
		TransactionManager localTransactionManager = new TransactionManager();

		for (MapperDAGEdge edge : transferEdges.keySet()) {
			int routeStepIndex = 0;
			Transaction lastTransaction = null;
			for (AbstractRouteStep step : transferEdges.get(edge)) {
				CommunicationRouterImplementer impl = getImplementer(step
						.getId());
				lastTransaction = impl.addVertices(step, edge,
						localTransactionManager, type, routeStepIndex,
						lastTransaction);

				routeStepIndex++;
			}
		}

		localTransactionManager.execute();
	}

}
