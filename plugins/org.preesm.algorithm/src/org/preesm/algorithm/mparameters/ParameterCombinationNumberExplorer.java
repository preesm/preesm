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
   * 
   * @param config
   *          Configuration to set, as returned per {@link ParameterCombinationExplorer#recordConfiguration}.
   * @return True if the configuration is valid.
   */
  public boolean setForNextIteration(List<Integer> config) {
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
      // actually following test is always true, see mparamsIR initialization in upper class
      if (mpir.nbValues > 1) {
        if (index == 0) {
          mpir.endIndex = mpir.indexHigh - 1;
          mpir.setValues();
        } else if (index == 1) {
          mpir.startIndex = mpir.indexLow + 1;
          mpir.setValues();
        }
        // set the value directly if only one
        if (mpir.startIndex == mpir.endIndex) {
          final Long value = mpir.values.get(mpir.startIndex);
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
