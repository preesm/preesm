/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * The Class FloorFunction.
 */
public class FloorFunction extends PostfixMathCommand {

  /**
   * Instantiates a new floor function.
   */
  public FloorFunction() {
    this.numberOfParameters = 1;
  }

  /**
   * Calculates the result of applying the floor function to the top of the stack and pushes it back on the stack.
   *
   * @param stack
   *          the stack
   * @throws ParseException
   *           the parse exception
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void run(Stack stack) throws ParseException {
    Object aObj = stack.pop();
    if (!(aObj instanceof Number)) {
      throw new ParseException("Floor: argument must be double. It is " + aObj + "(" + aObj.getClass().getName() + ")");
    }
    double aDouble = ((Number) aObj).doubleValue();

    stack.push(Math.floor(aDouble));
  }

}
