/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Jonathan Piat <jpiat@laas.fr> (2013)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2015)
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

import org.ietr.dftools.algorithm.DFToolsAlgoException;
import org.ietr.dftools.algorithm.model.AbstractEdgePropertyType;
import org.ietr.dftools.algorithm.model.parameters.IExpressionSolver;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.parameters.NoIntegerValueException;
import org.ietr.dftools.algorithm.model.parameters.Value;

/**
 * Class used to represent the integer edge property type in a SDF.
 *
 * @author jpiat
 */
public class ExpressionEdgePropertyType extends AbstractEdgePropertyType<Value> {

  /** The computed value. */
  private Long computedValue;

  /**
   * Creates a new SDFDefaultEdgePropertyType with the given graph value.
   *
   * @param val
   *          The Integer value of this SDFDefaultEdgePropertyType
   */
  public ExpressionEdgePropertyType(final Value val) {
    super(val);
    this.computedValue = null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractEdgePropertyType#clone()
   */
  @Override
  public ExpressionEdgePropertyType copy() {
    final ExpressionEdgePropertyType clone = new ExpressionEdgePropertyType(this.value);
    try {
      clone.computedValue = longValue();
    } catch (final InvalidExpressionException e) {
      clone.computedValue = null;
    }
    return clone;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractEdgePropertyType#setValue(java.lang.Object)
   */
  @Override
  public void setValue(final Value val) {
    super.setValue(val);
    this.computedValue = null;
  }

  /**
   * Sets the expression solver to use to compute intValue.
   *
   * @param solver
   *          The solver to be used
   */
  public void setExpressionSolver(final IExpressionSolver solver) {
    getValue().setExpressionSolver(solver);
    this.computedValue = null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractEdgePropertyType#toString()
   */
  @Override
  public String toString() {
    return this.value.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.AbstractEdgePropertyType#intValue()
   */
  @Override
  public long longValue() {
    if (this.computedValue == null) {
      try {
        this.computedValue = this.value.longValue();
        return this.computedValue;
      } catch (final NoIntegerValueException e) {
        throw new DFToolsAlgoException("Could not evaluate expression", e);
      }
    }
    return this.computedValue;

  }

}
