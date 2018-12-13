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
package org.preesm.algorithm.moa.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information on the activity of a system (number of tokens and quanta per architecture element). This information can
 * be printed in a human readable table or in a matrix (with 0s when no token appears) where PEs in increasing name
 * order are followed by CNs in increasing name order.
 *
 * @author mpelcat
 *
 */
public class Activity {

  /*
   * Information on number of tokens per architecture element (identified by name)
   */
  private Map<String, Long> tokens = null;

  /*
   * Information on number of quanta per architecture element (identified by name)
   */
  private Map<String, Long> quanta = null;

  public Activity() {
    this.tokens = new HashMap<>();
    this.quanta = new HashMap<>();
  }

  /**
   *
   */
  public void addTokenNumber(final String archiEltName, final long tokenNr) {
    if (this.tokens.keySet().contains(archiEltName)) {
      this.tokens.put(archiEltName, this.tokens.get(archiEltName) + tokenNr);
    } else {
      this.tokens.put(archiEltName, tokenNr);
    }
  }

  /**
   *
   */
  public void addQuantaNumber(final String archiEltName, final long quantaNr) {
    if (this.quanta.keySet().contains(archiEltName)) {
      this.quanta.put(archiEltName, this.quanta.get(archiEltName) + quantaNr);
    } else {
      this.quanta.put(archiEltName, quantaNr);
    }
  }

  @Override
  public String toString() {
    return "tokens " + this.tokens + "\n" + " quanta " + this.quanta;
  }

  public void clear() {
    this.tokens.clear();
    this.quanta.clear();
  }

  /**
   * Generating a string in CSV format from token information If human_readable=true, the names of the architecture
   * components are displayed.
   */
  public String tokensString(final boolean human_readable) {
    String str = "";

    final List<String> sortedNames = new ArrayList<>(this.tokens.keySet());
    Collections.sort(sortedNames);

    if (human_readable) {
      for (final String name : sortedNames) {
        str += name + ",";
      }
      str = str.substring(0, str.length() - 1);
      str = str + "\n";
    }

    for (final String name : sortedNames) {
      str += this.tokens.get(name) + ",";
    }
    str = str.substring(0, str.length() - 1);
    str = str + "\n";

    return str;
  }

  /**
   * Generating a string in CSV format from quanta information If human_readable=true, the names of the architecture
   * components are displayed.
   */
  public String quantaString(final boolean human_readable) {
    String str = "";
    final List<String> sortedNames = new ArrayList<>(this.quanta.keySet());
    Collections.sort(sortedNames);

    if (human_readable) {
      for (final String name : sortedNames) {
        str += name + ",";
      }
      str = str.substring(0, str.length() - 1);
      str = str + "\n";
    }

    for (final String name : sortedNames) {
      str += this.quanta.get(name) + ",";
    }
    str = str.substring(0, str.length() - 1);
    str = str + "\n";

    return str;
  }
}
