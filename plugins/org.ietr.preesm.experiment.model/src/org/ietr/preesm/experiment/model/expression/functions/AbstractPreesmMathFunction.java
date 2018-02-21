/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.preesm.experiment.model.expression.functions;

import java.util.Stack;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Abstract class to wrap simple postfix math functions for JEP.
 */
public abstract class AbstractPreesmMathFunction extends PostfixMathCommand {

  /**
   * Properly initialize number of argument with abstract method
   */
  public AbstractPreesmMathFunction() {
    super();
    this.numberOfParameters = getArgCount();
  }

  public final void integrateWithin(final JEP jep) {
    jep.addFunction(this.getName(), this);
  }

  /**
   * Gets the {@link #getArgCount()} peek elements of the stack and put them in order in an array of double, then call the {@link #compute(double...)} method to
   * compute the result.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void run(Stack stack) throws ParseException {

    final double[] args = new double[getArgCount()];
    for (int i = 0; i < getArgCount(); i++) {
      Object arg = stack.pop();
      if (!(arg instanceof Double)) {
        throw new ParseException("Argument must be a number, whereas it is " + arg + "(" + arg.getClass().getName() + ")");
      }
      args[i] = (double) arg;
    }
    final double result = compute(args);
    stack.push(result);
  }

  /**
   * returns the name of the function that will be used in the math expressions
   */
  protected abstract String getName();

  protected abstract int getArgCount();

  protected abstract double compute(double... args) throws ParseException;

}
