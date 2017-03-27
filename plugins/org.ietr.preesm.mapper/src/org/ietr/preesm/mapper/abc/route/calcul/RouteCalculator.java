/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.mapper.abc.route.calcul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.component.Operator;
import org.ietr.dftools.architecture.slam.component.impl.ComNodeImpl;
import org.ietr.dftools.architecture.slam.link.Link;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.route.RouteStepFactory;
import org.ietr.preesm.core.architecture.util.DesignTools;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * This class can evaluate a given transfer and choose the best route between
 * two operators
 * 
 * @author mpelcat
 */
public class RouteCalculator {

	private static Map<Design, RouteCalculator> instances = new HashMap<Design, RouteCalculator>();

	private Design archi;

	private RoutingTable table = null;

	private RouteStepFactory stepFactory = null;

	private PreesmScenario scenario = null;

	public static RouteCalculator getInstance(Design archi,
			PreesmScenario scenario) {
		if (instances.get(archi) == null) {
			instances.put(archi, new RouteCalculator(archi, scenario));
		}
		return instances.get(archi);
	}

	public static void recalculate(Design archi, PreesmScenario scenario) {
		instances.put(archi, new RouteCalculator(archi, scenario));
	}

	public static void deleteRoutes(Design archi, PreesmScenario scenario) {
		instances.remove(archi);
	}

	/**
	 * Constructor from a given architecture
	 */
	private RouteCalculator(Design archi, PreesmScenario scenario) {

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
		WorkflowLogger.getLogger().log(Level.INFO, "creating route steps.");

		for (ComponentInstance c : DesignTools.getOperatorInstances(archi)) {
			ComponentInstance o = c;

			createRouteSteps(o);
		}
	}

	private void createRouteSteps(ComponentInstance source) {

		// Iterating on outgoing and undirected edges
		Set<Link> outgoingAndUndirected = new HashSet<Link>();
		outgoingAndUndirected.addAll(DesignTools.getUndirectedLinks(archi,
				source));
		outgoingAndUndirected.addAll(DesignTools.getOutgoingDirectedLinks(
				archi, source));

		for (Link i : outgoingAndUndirected) {
			if (DesignTools.getOtherEnd(i, source).getComponent() instanceof ComNodeImpl) {
				ComponentInstance node = DesignTools.getOtherEnd(i, source);

				List<ComponentInstance> alreadyVisitedNodes = new ArrayList<ComponentInstance>();
				alreadyVisitedNodes.add(node);
				exploreRoute(source, node, alreadyVisitedNodes);
			}
		}
	}

	private void exploreRoute(ComponentInstance source, ComponentInstance node,
			List<ComponentInstance> alreadyVisitedNodes) {

		// Iterating on outgoing and undirected edges
		Set<Link> outgoingAndUndirected = new HashSet<Link>();
		outgoingAndUndirected.addAll(DesignTools
				.getUndirectedLinks(archi, node));
		outgoingAndUndirected.addAll(DesignTools.getOutgoingDirectedLinks(
				archi, node));

		for (Link i : outgoingAndUndirected) {
			if (DesignTools.getOtherEnd(i, node).getComponent() instanceof ComNodeImpl) {
				ComponentInstance newNode = DesignTools.getOtherEnd(i, node);
				if (!alreadyVisitedNodes.contains(newNode)) {
					List<ComponentInstance> newAlreadyVisitedNodes = new ArrayList<ComponentInstance>(
							alreadyVisitedNodes);
					newAlreadyVisitedNodes.add(newNode);
					exploreRoute(source, newNode, newAlreadyVisitedNodes);
				}
			} else if (DesignTools.getOtherEnd(i, node).getComponent() instanceof Operator
					&& !DesignTools.getOtherEnd(i, node).getInstanceName()
							.equals(source.getInstanceName())) {
				ComponentInstance target = DesignTools.getOtherEnd(i, node);
				AbstractRouteStep step = stepFactory.getRouteStep(source,
						alreadyVisitedNodes, target);
				table.addRoute(source, target, new Route(step));
			}
		}
	}

	/**
	 * Building recursively the routes between the cores.
	 */
	private void createRoutes() {
		WorkflowLogger.getLogger().log(Level.INFO,
				"Initializing routing table.");

		floydWarshall(table, DesignTools.getOperatorInstances(archi));
	}

	/**
	 * The floydWarshall algorithm is used to add routes in the table in
	 * increasing order of cost.
	 */
	private void floydWarshall(RoutingTable table,
			Set<ComponentInstance> operators) {

		for (ComponentInstance k : operators) {

			for (ComponentInstance src : operators) {

				for (ComponentInstance tgt : operators) {

					if (!k.equals(src) && !k.equals(tgt) && !src.equals(tgt)) {
						Route routeSrcK = table.getBestRoute(src, k);
						Route routeKTgt = table.getBestRoute(k, tgt);

						if (routeSrcK != null && routeKTgt != null) {
							Route compoundRoute = new Route(routeSrcK,
									routeKTgt);
							if (compoundRoute.isSingleAppearance()) {
								long averageDataSize = scenario
										.getSimulationManager()
										.getAverageDataSize();
								// If this if statement is removed, several
								// routes become available
								if (table.getBestRoute(src, tgt) == null) {
									table.addRoute(src, tgt, compoundRoute);
								} else if (table.getBestRoute(src, tgt)
										.evaluateTransferCost(averageDataSize) > compoundRoute
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
	 * Returns true if route1 better than route2
	 */
	public boolean compareRoutes(Route route1, Route route2) {

		return route1.size() < route2.size();
	}

	/**
	 * Choosing a route between 2 operators
	 */
	public Route getRoute(ComponentInstance op1, ComponentInstance op2) {
		Route r = table.getBestRoute(op1, op2);

		if (r == null) {
			WorkflowLogger.getLogger()
					.log(Level.SEVERE,
							"Did not find a route between " + op1 + " and "
									+ op2 + ".");
		}

		return r;
	}

	/**
	 * Choosing a route between 2 operators
	 */
	public Route getRoute(MapperDAGEdge edge) {
		MapperDAGVertex source = (MapperDAGVertex) edge.getSource();
		MapperDAGVertex target = (MapperDAGVertex) edge.getTarget();
		ComponentInstance sourceOp = source
				.getEffectiveOperator();
		ComponentInstance targetOp = target
				.getEffectiveOperator();
		return getRoute(sourceOp, targetOp);
	}

}
