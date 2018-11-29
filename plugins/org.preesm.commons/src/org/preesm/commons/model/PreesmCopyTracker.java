package org.preesm.commons.model;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.preesm.commons.exceptions.PreesmException;

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
      throw new PreesmException();
    }
    final PreesmCopyTracker<V> e = new PreesmCopyTracker<>(source);
    copy.eAdapters().add(e);
  }
}
