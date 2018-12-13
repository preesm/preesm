/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.preesm.algorithm.model.dag.edag;

import java.util.ArrayList;
import java.util.List;
import org.preesm.algorithm.model.AbstractEdge;
import org.preesm.algorithm.model.dag.DAGEdge;
import org.preesm.algorithm.model.dag.DAGVertex;

/**
 * Class used to represent a braodcast Vertex in a Directed Acyclic Graph.
 *
 * @author jpiat
 */
public class DAGForkVertex extends DAGVertex {

  /** Key to access to property dag_broadcast_vertex. */
  public static final String DAG_FORK_VERTEX = "dag_fork_vertex";

  /** String to access the property edges order. */
  private static final String EDGES_ORDER = "edges_order";

  /**
   * Creates a new DAGVertex.
   */
  public DAGForkVertex() {
    super();
    setKind(DAGForkVertex.DAG_FORK_VERTEX);
  }

  /**
   * Adds the connection.
   *
   * @param newEdge
   *          the new edge
   */
  @SuppressWarnings("unchecked")
  private void addConnection(final DAGEdge newEdge) {
    if (getPropertyBean().getValue(DAGForkVertex.EDGES_ORDER) == null) {
      final List<DAGEdge> connections = new ArrayList<>();
      getPropertyBean().setValue(DAGForkVertex.EDGES_ORDER, connections);
    }
    ((List<DAGEdge>) getPropertyBean().getValue(DAGForkVertex.EDGES_ORDER)).add(newEdge);
  }

  /**
   * Removes the connection.
   *
   * @param newEdge
   *          the new edge
   */
  @SuppressWarnings("unchecked")
  private void removeConnection(final DAGEdge newEdge) {
    if (getPropertyBean().getValue(DAGForkVertex.EDGES_ORDER) == null) {
      final List<DAGEdge> connections = new ArrayList<>();
      getPropertyBean().setValue(DAGForkVertex.EDGES_ORDER, connections);
    }
    ((List<DAGEdge>) getPropertyBean().getValue(DAGForkVertex.EDGES_ORDER)).remove(newEdge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#connectionAdded(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void connectionAdded(final AbstractEdge e) {
    addConnection((DAGEdge) e);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * org.ietr.dftools.algorithm.model.dag.DAGVertex#connectionRemoved(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void connectionRemoved(final AbstractEdge e) {
    removeConnection((DAGEdge) e);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.dag.DAGVertex#toString()
   */
  @Override
  public String toString() {
    return getName();
  }
}
