/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2020 - 2024) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2020)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
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

import java.util.ArrayList;
import java.util.List;
import org.preesm.model.pisdf.MoldableParameter;

/**
 * This class stores intermediate results on moldable parameters being numbers only.
 *
 * @author ahonorat
 */
public class MoldableParameterNumberIR extends MoldableParameterIR {

  final List<Long> oriValues;
  int              startIndex; // including itself, < oriValues.size(), >= 0
  int              endIndex;   // including itself, < oriValues.size(), >= 0
  int              indexLow;   // >= startIndex, <= indexHigh (equal only if one value
  int              indexHigh;  // <= endIndex, >= indexLow (equal only if one value)

  MoldableParameterNumberIR(MoldableParameter mp) {
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
