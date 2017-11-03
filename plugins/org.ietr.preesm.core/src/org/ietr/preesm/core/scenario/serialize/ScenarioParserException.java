package org.ietr.preesm.core.scenario.serialize;

/**
 *
 * @author anmorvan
 *
 */
public class ScenarioParserException extends RuntimeException {

  public ScenarioParserException(final String string, final Exception e) {
    super(string, e);
  }

  /**
   *
   */
  private static final long serialVersionUID = 1L;

}
