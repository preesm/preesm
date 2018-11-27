package org.preesm.algorithm.pisdf.helper;

/**
 *
 */
public enum BRVMethod {

  LCM("LCM"),

  TOPOLOGY("Topology");

  private final String literal;

  private BRVMethod(final String methodName) {
    this.literal = methodName;
  }

  public String getLiteral() {
    return literal;
  }

  /**
   */
  public static BRVMethod getByName(final String name) {
    for (BRVMethod m : BRVMethod.values()) {
      if (m.getLiteral().equals(name)) {
        return m;
      }
    }
    return null;
  }
}
