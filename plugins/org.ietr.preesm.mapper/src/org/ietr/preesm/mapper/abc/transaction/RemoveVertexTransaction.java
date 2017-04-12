/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012)
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

package org.ietr.preesm.mapper.abc.transaction;

import java.util.List;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.ietr.preesm.mapper.model.special.PrecedenceEdgeAdder;

// TODO: Auto-generated Javadoc
/**
 * A transaction that removes one vertex in an implementation.
 *
 * @author mpelcat
 */
public class RemoveVertexTransaction extends Transaction {
  // Inputs
  /** Implementation DAG from which the vertex is removed. */
  private MapperDAG implementation = null;

  /** vertex removed. */
  private MapperDAGVertex vertex = null;

  /** Order manager. */
  private OrderManager orderManager = null;

  /**
   * Instantiates a new removes the vertex transaction.
   *
   * @param vertex
   *          the vertex
   * @param implementation
   *          the implementation
   * @param orderManager
   *          the order manager
   */
  public RemoveVertexTransaction(final MapperDAGVertex vertex, final MapperDAG implementation,
      final OrderManager orderManager) {
    super();
    this.vertex = vertex;
    this.implementation = implementation;
    this.orderManager = orderManager;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#execute(java.util.List)
   */
  @Override
  public void execute(final List<Object> resultList) {
    super.execute(resultList);

    // Unscheduling first
    final MapperDAGVertex prev = this.orderManager.getPrevious(this.vertex);
    final MapperDAGVertex next = this.orderManager.getNext(this.vertex);
    final PrecedenceEdgeAdder adder = new PrecedenceEdgeAdder(this.orderManager,
        this.implementation);

    if (prev != null) {
      adder.removePrecedenceEdge(prev, this.vertex);
    }

    if (next != null) {
      adder.removePrecedenceEdge(this.vertex, next);
    }

    // Adding precedence between predecessor and sucessor if they don't
    // share data
    final Set<DAGEdge> edges = this.implementation.getAllEdges(prev, next);
    if (((prev != null) && (next != null)) && ((edges == null) || edges.isEmpty())) {
      adder.addPrecedenceEdge(prev, next);
    }

    this.orderManager.remove(this.vertex, true);

    this.implementation.getTimings().remove(this.vertex);
    this.implementation.getMappings().remove(this.vertex);
    // Removing vertex
    this.implementation.removeVertex(this.vertex);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.mapper.abc.transaction.Transaction#toString()
   */
  @Override
  public String toString() {
    return ("RemoveVertex(" + this.vertex.toString() + ")");
  }

}
