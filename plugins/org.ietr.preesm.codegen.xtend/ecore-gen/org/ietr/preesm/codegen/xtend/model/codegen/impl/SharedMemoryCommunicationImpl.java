/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.codegen.xtend.model.codegen.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Semaphore;
import org.ietr.preesm.codegen.xtend.model.codegen.SharedMemoryCommunication;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Shared Memory Communication</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SharedMemoryCommunicationImpl#getSemaphore <em>Semaphore</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SharedMemoryCommunicationImpl extends CommunicationImpl implements SharedMemoryCommunication {
  /**
   * The cached value of the '{@link #getSemaphore() <em>Semaphore</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSemaphore()
   * @generated
   * @ordered
   */
  protected Semaphore semaphore;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected SharedMemoryCommunicationImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return CodegenPackage.Literals.SHARED_MEMORY_COMMUNICATION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the semaphore
   * @generated
   */
  @Override
  public Semaphore getSemaphore() {
    if ((this.semaphore != null) && this.semaphore.eIsProxy()) {
      final InternalEObject oldSemaphore = (InternalEObject) this.semaphore;
      this.semaphore = (Semaphore) eResolveProxy(oldSemaphore);
      if (this.semaphore != oldSemaphore) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.SHARED_MEMORY_COMMUNICATION__SEMAPHORE, oldSemaphore, this.semaphore));
        }
      }
    }
    return this.semaphore;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the semaphore
   * @generated
   */
  public Semaphore basicGetSemaphore() {
    return this.semaphore;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSemaphore
   *          the new semaphore
   * @generated
   */
  @Override
  public void setSemaphore(final Semaphore newSemaphore) {
    final Semaphore oldSemaphore = this.semaphore;
    this.semaphore = newSemaphore;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.SHARED_MEMORY_COMMUNICATION__SEMAPHORE, oldSemaphore, this.semaphore));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case CodegenPackage.SHARED_MEMORY_COMMUNICATION__SEMAPHORE:
        if (resolve) {
          return getSemaphore();
        }
        return basicGetSemaphore();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param newValue
   *          the new value
   * @generated
   */
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case CodegenPackage.SHARED_MEMORY_COMMUNICATION__SEMAPHORE:
        setSemaphore((Semaphore) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case CodegenPackage.SHARED_MEMORY_COMMUNICATION__SEMAPHORE:
        setSemaphore((Semaphore) null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @return true, if successful
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case CodegenPackage.SHARED_MEMORY_COMMUNICATION__SEMAPHORE:
        return this.semaphore != null;
    }
    return super.eIsSet(featureID);
  }

} // SharedMemoryCommunicationImpl
