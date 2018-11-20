/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2014)
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
package org.preesm.algorithm.mapper.abc.route.calcul;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.preesm.algorithm.core.scenario.PreesmScenario;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.Design;
import org.preesm.model.slam.component.Operator;
import org.preesm.model.slam.component.impl.ComNodeImpl;
import org.preesm.model.slam.link.Link;
import org.preesm.model.slam.route.AbstractRouteStep;
import org.preesm.model.slam.route.Route;
import org.preesm.model.slam.route.RouteStepFactory;
import org.preesm.model.slam.utils.DesignTools;

/**
 * This class can evaluate a given transfer and choose the best route between two operators.
 *
 * @author mpelcat
 */
public class RouteCalculator {

  /** The instances. */
  private static Map<Design, RouteCalculator> instances = new LinkedHashMap<>();

  /** The archi. */
  private final Design archi;

  /** The table. */
  private RoutingTable table = null;

  /** The step factory. */
  private RouteStepFactory stepFactory = null;

  /** The scenario. */
  private PreesmScenario scenario = null;

  /**
   * Gets the single instance of RouteCalculator.
   *
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   * @return single instance of RouteCalculator
   */
  public static RouteCalculator getInstance(final Design archi, final PreesmScenario scenario) {
    if (RouteCalculator.instances.get(archi) == null) {
      RouteCalculator.instances.put(archi, new RouteCalculator(archi, scenario));
    }
    return RouteCalculator.instances.get(archi);
  }

  /**
   * Recalculate.
   *
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   */
  public static void recalculate(final Design archi, final PreesmScenario scenario) {
    RouteCalculator.instances.put(archi, new RouteCalculator(archi, scenario));
  }

  /**
   * Delete routes.
   *
   * @param archi
   *          the archi
   */
  public static void deleteRoutes(final Design archi) {
    RouteCalculator.instances.remove(archi);
  }

  /**
   * Constructor from a given architecture.
   *
   * @param archi
   *          the archi
   * @param scenario
   *          the scenario
   */
  private RouteCalculator(final Design archi, final PreesmScenario scenario) {

    this.archi = archi;
    this.table = new RoutingTable(scenario);
    this.stepFactory = new RouteStepFactory(archi);
    this.scenario = scenario;

    // Creating the route steps between directly connected operators
    createRouteSteps();
    // Concatenation of route steps to generate optimal routes using
    // the Floyd Warshall algorithm
    createRoutes();
  }

  /**
   * Creating recursively the route steps from the architecture.
   */
  private void createRouteSteps() {
    PreesmLogger.getLogger().log(Level.INFO, "creating route steps.");

    for (final ComponentInstance c : DesignTools.getOperatorInstances(this.archi)) {
      final ComponentInstance o = c;

      createRouteSteps(o);
    }
  }

  /**
   * Creates the route steps.
   *
   * @param source
   *          the source
   */
  private void createRouteSteps(final ComponentInstance source) {

    // Iterating on outgoing and undirected edges
    final Set<Link> outgoingAndUndirected = new LinkedHashSet<>();
    outgoingAndUndirected.addAll(DesignTools.getUndirectedLinks(this.archi, source));
    outgoingAndUndirected.addAll(DesignTools.getOutgoingDirectedLinks(this.archi, source));

    for (final Link i : outgoingAndUndirected) {
      if (DesignTools.getOtherEnd(i, source).getComponent() instanceof ComNodeImpl) {
        final ComponentInstance node = DesignTools.getOtherEnd(i, source);

        final List<ComponentInstance> alreadyVisitedNodes = new ArrayList<>();
        alreadyVisitedNodes.add(node);
        exploreRoute(source, node, alreadyVisitedNodes);
      }
    }
  }

  /**
   * Explore route.
   *
   * @param source
   *          the source
   * @param node
   *          the node
   * @param alreadyVisitedNodes
   *          the already visited nodes
   */
  private void exploreRoute(final ComponentInstance source, final ComponentInstance node,
      final List<ComponentInstance> alreadyVisitedNodes) {

    // Iterating on outgoing and undirected edges
    final Set<Link> outgoingAndUndirected = new LinkedHashSet<>();
    outgoingAndUndirected.addAll(DesignTools.getUndirectedLinks(this.archi, node));
    outgoingAndUndirected.addAll(DesignTools.getOutgoingDirectedLinks(this.archi, node));

    for (final Link i : outgoingAndUndirected) {
      if (DesignTools.getOtherEnd(i, node).getComponent() instanceof ComNodeImpl) {
        final ComponentInstance newNode = DesignTools.getOtherEnd(i, node);
        if (!alreadyVisitedNodes.contains(newNode)) {
          final List<ComponentInstance> newAlreadyVisitedNodes = new ArrayList<>(alreadyVisitedNodes);
          newAlreadyVisitedNodes.add(newNode);
          exploreRoute(source, newNode, newAlreadyVisitedNodes);
        }
      } else if ((DesignTools.getOtherEnd(i, node).getComponent() instanceof Operator)
          && !DesignTools.getOtherEnd(i, node).getInstanceName().equals(source.getInstanceName())) {
        final ComponentInstance target = DesignTools.getOtherEnd(i, node);
        final AbstractRouteStep step = this.stepFactory.getRouteStep(source, alreadyVisitedNodes, target);
        this.table.addRoute(source, target, new Route(step));
      }
    }
  }

  /**
   * Building recursively the routes between the cores.
   */
  private void createRoutes() {
    PreesmLogger.getLogger().log(Level.INFO, "Initializing routing table.");

    floydWarshall(this.table, DesignTools.getOperatorInstances(this.archi));
  }

  /**
   * The floydWarshall algorithm is used to add routes in the table in increasing order of cost.
   *
   * @param table
   *          the table
   * @param operators
   *          the operators
   */
  private void floydWarshall(final RoutingTable table, final Set<ComponentInstance> operators) {

    for (final ComponentInstance k : operators) {

      for (final ComponentInstance src : operators) {

        for (final ComponentInstance tgt : operators) {

          if (!k.equals(src) && !k.equals(tgt) && !src.equals(tgt)) {
            final Route routeSrcK = table.getBestRoute(src, k);
            final Route routeKTgt = table.getBestRoute(k, tgt);

            if ((routeSrcK != null) && (routeKTgt != null)) {
              final Route compoundRoute = new Route(routeSrcK, routeKTgt);
              if (compoundRoute.isSingleAppearance()) {
                final long averageDataSize = this.scenario.getSimulationManager().getAverageDataSize();
                // If this if statement is removed, several
                // routes become available
                if (table.getBestRoute(src, tgt) == null) {
                  table.addRoute(src, tgt, compoundRoute);
                } else if (table.getBestRoute(src, tgt).evaluateTransferCost(averageDataSize) > compoundRoute
                    .evaluateTransferCost(averageDataSize)) {
                  table.removeRoutes(src, tgt);
                  table.addRoute(src, tgt, compoundRoute);
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Returns true if route1 better than route2.
   *
   * @param route1
   *          the route 1
   * @param route2
   *          the route 2
   * @return true, if successful
   */
  public boolean compareRoutes(final Route route1, final Route route2) {

    return route1.size() < route2.size();
  }

  /**
   * Choosing a route between 2 operators.
   *
   * @param op1
   *          the op 1
   * @param op2
   *          the op 2
   * @return the route
   */
  public Route getRoute(final ComponentInstance op1, final ComponentInstance op2) {
    final Route r = this.table.getBestRoute(op1, op2);

    if (r == null) {
      final String msg = "Did not find a route between " + op1 + " and " + op2 + ".";
      PreesmLogger.getLogger().log(Level.SEVERE, msg);
    }

    return r;
  }

  /**
   * Choosing a route between 2 operators.
   *
   * @param edge
   *          the edge
   * @return the route
   */
  public Route getRoute(final MapperDAGEdge edge) {
    final MapperDAGVertex source = (MapperDAGVertex) edge.getSource();
    final MapperDAGVertex target = (MapperDAGVertex) edge.getTarget();
    final ComponentInstance sourceOp = source.getEffectiveOperator();
    final ComponentInstance targetOp = target.getEffectiveOperator();
    return getRoute(sourceOp, targetOp);
  }

}
