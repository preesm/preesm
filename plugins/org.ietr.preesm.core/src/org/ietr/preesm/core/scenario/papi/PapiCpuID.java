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
    return family;
  }

  public void setFamily(int family) {
    this.family = family;
  }

  public int getModel() {
    return model;
  }

  public void setModel(int model) {
    this.model = model;
  }

  public int getStepping() {
    return stepping;
  }

  public void setStepping(int stepping) {
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
