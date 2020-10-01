/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2020)
 * Julien Heulot [julien.heulot@insa-rennes.fr] (2020)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.model.pisdf.Parameter;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;

/**
 * This class is dedicated to explore combination of MalleableParameterIR.
 * 
 * @author ahonorat
 */
public class ParameterCombinationExplorer {

  protected List<MalleableParameterIR> mparamsIR;           // mparamsIR having more than 1 possible value
  protected Scenario                   scenario;
  protected Map<Parameter, Parameter>  mparamTOscenarParam; // map from mparam to corresponding scenario parameter

  /**
   * Builds a new explorer.
   * 
   * @param mparamsIR
   *          MalleableParameterIR.
   * @param scenario
   *          Scenario containing the parameters.
   */
  public ParameterCombinationExplorer(List<MalleableParameterIR> mparamsIR, Scenario scenario) {
    this.scenario = scenario;
    mparamTOscenarParam = new HashMap<>();
    final PiGraph algoScenar = scenario.getAlgorithm();
    for (MalleableParameterIR mpir : mparamsIR) {
      String name = mpir.mp.getName();
      String parentName = mpir.mp.getContainingPiGraph().getName();
      Parameter ps = algoScenar.lookupParameterGivenGraph(name, parentName);
      if (ps == null) {
        throw new PreesmRuntimeException("Unable to find malleable parameter in scenario.");
      }
      mparamTOscenarParam.put(mpir.mp, ps);
    }
    this.mparamsIR = mparamsIR.stream().filter(x -> x.nbValues > 1).collect(Collectors.toList());
    resetUntilIndex(this.mparamsIR.size());
  }

  protected void resetUntilIndex(int index) {
    for (int i = 0; i < index; i++) {
      resetIndex(i);
    }
    // the first one is not visited yet
    // all others are visited at the same time, so they start at one
    mparamsIR.get(0).currentExprIndex = 0;
  }

  protected void resetIndex(int index) {
    MalleableParameterIR mpir = mparamsIR.get(index);
    mpir.currentExprIndex = 1;
    if (mpir.values.isEmpty()) {
      final String expr = mpir.exprs.get(0);
      mpir.mp.setExpression(expr);
      scenario.getParameterValues().put(mparamTOscenarParam.get(mpir.mp), expr);
    } else {
      final Long value = mpir.values.get(0);
      mpir.mp.setExpression(value);
      scenario.getParameterValues().put(mparamTOscenarParam.get(mpir.mp), value.toString());
    }
  }

  /**
   * Set the next combination to visit. If false, all combinations have been visited and a new object must be create.
   * 
   * @return True if some combinations have not yet been visited.
   */
  public boolean setNext() {
    return setNext(0);
  }

  protected boolean setNext(int index) {
    if (mparamsIR.isEmpty()) {
      return false;
    }
    MalleableParameterIR lmpir = mparamsIR.get(index);
    if (lmpir.nbValues == lmpir.currentExprIndex) {
      resetIndex(index);
      if (mparamsIR.size() - 1 == index) {
        return false;
      } else {
        return setNext(index + 1);
      }
    } else {
      if (lmpir.values.isEmpty()) {
        final String expr = lmpir.exprs.get(lmpir.currentExprIndex);
        lmpir.mp.setExpression(expr);
        scenario.getParameterValues().put(mparamTOscenarParam.get(lmpir.mp), expr);
      } else {
        final Long value = lmpir.values.get(lmpir.currentExprIndex);
        lmpir.mp.setExpression(value);
        scenario.getParameterValues().put(mparamTOscenarParam.get(lmpir.mp), value.toString());
      }
      lmpir.currentExprIndex += 1;
    }
    return true;
  }

  /**
   * Records the index of the current expression set for each malleable parameter having several possible values. To be
   * used only with the original {@link ParameterCombinationExplorer} that created this configuration.
   * 
   * @return A list of index (each from 0 to mparamIR.nbValues - 1 included) to later set back this configuration.
   */
  protected List<Integer> recordConfiguration() {
    return mparamsIR.stream().map(x -> x.currentExprIndex - 1).collect(Collectors.toList());
  }

  /**
   * Set the default expression of each malleable parameters as recorded in the given configuration.
   * 
   * @param config
   *          Configuration to set, as returned per {@link ParameterCombinationExplorer#recordConfiguration}.
   * @return True if the configuration is valid.
   */
  protected boolean setConfiguration(List<Integer> config) {
    final int size = mparamsIR.size();
    if (config.size() != size) {
      throw new PreesmRuntimeException("Wrong number of malleable parameters in configuration record.");
    }
    for (int i = 0; i < size; i++) {
      final MalleableParameterIR mpir = mparamsIR.get(i);
      final int index = config.get(i);
      if (index < 0 || index >= mpir.nbValues) {
        return false;
      }
      mpir.currentExprIndex = index;
      if (mpir.values.isEmpty()) {
        final String expr = mpir.exprs.get(mpir.currentExprIndex);
        mpir.mp.setExpression(expr);
        scenario.getParameterValues().put(mparamTOscenarParam.get(mpir.mp), expr);
      } else {
        final Long value = mpir.values.get(mpir.currentExprIndex);
        mpir.mp.setExpression(value);
        scenario.getParameterValues().put(mparamTOscenarParam.get(mpir.mp), value.toString());
      }
    }
    return true;
  }

}
