/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2010 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 * Jonathan Piat [jpiat@laas.fr] (2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2013)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2010 - 2012)
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
package org.preesm.algorithm.mapper.graphtransfo;

import java.util.Objects;

/**
 * Represents the type of a vertex in its propertybeans.
 *
 * @author mpelcat
 */
public class VertexType {

  public static final String     TYPE_RECEIVE = "receive";
  public static final String     TYPE_SEND    = "send";
  public static final String     TYPE_TASK    = "task";
  public static final VertexType RECEIVE      = new VertexType(VertexType.TYPE_RECEIVE);
  public static final VertexType SEND         = new VertexType(VertexType.TYPE_SEND);
  public static final VertexType TASK         = new VertexType(VertexType.TYPE_TASK);
  private final String           type;

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

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof final VertexType vertexType) {
      return vertexType.type.equals(this.type);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.type);
  }

  public boolean isReceive() {
    return (this == VertexType.RECEIVE);
  }

  public boolean isSend() {
    return (this == VertexType.SEND);
  }

  public boolean isTask() {
    return (this == VertexType.TASK);
  }

  @Override
  public String toString() {
    return this.type;
  }

}
