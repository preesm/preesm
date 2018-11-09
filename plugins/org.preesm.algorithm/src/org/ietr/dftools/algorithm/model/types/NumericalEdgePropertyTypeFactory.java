/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
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
package org.ietr.dftools.algorithm.model.types;

import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.PropertyFactory;
import org.ietr.dftools.algorithm.model.parameters.ExpressionValue;

/**
 * Factory to build SDF edge property base on an input string.
 *
 * @author jpiat
 */
public class NumericalEdgePropertyTypeFactory implements PropertyFactory {

  /** The instance. */
  private static NumericalEdgePropertyTypeFactory instance;

  /**
   * Instantiates a new SDF numerical edge property type factory.
   */
  private NumericalEdgePropertyTypeFactory() {

  }

  /**
   * Gets the single instance of SDFNumericalEdgePropertyTypeFactory.
   *
   * @return single instance of SDFNumericalEdgePropertyTypeFactory
   */
  public static NumericalEdgePropertyTypeFactory getInstance() {
    if (NumericalEdgePropertyTypeFactory.instance == null) {
      NumericalEdgePropertyTypeFactory.instance = new NumericalEdgePropertyTypeFactory();
    }
    return NumericalEdgePropertyTypeFactory.instance;
  }

  /**
   * Creates a new SDFExpressionEdgePropertyType given the expression expr.
   *
   * @param expr
   *          The expression
   * @return The created SDFExpressionEdgePropertyType
   */
  public AbstractEdgePropertyType<?> getSDFEdgePropertyType(final String expr) {
    try {
      final long value = Long.parseLong(expr);
      return new LongEdgePropertyType(value);
    } catch (final NumberFormatException e) {
      return new ExpressionEdgePropertyType(new ExpressionValue(expr));
    }
  }

  /**
   * Creates a new SDFIntEdgePropertyType given the value val.
   *
   * @param val
   *          The integer value
   * @return The created SDFIntEdgePropertyType
   */
  public AbstractEdgePropertyType<?> getSDFEdgePropertyType(final long val) {
    return new LongEdgePropertyType(val);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.PropertyFactory#create(java.lang.Object)
   */
  @Override
  public Object create(final Object value) {
    if (value instanceof String) {
      return getSDFEdgePropertyType((String) value);
    } else if (value instanceof Long) {
      return getSDFEdgePropertyType((Long) value);
    }
    return null;
  }
}
