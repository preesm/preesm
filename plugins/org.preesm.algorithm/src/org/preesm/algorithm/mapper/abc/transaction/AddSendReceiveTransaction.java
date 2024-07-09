/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013 - 2017)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2016)
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
import java.util.stream.Stream;
import org.preesm.algorithm.mapper.abc.order.OrderManager;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.mapper.model.MapperDAGEdge;
import org.preesm.algorithm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.mapper.model.special.PrecedenceEdge;
import org.preesm.algorithm.mapper.model.special.ReceiveVertex;
import org.preesm.algorithm.mapper.model.special.SendVertex;
import org.preesm.algorithm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.mapper.tools.CommunicationOrderChecker;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;
import org.preesm.model.slam.SlamRouteStep;

/**
 * A transaction that adds a send and a receive vertex in an implementation.
 *
 * @author mpelcat
 */
public class AddSendReceiveTransaction implements Transaction {
  // Inputs
  /** If not null, the transfer vertices need to be chained with formerly added ones. */
  private final Transaction precedingTransaction;

  /** Implementation DAG to which the vertex is added. */
  private final MapperDAG implementation;

  /** Route step corresponding to this overhead. */
  private final SlamRouteStep step;

  /** Original edge corresponding to this overhead. */
  private final MapperDAGEdge edge;

  /** manager keeping scheduling orders. */
  private final OrderManager orderManager;

  /** Cost of the transfer to give to the transfer vertex. */
  private final long transferCost;

  /** Index of the route step within its route. */
  private final int routeIndex;

  /** overhead vertex added. */
  private TransferVertex sendVertex = null;

  /** The receive vertex. */
  private TransferVertex receiveVertex = null;

  /**
   * Instantiates a new adds the send receive transaction.
   *
   * @param precedingTransaction
   *          the preceding transaction
   * @param edge
   *          the edge
   * @param implementation
   *          the implementation
   * @param orderManager
   *          the order manager
   * @param routeIndex
   *          the route index
   * @param step
   *          the step
   * @param transferCost
   *          the transfer cost
   */
  public AddSendReceiveTransaction(final Transaction precedingTransaction, final MapperDAGEdge edge,
      final MapperDAG implementation, final OrderManager orderManager, final int routeIndex, final SlamRouteStep step,
      final long transferCost) {
    super();
    this.precedingTransaction = precedingTransaction;
    this.edge = edge;
    this.implementation = implementation;
    this.orderManager = orderManager;
    this.routeIndex = routeIndex;
    this.step = step;
    this.transferCost = transferCost;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#execute(java.util.List)
   */
  @Override
  public void execute(final List<MapperDAGVertex> resultList) {

    MapperDAGVertex currentSource = null;
    final MapperDAGVertex currentTarget = (MapperDAGVertex) this.edge.getTarget();
    if (this.precedingTransaction instanceof final AddSendReceiveTransaction asrt) {
      currentSource = asrt.receiveVertex;

      ((MapperDAG) currentSource.getBase()).removeAllEdges(currentSource, currentTarget);
    } else {
      currentSource = (MapperDAGVertex) this.edge.getSource();
    }

    // Careful!!! Those names are used in code generation
    final String nameRadix = ((MapperDAGVertex) this.edge.getSource()).getName() + currentTarget.getName() + "_"
        + this.routeIndex;

    final String sendVertexID = "s_" + nameRadix;

    final String receiveVertexID = "r_" + nameRadix;

    if (this.edge instanceof PrecedenceEdge) {
      PreesmLogger.getLogger().log(Level.INFO, "no transfer vertex corresponding to a schedule edge");
      return;
    }

    final ComponentInstance senderOperator = this.step.getSender();
    final ComponentInstance receiverOperator = this.step.getReceiver();

    this.sendVertex = new SendVertex(sendVertexID, this.implementation, (MapperDAGVertex) this.edge.getSource(),
        (MapperDAGVertex) this.edge.getTarget(), 0, 0, null);
    this.implementation.getTimings().dedicate(this.sendVertex);
    this.implementation.getMappings().dedicate(this.sendVertex);
    this.sendVertex.setRouteStep(this.step);
    this.implementation.addVertex(this.sendVertex);
    this.sendVertex.getTiming().setCost(this.transferCost);
    this.sendVertex.setEffectiveComponent(senderOperator);

    // Find insertion position. Insert sendVertices after the current source, and after sendVertex(es) immediately
    // following it. This is done to ensure that communications are inserted in increasing scheduling order.
    MapperDAGVertex insertionPosition = currentSource;
    while (this.orderManager.getNext(insertionPosition) instanceof SendVertex) {
      insertionPosition = this.orderManager.getNext(insertionPosition);
    }

    this.orderManager.insertAfter(insertionPosition, this.sendVertex);

    this.receiveVertex = new ReceiveVertex(receiveVertexID, this.implementation,
        (MapperDAGVertex) this.edge.getSource(), (MapperDAGVertex) this.edge.getTarget(), 0, 0, null);
    this.implementation.getTimings().dedicate(this.receiveVertex);
    this.implementation.getMappings().dedicate(this.receiveVertex);
    this.receiveVertex.setRouteStep(this.step);
    this.implementation.addVertex(this.receiveVertex);
    this.receiveVertex.getTiming().setCost(this.transferCost);
    this.receiveVertex.setEffectiveComponent(receiverOperator);

    // Place the receive just before the vertex consuming the corresponding data. (position is not definitive, cf.
    // reorderReceive method)
    this.orderManager.insertBefore(currentTarget, this.receiveVertex);

    final MapperDAGEdge sourceToSendEdge = (MapperDAGEdge) this.implementation.addEdge(currentSource, this.sendVertex);
    final MapperDAGEdge sendToReceiveEdge = (MapperDAGEdge) this.implementation.addEdge(this.sendVertex,
        this.receiveVertex);
    final MapperDAGEdge receiveToTargetEdge = (MapperDAGEdge) this.implementation.addEdge(this.receiveVertex,
        currentTarget);

    sourceToSendEdge.setInit(this.edge.getInit().copy());
    sendToReceiveEdge.setInit(this.edge.getInit().copy());
    receiveToTargetEdge.setInit(this.edge.getInit().copy());

    sourceToSendEdge.getTiming().setCost(0);
    sendToReceiveEdge.getTiming().setCost(0);
    receiveToTargetEdge.getTiming().setCost(0);

    sourceToSendEdge.setAggregate(this.edge.getAggregate());
    sendToReceiveEdge.setAggregate(this.edge.getAggregate());
    receiveToTargetEdge.setAggregate(this.edge.getAggregate());

    // Reorder receiveVertex if needed. This is done to ensure that send and receive operation between a pair of cores
    // are always in the same order.
    reorderReceiveVertex(senderOperator, receiverOperator);

    if (resultList != null) {
      resultList.add(this.sendVertex);
      resultList.add(this.receiveVertex);
    }
  }

  /**
   * The purpose of this method is to reschedule {@link ReceiveVertex} of the receiverOperator to comply with
   * constraints on communication primitive order enforced by the {@link CommunicationOrderChecker}. <br>
   * <br>
   * Briefly, if there exists {@link ReceiveVertex}es scheduled after the current {@link #receiveVertex} on the
   * receiverOperator, (but associated to a {@link SendVertex}es scheduled before the current {@link #sendVertex} on the
   * senderOperator), then, these {@link ReceiveVertex}es must be rescheduled before the current {@link #receiveVertex}.
   *
   * @param senderOperator
   *          {@link ComponentInstance} instance on which the current {@link #sendVertex} was scheduled.
   * @param receiverOperator
   *          {@link ComponentInstance} instance on which the current {@link #receiveVertex} was scheduled.
   */
  private void reorderReceiveVertex(final ComponentInstance senderOperator, final ComponentInstance receiverOperator) {
    // Get vertices scheduled on the same Operator
    final Stream<MapperDAGVertex> verticesOnRecivingOperator2 = this.orderManager.getVertexList(receiverOperator)
        .stream()
        // Keep only receive vertices
        .filter(ReceiveVertex.class::isInstance)
        // Keep only receiveVertex scheduled after the inserted one.
        .filter(vertex -> this.orderManager.totalIndexOf(vertex) > this.orderManager.totalIndexOf(this.receiveVertex))
        // Keep only those with the same sender
        .filter(vertex -> (((MapperDAGVertex) this.implementation.incomingEdgesOf(vertex).iterator().next().getSource())
            .getEffectiveOperator()).equals(senderOperator))
        // Keep only those whose sender is scheduled before the current one
        .filter(vertex -> this.orderManager.totalIndexOf(((MapperDAGVertex) this.implementation.incomingEdgesOf(vertex)
            .iterator().next().getSource())) < this.orderManager.totalIndexOf(this.sendVertex));

    // Insert all receiveVertices satisfying previous filters before the current receiveVertex
    verticesOnRecivingOperator2.peek(vertex -> this.orderManager.remove(vertex, true))
        .forEachOrdered(vertex -> this.orderManager.insertBefore(this.receiveVertex, vertex));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#toString()
   */
  @Override
  public String toString() {
    return ("AddSendReceive");
  }

}
