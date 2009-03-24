/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.NodeRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.route.calcul.RouteCalculator;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.sdf4j.model.dag.DAGEdge;

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
	public static final int sendReceive = 2;

	private RouteCalculator calculator = null;

	public CommunicationRouter(MultiCoreArchitecture archi,
			MapperDAG implementation, IEdgeSched edgeScheduler,
			SchedOrderManager orderManager) {
		super(implementation,edgeScheduler,orderManager);
		this.calculator = RouteCalculator.getInstance(archi);

		// Initializing the available router implementers

		this.addImplementer(DmaRouteStep.id, new DmaComRouterImplementer(this));
		this.addImplementer(MediumRouteStep.id, new MediumRouterImplementer(this));
		this.addImplementer(NodeRouteStep.id, new MessageComRouterImplementer(this));
	}

	public void routeAll(MapperDAG implementation, Integer type) {
		TransactionManager localTransactionManager = new TransactionManager();

		// We iterate the edges and process the ones with different allocations
		Iterator<DAGEdge> iterator = implementation.edgeSet().iterator();

		while (iterator.hasNext()) {
			MapperDAGEdge currentEdge = (MapperDAGEdge) iterator.next();

			if (!(currentEdge instanceof PrecedenceEdge)) {
				ImplementationVertexProperty currentSourceProp = ((MapperDAGVertex) currentEdge
						.getSource()).getImplementationVertexProperty();
				ImplementationVertexProperty currentDestProp = ((MapperDAGVertex) currentEdge
						.getTarget()).getImplementationVertexProperty();

				if (currentSourceProp.hasEffectiveOperator()
						&& currentDestProp.hasEffectiveOperator()) {
					if (currentSourceProp.getEffectiveOperator() != currentDestProp
							.getEffectiveOperator()) {
						// Adds several transfers for one edge depending on the
						// route steps
						Route route = calculator.getRoute(currentEdge);
						int routeStepIndex = 0;
						Transaction lastTransaction = null;

						for (AbstractRouteStep step : route) {
							CommunicationRouterImplementer impl = getImplementer(step
									.getId());
							lastTransaction = impl.addVertices(step, currentEdge,
									localTransactionManager, type,
									routeStepIndex, lastTransaction);
							routeStepIndex++;
						}
					}
				}
			}
		}

		localTransactionManager.execute();
	}

	public void routeNewVertex(MapperDAGVertex newVertex, List<Integer> types) {

		Map<MapperDAGEdge, Route> transferEdges = getRouteMap(newVertex);

		if (!transferEdges.isEmpty()) {
			for(Integer type:types){
				addVertices(transferEdges, type);
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

	/**
	 * Evaluates the transfer between two operators
	 */
	public long evaluateTransfer(MapperDAGEdge edge) {

		ImplementationVertexProperty sourceimp = ((MapperDAGVertex) edge
				.getSource()).getImplementationVertexProperty();
		ImplementationVertexProperty destimp = ((MapperDAGVertex) edge
				.getTarget()).getImplementationVertexProperty();

		Operator sourceOp = sourceimp.getEffectiveOperator();
		Operator destOp = destimp.getEffectiveOperator();

		long cost = 1000000000;

		// Retrieving the route
		if (sourceOp != null && destOp != null) {
			Route route = calculator.getRoute(sourceOp, destOp);
			cost = 0;
			// Iterating the route and incrementing transfer cost
			for (AbstractRouteStep step : route) {
				CommunicationRouterImplementer impl = getImplementer(step
						.getId());
				cost += impl.evaluateSingleTransfer(edge,
						(MediumRouteStep) step);
			}
		}

		return cost;
	}

}
