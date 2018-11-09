/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2012)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
package org.ietr.dftools.algorithm.model.sdf.esdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFEdge;
import org.ietr.dftools.algorithm.model.sdf.SDFInterfaceVertex;

/**
 * ROund buffer vertex.
 *
 * @author jpiat
 */
public class SDFRoundBufferVertex extends SDFBroadcastVertex {

  /** Kind of node. */
  public static final String ROUND_BUFFER = "RoundBuffer";

  /**
   * Creates a new SDFInterfaceVertex with the default direction (SINK).
   */
  public SDFRoundBufferVertex() {
    super();
    setNbRepeat(1L);
  }

  /**
   * Gives the edge connection index.
   *
   * @param edge
   *          The edge to get the connection index
   * @return The connection index of the edge
   */
  @Override
  public Long getEdgeIndex(final SDFEdge edge) {
    for (final Long index : getConnections().keySet()) {
      if (getConnections().get(index).equals(edge)) {
        return index;
      }
    }
    return null;
  }

  /**
   * Gets the incoming connections.
   *
   * @return The incoming connection of this for in an ordered list
   */
  public List<SDFEdge> getIncomingConnections() {
    final List<SDFEdge> edges = new ArrayList<>(getConnections().size());
    for (final Long index : getConnections().keySet()) {
      edges.add(index.intValue(), getConnections().get(index));
    }
    return edges;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex#getOutgoingConnections()
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<SDFEdge> getOutgoingConnections() {
    return new ArrayList<>(getBase().outgoingEdgesOf(this));
  }

  /**
   * Sets this edge connection index.
   *
   * @param edge
   *          The edge connected to the vertex
   * @param index
   *          The index in the connections
   */
  public void setConnectionIndex(final SDFEdge edge, final long index) {
    final Map<Long, SDFEdge> connections = getConnections();
    connections.put(index, edge);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.sdf.esdf.SDFBroadcastVertex#clone()
   */
  @Override
  public SDFRoundBufferVertex copy() {
    final SDFRoundBufferVertex copy = new SDFRoundBufferVertex();
    copy.setName(getName());
    try {
      copy.setNbRepeat(getNbRepeat());
    } catch (final InvalidExpressionException e) {
      throw new DFToolsAlgoException("could not clone vertex", e);
    }

    // Copy the ports
    for (final SDFInterfaceVertex sink : getSinks()) {
      if ((copy.getGraphDescription() != null) && (copy.getGraphDescription().getVertex(sink.getName()) != null)) {
        copy.addSink((SDFSinkInterfaceVertex) getGraphDescription().getVertex(sink.getName()));
      } else {
        copy.addSink((SDFSinkInterfaceVertex) sink.copy());
      }
    }
    for (final SDFInterfaceVertex source : getSources()) {
      if ((copy.getGraphDescription() != null) && (copy.getGraphDescription().getVertex(source.getName()) != null)) {
        copy.addSource((SDFSourceInterfaceVertex) getGraphDescription().getVertex(source.getName()));
      } else {
        copy.addSource((SDFSourceInterfaceVertex) source.copy());
      }
    }

    return copy;
  }

}
