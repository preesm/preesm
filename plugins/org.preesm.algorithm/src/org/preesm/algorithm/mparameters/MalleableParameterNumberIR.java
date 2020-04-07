package org.preesm.algorithm.mparameters;

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.MalleableParameter;

/**
 * This class stores intermediate results on malleable parameters being numbers only.
 * 
 * @author ahonorat
 */
public class MalleableParameterNumberIR extends MalleableParameterIR {

  final List<Long> oriValues;
  int              startIndex; // including itself, < oriValues.size(), >= 0
  int              endIndex;   // including itself, < oriValues.size(), >= 0
  int              indexLow;   // >= startIndex, <= indexHigh (equal only if one value
  int              indexHigh;  // <= endIndex, >= indexLow (equal only if one value)

  MalleableParameterNumberIR(MalleableParameter mp) {
    super(mp);
    oriValues = values;
    startIndex = 0;
    endIndex = oriValues.size() - 1;
    setValues();
  }

  /**
   * Initialize the two values to test in a round.
   */
  void setValues() {
    if (endIndex == startIndex) {
      values = new ArrayList<>();
      values.add(0, oriValues.get(startIndex));
      nbValues = 1;
    } else if (endIndex > startIndex) {
      // sort of "trichotomy"
      final int oneThirdIndex = (endIndex - startIndex) / 3;
      indexLow = startIndex + oneThirdIndex;
      indexHigh = endIndex - oneThirdIndex;
      values = new ArrayList<>();
      values.add(0, oriValues.get(indexLow));
      values.add(1, oriValues.get(indexHigh));
      nbValues = 2;
    }
  }

}
