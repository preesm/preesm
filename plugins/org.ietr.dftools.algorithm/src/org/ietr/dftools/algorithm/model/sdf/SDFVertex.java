/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Antoine Morvan <antoine.morvan.pro@gmail.com> (2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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
package org.ietr.dftools.algorithm.model.sdf;

import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSinkInterfaceVertex;
import org.ietr.dftools.algorithm.model.sdf.esdf.SDFSourceInterfaceVertex;

/**
 * Class used to represent Hierachical vertices, meaning it is a vertex in the parent graph, but is itself a graph.
 *
 * @author jpiat
 */
public class SDFVertex extends SDFAbstractVertex {

  /** Property memory script. */
  public static final String MEMORY_SCRIPT = "memory_script";

  static {
    AbstractVertex.public_properties.add(SDFVertex.MEMORY_SCRIPT);
  }

  /** The Constant serialVersionUID. */
  protected static final long serialVersionUID = -4281714330859590518L;

  /** Kind of node. */
  public static final String VERTEX = "vertex";

  /**
   * Builds a new vertex.
   */
  public SDFVertex() {
    super();
    setKind(SDFVertex.VERTEX);
  }

  /**
   * Constructs a new SDFVertex given its base graph arg0.
   *
   * @param arg0
   *          the arg 0
   */
  public SDFVertex(final SDFGraph arg0) {
    super();
    setKind(SDFVertex.VERTEX);
    setBase(arg0);
  }

  /**
   * Clone the vertex.
   *
   * @return the SDF vertex
   */
  @Override
  public SDFVertex copy() {
    final SDFVertex newVertex = new SDFVertex(null);
    for (final String key : getPropertyBean().keys()) {
      if (getPropertyBean().getValue(key) != null) {
        final Object val = getPropertyBean().getValue(key);
        newVertex.getPropertyBean().setValue(key, val);
      }
    }
    // Copy refinement properties
    newVertex.setRefinement(getRefinement());
    for (final SDFInterfaceVertex sink : getSinks()) {
      if ((newVertex.getGraphDescription() != null)
          && (newVertex.getGraphDescription().getVertex(sink.getName()) != null)) {
        newVertex.addSink((SDFSinkInterfaceVertex) getGraphDescription().getVertex(sink.getName()));
      } else {
        newVertex.addSink((SDFSinkInterfaceVertex) sink.copy());
      }
    }
    for (final SDFInterfaceVertex source : getSources()) {
      if ((newVertex.getGraphDescription() != null)
          && (newVertex.getGraphDescription().getVertex(source.getName()) != null)) {
        newVertex.addSource((SDFSourceInterfaceVertex) getGraphDescription().getVertex(source.getName()));
      } else {
        newVertex.addSource((SDFSourceInterfaceVertex) source.copy());
      }
    }
    try {
      newVertex.setNbRepeat(getNbRepeat());
    } catch (final InvalidExpressionException e) {
      throw new DFToolsAlgoException("Could not clone vertex", e);
    }

    return newVertex;
  }

}
