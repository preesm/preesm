/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2018)
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

import java.util.HashMap;
import java.util.Map;

/**
 * Storing the number of custom quanta per actor. Information is stored as strings containing JEP compatible formula.
 *
 * @author mpelcat
 *
 */
public class CustomQuanta {

  /**
   * Key for storing the number of custom quanta in a 2D map
   */
  private static class MapKeyPair {
    private final String actor;
    private final String operator;

    private MapKeyPair(final String actor, final String operator) {
      this.actor = actor;
      this.operator = operator;
    }

    @Override
    public boolean equals(final Object o) {
      if (o instanceof MapKeyPair) {
        final MapKeyPair m = (MapKeyPair) o;
        return m.actor.equals(this.actor) && m.operator.equals(this.operator);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.actor.hashCode();
    }

    @Override
    public String toString() {
      return "(" + this.actor + "," + this.operator + ")";
    }
  }

  private Map<MapKeyPair, String> customQuanta = null;

  public CustomQuanta() {
    this.customQuanta = new HashMap<>();
  }

  public void addQuantaExpression(final String actor, final String operator, final String expression) {
    final MapKeyPair mkp = new MapKeyPair(actor, operator);
    this.customQuanta.put(mkp, expression);
  }

  /**
   *
   */
  public String getQuanta(final String actor, final String operator) {
    final MapKeyPair mkp = new MapKeyPair(actor, operator);
    if (this.customQuanta.containsKey(mkp)) {
      return this.customQuanta.get(mkp);
    }
    return "";
  }

  @Override
  public String toString() {
    return this.customQuanta.toString();
  }

}
