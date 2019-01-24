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
package org.preesm.commons.model;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.preesm.commons.exceptions.PreesmRuntimeException;

/**
 *
 * <p>
 * Preesm Adapter for keeping track of object copies, created using {@link PreesmUserFactory#copyWithHistory(EObject)}.
 * </p>
 *
 * <p>
 * This functionality is useful when flattening the hierarchy of a PiSDF or Slam graph: actors/components are duplicated
 * and a new ID is given. In order to find their properties in the scenario, we need a way to track the original object,
 * source of the copy, that holds the proper ID.
 * </p>
 *
 * @param <T>
 *          The object of the copy.
 */
public class PreesmCopyTracker<T extends Notifier> extends PreesmAdapter {

  private final T sourceObject;

  private PreesmCopyTracker(final T originalObject) {
    super();
    this.sourceObject = originalObject;
  }

  private T getSourceObject() {
    return sourceObject;
  }

  /**
   * Find the adapter that hold the source of copy, then return its source, or null if it does not exist.
   */
  public static final <V extends Notifier> V getSource(final V copy) {
    @SuppressWarnings("unchecked")
    final PreesmCopyTracker<V> adapt = PreesmAdapter.adapt(copy, PreesmCopyTracker.class);
    if (adapt != null) {
      return adapt.getSourceObject();
    }
    return null;
  }

  /**
   * Lookup the original copy in case of chained copy. Return copy if it has no PreesmCopyTracker adapter.
   */
  public static final <V extends Notifier> V getOriginalSource(final V copy) {
    final V source = getSource(copy);
    if (source == null) {
      return copy;
    } else {
      return getOriginalSource(source);
    }
  }

  /**
   *
   */
  public static final <V extends Notifier> void trackCopy(final V source, final V copy) {
    final V existingSource = getSource(copy);
    if (existingSource != null && existingSource != source) {
      throw new PreesmRuntimeException();
    }
    final PreesmCopyTracker<V> e = new PreesmCopyTracker<>(source);
    copy.eAdapters().add(e);
  }
}
