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
package org.ietr.preesm.mapper.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * This iterator iterates any list of objects with type E.
 *
 * @author pmenuet
 * @param <E> the element type
 */

/**
 * Provides a random iteration over the given list of unknown object.
 *
 * @param <E>
 *          The generic type to iterate over.
 */
public class RandomIterator<E> implements Iterator<E> {

  // variables

  /** The index. */
  // the index of the chosen element
  private int index;

  /** The list. */
  // the list which is iterated
  private List<E> list = null;

  /** The list size. */
  // size of the list where the iterator is set
  private final int listSize;

  /** The rand. */
  // random
  private final Random rand;

  /**
   * RandomIterator constructor.
   *
   * @param list
   *          the list
   * @param rand
   *          the rand
   */
  public RandomIterator(final List<E> list, final Random rand) {

    this.list = list;
    this.listSize = list.size();
    final long seed = System.nanoTime();
    rand.setSeed(seed);
    this.rand = rand;
  }

  /**
   * RandomIterator constructor.
   *
   * @param set
   *          the set
   * @param rand
   *          the rand
   */
  public RandomIterator(final Set<E> set, final Random rand) {

    this.list = new ArrayList<>(set);
    this.listSize = set.size();
    final long seed = System.nanoTime();
    rand.setSeed(seed);
    this.rand = rand;
  }

  /**
   * Impossible to know because there is a next always.
   *
   * @return true, if successful
   */
  @Override
  public boolean hasNext() {
    throw new UnsupportedOperationException();
  }

  /**
   * determine the next random index and return the designated object.
   *
   * @return the e
   */
  @Override
  public E next() {

    this.index = this.rand.nextInt(this.listSize);

    return this.list.get(this.index);
  }

  /**
   * Useless in this case.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
