/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2016)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.mapper.abc.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.DmaRouteStep;
import org.ietr.preesm.core.architecture.route.MemRouteStep;
import org.ietr.preesm.core.architecture.route.MessageRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.route.calcul.RouteCalculator;
import org.ietr.preesm.mapper.abc.route.impl.DmaComRouterImplementer;
import org.ietr.preesm.mapper.abc.route.impl.MessageComRouterImplementer;
import org.ietr.preesm.mapper.abc.route.impl.SharedRamRouterImplementer;
import org.ietr.preesm.mapper.abc.transaction.Transaction;
import org.ietr.preesm.mapper.abc.transaction.TransactionManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;

// TODO: Auto-generated Javadoc
/**
 * Routes the communications. Based on bridge design pattern. The processing is delegated to implementers
 *
 * @author mpelcat
 */
public class CommunicationRouter extends AbstractCommunicationRouter {

  /** The Constant transferType. */
  public static final int transferType = 0;

  /** The Constant overheadType. */
  public static final int overheadType = 1;

  /** The Constant sendReceiveType. */
  public static final int sendReceiveType = 2;

  /** The Constant synchroType. */
  public static final int synchroType = 3;

  /** The Constant involvementType. */
  public static final int involvementType = 4;

  /** The calculator. */
  private RouteCalculator calculator = null;

  /**
   * Instantiates a new communication router.
   *
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   * @param implementation
   *          the implementation
   * @param edgeScheduler
   *          the edge scheduler
   * @param orderManager
   *          the order manager
   */
  public CommunicationRouter(final Design archi, final PreesmScenario scenario, final MapperDAG implementation, final IEdgeSched edgeScheduler,
      final OrderManager orderManager) {
    super(implementation, edgeScheduler, orderManager);
    this.calculator = RouteCalculator.getInstance(archi, scenario);

    // Initializing the available router implementers
    addImplementer(DmaRouteStep.type, new DmaComRouterImplementer(this));
    addImplementer(MessageRouteStep.type, new MessageComRouterImplementer(this));
    addImplementer(MemRouteStep.type, new SharedRamRouterImplementer(this));
  }

  /**
   * adds all the necessary communication vertices with the given type.
   *
   * @param implementation
   *          the implementation
   * @param type
   *          the type
   */
  @Override
  public void routeAll(final MapperDAG implementation, final Integer type) {
    final TransactionManager localTransactionManager = new TransactionManager();

    // Get edges in scheduling order of their producers
    Iterator<MapperDAGVertex> dagIterator = this.orderManager.getTotalOrder().getList().iterator();
    List<DAGEdge> edgesInPrecedenceOrder = new ArrayList<DAGEdge>(implementation.edgeSet().size());

    while (dagIterator.hasNext()) {
      DAGVertex vertex = dagIterator.next();
      edgesInPrecedenceOrder.addAll(vertex.outgoingEdges());
    }

    if (edgesInPrecedenceOrder.size() != this.implementation.edgeSet().size()) {
      // If this happens, this means that not all edges are covered by the previous while loop.
      throw new RuntimeException("Some DAG edges are not covered");
    }

    // We iterate the edges and process the ones with different allocations
    final Iterator<DAGEdge> iterator = edgesInPrecedenceOrder.iterator();
    while (iterator.hasNext()) {
      final MapperDAGEdge currentEdge = (MapperDAGEdge) iterator.next();

      if (!(currentEdge instanceof PrecedenceEdge) && (currentEdge.getInit().getDataSize() != 0)) {
        final MapperDAGVertex currentSource = ((MapperDAGVertex) currentEdge.getSource());
        final MapperDAGVertex currentDest = ((MapperDAGVertex) currentEdge.getTarget());

        if (currentSource.hasEffectiveOperator() && currentDest.hasEffectiveOperator()) {
          if (!currentSource.getEffectiveOperator().equals(currentDest.getEffectiveOperator())) {
            // Adds several transfers for one edge depending on the
            // route steps
            final Route route = this.calculator.getRoute(currentEdge);
            int routeStepIndex = 0;
            Transaction lastTransaction = null;

            // Adds send and receive vertices and links them
            for (final AbstractRouteStep step : route) {
              final CommunicationRouterImplementer impl = getImplementer(step.getType());
              lastTransaction = impl.addVertices(step, currentEdge, localTransactionManager, type, routeStepIndex, lastTransaction, null);
              routeStepIndex++;
            }
          }
        }
      }
    }

    localTransactionManager.execute();
  }

  /**
   * adds all the necessary communication vertices with the given type affected by the mapping of newVertex.
   *
   * @param newVertex
   *          the new vertex
   * @param types
   *          the types
   */
  @Override
  public void routeNewVertex(final MapperDAGVertex newVertex, final List<Integer> types) {

    final Map<MapperDAGEdge, Route> transferEdges = getRouteMap(newVertex);
    final List<Object> createdVertices = new ArrayList<>();

    if (!transferEdges.isEmpty()) {
      for (final Integer type : types) {
        addVertices(transferEdges, type, createdVertices);
      }
    }
  }

  /**
   * Creates a map associating to each edge to be routed the corresponding route.
   *
   * @param newVertex
   *          the new vertex
   * @return the route map
   */
  public Map<MapperDAGEdge, Route> getRouteMap(final MapperDAGVertex newVertex) {
    final Map<MapperDAGEdge, Route> transferEdges = new LinkedHashMap<>();

    final Set<DAGEdge> edges = new LinkedHashSet<>();
    if (newVertex.incomingEdges() != null) {
      edges.addAll(newVertex.incomingEdges());
    }
    if (newVertex.outgoingEdges() != null) {
      edges.addAll(newVertex.outgoingEdges());
    }

    for (final DAGEdge edge : edges) {

      if (!(edge instanceof PrecedenceEdge)) {
        final MapperDAGVertex currentSource = ((MapperDAGVertex) edge.getSource());
        final MapperDAGVertex currentDest = ((MapperDAGVertex) edge.getTarget());

        if (currentSource.hasEffectiveOperator() && currentDest.hasEffectiveOperator()) {
          if (!currentSource.getEffectiveOperator().equals(currentDest.getEffectiveOperator())) {
            final MapperDAGEdge mapperEdge = (MapperDAGEdge) edge;
            transferEdges.put(mapperEdge, this.calculator.getRoute(mapperEdge));
          }
        }
      }
    }
    return transferEdges;
  }

  /**
   * Adds the dynamic vertices to simulate the transfers of the given edges.
   *
   * @param transferEdges
   *          the transfer edges
   * @param type
   *          the type
   * @param createdVertices
   *          the created vertices
   */
  public void addVertices(final Map<MapperDAGEdge, Route> transferEdges, final int type, final List<Object> createdVertices) {
    final TransactionManager localTransactionManager = new TransactionManager(createdVertices);

    for (final MapperDAGEdge edge : transferEdges.keySet()) {
      int routeStepIndex = 0;
      Transaction lastTransaction = null;
      for (final AbstractRouteStep step : transferEdges.get(edge)) {
        final CommunicationRouterImplementer impl = getImplementer(step.getType());
        lastTransaction = impl.addVertices(step, edge, localTransactionManager, type, routeStepIndex, lastTransaction, createdVertices);

        routeStepIndex++;
      }
    }

    localTransactionManager.execute();
  }

  /**
   * Evaluates the transfer between two operators.
   *
   * @param edge
   *          the edge
   * @return the long
   */
  @Override
  public long evaluateTransferCost(final MapperDAGEdge edge) {

    final MapperDAGVertex source = ((MapperDAGVertex) edge.getSource());
    final MapperDAGVertex dest = ((MapperDAGVertex) edge.getTarget());

    final ComponentInstance sourceOp = source.getEffectiveOperator();
    final ComponentInstance destOp = dest.getEffectiveOperator();

    long cost = 0;

    // Retrieving the route
    if ((sourceOp != null) && (destOp != null)) {
      final Route route = this.calculator.getRoute(sourceOp, destOp);
      cost = route.evaluateTransferCost(edge.getInit().getDataSize());
    } else {
      WorkflowLogger.getLogger().log(Level.SEVERE, "trying to evaluate a transfer between non mapped operators.");
    }

    return cost;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.route.AbstractCommunicationRouter#getRoute(org.ietr.preesm.mapper. model.MapperDAGEdge)
   */
  @Override
  public Route getRoute(final MapperDAGEdge edge) {
    return this.calculator.getRoute(edge);
  }

}
