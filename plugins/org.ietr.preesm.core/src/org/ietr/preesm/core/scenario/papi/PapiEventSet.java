package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 */
public class PapiEventSet {

  private PapiEventSetType type;

  public PapiEventSetType getType() {
    return this.type;
  }

  public void setType(final PapiEventSetType type) {
    this.type = type;
  }

  public List<PapiEvent> getEvents() {
    return this.events;
  }

  public void setEvents(final List<PapiEvent> events) {
    this.events = events;
  }

  private List<PapiEvent> events;

  /**
   *
   */
  public boolean containsEvent(final PapiEvent event) {
    return this.events.contains(event);
  }

  @Override
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean typeComp = false;
    boolean eventsComp = false;

    if (comparer instanceof PapiEventSet) {
      final PapiEventSet tester = (PapiEventSet) comparer;
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
    return this.type.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("    <eventset type=\"%s\">%n", string));
    for (final PapiEvent event : this.events) {
      b.append(event.toString());
    }
    b.append(String.format("    </eventset>%n"));
    return b.toString();
  }
}
