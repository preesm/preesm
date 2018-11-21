/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Alexandre Honorat <ahonorat@insa-rennes.fr> (2018)
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
package org.preesm.model.pisdf.expression.functions;

import org.nfunk.jep.ParseException;

/**
 * Computes a geometric sum. It takes three parameters: the first term a, the ratio r, and the number of iteration
 * (including the first term). Ex: geo_sum(3,1/2,4) = 3 + 3/2 + 3/4 + 3/8
 * <p>
 * It uses an iterative implementation (when |r| &lt 1) instead of the direct formula. The point is to avoid floating
 * point approximations.
 *
 * @author ahonorat
 */
public class GeometricSum extends AbstractPreesmMathFunction {

  @Override
  protected String getName() {
    return "geo_sum";
  }

  @Override
  protected int getArgCount() {
    return 3;
  }

  @Override
  protected double compute(final double... args) throws ParseException {
    // the stack is in the reverse order
    final double a = args[2];
    final double r = args[1];
    int i = (int) args[0];
    if (i < 1) {
      throw new ParseException("Third argument of geo_sum must be a strictly positive integer.");
    }
    if (r == -1) {
      if ((i % 2) == 0) {
        return 0;
      }
      return a;
    } else if (r == 1) {
      return a * i;
    } else if (r == 0) {
      return 0;
    } else if ((r > 1) || (r < -1)) {
      return (a - (a * Math.pow(r, i))) / (1 - r);
    }
    double sum = a;
    double reduceda = a * r;
    for (; i > 1; i--, reduceda *= r) {
      sum += reduceda;
    }
    return sum;
  }

}
