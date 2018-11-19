/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.mapper.model.special;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.ietr.preesm.mapper.abc.order.OrderManager;
import org.ietr.preesm.mapper.abc.transaction.AddPrecedenceEdgeTransaction;
import org.ietr.preesm.mapper.abc.transaction.RemoveEdgeTransaction;
import org.ietr.preesm.mapper.abc.transaction.Transaction;
import org.ietr.preesm.mapper.abc.transaction.TransactionManager;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.slam.ComponentInstance;

// TODO: Auto-generated Javadoc
/**
 * The edge adder automatically generates edges between vertices successive on a single operator. It can also remove all
 * the edges of type PrecedenceEdgeAdder from the graph
 *
 * @author mpelcat
 */
public class PrecedenceEdgeAdder {

  /** The order manager. */
  private final OrderManager orderManager;

  /** The implementation. */
  private final MapperDAG implementation;

  /** The transaction manager. */
  private final TransactionManager transactionManager;

  /**
   * Instantiates a new precedence edge adder.
   *
   * @param orderManager
   *          the order manager
   * @param implementation
   *          the implementation
   */
  public PrecedenceEdgeAdder(final OrderManager orderManager, final MapperDAG implementation) {
    super();
    this.orderManager = orderManager;
    this.implementation = implementation;
    this.transactionManager = new TransactionManager();
  }

  /**
   * Removes all schedule edges.
   */
  public void removePrecedenceEdges() {

    for (final DAGEdge e : this.implementation.edgeSet()) {
      if (e instanceof PrecedenceEdge) {
        this.transactionManager.add(new RemoveEdgeTransaction((MapperDAGEdge) e, this.implementation));
      }
    }
    this.transactionManager.execute();
    this.transactionManager.clear();
  }

  /**
   * Adds all necessary precedence edges to an implementation respecting the order given by the scheduling order
   * manager.
   */
  public void addPrecedenceEdges() {

    final TransactionManager localTransactionManager = new TransactionManager();
    final Iterator<ComponentInstance> opIt = this.orderManager.getArchitectureComponents().iterator();

    // Iterates the schedules
    while (opIt.hasNext()) {
      final List<MapperDAGVertex> schedule = this.orderManager.getVertexList(opIt.next());

      final Iterator<MapperDAGVertex> schedit = schedule.iterator();

      MapperDAGVertex src;

      // Iterates all vertices in each schedule
      if (schedit.hasNext()) {
        MapperDAGVertex dst = schedit.next();

        while (schedit.hasNext()) {

          src = dst;
          dst = schedit.next();

          if (this.implementation.getAllEdges(src, dst).isEmpty()) {
            // Adds a transaction
            final Transaction transaction = new AddPrecedenceEdgeTransaction(this.implementation, src, dst);
            localTransactionManager.add(transaction);
          }
        }
      }
    }

    // Executes the transactions
    localTransactionManager.execute();
  }

  /**
   * Adds the precedence edge.
   *
   * @param v1
   *          the v 1
   * @param v2
   *          the v 2
   * @return the precedence edge
   */
  public PrecedenceEdge addPrecedenceEdge(final MapperDAGVertex v1, final MapperDAGVertex v2) {
    final PrecedenceEdge precedenceEdge = new PrecedenceEdge(v1, v2);
    precedenceEdge.getTiming().setCost(0);
    this.implementation.addEdge(v1, v2, precedenceEdge);
    return precedenceEdge;
  }

  /**
   * Removes the precedence edge.
   *
   * @param v1
   *          the v 1
   * @param v2
   *          the v 2
   */
  public void removePrecedenceEdge(final MapperDAGVertex v1, final MapperDAGVertex v2) {
    if (v1 != null && v2 != null) {
      final Set<DAGEdge> edges = this.implementation.getAllEdges(v1, v2);

      if (edges != null) {
        if (edges.size() >= 2) {
          PreesmLogger.getLogger().log(Level.SEVERE,
              "too many edges between " + v1.toString() + " and " + v2.toString());
        }

        for (final DAGEdge edge : edges) {
          if (edge instanceof PrecedenceEdge) {
            this.implementation.removeEdge(edge);
          }
        }
      }
    }
  }

  /**
   * For Debug purposes, checks that all necessary precedence edges are present.
   *
   * @param newVertex
   *          the new vertex
   */
  /*
   * public static void checkPrecedences(SchedOrderManager orderManager, MapperDAG implementation, MultiCoreArchitecture
   * archi) {
   *
   * Set<ArchitectureComponent> cmpSet = new LinkedHashSet<ArchitectureComponent>();
   * cmpSet.addAll(archi.getComponents(ArchitectureComponentType.medium));
   * cmpSet.addAll(archi.getComponents(ArchitectureComponentType.operator));
   *
   * for (ArchitectureComponent o : cmpSet) { List<MapperDAGVertex> schedule = orderManager.getVertexList(o); if
   * (schedule != null) { MapperDAGVertex pv = null; for (IScheduleElement v : schedule) { if (pv != null) { if
   * (implementation.getAllEdges(pv, (MapperDAGVertex)v) == null || implementation.getAllEdges(pv,
   * (MapperDAGVertex)v).isEmpty()) {
   *
   * PreesmLogger.getLogger().log( Level.SEVERE, "Lacking precedence edge between " + pv.toString() + " and " +
   * v.toString()); } } pv = (MapperDAGVertex)v; } } } }
   */

  /**
   * Schedules a given vertex
   */
  public void scheduleVertex(final MapperDAGVertex newVertex) {

    final MapperDAGVertex prev = this.orderManager.getPrevious(newVertex);
    final MapperDAGVertex next = this.orderManager.getNext(newVertex);

    boolean prevAndNewLinked = false;
    if (prev != null) {
      final Set<DAGEdge> prevEdges = this.implementation.getAllEdges(prev, newVertex);
      prevAndNewLinked = ((prevEdges != null) && !prevEdges.isEmpty());
      if (((prev != null) && (newVertex != null)) && !prevAndNewLinked) {
        addPrecedenceEdge(prev, newVertex);
        prevAndNewLinked = true;
      }
    }

    boolean newAndNextLinked = false;
    if (next != null) {
      final Set<DAGEdge> nextEdges = this.implementation.getAllEdges(newVertex, next);
      newAndNextLinked = ((nextEdges != null) && !nextEdges.isEmpty());
      if (((newVertex != null) && (next != null)) && !newAndNextLinked) {
        addPrecedenceEdge(newVertex, next);
        newAndNextLinked = true;
      }
    }

    if (prevAndNewLinked && newAndNextLinked) {
      removePrecedenceEdge(prev, next);
    }
  }
}
