/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
package org.ietr.dftools.architecture.utils;

import java.util.Comparator;
import org.ietr.dftools.architecture.slam.attributes.VLNV;

/**
 *
 */
public class VLNVComparator implements Comparator<VLNV> {

  public static final boolean areSame(final VLNV v1, final VLNV v2) {
    return new VLNVComparator().compare(v1, v2) == 0;
  }

  public static final int compareVLNV(final VLNV v1, final VLNV v2) {
    return new VLNVComparator().compare(v1, v2);
  }

  private static final int compare(final String str1, final String str2) {
    final boolean is1null = str1 == null;
    final boolean is2null = str2 == null;

    if (!is1null) {
      return str1.compareTo(str2);
    } else if (!is2null) {
      final int compareTo = str2.compareTo(str1);
      return -compareTo;
    } else {
      return 0;
    }
  }

  @Override
  public int compare(final VLNV v1, final VLNV v2) {
    int compare = VLNVComparator.compare(v1.getVendor(), v2.getVendor());
    if (compare == 0) {
      compare = VLNVComparator.compare(v1.getVendor(), v2.getVendor());
      if (compare == 0) {
        compare = VLNVComparator.compare(v1.getLibrary(), v2.getLibrary());
        if (compare == 0) {
          compare = VLNVComparator.compare(v1.getName(), v2.getName());
          if (compare == 0) {
            compare = VLNVComparator.compare(v1.getVersion(), v2.getVersion());
          }
        }
      }
    }
    return compare;
  }
}
