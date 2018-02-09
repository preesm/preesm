package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiEventInfo {

  private final PapiHardware        hardware;
  private final List<PapiComponent> components;

  /**
   *
   */
  public PapiEventInfo(final PapiHardware hardware, final List<PapiComponent> components) {
    this.hardware = hardware;
    this.components = components;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("<eventinfo>%n"));
    b.append(this.hardware.toString());
    for (final PapiComponent component : this.components) {
      b.append(component.toString());
    }
    b.append(String.format("</eventinfo>%n"));
    return b.toString();
  }
}
