/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

/**
 * Represents an numerical expression.
 *
 * @author jpiat
 */
public class ExpressionValue implements Value {

  /** The parent graph to use to solve this expresion. */
  private IExpressionSolver solver;

  /** The value. */
  private Long value;

  /** The expression. */
  private String expression;

  /**
   * Constructs a new Expression.
   *
   * @param expression
   *          The string representation of the expression
   */
  public ExpressionValue(final String expression) {
    this.expression = expression;
    this.value = null;
  }

  /**
   * Gives the long value of this expression.
   *
   * @return The long value of the expression
   * @throws InvalidExpressionException
   *           When expression can't be solved
   * @throws NoIntegerValueException
   *           the no integer value exception
   */
  @Override
  public long longValue() {
    if (this.value == null) {
      if (this.solver != null) {
        this.value = this.solver.solveExpression(this.expression, this);
      } else {
        Object result;
        try {
          final JEP jep = new JEP();
          final Node mainExpressionNode = jep.parse(this.expression);
          result = jep.evaluate(mainExpressionNode);
          if (result instanceof Number) {
            this.value = ((Number) result).longValue();
          } else {
            throw (new InvalidExpressionException("Not a numeric expression"));
          }
        } catch (final ParseException e) {
          throw (new InvalidExpressionException("Can't parse expresion :" + this.expression));
        }
      }
    }
    return this.value;
  }

  /**
   * Set the solver to use for this expression.
   *
   * @param solver
   *          The solver to use to compute int value
   */
  @Override
  public void setExpressionSolver(final IExpressionSolver solver) {
    this.solver = solver;
    this.value = null;
  }

  /**
   * Gives the value of the variable.
   *
   * @return The value of the variable
   */
  @Override
  public String getValue() {
    return this.expression;
  }

  /**
   * Sets the value of the variable.
   *
   * @param value
   *          The value to set for the variable
   */
  @Override
  public void setValue(final String value) {
    this.value = null;
    this.expression = value;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.expression;
  }
}
