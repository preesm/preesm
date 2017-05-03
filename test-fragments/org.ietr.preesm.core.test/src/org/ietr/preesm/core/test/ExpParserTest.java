package org.ietr.preesm.core.test;

import org.ietr.preesm.experiment.model.expression.ExprParser;
import org.junit.Test;
import org.nfunk.jep.JEP;

/**
 */
public class ExpParserTest {

  @Test
  public void testParser() {
    final JEP jep = new JEP();
    jep.setAllowUndeclared(true);
    final String string = "%2";
    final ExprParser exprParser = new ExprParser(string);
    exprParser.startParser();
  }

}
