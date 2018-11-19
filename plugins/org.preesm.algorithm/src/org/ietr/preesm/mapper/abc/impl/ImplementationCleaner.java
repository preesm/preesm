/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2009 - 2014)
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
package org.ietr.preesm.mapper.abc.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.transaction.RemoveVertexTransaction;
import org.ietr.preesm.mapper.abc.transaction.TransactionManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.InvolvementVertex;
import org.ietr.preesm.mapper.model.special.OverheadVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;
import org.ietr.preesm.mapper.model.special.TransferVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;

// TODO: Auto-generated Javadoc
/**
 * Class cleaning an implementation i.e. removing added transfers and edges.
 *
 * @author mpelcat
 */
public class ImplementationCleaner {

  /** The order manager. */
  private final OrderManager orderManager;

  /** The implementation. */
  private final MapperDAG implementation;

  /** The transaction manager. */
  private final TransactionManager transactionManager;

  /**
   * Instantiates a new implementation cleaner.
   *
   * @param orderManager
   *          the order manager
   * @param implementation
   *          the implementation
   */
  public ImplementationCleaner(final OrderManager orderManager, final MapperDAG implementation) {
    super();
    this.orderManager = orderManager;
    this.implementation = implementation;
    this.transactionManager = new TransactionManager();
  }

  /**
   * Removes all transfers from routes coming from or going to vertex.
   *
   * @param vertex
   *          the vertex
   */
  public void removeAllTransfers(final MapperDAGVertex vertex) {

    for (final DAGVertex v : ImplementationCleaner.getAllTransfers(vertex)) {
      if (v instanceof TransferVertex) {
        this.transactionManager
            .add(new RemoveVertexTransaction((MapperDAGVertex) v, this.implementation, this.orderManager));

      }
    }

    this.transactionManager.execute();
    this.transactionManager.clear();
  }

  /**
   * Removes all overheads from routes coming from or going to vertex.
   *
   * @param vertex
   *          the vertex
   */
  public void removeAllOverheads(final MapperDAGVertex vertex) {
    this.transactionManager.clear();

    for (final DAGVertex v : ImplementationCleaner.getAllTransfers(vertex)) {
      if (v instanceof TransferVertex) {
        final MapperDAGVertex o = ((TransferVertex) v).getPrecedingOverhead();
        if ((o != null) && (o instanceof OverheadVertex)) {
          this.transactionManager.add(new RemoveVertexTransaction(o, this.implementation, this.orderManager));
        }
      }
    }

    this.transactionManager.execute();
    this.transactionManager.clear();
  }

  /**
   * Removes all overheads from routes coming from or going to vertex.
   *
   * @param vertex
   *          the vertex
   */
  public void removeAllInvolvements(final MapperDAGVertex vertex) {
    this.transactionManager.clear();

    for (final DAGVertex v : ImplementationCleaner.getAllTransfers(vertex)) {
      if (v instanceof TransferVertex) {
        final MapperDAGVertex o = ((TransferVertex) v).getInvolvementVertex();
        if ((o != null) && (o instanceof InvolvementVertex)) {
          this.transactionManager.add(new RemoveVertexTransaction(o, this.implementation, this.orderManager));
        }
      }
    }

    this.transactionManager.execute();
    this.transactionManager.clear();
  }

  /**
   * Removes the precedence edges scheduling a vertex and schedules its successor after its predecessor.
   *
   * @param vertex
   *          the vertex
   */
  public void unscheduleVertex(final MapperDAGVertex vertex) {

    final MapperDAGVertex prev = this.orderManager.getPrevious(vertex);
    final MapperDAGVertex next = this.orderManager.getNext(vertex);
    final PrecedenceEdgeAdder adder = new PrecedenceEdgeAdder(this.orderManager, this.implementation);

    if (prev != null) {
      adder.removePrecedenceEdge(prev, vertex);
    }

    if (next != null) {
      adder.removePrecedenceEdge(vertex, next);
    }
    final Set<DAGEdge> edges;
    if (prev != null && next != null) {
      edges = this.implementation.getAllEdges(prev, next);
    } else {
      edges = null;
    }

    if (((prev != null) && (next != null)) && ((edges == null) || edges.isEmpty())) {
      // TODO: Remove, only for debug
      if (!prev.getEffectiveOperator().getInstanceName().equals(next.getEffectiveOperator().getInstanceName())) {
        System.out.println("wrong!!");
      }
      adder.addPrecedenceEdge(prev, next);
    }

  }

  /**
   * Gets all transfers from routes coming from or going to vertex. Do not execute if overheads are present
   *
   * @param vertex
   *          the vertex
   * @return the all transfers
   */
  public static Set<DAGVertex> getAllTransfers(final MapperDAGVertex vertex) {

    final Set<DAGVertex> transfers = new LinkedHashSet<>();

    transfers.addAll(ImplementationCleaner.getPrecedingTransfers(vertex));
    transfers.addAll(ImplementationCleaner.getFollowingTransfers(vertex));

    return transfers;
  }

  /**
   * Gets all transfers preceding vertex. Recursive function
   *
   * @param vertex
   *          the vertex
   * @return the preceding transfers
   */
  public static Set<DAGVertex> getPrecedingTransfers(final MapperDAGVertex vertex) {

    final Set<DAGVertex> transfers = new LinkedHashSet<>();

    for (final MapperDAGVertex v : vertex.getPredecessors(true).keySet()) {
      if (v instanceof TransferVertex) {
        transfers.add(v);
        transfers.addAll(ImplementationCleaner.getPrecedingTransfers(v));
      }
    }

    return transfers;
  }

  /**
   * Gets all transfers following vertex. Recursive function
   *
   * @param vertex
   *          the vertex
   * @return the following transfers
   */
  public static Set<DAGVertex> getFollowingTransfers(final MapperDAGVertex vertex) {

    final Set<DAGVertex> transfers = new LinkedHashSet<>();

    for (final MapperDAGVertex v : vertex.getSuccessors(true).keySet()) {
      if (v instanceof TransferVertex) {
        transfers.add(v);
        transfers.addAll(ImplementationCleaner.getFollowingTransfers(v));
      }
    }

    return transfers;
  }
}
