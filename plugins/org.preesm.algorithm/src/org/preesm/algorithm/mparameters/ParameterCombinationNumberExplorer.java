package org.preesm.algorithm.mparameters;

import java.util.List;
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
    boolean isNonNumber = mparamsIR.stream().anyMatch(x -> !(x instanceof MalleableParameterNumberIR));
    if (isNonNumber) {
      throw new PreesmRuntimeException(
          "It is not possible to explore non number malleable parameters with this class.");
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
      final MalleableParameterNumberIR mpir = (MalleableParameterNumberIR) mparamsIR.get(i);
      final int index = config.get(i);
      if (index < 0 || index >= mpir.nbValues) {
        return false;
      }
      // actually, following test is always true, see mparamsIR initialization in upper class
      if (mpir.nbValues > 1) {
        if (index == 0) {
          // we take lower values
          mpir.endIndex = mpir.indexHigh - 1;
          mpir.setValues();
        } else if (index == 1) {
          // we take higher values
          mpir.startIndex = mpir.indexLow + 1;
          mpir.setValues();
        }
        // set the value directly if only one
        if (mpir.startIndex == mpir.endIndex) {
          final Long value = mpir.oriValues.get(mpir.startIndex);
          mpir.mp.setExpression(value);
          scenario.getParameterValues().put(mparamTOscenarParam.get(mpir.mp), value.toString());
        } else {
          // otherwise we need another iteration
          oneNeedsIter = true;
        }
      }
    }
    return oneNeedsIter;
  }

}
