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

  final MalleableParameter mp;               // original malleable parameter
  final List<String>       exprs;            // expressions stored in mp
  List<Long>               values;           // evaluated expressions, if only numbers
  int                      nbValues;         // size of the number of exprs/values (redundant)
  int                      currentExprIndex; // index from 0 to nbValues (included)

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
