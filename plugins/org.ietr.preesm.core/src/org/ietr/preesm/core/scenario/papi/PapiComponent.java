package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiComponent {

  String             id;
  PapiComponentType  type;
  int                index;
  List<PapiEventSet> eventSets;

  /**
   *
   */
  public PapiComponent(final String componentID, final String componentIndex, final String componentType) {
    this.index = Integer.valueOf(componentIndex);
    this.id = componentID;
    this.type = PapiComponentType.parse(componentType);
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("  <component id=\"%s\" index=\"%d\" type=\"%s\">%n", this.id, this.index, string));
    for (PapiEventSet eventSet : eventSets) {
      b.append(eventSet.toString());
    }
    b.append(String.format("  </component>%n"));
    return b.toString();
  }
}
