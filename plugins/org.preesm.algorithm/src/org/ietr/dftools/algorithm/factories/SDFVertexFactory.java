/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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

import java.util.Map;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.IInterface;
import org.ietr.dftools.algorithm.model.InterfaceDirection;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFForkVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFInitVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFJoinVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFRoundBufferVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;
import org.w3c.dom.Element;

/**
 * Class used as an SDFVertex factory to provides user with convinient method to creates SDFAbstractVertex.
 *
 * @author jpiat
 */
public class SDFVertexFactory implements IModelVertexFactory<SDFAbstractVertex> {

  /** The instance. */
  private static SDFVertexFactory instance;

  /**
   * Instantiates a new SDF vertex factory.
   */
  private SDFVertexFactory() {

  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.factories.ModelVertexFactory#createVertex(org.w3c.dom.Element)
   */
  @Override
  public SDFAbstractVertex createVertex(final Element vertexElt) {
    final String kind = getProperty(vertexElt, AbstractVertex.KIND_LITERAL);
    if (kind == null) {
      throw new NullPointerException("Vertex elements should have a child element of type 'kind'");
    } else {
      switch (kind) {
        case SDFVertex.VERTEX:
          final SDFVertex newVertex = new SDFVertex();
          newVertex.setName("default");
          return newVertex;

        case SDFInterfaceVertex.PORT:
          final String direction = getProperty(vertexElt, SDFInterfaceVertex.PORT_DIRECTION);
          if (direction != null) {
            if (direction.equals(InterfaceDirection.INPUT.name())) {
              return new SDFSourceInterfaceVertex();
            } else if (direction.equals(InterfaceDirection.OUTPUT.name())) {
              return new SDFSinkInterfaceVertex();
            }
          }
          return null;

        case SDFBroadcastVertex.BROADCAST:
          return new SDFBroadcastVertex();

        case SDFRoundBufferVertex.ROUND_BUFFER:
          return new SDFRoundBufferVertex();

        case SDFForkVertex.FORK:
          return new SDFForkVertex();

        case SDFJoinVertex.JOIN:
          return new SDFJoinVertex();

        case SDFInitVertex.INIT:
          return new SDFInitVertex();
        default:
          throw new UnsupportedOperationException("Unsupported vertex kind " + kind);
      }
    }

  }

  /**
   * Creates a new SDFVertex object.
   *
   * @param attributes
   *          The attributes of the vertex
   * @return The created vertex
   * @deprecated Creates a vertex with the given parameters Used when kind was a node attribute, now a property
   */
  @Deprecated
  public SDFAbstractVertex createVertex(final Map<String, String> attributes) {
    final String kind = attributes.get("kind");
    if (kind.equals(SDFVertex.VERTEX)) {
      final SDFVertex newVertex = new SDFVertex();
      newVertex.setName("default");
      return newVertex;
    } else if (kind.equals(SDFInterfaceVertex.PORT)) {
      if (attributes.get(SDFInterfaceVertex.PORT_DIRECTION) != null) {
        if (attributes.get(SDFInterfaceVertex.PORT_DIRECTION).equals(InterfaceDirection.INPUT.name())) {
          return new SDFSourceInterfaceVertex();
        } else if (attributes.get(SDFInterfaceVertex.PORT_DIRECTION).equals(InterfaceDirection.OUTPUT.name())) {
          return new SDFSinkInterfaceVertex();
        }
        return null;
      }
    } else if (kind.equals(SDFBroadcastVertex.BROADCAST)) {
      return new SDFBroadcastVertex();
    } else if (kind.equals(SDFRoundBufferVertex.ROUND_BUFFER)) {
      return new SDFRoundBufferVertex();
    } else if (kind.equals(SDFForkVertex.FORK)) {
      return new SDFForkVertex();
    } else if (kind.equals(SDFJoinVertex.JOIN)) {
      return new SDFJoinVertex();
    } else if (kind.equals(SDFInitVertex.INIT)) {
      return new SDFInitVertex();
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.factories.ModelVertexFactory#createVertex(java.lang.String)
   */
  @Override
  public SDFAbstractVertex createVertex(final String kind) {
    throw new UnsupportedOperationException("Unimplemented method");
  }

  /**
   * Gets the single instance of SDFVertexFactory.
   *
   * @return single instance of SDFVertexFactory
   */
  public static SDFVertexFactory getInstance() {
    if (SDFVertexFactory.instance == null) {
      SDFVertexFactory.instance = new SDFVertexFactory();
    }
    return SDFVertexFactory.instance;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.factories.ModelVertexFactory#createInterface(java.lang.String, int)
   */
  @Override
  public IInterface createInterface(final String name, final int dir) {
    IInterface port;
    if (dir == 1) {
      port = new SDFSinkInterfaceVertex();
    } else {
      port = new SDFSourceInterfaceVertex();
    }
    port.setName(name);
    return port;
  }
}
