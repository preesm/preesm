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
