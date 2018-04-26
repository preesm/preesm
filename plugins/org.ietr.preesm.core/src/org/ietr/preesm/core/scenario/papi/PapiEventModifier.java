package org.ietr.preesm.core.scenario.papi;

/**
 *
 */
public class PapiEventModifier {

  private String name;

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  private String description;

  @Override
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean nameComp = false;
    boolean descriptionComp = false;

    if (comparer instanceof PapiEventModifier) {
      final PapiEventModifier tester = (PapiEventModifier) comparer;
      if (this.description.equals(tester.getDescription())) {
        descriptionComp = true;
      }
      if (this.name.equals(tester.getName())) {
        nameComp = true;
      }
      if (nameComp && descriptionComp) {
        decision = true;
      }
    }

    return decision;
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("        <modifier name=\"%s\" desc=\"%s\">", this.name, this.description));
    b.append(String.format(" </modifier>%n"));
    return b.toString();
  }
}
