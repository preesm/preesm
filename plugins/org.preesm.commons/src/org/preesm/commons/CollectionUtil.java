/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2019) :
 *
 * Alexandre Honorat [alexandre.honorat@insa-rennes.fr] (2019)
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2019)
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
package org.preesm.commons;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author anmorvan
 *
 */
public class CollectionUtil {

  private CollectionUtil() {
    // forbid instantiation
  }

  /**
   * Retrieves all the map values of the selected keys in a list. If selectedKeys is ordered, the resulting list will be
   * ordered accordingly. If a key is absent, the result will omit the value.
   */
  public static final <V, T> List<V> mapGetAll(final Map<T, V> map, final Collection<? extends T> selectedKeys) {
    final List<V> result = new LinkedList<>();
    for (final T key : selectedKeys) {
      if (map.containsKey(key)) {
        result.add(map.get(key));
      }
    }
    return result;
  }

  /**
   * Insert newElements after listElement, in order; takes care of end of list
   */
  @SafeVarargs
  public static final <T> void insertAfter(final List<T> list, final T listElement, final T... newElements) {
    final int length = newElements.length;
    if (length > 0) {
      final int indexOf = list.indexOf(listElement);
      for (int i = 0; i < length; i++) {
        list.add(indexOf + i + 1, newElements[i]);
      }
    }
  }

  /**
   * Insert newElements before listElement, in order; takes care of end of list
   */
  @SafeVarargs
  public static final <T> void insertBefore(final List<T> list, final T listElement, final T... newElements) {
    final int length = newElements.length;
    if (length > 0) {
      final int indexOf = list.indexOf(listElement);
      for (int i = newElements.length - 1; i >= 0; i--) {
        list.add(indexOf, newElements[i]);
      }
    }
  }

  /**
   * Insert 2 newElements after listElement, in order; takes care of end of list
   */
  public static final <T> void insertAfter2(final List<T> list, final T listElement, final T newElement1,
      final T newElement2) {
    int indexOf = list.indexOf(listElement);
    list.add(++indexOf, newElement1);
    list.add(++indexOf, newElement2);
  }

  /**
   * Insert newElements before listElement, in order; takes care of end of list
   */
  public static final <T> void insertBefore2(final List<T> list, final T listElement, final T newElement1,
      final T newElement2) {
    int indexOf = list.indexOf(listElement);
    if (indexOf < 0) {
      indexOf = 0;
    }
    list.add(indexOf, newElement2);
    list.add(indexOf, newElement1);
  }

}
