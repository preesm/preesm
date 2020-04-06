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

  protected List<MalleableParameterIR> mparamsIR;
  protected Scenario                   scenario;
  protected Map<Parameter, Parameter>  mparamTOscenarParam;

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
   * @return True is some combinations have not yet been visited.
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
      if (mparamsIR.size() - 1 == index) {
        return false;
      } else {
        resetIndex(index);
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
   * 
   * @return A list of index (from 0 to nbValues) to later set back this configuration.
   */
  protected List<Integer> recordConfiguration() {
    return mparamsIR.stream().map(x -> x.currentExprIndex - 1).collect(Collectors.toList());
  }

  /**
   * 
   * @param config
   *          Configuration to set, as returned per {@link ParameterCombinationExplorer#recordConfiguration}.
   * @return True if the configuration is valid.
   */
  protected boolean setConfiguration(List<Integer> config) {
    final int size = mparamsIR.size();
    if (config.size() != size) {
      return false;
    }
    for (int i = 0; i < size; i++) {
      final MalleableParameterIR mpir = mparamsIR.get(i);
      final int index = config.get(i);
      if (index < 0 || index >= mpir.nbValues) {
        return false;
      }
      if (index == mpir.currentExprIndex) {
        continue;
      } else {
        mpir.currentExprIndex = index;
      }
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
