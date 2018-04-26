package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiComponent {

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public PapiComponentType getType() {
    return this.type;
  }

  public void setType(final PapiComponentType type) {
    this.type = type;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(final int index) {
    this.index = index;
  }

  public List<PapiEventSet> getEventSets() {
    return this.eventSets;
  }

  public void setEventSets(final List<PapiEventSet> eventSets) {
    this.eventSets = eventSets;
  }

  /**
   *
   */
  public boolean containsEvent(final PapiEvent event) {
    boolean decision = false;
    for (final PapiEventSet eventSet : this.eventSets) {
      if (eventSet.containsEvent(event)) {
        decision = true;
      }
    }
    return decision;
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
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean idComp = false;
    boolean typeComp = false;
    boolean indexComp = false;
    boolean eventSetsComp = false;

    if (comparer instanceof PapiComponent) {
      final PapiComponent tester = (PapiComponent) comparer;
      if (this.id.equals(tester.getId())) {
        idComp = true;
      }
      if (this.type.equals(tester.getType())) {
        typeComp = true;
      }
      if (this.index == tester.getIndex()) {
        indexComp = true;
      }
      if (this.eventSets.equals(tester.getEventSets())) {
        eventSetsComp = true;
      }
      if (idComp && typeComp && indexComp && eventSetsComp) {
        decision = true;
      }
    }
    return decision;
  }

  @Override
  public int hashCode() {
    return this.index;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("  <component id=\"%s\" index=\"%d\" type=\"%s\">%n", this.id, this.index, string));
    for (final PapiEventSet eventSet : this.eventSets) {
      b.append(eventSet.toString());
    }
    b.append(String.format("  </component>%n"));
    return b.toString();
  }
}
