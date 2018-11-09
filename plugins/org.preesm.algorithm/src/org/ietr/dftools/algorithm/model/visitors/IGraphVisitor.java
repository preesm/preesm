/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.algorithm.model.visitors;

import org.ietr.dftools.algorithm.model.AbstractEdge;
import org.ietr.dftools.algorithm.model.AbstractGraph;
import org.ietr.dftools.algorithm.model.AbstractVertex;

/**
 * Interface of the SDF visitor.
 *
 * @author jpiat
 * @param <G>
 *          The graph type
 * @param <V>
 *          The vertex type
 * @param <E>
 *          The edge type
 */
@SuppressWarnings("rawtypes")
public interface IGraphVisitor<G extends AbstractGraph, V extends AbstractVertex, E extends AbstractEdge> {

  /**
   * Visit the given edge.
   *
   * @param sdfEdge
   *          the sdf edge
   */
  public default void visit(E sdfEdge) {
    // nothing by default
  }

  /**
   * Visit the given graph.
   *
   * @param sdf
   *          the sdf
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public default void visit(G sdf) {
    // nothing by default
  }

  /**
   * Visit the given vertex.
   *
   * @param sdfVertex
   *          the sdf vertex
   * @throws SDF4JException
   *           the SDF 4 J exception
   */
  public default void visit(V sdfVertex) {
    // nothing by default
  }

}
