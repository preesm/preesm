package org.preesm.commons.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Adapter implementation for Preesm. Wraps EMF adapter implementation and adds Generic return types. Also properly
 * implements {@link #isAdapterForType(Object)}.
 *
 * @author anmorvan
 *
 */
public abstract class PreesmAdapter extends EContentAdapter {

  public static final <T extends PreesmAdapter> T adapt(final Notifier notifier, final Class<T> target) {
    return adapt(notifier.eAdapters(), target);
  }

  /**
   *
   */
  public static final <T extends PreesmAdapter> T adapt(final List<? extends Adapter> adapters, final Class<T> target) {
    final List<Adapter> arrayList = new ArrayList<>(adapters);
    final Adapter adapter = EcoreUtil.getAdapter(arrayList, target);
    @SuppressWarnings("unchecked")
    final T result = (T) adapter;
    return result;
  }

  @Override
  public boolean isAdapterForType(Object type) {
    if (type instanceof Class) {
      return isAdapterForType((Class<?>) type);
    } else {
      return super.isAdapterForType(type);
    }
  }

  private boolean isAdapterForType(Class<?> type) {
    return this.getClass() == type;
  }
}
