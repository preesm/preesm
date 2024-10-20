/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2021) :
 *
 * Alexandre Honorat [alexandre.honorat@inria.fr] (2021)
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

package org.preesm.model.slam.utils;

import java.util.Comparator;

/**
 * Compares name of component instance (compares digits only if text is different).
 */
public class ComponentInstanceNameComparator implements Comparator<String> {

  @Override
  public int compare(String o1, String o2) {
    return compareStatic(o1, o2);
  }

  public static int compareStatic(String o1, String o2) {
    final String o1StringPart = o1.replaceAll("\\d", "");
    final String o2StringPart = o2.replaceAll("\\d", "");

    if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
      return Long.compare(extractInt(o1), extractInt(o2));
    } else {
      return o1.compareTo(o2);
    }
  }

  static long extractInt(String s) {
    String num = s.replaceAll("\\D", "");
    // return 0 if no digits found
    return num.isEmpty() ? 0 : Long.parseLong(num);
  }

}
