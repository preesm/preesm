package org.ietr.preesm.core.scenario.papi;

/**
 *
 * @author anmorvan
 *
 */
public class PapiCpuID {
  int family;
  int model;
  int stepping;

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("    <cpuID>%n"));
    b.append(String.format("      <family value=\"%d\"/>%n", this.family));
    b.append(String.format("      <model value=\"%d\"/>%n", this.model));
    b.append(String.format("      <stepping value=\"%d\"/>%n", this.stepping));
    b.append(String.format("    </cpuID>%n"));
    return b.toString();

  }
}
