package org.preesm.algorithm.mparameters;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.MalleableParameter;
import org.preesm.model.pisdf.check.MalleableParameterExprChecker;

/**
 * This class stores intermediate results on malleable parameters.
 * 
 * @author ahonorat
 */
class MalleableParameterIR {

  final MalleableParameter mp;
  final List<String>       exprs;
  final List<Long>         values;
  final int                nbValues;
  int                      currentExprIndex;

  MalleableParameterIR(MalleableParameter mp) {
    this.mp = mp;
    exprs = mp.getStrExpressions();
    values = new ArrayList<>(MalleableParameterExprChecker.getUniqueValues(mp.getUserExpression()));
    if (!values.isEmpty()) {
      nbValues = values.size();
    } else {
      nbValues = exprs.size();
    }
    currentExprIndex = 0;
  }

}
