/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011)
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
package org.ietr.dftools.architecture.slam.serialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A complex refinement made of a list of files. Used to serialize multiple refinements.
 *
 * @author mpelcat
 */
public class RefinementList {

  /** Storing relative paths to the refinements. */
  List<String> nameList = new ArrayList<>();

  /**
   * Instantiates a new refinement list.
   */
  public RefinementList() {
  }

  /**
   * Instantiates a new refinement list.
   *
   * @param stringList
   *          the string list
   */
  public RefinementList(final String stringList) {
    fromString(stringList);
  }

  /**
   * Adds the name.
   *
   * @param name
   *          the name
   */
  public void addName(final String name) {
    this.nameList.add(name);
  }

  /**
   * Removes the name.
   *
   * @param name
   *          the name
   */
  public void removeName(final String name) {
    final Iterator<String> iterator = this.nameList.listIterator();
    while (iterator.hasNext()) {
      final String next = iterator.next();
      if (next.equals(name)) {
        iterator.remove();
      }
    }
  }

  /**
   * Parsing the refinement paths from a comma-separated list of strings.
   *
   * @param stringList
   *          the string list
   */
  public void fromString(final String stringList) {
    this.nameList.addAll(Arrays.asList(stringList.split(",")));
  }

  /**
   * Size.
   *
   * @return the int
   */
  public int size() {
    return this.nameList.size();
  }

  /**
   * To string array.
   *
   * @return the string[]
   */
  public String[] toStringArray() {
    return this.nameList.toArray(new String[0]);
  }

  /**
   * Exporting the refinement paths as a comma-separated list of strings.
   *
   * @return the string
   */
  @Override
  public String toString() {
    final StringBuilder stringList = new StringBuilder();
    for (final String fileName : this.nameList) {
      stringList.append(fileName + ",");
    }
    if (stringList.length() != 0) {
      stringList.deleteCharAt(stringList.length() - 1);
    }
    return stringList.toString();
  }
}
