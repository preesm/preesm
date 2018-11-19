/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2010 - 2012)
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
package org.ietr.preesm.core.types;

import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFEdge;
import org.preesm.algorithm.model.sdf.SDFGraph;

/**
 * Represents the type of a vertex in its propertybeans.
 *
 * @author mpelcat
 */
public class VertexType {

  /** String used to qualify receive actors. */
  public static final String TYPE_RECEIVE = "receive";

  /** String used to qualify send actors. */
  public static final String TYPE_SEND = "send";

  /** String used to qualify task actors. */
  public static final String TYPE_TASK = "task";

  /** VertexType representing a receive operation. */
  public static final VertexType RECEIVE = new VertexType(VertexType.TYPE_RECEIVE);

  /** VertexType representing a send operation. */
  public static final VertexType SEND = new VertexType(VertexType.TYPE_SEND);

  /** VertexType representing a task. */
  public static final VertexType TASK = new VertexType(VertexType.TYPE_TASK);

  /**
   * Returns true if this receive operation leads to a send operation.
   *
   * @param vertex
   *          the vertex
   * @return true, if is intermediate receive
   */
  public static boolean isIntermediateReceive(final SDFAbstractVertex vertex) {

    final VertexType vType = (VertexType) vertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.Vertex_vertexType);

    // If the communication operation is an intermediate step of a route
    if (vType.isReceive()) {
      for (final SDFEdge outEdge : ((SDFGraph) vertex.getBase()).outgoingEdgesOf(vertex)) {

        final VertexType nextVType = (VertexType) outEdge.getTarget().getPropertyBean()
            .getValue(ImplementationPropertyNames.Vertex_vertexType);

        if (nextVType.isSend()) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Returns true if this send operation follows a receive operation.
   *
   * @param vertex
   *          the vertex
   * @return true, if is intermediate send
   */
  public static boolean isIntermediateSend(final SDFAbstractVertex vertex) {

    final VertexType vType = (VertexType) vertex.getPropertyBean()
        .getValue(ImplementationPropertyNames.Vertex_vertexType);

    // If the communication operation is an intermediate step of a route
    if (vType.isSend()) {
      final SDFEdge inEdge = (SDFEdge) (((SDFGraph) vertex.getBase()).incomingEdgesOf(vertex).toArray()[0]);
      final VertexType prevVType = (VertexType) inEdge.getSource().getPropertyBean()
          .getValue(ImplementationPropertyNames.Vertex_vertexType);

      if (prevVType.isReceive()) {
        return true;
      }
    }

    return false;
  }

  /** VertexType representing a task. */
  private String type = "";

  /**
   * Instantiates a new vertex type.
   *
   * @param type
   *          the type
   */
  private VertexType(final String type) {
    super();
    this.type = type;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {

    if (obj instanceof VertexType) {
      return (((VertexType) obj).type.equals(this.type));
    }
    return false;
  }

  /**
   * Checks if is receive.
   *
   * @return true, if is receive
   */
  public boolean isReceive() {
    return (this == VertexType.RECEIVE);
  }

  /**
   * Checks if is send.
   *
   * @return true, if is send
   */
  public boolean isSend() {
    return (this == VertexType.SEND);
  }

  /**
   * Checks if is task.
   *
   * @return true, if is task
   */
  public boolean isTask() {
    return (this == VertexType.TASK);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.type;
  }

}
