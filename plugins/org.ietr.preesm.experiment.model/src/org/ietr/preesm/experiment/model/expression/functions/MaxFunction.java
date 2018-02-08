package org.ietr.preesm.experiment.model.expression.functions;

/**
 * Max function (for two double numbers)
 *
 * @author ahonorat
 *
 */
public class MaxFunction extends AbstractPreesmMathFunction {

  @Override
  protected String getName() {
    return "max";
  }

  @Override
  protected int getArgCount() {
    return 2;
  }

  @Override
  protected double compute(double... args) {
    return Math.max(args[0], args[1]);
  }

}
