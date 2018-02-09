package org.ietr.preesm.core.scenario.papi;

/**
 *
 * @author anmorvan
 *
 */
public enum PapiComponentType {
  UNKNOWN("Unkown"), CPU("CPU");

  private final String name;

  private PapiComponentType(final String typeName) {
    this.name = typeName;
  }

  public boolean equalsName(final String otherName) {
    // (otherName == null) check is not needed because name.equals(null) returns false
    return this.name.equals(otherName);
  }

  @Override
  public String toString() {
    return this.name;
  }

  /**
   *
   */
  public static PapiComponentType parse(final String componentType) {
    final String upperCase = componentType.toUpperCase();
    switch (upperCase) {
      case "CPU":
        return CPU;
      case "UNKNOWN":
        return UNKNOWN;
      default:
        return null;
    }
  }
}
