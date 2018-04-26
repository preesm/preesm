package org.ietr.preesm.core.scenario.papi;

import java.util.List;

/**
 *
 * @author anmorvan
 *
 */
public class PapiEvent {

  private int                     index;
  private String                  name;
  private String                  description;
  private List<PapiEventModifier> modifiers;

  public int getIndex() {
    return this.index;
  }

  public void setIndex(final int index) {
    this.index = index;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDesciption(final String desciption) {
    this.description = desciption;
  }

  public List<PapiEventModifier> getModifiers() {
    return this.modifiers;
  }

  public void setModifiers(final List<PapiEventModifier> modifiers) {
    this.modifiers = modifiers;
  }

  @Override
  public boolean equals(final Object comparer) {

    boolean decision = false;
    boolean nameComp = false;
    boolean descriptionComp = false;
    boolean idComp = false;
    boolean modifiersComp = false;

    if (comparer instanceof PapiEvent) {
      final PapiEvent tester = (PapiEvent) comparer;
      if (this.description.equals(tester.getDescription())) {
        descriptionComp = true;
      }
      if (this.name.equals(tester.getName())) {
        nameComp = true;
      }
      if (this.description.equals(tester.getDescription())) {
        descriptionComp = true;
      }
      if (this.index == tester.getIndex()) {
        idComp = true;
      }
      if (this.modifiers.equals(tester.getModifiers())) {
        modifiersComp = true;
      }
      if (nameComp && descriptionComp && idComp && modifiersComp) {
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
    b.append(String.format("      <event index=\"%d\" name=\"%s\" desc=\"%s\">%n", this.index, this.name, this.description));
    for (final PapiEventModifier modifier : this.modifiers) {
      b.append(modifier.toString());
    }
    b.append(String.format("      </event>%n"));
    return b.toString();
  }
}
