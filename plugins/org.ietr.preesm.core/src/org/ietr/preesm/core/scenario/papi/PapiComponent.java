package org.ietr.preesm.core.scenario.papi;

/**
 *
 * @author anmorvan
 *
 */
public class PapiComponent {

  private final String            id;
  private final PapiComponentType type;
  private final int               index;

  /**
   *
   */
  public PapiComponent(final String componentID, final String componentIndex, final String componentType) {
    this.index = Integer.valueOf(componentIndex);
    this.id = componentID;
    this.type = PapiComponentType.parse(componentType);
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder();
    final String string = this.type.toString();
    b.append(String.format("  <component id=\"%s\" index=\"%d\" type=\"%s\">%n", this.id, this.index, string));
    b.append("todo\n");
    String.format("  </component>%n");
    return b.toString();
  }
}
