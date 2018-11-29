package org.preesm.commons.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 *
 */
public interface PreesmUserFactory {

  static final EcoreUtil.Copier copier = new EcoreUtil.Copier(false);

  /**
   * Copy an existing PiMM node
   */
  public default <T extends EObject> T copy(final T eObject) {
    @SuppressWarnings("unchecked")
    final T copy = (T) copier.copy(eObject);
    return copy;
  }
}
