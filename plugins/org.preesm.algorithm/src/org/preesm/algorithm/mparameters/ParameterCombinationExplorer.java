package org.preesm.algorithm.mparameters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is dedicated to explore combination of MalleableParameterIR.
 * 
 * @author ahonorat
 */
public class ParameterCombinationExplorer {

  protected List<MalleableParameterIR> mparamsIR;

  public ParameterCombinationExplorer(List<MalleableParameterIR> mparamsIR) {
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
      mpir.mp.setExpression(mpir.exprs.get(0));
    } else {
      mpir.mp.setExpression(mpir.values.get(0));
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
        lmpir.mp.setExpression(lmpir.exprs.get(lmpir.currentExprIndex));
      } else {
        lmpir.mp.setExpression(lmpir.values.get(lmpir.currentExprIndex));
      }
      lmpir.currentExprIndex += 1;
    }
    return true;
  }

}
