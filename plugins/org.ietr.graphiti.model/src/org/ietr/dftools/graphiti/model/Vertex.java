/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2009 - 2010)
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
package org.ietr.dftools.graphiti.model;

import java.util.List;

/**
 * This class represents a vertex.
 *
 * @author Matthieu Wipliez
 *
 */
public class Vertex extends AbstractObject {

  /**
   * String for the "destination vertex" property. Set when a vertex becomes the destination of a dependency. zef zef ze
   * f fez fze fz
   */
  public static final String PROPERTY_DST_VERTEX = "destination vertex";

  /**
   * String for the "size" property. Set when the location/size of a vertex changes.
   */
  public static final String PROPERTY_SIZE = "size";

  /**
   * String for the "source vertex" property. Set when a vertex becomes the source of a dependency.
   */
  public static final String PROPERTY_SRC_VERTEX = "source vertex";

  /**
   * String for the "Input port" type.
   */
  public static final String TYPE_INPUT_PORT = "Input port";

  /**
   * String for the "Output port" type.
   */
  public static final String TYPE_OUTPUT_PORT = "Output port";

  /**
   * String for the "Port" type.
   */
  public static final String TYPE_PORT = "Port";

  /**
   * The parent graph of this vertex.
   */
  Graph parent;

  /**
   * Creates a vertex with the given type.
   *
   * @param type
   *          The vertex type.
   */
  public Vertex(final ObjectType type) {
    super(type);

    // set default values
    final List<Parameter> parameters = type.getParameters();
    for (final Parameter parameter : parameters) {
      setValue(parameter.getName(), parameter.getDefault());
    }
  }

  /**
   * Creates a new vertex which is a copy of the given vertex.
   *
   * @param vertex
   *          The source vertex.
   */
  public Vertex(final Vertex vertex) {
    super(vertex);
    this.parent = vertex.parent;
  }

  /**
   * Returns the configuration associated with this Vertex.
   *
   * @return The configuration associated with this Vertex.
   */
  public Configuration getConfiguration() {
    return this.parent.getConfiguration();
  }

  /**
   * Returns the parent {@link Graph} of this Vertex.
   *
   * @return The parent {@link Graph} of this Vertex.
   */
  public Graph getParent() {
    return this.parent;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.graphiti.model.AbstractObject#setValue(java.lang.String, java.lang.Object)
   */
  @Override
  public Object setValue(final String propertyName, final Object newValue) {
    if (ObjectType.PARAMETER_ID.equals(propertyName) && (this.parent != null)) {
      this.parent.changeVertexId(this, (String) newValue);
    }
    return super.setValue(propertyName, newValue);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getType() + ": " + getValue(ObjectType.PARAMETER_ID);
  }

}
