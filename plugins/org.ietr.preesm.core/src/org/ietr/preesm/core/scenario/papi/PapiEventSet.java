package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 */
public class PapiEventSet {

  private PapiEventSetType type;

  public PapiEventSetType getType() {
    return type;
  }

  public void setType(PapiEventSetType type) {
    this.type = type;
  }

  public List<PapiEvent> getEvents() {
    return events;
  }

  public void setEvents(List<PapiEvent> events) {
    this.events = events;
  }

  private List<PapiEvent> events;

  @Override
  public boolean equals(Object comparer) {

    boolean decision = false;
    boolean typeComp = false;
    boolean eventsComp = false;

    if (comparer instanceof PapiEventSet) {
      PapiEventSet tester = (PapiEventSet) comparer;
      if (this.type.equals(tester.getType())) {
        typeComp = true;
      }
      if (this.events.equals(tester.getEvents())) {
        eventsComp = true;
      }
      if (typeComp && eventsComp) {
        decision = true;
      }
    }
    return decision;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("    <eventset type=\"%s\">%n", string));
    for (final PapiEvent event : events) {
      b.append(event.toString());
    }
    b.append(String.format("    </eventset>%n"));
    return b.toString();
  }
}
