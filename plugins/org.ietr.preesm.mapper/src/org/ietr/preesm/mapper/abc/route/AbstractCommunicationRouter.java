/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2016)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.mapper.abc.route;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.preesm.core.architecture.route.Route;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;

/**
 * Routes a communication and creates the necessary communication vertices.
 * 
 * @author mpelcat
 */
public abstract class AbstractCommunicationRouter {

	/**
	 * Several ways to simulate a communication depending on which Route is
	 * taken into account.
	 */
	private Map<String, CommunicationRouterImplementer> implementers;

	/**
	 * DAG with communication vertices
	 */
	protected MapperDAG implementation = null;

	/**
	 * manager of the generated transfers scheduling
	 */
	protected IEdgeSched edgeScheduler = null;

	/**
	 * manager of the vertices order in general
	 */
	protected OrderManager orderManager = null;

	public AbstractCommunicationRouter(MapperDAG implementation,
			IEdgeSched edgeScheduler, OrderManager orderManager) {
		super();
		this.implementers = new HashMap<String, CommunicationRouterImplementer>();
		setManagers(implementation, edgeScheduler, orderManager);
	}

	protected void addImplementer(String name,
			CommunicationRouterImplementer implementer) {
		implementers.put(name, implementer);
	}

	protected CommunicationRouterImplementer getImplementer(String name) {
		return implementers.get(name);
	}

	public MapperDAG getImplementation() {
		return implementation;
	}

	public IEdgeSched getEdgeScheduler() {
		return edgeScheduler;
	}

	public OrderManager getOrderManager() {
		return orderManager;
	}

	public void setManagers(MapperDAG implementation, IEdgeSched edgeScheduler,
			OrderManager orderManager) {
		this.implementation = implementation;
		this.edgeScheduler = edgeScheduler;
		this.orderManager = orderManager;
	}

	/**
	 * adds all the necessary communication vertices with the given type
	 */
	public abstract void routeAll(MapperDAG implementation, Integer type);

	/**
	 * adds all the necessary communication vertices with the given type
	 * affected by the mapping of newVertex
	 */
	public abstract void routeNewVertex(MapperDAGVertex newVertex,
			List<Integer> types);

	/**
	 * Evaluates the cost of a routed edge
	 */
	public abstract long evaluateTransferCost(MapperDAGEdge edge);

	/**
	 * Gets the route of a routed edge
	 */
	public abstract Route getRoute(MapperDAGEdge edge);
}
