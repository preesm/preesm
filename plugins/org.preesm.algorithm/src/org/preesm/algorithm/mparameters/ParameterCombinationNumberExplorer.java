/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.mparameters;

import java.util.List;
import java.util.logging.Level;
import org.preesm.commons.exceptions.PreesmRuntimeException;
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
      throw new PreesmRuntimeException("Wrong number of malleable parameters in configuration record.");
    }
    boolean oneNeedsIter = false;
    for (int i = 0; i < size; i++) {
      final MalleableParameterIR mpir = mparamsIR.get(i);
      // if mpir is not a number, we do nothing
      if (mpir instanceof MalleableParameterNumberIR) {
        final MalleableParameterNumberIR mpnir = (MalleableParameterNumberIR) mpir;
        final int index = config.get(i);
        if (index < 0 || index >= mpnir.nbValues) {
          throw new PreesmRuntimeException("Index out of bounds in malleable parameter configuration record.");
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
