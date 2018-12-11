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
