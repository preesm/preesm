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
  private String                  desciption;
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

  public String getDesciption() {
    return desciption;
  }

  public void setDesciption(String desciption) {
    this.desciption = desciption;
  }

  public List<PapiEventModifier> getModifiers() {
    return modifiers;
  }

  public void setModifiers(List<PapiEventModifier> modifiers) {
    this.modifiers = modifiers;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    b.append(String.format("      <event index=\"%d\" name=\"%s\" desc=\"%s\">%n", index, name, desciption));
    for (PapiEventModifier modifier : modifiers) {
      b.append(modifier.toString());
    }
    b.append(String.format("      </event>%n"));
    return b.toString();
  }
}
