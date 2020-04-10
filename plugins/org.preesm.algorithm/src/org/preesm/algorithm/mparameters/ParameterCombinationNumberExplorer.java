package org.preesm.algorithm.mparameters;

import java.util.List;
import java.util.logging.Level;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.Scenario;

/**
 * Provides special features to fasten DSE when all parameters are numbers only.
 * 
 * @author ahonorat
 */
public class ParameterCombinationNumberExplorer extends ParameterCombinationExplorer {

  /**
   * Builds a new explorer.
   * 
   * @param mparamsIR
   *          MalleableParameterIR.
   * @param scenario
   *          Scenario containing the parameters.
   */
  public ParameterCombinationNumberExplorer(List<MalleableParameterIR> mparamsIR, Scenario scenario) {
    super(mparamsIR, scenario);
    boolean isNumber = mparamsIR.stream().anyMatch(x -> (x instanceof MalleableParameterNumberIR));
    if (!isNumber) {
      PreesmLogger.getLogger().log(Level.WARNING,
          "Heuristic has been asked for Malleable Parameters being only numbers, however there are none.");
    }
  }

  /**
   * Sets the parameter values to test for a next round of DSE with reduced number of possible values. It necessitates
   * to reinstantiate this class with the updated parameters before starting the next round.
   * 
   * @param config
   *          Configuration to set, as returned per {@link ParameterCombinationExplorer#recordConfiguration}.
   * @return True if another round is needed, false otherwise (including if config is not valid).
   */
  public boolean setForNextPartialDSEround(List<Integer> config) {
    final int size = mparamsIR.size();
    if (config.size() != size) {
      return false;
    }
    boolean oneNeedsIter = false;
    for (int i = 0; i < size; i++) {
      final MalleableParameterIR mpir = mparamsIR.get(i);
      // if mpir is not a number, we do nothing
      if (mpir instanceof MalleableParameterNumberIR) {
        final MalleableParameterNumberIR mpnir = (MalleableParameterNumberIR) mpir;
        final int index = config.get(i);
        if (index < 0 || index >= mpnir.nbValues) {
          return false;
        }
        // actually, following test is always true, see mparamsIR initialization in upper class
        if (mpnir.nbValues > 1) {
          if (index == 0) {
            // we take lower values
            mpnir.endIndex = mpnir.indexHigh - 1;
            mpnir.setValues();
          } else if (index == 1) {
            // we take higher values
            mpnir.startIndex = mpnir.indexLow + 1;
            mpnir.setValues();
          }
          // set the value directly if only one
          if (mpnir.startIndex == mpnir.endIndex) {
            final Long value = mpnir.oriValues.get(mpnir.startIndex);
            mpnir.mp.setExpression(value);
            scenario.getParameterValues().put(mparamTOscenarParam.get(mpnir.mp), value.toString());
          } else {
            // otherwise we need another iteration
            oneNeedsIter = true;
          }
        }
      }
    }
    return oneNeedsIter;
  }

}
