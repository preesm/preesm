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
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDesciption(String desciption) {
    this.description = desciption;
  }

  public List<PapiEventModifier> getModifiers() {
    return modifiers;
  }

  public void setModifiers(List<PapiEventModifier> modifiers) {
    this.modifiers = modifiers;
  }

  @Override
  public boolean equals(Object comparer) {

    boolean decision = false;
    boolean nameComp = false;
    boolean descriptionComp = false;
    boolean idComp = false;
    boolean modifiersComp = false;

    if (comparer instanceof PapiEvent) {
      PapiEvent tester = (PapiEvent) comparer;
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
    return index;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("      <event index=\"%d\" name=\"%s\" desc=\"%s\">%n", index, name, description));
    for (PapiEventModifier modifier : modifiers) {
      b.append(modifier.toString());
    }
    b.append(String.format("      </event>%n"));
    return b.toString();
  }
}
