/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2009 - 2012)
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
package org.preesm.algorithm.mapper.abc.route;

import java.util.List;
import org.preesm.algorithm.mapper.abc.edgescheduling.IEdgeSched;
import org.preesm.algorithm.mapper.abc.order.OrderManager;
import org.preesm.algorithm.mapper.abc.transaction.Transaction;
import org.preesm.algorithm.mapper.abc.transaction.TransactionManager;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.model.slam.SlamRouteStep;

/**
 * Routes a communication and creates the necessary communication vertices.
 *
 * @author mpelcat
 */
public abstract class CommunicationRouterImplementer {

  private CommunicationRouter router = null;

  /**
   * Instantiates a new communication router implementer.
   *
   * @param router
   *          the user
   */
  protected CommunicationRouterImplementer(final CommunicationRouter router) {
    super();
    this.router = router;
  }

  public MapperDAG getImplementation() {
    return this.router.getImplementation();
  }

  public IEdgeSched getEdgeScheduler() {
    return this.router.getEdgeScheduler();
  }

  public OrderManager getOrderManager() {
    return this.router.getOrderManager();
  }

  /**
   * Adds the vertices.
   *
   * @param routeStep
   *          the route step
   * @param edge
   *          the edge
   * @param transactions
   *          the transactions
   * @param type
   *          the type
   * @param routeStepIndex
   *          the route step index
   * @param lastTransaction
   *          the last transaction
   * @param alreadyCreatedVertices
   *          the already created vertices
   * @return the transaction
   */
  public abstract Transaction addVertices(SlamRouteStep routeStep, MapperDAGEdge edge, TransactionManager transactions,
      int type, int routeStepIndex, Transaction lastTransaction, List<MapperDAGVertex> alreadyCreatedVertices);

  /**
   * Removes the vertices.
   *
   * @param edge
   *          the edge
   * @param transactions
   *          the transactions
   */
  public abstract void removeVertices(MapperDAGEdge edge, TransactionManager transactions);
}
