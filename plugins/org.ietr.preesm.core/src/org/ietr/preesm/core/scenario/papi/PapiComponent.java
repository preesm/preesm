package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiComponent {

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public PapiComponentType getType() {
    return type;
  }

  public void setType(PapiComponentType type) {
    this.type = type;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public List<PapiEventSet> getEventSets() {
    return eventSets;
  }

  public void setEventSets(List<PapiEventSet> eventSets) {
    this.eventSets = eventSets;
  }

  private String             id;
  private PapiComponentType  type;
  private int                index;
  private List<PapiEventSet> eventSets;

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
