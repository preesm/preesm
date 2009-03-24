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

package org.ietr.preesm.plugin.abc.route.calcul;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.route.MediumRouteStep;
import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.core.architecture.simplemodel.Medium;
import org.ietr.preesm.core.architecture.simplemodel.MediumDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.plugin.mapper.model.MapperDAGEdge;
import org.ietr.preesm.plugin.mapper.model.MapperDAGVertex;

/**
 * This class can evaluate a given transfer and choose the best route between
 * two operators
 * 
 * @author mpelcat
 */
public class RouteCalculator {

	private MultiCoreArchitecture archi;

	/**
	 * Constructor from a given architecture
	 */
	public RouteCalculator(MultiCoreArchitecture archi) {

		this.archi = archi;
	}

	/**
	 * Choosing the medium with best speed between 2 operators.
	 */
	public Medium getDirectRoute(Operator op1, Operator op2) {

		Medium bestMedium = null;

		if (!op1.equals(op2)) {
			// List of all media between op1 and op2
			Set<Medium> media = archi.getMedia(op1, op2);
			MediumDefinition bestprop = null;

			if (!media.isEmpty()) {

				Iterator<Medium> iterator = media.iterator();

				// iterating media and choosing the one with best speed
				while (iterator.hasNext()) {
					Medium currentMedium = iterator.next();
					MediumDefinition currentProp = (MediumDefinition) currentMedium
							.getDefinition();

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
	 * Choosing a route between 2 operators and appending it to the list of
	 * already visited operators and to the route.
	 */
	public boolean appendRoute(List<Operator> alreadyVisited, Route route,
			Operator op1, Operator op2) {

		// Gets the fastest medium directly connecting op1 and op2
		Medium direct = getDirectRoute(op1, op2);

		if (direct != null) {
			// There is a best medium directly connecting op1 and op2
			// Adding it to the route
			route.add(new MediumRouteStep(op1, direct, op2));
			return true;
		} else {

			// There is no best medium directly connecting op1 and op2
			// Recursively appending best routes
			Iterator<ArchitectureComponent> iterator = archi.getComponents(
					ArchitectureComponentType.operator).iterator();
			Route bestSubRoute = null;

			// Iterating all operators
			while (iterator.hasNext()) {

				Operator op = (Operator) iterator.next();
				Medium m = getDirectRoute(op1, op);

				// If there is a direct route between op1 and the current op,
				// search a sub-route between op and op2
				if (m != null && !alreadyVisited.contains(op)) {
					alreadyVisited.add(op);

					Route subRoute = new Route();
					subRoute.add(new MediumRouteStep(op1, m, op));

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

	/**
	 * Choosing a route between 2 operators
	 */
	public Route getRoute(MapperDAGEdge edge) {
		MapperDAGVertex source = (MapperDAGVertex) edge.getSource();
		MapperDAGVertex target = (MapperDAGVertex) edge.getTarget();
		return getRoute(source.getImplementationVertexProperty()
				.getEffectiveOperator(), target
				.getImplementationVertexProperty().getEffectiveOperator());
	}

}
