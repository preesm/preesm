/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
package org.preesm.algorithm.mapper.abc.transaction;

import java.util.List;
import java.util.logging.Level;
import org.preesm.algorithm.core.architecture.route.AbstractRouteStep;
import org.preesm.algorithm.mapper.abc.order.OrderManager;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.special.OverheadVertex;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdge;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdgeAdder;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * Transaction executing the addition of an overhead (or set-up) vertex.
 *
 * @author mpelcat
 */
public class AddOverheadVertexTransaction extends Transaction {

  // Inputs
  /** Implementation DAG to which the vertex is added. */
  private MapperDAG implementation = null;

  /** Route step corresponding to this overhead. */
  private AbstractRouteStep step = null;

  /** time of this overhead. */
  long overheadTime = 0;

  /** Original edge corresponding to this overhead. */
  private MapperDAGEdge edge = null;

  /** manager keeping scheduling orders. */
  private OrderManager orderManager = null;

  // Generated objects
  /** overhead vertex added. */
  private OverheadVertex oVertex = null;

  /** edges added. */
  private MapperDAGEdge newInEdge = null;

  /** The new out edge. */
  private MapperDAGEdge newOutEdge = null;

  /**
   * Instantiates a new adds the overhead vertex transaction.
   *
   * @param edge
   *          the edge
   * @param implementation
   *          the implementation
   * @param step
   *          the step
   * @param overheadTime
   *          the overhead time
   * @param orderManager
   *          the order manager
   */
  public AddOverheadVertexTransaction(final MapperDAGEdge edge, final MapperDAG implementation,
      final AbstractRouteStep step, final long overheadTime, final OrderManager orderManager) {
    super();
    this.edge = edge;
    this.implementation = implementation;
    this.step = step;
    this.orderManager = orderManager;
    this.overheadTime = overheadTime;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#execute(java.util.List)
   */
  @Override
  public void execute(final List<Object> resultList) {

    super.execute(resultList);

    final MapperDAGVertex currentSource = (MapperDAGVertex) this.edge.getSource();
    final MapperDAGVertex currentTarget = (MapperDAGVertex) this.edge.getTarget();

    if (this.edge instanceof PrecedenceEdge) {
      PreesmLogger.getLogger().log(Level.INFO, "no overhead vertex corresponding to a schedule edge");
      return;
    }

    final String overtexID = "__overhead (" + currentSource.getName() + "," + currentTarget.getName() + ")";

    if (this.overheadTime > 0) {
      this.oVertex = new OverheadVertex(overtexID, this.implementation);
      this.implementation.getTimings().dedicate(this.oVertex);
      this.implementation.getMappings().dedicate(this.oVertex);

      if (!(currentTarget instanceof TransferVertex)) {
        PreesmLogger.getLogger().log(Level.SEVERE, "An overhead must be followed by a transfer");
      }

      this.implementation.addVertex(this.oVertex);
      this.oVertex.getTiming().setCost(this.overheadTime);
      this.oVertex.setEffectiveOperator(this.step.getSender());

      this.newInEdge = (MapperDAGEdge) this.implementation.addEdge(currentSource, this.oVertex);
      this.newOutEdge = (MapperDAGEdge) this.implementation.addEdge(this.oVertex, currentTarget);

      this.newInEdge.setInit(this.edge.getInit().copy());
      this.newOutEdge.setInit(this.edge.getInit().copy());

      this.newInEdge.getTiming().setCost(0);
      this.newOutEdge.getTiming().setCost(0);

      // TODO: Look at switching possibilities
      /*
       * if (true) { TaskSwitcher taskSwitcher = new TaskSwitcher(); taskSwitcher.setOrderManager(orderManager);
       * taskSwitcher.insertVertexBefore(currentTarget, oVertex); } else
       */
      this.orderManager.insertBefore(currentTarget, this.oVertex);

      // Scheduling overhead vertex
      new PrecedenceEdgeAdder(this.orderManager, this.implementation).scheduleVertex(this.oVertex);

      if (resultList != null) {
        resultList.add(this.oVertex);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#toString()
   */
  @Override
  public String toString() {
    return ("AddOverhead(" + this.oVertex.toString() + ")");
  }

}
