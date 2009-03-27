/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/

package org.ietr.preesm.plugin.abc.route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.NodeRouteStep;
import org.ietr.preesm.core.architecture.route.RamRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.scenario.IScenario;
import org.ietr.preesm.core.tools.PreesmLogger;
import org.ietr.preesm.plugin.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.plugin.abc.order.SchedOrderManager;
import org.ietr.preesm.plugin.abc.route.calcul.RouteCalculator;
import org.ietr.preesm.plugin.abc.route.impl.DmaComRouterImplementer;
import org.ietr.preesm.plugin.abc.route.impl.MediumRouterImplementer;
import org.ietr.preesm.plugin.abc.route.impl.MessageComRouterImplementer;
import org.ietr.preesm.plugin.abc.route.impl.SharedRamRouterImplementer;
import org.ietr.preesm.plugin.abc.transaction.Transaction;
import org.ietr.preesm.plugin.abc.transaction.TransactionManager;
import org.ietr.preesm.plugin.mapper.model.ImplementationVertexProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAG;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;
import org.ietr.preesm.plugin.mapper.model.impl.PrecedenceEdge;
import org.sdf4j.model.dag.DAGEdge;

/**
 * Routes the communications. Based on bridge design pattern. The processing is
 * delegated to implementers
 * 
 * @author mpelcat
 */
public class CommunicationRouter extends AbstractCommunicationRouter {

	public static final int transferType = 0;
	public static final int overheadType = 1;
	public static final int sendReceive = 2;

	private RouteCalculator calculator = null;

	public CommunicationRouter(MultiCoreArchitecture archi, IScenario scenario,
			MapperDAG implementation, IEdgeSched edgeScheduler,
			SchedOrderManager orderManager) {
		super(implementation, edgeScheduler, orderManager);
		this.calculator = RouteCalculator.getInstance(archi, scenario);

		// Initializing the available router implementers
		this.addImplementer(DmaRouteStep.type,
				new DmaComRouterImplementer(this));
		this.addImplementer(MediumRouteStep.type, new MediumRouterImplementer(
				this));
		this.addImplementer(NodeRouteStep.type,
				new MessageComRouterImplementer(this));
		this.addImplementer(RamRouteStep.type,
				new SharedRamRouterImplementer(this));
	}

	/**
	 * adds all the necessary communication vertices with the given type
	 */
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
					if (!currentSourceProp.getEffectiveOperator().equals(
							currentDestProp.getEffectiveOperator())) {
						// Adds several transfers for one edge depending on the
						// route steps
						Route route = calculator.getRoute(currentEdge);
						int routeStepIndex = 0;
						Transaction lastTransaction = null;

						// Adds send and receive vertices and links them
						for (AbstractRouteStep step : route) {
							CommunicationRouterImplementer impl = getImplementer(step
									.getType());
							lastTransaction = impl.addVertices(step,
									currentEdge, localTransactionManager, type,
									routeStepIndex, lastTransaction);
							routeStepIndex++;
						}
					}
				}
			}
		}

		localTransactionManager.execute();
	}

	/**
	 * adds all the necessary communication vertices with the given type
	 * affected by the mapping of newVertex
	 */
	public void routeNewVertex(MapperDAGVertex newVertex, List<Integer> types) {

		Map<MapperDAGEdge, Route> transferEdges = getRouteMap(newVertex);

		if (!transferEdges.isEmpty()) {
			for (Integer type : types) {
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
						.getType());
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

		long cost = 0;

		// Retrieving the route
		if (sourceOp != null && destOp != null) {
			Route route = calculator.getRoute(sourceOp, destOp);
			cost = route.evaluateTransfer(edge.getInitialEdgeProperty()
					.getDataSize());
		} else {
			PreesmLogger
					.getLogger()
					.log(Level.SEVERE,
							"trying to evaluate a transfer between non mapped operators.");
		}

		return cost;
	}

}
