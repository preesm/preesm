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
package org.ietr.preesm.mapper.abc.transaction;

import java.util.List;
import java.util.logging.Level;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.preesm.core.architecture.route.AbstractRouteStep;
import org.ietr.preesm.mapper.abc.edgescheduling.IEdgeSched;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdge;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * A transaction that adds one transfer vertex in an implementation and schedules it given the right edge scheduler.
 *
 * @author mpelcat
 */
public class AddTransferVertexTransaction extends Transaction {
  // Inputs
  /**
   * The beginning of the transfer name. Typically: 'transfer', 'read' or 'write'
   */
  private String transferType = null;

  /** If not null, the transfer vertices need to be chained with formerly added ones. */
  private Transaction precedingTransaction = null;

  /** Scheduling the transfer vertices on the media. */
  private IEdgeSched edgeScheduler = null;

  /** Vertices order manager. */
  private final OrderManager orderManager;

  /** Implementation DAG to which the vertex is added. */
  private MapperDAG implementation = null;

  /** Route step corresponding to this transfer. */
  private AbstractRouteStep step = null;

  /** time of this transfer. */
  private long transferTime = 0;

  /** Component corresponding to this transfer vertex. */
  private ComponentInstance effectiveComponent = null;

  /** Original edge corresponding to this overhead. */
  private MapperDAGEdge edge = null;

  /** Index of the route step within its route and of the node within its route step. */
  private int routeIndex = 0;

  /** The node index. */
  private int nodeIndex = 0;

  // Generated objects
  /** transfer vertex added. */
  private TransferVertex tVertex = null;

  /** true if the added vertex needs to be scheduled. */
  private boolean scheduleVertex = false;

  /** edges added. */
  private MapperDAGEdge newInEdge = null;

  /** The new out edge. */
  private MapperDAGEdge newOutEdge = null;

  /**
   * Vertex preceding the transfer. It can be the transfer source or an overhead or a preceding transfer
   */
  private MapperDAGVertex currentSource = null;

  /**
   * Vertex following the transfer. At the time we add the transfer, can be only the transfer receiver.
   */
  private final MapperDAGVertex currentTarget = null;

  /**
   * Instantiates a new adds the transfer vertex transaction.
   *
   * @param transferType
   *          the transfer type
   * @param precedingTransaction
   *          the preceding transaction
   * @param edgeScheduler
   *          the edge scheduler
   * @param edge
   *          the edge
   * @param implementation
   *          the implementation
   * @param orderManager
   *          the order manager
   * @param routeIndex
   *          the route index
   * @param nodeIndex
   *          the node index
   * @param step
   *          the step
   * @param transferTime
   *          the transfer time
   * @param effectiveComponent
   *          the effective component
   * @param scheduleVertex
   *          the schedule vertex
   */
  public AddTransferVertexTransaction(final String transferType, final Transaction precedingTransaction,
      final IEdgeSched edgeScheduler, final MapperDAGEdge edge, final MapperDAG implementation,
      final OrderManager orderManager, final int routeIndex, final int nodeIndex, final AbstractRouteStep step,
      final long transferTime, final ComponentInstance effectiveComponent, final boolean scheduleVertex) {
    super();
    this.transferType = transferType;
    this.precedingTransaction = precedingTransaction;
    this.edgeScheduler = edgeScheduler;
    this.edge = edge;
    this.implementation = implementation;
    this.step = step;
    this.effectiveComponent = effectiveComponent;
    this.orderManager = orderManager;
    this.scheduleVertex = scheduleVertex;
    this.routeIndex = routeIndex;
    this.nodeIndex = nodeIndex;
    this.transferTime = transferTime;

    if (transferTime == 0) {
      PreesmLogger.getLogger().log(Level.WARNING, "adding a transfer of size 0.");
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#execute(java.util.List)
   */
  @Override
  public void execute(final List<Object> resultList) {
    super.execute(resultList);

    final MapperDAGVertex currentTarget = (MapperDAGVertex) this.edge.getTarget();

    // Linking with previous transaction consists in chaining the new
    // transfers with
    // the ones from previous transaction
    if (this.precedingTransaction != null) {
      if (this.precedingTransaction instanceof AddTransferVertexTransaction) {
        this.currentSource = ((AddTransferVertexTransaction) this.precedingTransaction).getTransfer();
        ((MapperDAG) this.currentSource.getBase()).removeAllEdges(this.currentSource, currentTarget);
      }
    } else {
      this.currentSource = (MapperDAGVertex) this.edge.getSource();
    }

    final String tvertexID = "__" + this.transferType + this.routeIndex + "_" + this.nodeIndex + " ("
        + ((MapperDAGVertex) this.edge.getSource()).getName() + "," + currentTarget.getName() + ")";

    if (this.edge instanceof PrecedenceEdge) {
      PreesmLogger.getLogger().log(Level.INFO, "no transfer vertex corresponding to a schedule edge");
      return;
    }

    if (this.transferTime > 0) {
      this.tVertex = new TransferVertex(tvertexID, this.implementation, (MapperDAGVertex) this.edge.getSource(),
          (MapperDAGVertex) this.edge.getTarget(), this.routeIndex, this.nodeIndex);
      this.implementation.getTimings().dedicate(this.tVertex);
      this.implementation.getMappings().dedicate(this.tVertex);

      this.tVertex.setRouteStep(this.step);

      this.implementation.addVertex(this.tVertex);
      this.tVertex.getTiming().setCost(this.transferTime);
      this.tVertex.setEffectiveComponent(this.effectiveComponent);

      this.newInEdge = (MapperDAGEdge) this.implementation.addEdge(this.currentSource, this.tVertex);
      this.newOutEdge = (MapperDAGEdge) this.implementation.addEdge(this.tVertex, currentTarget);

      this.newInEdge.setInit(this.edge.getInit().copy());
      this.newOutEdge.setInit(this.edge.getInit().copy());

      this.newInEdge.getTiming().setCost(0);
      this.newOutEdge.getTiming().setCost(0);

      this.newInEdge.setAggregate(this.edge.getAggregate());
      this.newOutEdge.setAggregate(this.edge.getAggregate());

      if (this.scheduleVertex) {
        // Scheduling transfer vertex
        this.edgeScheduler.schedule(this.tVertex, this.currentSource, currentTarget);

        new PrecedenceEdgeAdder(this.orderManager, this.implementation).scheduleVertex(this.tVertex);
      }

      if (resultList != null) {
        resultList.add(this.tVertex);
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
    return ("AddTransfer(" + this.tVertex.toString() + ")");
  }

  /**
   * Gets the transfer.
   *
   * @return the transfer
   */
  public TransferVertex getTransfer() {
    return this.tVertex;
  }

  /**
   * Gets the source.
   *
   * @return the source
   */
  public MapperDAGVertex getSource() {
    return this.currentSource;
  }

  /**
   * Gets the target.
   *
   * @return the target
   */
  public MapperDAGVertex getTarget() {
    return this.currentTarget;
  }
}
