/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependency</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getSetter <em>Setter</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getGetter <em>Getter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependencyImpl extends EdgeImpl implements Dependency {
  /**
   * The cached value of the '{@link #getSetter() <em>Setter</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSetter()
   * @generated
   * @ordered
   */
  protected ISetter setter;

  /**
   * The cached value of the '{@link #getGetter() <em>Getter</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGetter()
   * @generated
   * @ordered
   */
  protected ConfigInputPort getter;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DependencyImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.DEPENDENCY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ISetter getSetter() {
    if (setter != null && setter.eIsProxy()) {
      InternalEObject oldSetter = (InternalEObject)setter;
      setter = (ISetter)eResolveProxy(oldSetter);
      if (setter != oldSetter) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__SETTER, oldSetter, setter));
      }
    }
    return setter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ISetter basicGetSetter() {
    return setter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSetter(ISetter newSetter, NotificationChain msgs) {
    ISetter oldSetter = setter;
    setter = newSetter;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, oldSetter, newSetter);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSetter(ISetter newSetter) {
    if (newSetter != setter) {
      NotificationChain msgs = null;
      if (setter != null)
        msgs = ((InternalEObject)setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
      if (newSetter != null)
        msgs = ((InternalEObject)newSetter).eInverseAdd(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
      msgs = basicSetSetter(newSetter, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, newSetter, newSetter));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputPort getGetter() {
    if (getter != null && getter.eIsProxy()) {
      InternalEObject oldGetter = (InternalEObject)getter;
      getter = (ConfigInputPort)eResolveProxy(oldGetter);
      if (getter != oldGetter) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__GETTER, oldGetter, getter));
      }
    }
    return getter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputPort basicGetGetter() {
    return getter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetGetter(ConfigInputPort newGetter, NotificationChain msgs) {
    ConfigInputPort oldGetter = getter;
    getter = newGetter;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, oldGetter, newGetter);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGetter(ConfigInputPort newGetter) {
    if (newGetter != getter) {
      NotificationChain msgs = null;
      if (getter != null)
        msgs = ((InternalEObject)getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
      if (newGetter != null)
        msgs = ((InternalEObject)newGetter).eInverseAdd(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
      msgs = basicSetGetter(newGetter, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, newGetter, newGetter));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        if (setter != null)
          msgs = ((InternalEObject)setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
        return basicSetSetter((ISetter)otherEnd, msgs);
      case PiMMPackage.DEPENDENCY__GETTER:
        if (getter != null)
          msgs = ((InternalEObject)getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
        return basicSetGetter((ConfigInputPort)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        return basicSetSetter(null, msgs);
      case PiMMPackage.DEPENDENCY__GETTER:
        return basicSetGetter(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        if (resolve) return getSetter();
        return basicGetSetter();
      case PiMMPackage.DEPENDENCY__GETTER:
        if (resolve) return getGetter();
        return basicGetGetter();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        setSetter((ISetter)newValue);
        return;
      case PiMMPackage.DEPENDENCY__GETTER:
        setGetter((ConfigInputPort)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        setSetter((ISetter)null);
        return;
      case PiMMPackage.DEPENDENCY__GETTER:
        setGetter((ConfigInputPort)null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        return setter != null;
      case PiMMPackage.DEPENDENCY__GETTER:
        return getter != null;
    }
    return super.eIsSet(featureID);
  }

} //DependencyImpl
