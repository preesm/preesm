package org.ietr.preesm.plugin.abc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.ietr.preesm.core.architecture.IArchitecture;
import org.ietr.preesm.core.architecture.Medium;
import org.ietr.preesm.core.architecture.MediumDefinition;
import org.ietr.preesm.core.architecture.MediumProperty;
import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.Route;
import org.ietr.preesm.core.architecture.RouteStep;
import org.ietr.preesm.core.log.PreesmLogger;
import org.ietr.preesm.plugin.mapper.model.InitialEdgeProperty;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;

/**
 *	This class can evaluate a given transfer and choose the best route
 *	between two operators
 *   
 *	@author mpelcat
 */
public class CommunicationRouter {

	private IArchitecture archi;

	/**
	 * Constructor from a given architecture
	 */
	public CommunicationRouter(IArchitecture archi) {

		this.archi = archi;
	}

	/**
	 * Evaluates the transfer between two operators
	 */
	public int evaluateTransfer(MapperDAGEdge edge, Operator op1, Operator op2) {

		// Retrieving the route
		Route route = getRoute(op1, op2);
		int cost = 0;

		Iterator<RouteStep> it = route.iterator();

		// Iterating the route and incrementing transfer cost
		while (it.hasNext()) {
			RouteStep step = it.next();

			cost += evaluateSingleTransfer(edge, step);
		}

		return cost;
	}

	/**
	 * Evaluates the transfer between two operators
	 */
	public int evaluateSingleTransfer(MapperDAGEdge edge, RouteStep step) {

		Operator sender = step.getSender();
		Operator receiver = step.getReceiver();
		Medium medium = step.getMedium();

		if (medium != null) {
			MediumDefinition def = (MediumDefinition) medium.getDefinition();
			InitialEdgeProperty edgeprop = edge.getInitialEdgeProperty();
			Integer datasize = edgeprop.getDataSize();

			if (def.hasMediumProperty()) {
				Float time = datasize.floatValue()
						* def.getMediumProperty().getInvSpeed();

				return time.intValue();
			} else {
				PreesmLogger.getLogger().log(Level.SEVERE,
						"There is no time property for medium " + def.getId());
				return 0;
			}
		} else {

			PreesmLogger.getLogger().log(
					Level.SEVERE,
					"Data could not be correctly transfered from "
							+ sender.getName() + " to " + receiver.getName());

			return 0;
		}
	}

	/**
	 * Choosing the medium with best speed between 2 operators. 
	 */
	public Medium getDirectRoute(Operator op1, Operator op2) {

		Medium bestMedium = null;

		if (!op1.equals(op2)) {
			// List of all media between op1 and op2
			Set<Medium> media = archi.getMedia(op1, op2);
			MediumProperty bestprop = null;

			if (!media.isEmpty()) {

				Iterator<Medium> iterator = media.iterator();

				// iterating media and choosing the one with best speed
				while (iterator.hasNext()) {
					Medium currentMedium = iterator.next();
					MediumProperty currentProp = ((MediumDefinition) currentMedium
							.getDefinition()).getMediumProperty();

					if (bestprop == null
							|| bestprop.getInvSpeed() > currentProp
									.getInvSpeed()) {
						bestprop = currentProp;
						bestMedium = currentMedium;
					}
				}

			}

		}

		return bestMedium;
	}

	/**
	 * Choosing a route between 2 operators and appending it 
	 * to the list of already visited operators and to the route.
	 */
	public boolean appendRoute(List<Operator> alreadyVisited,
			Route route, Operator op1, Operator op2) {

		// Gets the fastest medium directly connecting op1 and op2
		Medium direct = getDirectRoute(op1, op2);

		if (direct != null) {
			// There is a best medium directly connecting op1 and op2
			// Adding it to the route
			route.add(new RouteStep(op1, direct, op2));
			return true;
		} else {

			// There is no best medium directly connecting op1 and op2
			// Recursively appending best routes
			Iterator<Operator> iterator = archi.getOperators().iterator();
			Route bestSubRoute = null;

			// Iterating all operators
			while (iterator.hasNext()) {
				
				Operator op = iterator.next();
				Medium m = getDirectRoute(op1, op);

				// If there is a direct route between op1 and the current op,
				// search a sub-route between op and op2
				if (m != null && !alreadyVisited.contains(op)) {
					alreadyVisited.add(op);

					Route subRoute = new Route();
					subRoute.add(new RouteStep(op1, m, op));

					if (appendRoute(new ArrayList<Operator>(alreadyVisited),
							subRoute, op, op2)) {

						if (bestSubRoute == null
								|| compareRoutes(subRoute, bestSubRoute)) {
							bestSubRoute = subRoute;
						}
					}
				}
			}

			if (bestSubRoute != null) {
				route.addAll(bestSubRoute);
				return true;
			}
		}

		return false;
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
	public Route getRoute(Operator op1, Operator op2) {
		Route route = new Route();
		List<Operator> alreadyVisited = new ArrayList<Operator>();

		// appends the route between op1 and op2 to an empty route
		if (appendRoute(alreadyVisited, route, op1, op2))
			return route;
		else
			return null;
	}

}
