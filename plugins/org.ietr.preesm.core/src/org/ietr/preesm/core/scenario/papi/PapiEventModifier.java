package org.ietr.preesm.core.scenario.papi;

/**
 *
 */
public class PapiEventModifier {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  private String description;

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("        <modifier name=\"%s\" desc=\"%s\">", name, description));
    b.append(String.format(" </modifier>%n"));
    return b.toString();
  }
}
