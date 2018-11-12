/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
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
package org.ietr.dftools.algorithm.factories;

import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGBroadcastVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGEndVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGForkVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGInitVertex;
import org.ietr.dftools.algorithm.model.dag.edag.DAGJoinVertex;
import org.w3c.dom.Element;

/**
 * Factory for DAGVertex creation.
 *
 * @author jpiat
 */
public class DAGVertexFactory implements IModelVertexFactory<DAGVertex> {

  /** The instance. */
  private static DAGVertexFactory instance;

  /**
   * Instantiates a new DAG vertex factory.
   */
  private DAGVertexFactory() {

  }

  /**
   * Gets the single instance of DAGVertexFactory.
   *
   * @return single instance of DAGVertexFactory
   */
  public static DAGVertexFactory getInstance() {
    if (DAGVertexFactory.instance == null) {
      DAGVertexFactory.instance = new DAGVertexFactory();
    }
    return DAGVertexFactory.instance;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.factories.ModelVertexFactory#createVertex(org.w3c.dom.Element)
   */
  @Override
  public DAGVertex createVertex(final Element vertexElt) {
    final String kind = getProperty(vertexElt, AbstractVertex.KIND_LITERAL);
    return this.createVertex(kind);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.factories.ModelVertexFactory#createVertex(java.lang.String)
   */
  @Override
  public DAGVertex createVertex(final String kind) {
    if (kind.equals(DAGVertex.DAG_VERTEX)) {
      return new DAGVertex();
    } else if (kind.equals(DAGBroadcastVertex.DAG_BROADCAST_VERTEX)) {
      return new DAGBroadcastVertex();
    } else if (kind.equals(DAGForkVertex.DAG_FORK_VERTEX)) {
      return new DAGForkVertex();
    } else if (kind.equals(DAGJoinVertex.DAG_JOIN_VERTEX)) {
      return new DAGJoinVertex();
    } else if (kind.equals(DAGInitVertex.DAG_INIT_VERTEX)) {
      return new DAGInitVertex();
    } else if (kind.equals(DAGEndVertex.DAG_END_VERTEX)) {
      return new DAGEndVertex();
    } else {
      return new DAGVertex();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.factories.ModelVertexFactory#createInterface(java.lang.String, int)
   */
  @Override
  public IInterface createInterface(final String name, final int dir) {
    return null;
  }

}
