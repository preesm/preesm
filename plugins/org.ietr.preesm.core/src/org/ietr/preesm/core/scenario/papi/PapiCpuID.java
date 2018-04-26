package org.ietr.preesm.core.scenario.papi;

/**
 *
 * @author anmorvan
 *
 */
public class PapiCpuID {
  private int family;
  private int model;

  public int getFamily() {
    return this.family;
  }

  public void setFamily(final int family) {
    this.family = family;
  }

  public int getModel() {
    return this.model;
  }

  public void setModel(final int model) {
    this.model = model;
  }

  public int getStepping() {
    return this.stepping;
  }

  public void setStepping(final int stepping) {
    this.stepping = stepping;
  }

  private int stepping;

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
