/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
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
package org.ietr.dftools.algorithm.model.parameters;

/**
 * Class used to represent Argument.
 *
 * @author jpiat
 * @author kdesnos
 */
public class Argument {

  /** The name. */
  private String name;

  /** The value. */
  private Value value;

  /**
   * Builds a new argument with the given name.
   *
   * @param name
   *          The name of the argument
   */
  public Argument(final String name) {
    this.name = name;
    this.value = new ExpressionValue("");
  }

  /**
   * Builds a new argument with the given name and value.
   *
   * @param name
   *          The name of the argument
   * @param value
   *          The value of the argument
   */
  public Argument(final String name, final String value) {
    this.name = name;
    try {
      final long ongValue = Long.parseLong(value);
      this.value = new ConstantValue(ongValue);
    } catch (final NumberFormatException e) {
      this.value = new ExpressionValue(value);
    }
  }

  /**
   * Builds a new argument with the given name and value.
   *
   * @param name
   *          The name of the argument
   * @param value
   *          The value of the argument
   */
  public Argument(final String name, final Value value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Gives the name of the argument.
   *
   * @return The name of the argument
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of the argument.
   *
   * @param name
   *          The name to set for the argument
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Set the solver to use for this expression.
   *
   * @param solver
   *          The solver to use to compute int value
   */
  public void setExpressionSolver(final IExpressionSolver solver) {
    this.value.setExpressionSolver(solver);
  }

  /**
   * Int value.
   *
   * @return the int
   * @throws InvalidExpressionException
   *           the invalid expression exception
   * @throws NoIntegerValueException
   *           the no integer value exception
   */
  public long longValue() {
    // kdesnos: Removed this line because it was
    // removing information from the graph before
    // flattening. Putting this line back implies
    // that all expressions be replaced with their constant
    // value very early in graph transformations
    return this.value.longValue();
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return this.value.getValue();
  }

  /**
   * Gets the object value.
   *
   * @return the object value
   */
  public Value getObjectValue() {
    return this.value;
  }

  /**
   * Sets the value.
   *
   * @param value
   *          the new value
   */
  public void setValue(final String value) {
    try {
      final long longValue = Long.parseLong(value);
      this.value = new ConstantValue(longValue);
    } catch (final NumberFormatException e) {
      this.value = new ExpressionValue(value);
    }
  }

}
