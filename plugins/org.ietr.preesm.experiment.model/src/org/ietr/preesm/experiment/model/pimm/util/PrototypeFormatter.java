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
