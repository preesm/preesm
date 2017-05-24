/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2012 - 2014)
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

package org.ietr.preesm.mapper.tools;

import java.util.HashSet;
import java.util.Set;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.model.MapperDAGEdge;
import org.ietr.preesm.mapper.model.MapperDAGVertex;
import org.jgrapht.traverse.AbstractGraphIterator;

// TODO: Auto-generated Javadoc
/**
 * Iterates the graph in ascending or descending topological order.
 *
 * @author mpelcat
 */
public class CustomTopologicalIterator extends AbstractGraphIterator<MapperDAGVertex, MapperDAGEdge> {

  /** The direct order. */
  protected boolean directOrder;

  /** The dag. */
  MapperDAG dag;

  /** The visited vertices. */
  private Set<MapperDAGVertex> visitedVertices = null;

  /**
   * Instantiates a new custom topological iterator.
   *
   * @param dag
   *          the dag
   * @param directOrder
   *          the direct order
   */
  public CustomTopologicalIterator(final MapperDAG dag, final boolean directOrder) {
    this.directOrder = directOrder;
    this.dag = dag;
    this.visitedVertices = new HashSet<>();
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    return (this.visitedVertices.size() < this.dag.vertexSet().size());
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.Iterator#next()
   */
  @Override
  public MapperDAGVertex next() {
    if (this.directOrder) {
      for (final DAGVertex v : this.dag.vertexSet()) {
        final MapperDAGVertex mv = (MapperDAGVertex) v;
        if (mv.incomingEdges().isEmpty() && !this.visitedVertices.contains(mv)) {
          this.visitedVertices.add(mv);
          return mv;
        } else {
          final Set<MapperDAGVertex> preds = mv.getPredecessors(true).keySet();
          if (this.visitedVertices.containsAll(preds) && !this.visitedVertices.contains(mv)) {
            this.visitedVertices.add(mv);
            return mv;
          }
        }
      }
    } else {
      for (final DAGVertex v : this.dag.vertexSet()) {
        final MapperDAGVertex mv = (MapperDAGVertex) v;
        if (mv.outgoingEdges().isEmpty() && !this.visitedVertices.contains(mv)) {
          this.visitedVertices.add(mv);
          return mv;
        } else {
          final Set<MapperDAGVertex> succs = mv.getSuccessors(true).keySet();
          if (this.visitedVertices.containsAll(succs) && !this.visitedVertices.contains(mv)) {
            this.visitedVertices.add(mv);
            return mv;
          }
        }
      }
    }
    return null;
  }

}
