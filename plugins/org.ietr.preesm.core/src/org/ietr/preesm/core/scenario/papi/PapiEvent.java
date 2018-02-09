package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiEvent {

  int                     index;
  String                  name;
  String                  desciption;
  List<PapiEventModifier> modifiers;

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("      <event index=\"%d\" name=\"%s\" desc=\"%s\">%n", index, name, desciption));
    for (PapiEventModifier modifier : modifiers) {
      b.append(modifier.toString());
    }
    b.append(String.format("      </event>%n"));
    return b.toString();
  }
}
