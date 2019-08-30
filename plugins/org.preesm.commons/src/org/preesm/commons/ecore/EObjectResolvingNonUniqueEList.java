package org.preesm.commons.ecore;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 *
 * @author anmorvan
 *
 * @param <T>
 *          Type of list objects
 */
public class EObjectResolvingNonUniqueEList<T> extends EObjectResolvingEList<T> {

  private static final long serialVersionUID = 1L;

  public EObjectResolvingNonUniqueEList(Class<?> dataClass, InternalEObject owner, int featureID) {
    super(dataClass, owner, featureID);
  }

  @Override
  protected boolean isUnique() {
    return false;
  }

}
