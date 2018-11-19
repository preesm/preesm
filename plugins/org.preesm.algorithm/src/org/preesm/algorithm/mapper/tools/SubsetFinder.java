/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.preesm.algorithm.mapper.tools;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * Given a set of T, gives a subset verifying the subsetCondition.
 *
 * @author mpelcat
 * @param <T>
 *          the generic type
 * @param <U>
 *          the generic type
 */
public abstract class SubsetFinder<T, U> {

  /** The compared value. */
  private U comparedValue;

  /** The inputset. */
  private Set<T> inputset;

  /**
   * Instantiates a new subset finder.
   */
  public SubsetFinder() {
    super();
  }

  /**
   * Instantiates a new subset finder.
   *
   * @param inputset
   *          the inputset
   * @param comparedValue
   *          the compared value
   */
  public SubsetFinder(final Set<T> inputset, final U comparedValue) {
    this.inputset = inputset;
    this.comparedValue = comparedValue;
  }

  /**
   * Subset.
   *
   * @return the sets the
   */
  public Set<T> subset() {
    final Set<T> subset = new LinkedHashSet<>();

    final Iterator<T> iterator = this.inputset.iterator();

    while (iterator.hasNext()) {
      final T next = iterator.next();

      if (subsetCondition(next, this.comparedValue)) {
        subset.add(next);
      }
    }
    return subset;
  }

  /**
   * Subset condition.
   *
   * @param tested
   *          the tested
   * @param comparedValue
   *          the compared value
   * @return true, if successful
   */
  protected abstract boolean subsetCondition(T tested, U comparedValue);
}
