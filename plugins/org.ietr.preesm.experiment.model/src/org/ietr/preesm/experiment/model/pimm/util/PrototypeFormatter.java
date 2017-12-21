/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.experiment.model.pimm.util;

import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;

/**
 * Utility class to pretty print a loop/init prototype
 */
public class PrototypeFormatter {

  private PrototypeFormatter() {
    // avoid instantiation
  }

  /**
   * Pretty prints a FunctionPrototype as a String formatted as follows: name (parameters types)
   */
  public static final String format(final FunctionPrototype prototype) {
    StringBuilder result = new StringBuilder(prototype.getName() + "(");
    boolean first = true;
    for (final FunctionParameter p : prototype.getParameters()) {
      if (first) {
        first = false;
      } else {
        result.append(", ");
      }
      result.append(p.getType());
      result.append((!p.isIsConfigurationParameter()) ? " * " : "");
      result.append(" " + p.getName());
    }
    result.append(")");
    return result.toString();
  }
}
