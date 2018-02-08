package org.ietr.preesm.experiment.model.expression.functions;

/**
 * Min function (for two double numbers)
 *
 * @author ahonorat
 */
public class MinFunction extends AbstractPreesmMathFunction {

  @Override
  protected String getName() {
    return "min";
  }

  @Override
  protected int getArgCount() {
    return 2;
  }

  @Override
  protected double compute(double... args) {
    return Math.min(args[0], args[1]);
  }

}
