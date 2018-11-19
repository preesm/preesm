/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
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
package org.ietr.preesm.mapper.abc.taskscheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.ietr.preesm.mapper.abc.order.VertexOrderList;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.preesm.algorithm.model.dag.DAGVertex;
import org.preesm.commons.logger.PreesmLogger;

// TODO: Auto-generated Javadoc
/**
 * Scheduling the tasks in topological order and alphabetical order.
 *
 * @author mpelcat
 */
public class TopologicalTaskSched extends AbstractTaskSched {

  /** The init list. */
  private VertexOrderList initList = null;

  /** The topolist. */
  private List<MapperDAGVertex> topolist = null;

  /**
   * The Class InitListComparator.
   */
  private static class InitListComparator implements Comparator<MapperDAGVertex> {

    /** The init list. */
    private VertexOrderList initList = null;

    /**
     * Instantiates a new inits the list comparator.
     *
     * @param initlist
     *          the initlist
     */
    public InitListComparator(final VertexOrderList initlist) {
      super();
      this.initList = initlist;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final MapperDAGVertex v0, final MapperDAGVertex v1) {
      int compare;

      compare = this.initList.orderOf(v0.getName()) - this.initList.orderOf(v1.getName());

      return compare;
    }

  }

  /**
   * Instantiates a new topological task sched.
   *
   * @param initlist
   *          the initlist
   */
  public TopologicalTaskSched(final VertexOrderList initlist) {
    this.initList = initlist;
  }

  /**
   * Reuse infinite homogeneous order.
   *
   * @param dag
   *          the dag
   * @return the list
   */

  public List<MapperDAGVertex> createTopology(final MapperDAG dag) {
    this.topolist = new ArrayList<>();

    for (final DAGVertex v : dag.vertexSet()) {
      this.topolist.add((MapperDAGVertex) v);

      if (!this.initList.contains(v.getName())) {
        PreesmLogger.getLogger().log(Level.SEVERE, "problem with topological ordering.");
      }
    }

    Collections.sort(this.topolist, new InitListComparator(this.initList));

    return this.topolist;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.mapper.abc.taskscheduling.AbstractTaskSched#insertVertex(org.ietr.preesm.mapper
   * .model.MapperDAGVertex)
   */
  @Override
  public void insertVertex(final MapperDAGVertex vertex) {
    if (vertex == null) {
      throw new IllegalArgumentException();
    }
    if (this.topolist == null) {
      throw new IllegalStateException();
    }
    int topoOrder = this.topolist.indexOf(vertex);
    boolean inserted = false;

    if ((this.topolist != null) && (topoOrder >= 0)) {

      topoOrder--;
      while (topoOrder >= 0) {
        final MapperDAGVertex previousCandidate = this.topolist.get(topoOrder);
        final int totalOrder = this.orderManager.totalIndexOf(previousCandidate);
        if (totalOrder >= 0) {
          this.orderManager.insertAtIndex(totalOrder + 1, vertex);
          inserted = true;
          break;
        }
        topoOrder--;
      }

      if (!inserted && vertex.getPredecessors(false).isEmpty()) {
        this.orderManager.addFirst(vertex);
      }
    } else {
      this.orderManager.addLast(vertex);
    }
  }
}
