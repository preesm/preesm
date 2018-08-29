/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2012)
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

import java.util.List;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.transaction.Transaction;
import org.ietr.preesm.mapper.abc.transaction.TransactionManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;

// TODO: Auto-generated Javadoc
/**
 * Routes a communication and creates the necessary communication vertices.
 *
 * @author mpelcat
 */
public abstract class CommunicationRouterImplementer {

  /** The user. */
  private AbstractCommunicationRouter user = null;

  /**
   * Instantiates a new communication router implementer.
   *
   * @param user
   *          the user
   */
  public CommunicationRouterImplementer(final AbstractCommunicationRouter user) {
    super();
    this.user = user;
  }

  /**
   * Gets the implementation.
   *
   * @return the implementation
   */
  public MapperDAG getImplementation() {
    return this.user.getImplementation();
  }

  /**
   * Gets the edge scheduler.
   *
   * @return the edge scheduler
   */
  public IEdgeSched getEdgeScheduler() {
    return this.user.getEdgeScheduler();
  }

  /**
   * Gets the order manager.
   *
   * @return the order manager
   */
  public OrderManager getOrderManager() {
    return this.user.getOrderManager();
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
  public abstract Transaction addVertices(AbstractRouteStep routeStep, MapperDAGEdge edge,
      TransactionManager transactions, int type, int routeStepIndex, Transaction lastTransaction,
      List<Object> alreadyCreatedVertices);

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
