/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2009 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014)
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides a parameter associated with an object (vertex, graph, edge). It has a name, a type, and a
 * position.
 *
 * @author Jonathan Piat
 * @author Matthieu Wipliez
 *
 */
public class Parameter {

  /** The default value. */
  private final Object defaultValue;

  /** The name. */
  private final String name;

  /** The position. */
  private final ParameterPosition position;

  /** The type. */
  private final Class<?> type;

  /**
   * Creates a new parameter.
   *
   * @param name
   *          The parameter name.
   * @param value
   *          The parameter default value.
   * @param position
   *          Its position, may be <code>null</code>.
   * @param type
   *          The parameter type, as a Java {@link Class}&lt;?&gt;.
   */
  public Parameter(final String name, final Object value, final ParameterPosition position, final Class<?> type) {
    this.defaultValue = value;
    this.name = name;
    this.position = position;
    this.type = type;
  }

  /**
   * Returns this parameter's default value.
   *
   * @return This parameter's default value.
   */
  public Object getDefault() {
    if (this.defaultValue instanceof List<?>) {
      return new ArrayList<Object>((List<?>) this.defaultValue);
    } else if (this.defaultValue instanceof Map<?, ?>) {
      return new TreeMap<Object, Object>((Map<?, ?>) this.defaultValue);
    } else {
      return this.defaultValue;
    }
  }

  /**
   * Returns this parameter's name.
   *
   * @return This parameter's name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns this parameter's position.
   *
   * @return This parameter's position.
   */
  public ParameterPosition getPosition() {
    return this.position;
  }

  /**
   * Returns this parameter's type.
   *
   * @return This parameter's type.
   */
  public Class<?> getType() {
    return this.type;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.name;
  }

}
