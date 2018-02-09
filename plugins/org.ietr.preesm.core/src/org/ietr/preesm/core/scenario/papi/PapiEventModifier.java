package org.ietr.preesm.core.scenario.papi;

/**
 *
 */
public class PapiEventModifier {

  String name;
  String description;

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("        <modifier name=\"%s\" desc=\"%s\">", name, description));
    b.append(String.format(" </modifier>%n"));
    return b.toString();
  }
}
