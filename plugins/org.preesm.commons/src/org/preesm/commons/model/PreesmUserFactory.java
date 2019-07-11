/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2018 - 2019)
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
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 *
 */
public interface PreesmUserFactory {

  /**
   * Copy an existing Preesm object.
   */
  public default <T extends EObject> T copy(final T eObject) {
    final EcoreUtil.Copier copier = new EcoreUtil.Copier(false);
    copier.copyReferences();
    @SuppressWarnings("unchecked")
    final T copy = (T) copier.copy(eObject);
    return copy;
  }

  /**
   * Copy an existing Preesm object. The original version of the object can be accessed using
   * {@link PreesmCopyTracker#getSource(Notifier)} and {@link PreesmCopyTracker#getOriginalSource(Notifier)}.
   */
  public default <T extends EObject> T copyWithHistory(final T eObject) {
    final T copy = copy(eObject);
    PreesmCopyTracker.trackCopy(eObject, copy);
    return copy;
  }
}
