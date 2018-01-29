/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dependency</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getSetter <em>Setter</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getGetter <em>Getter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependencyImpl extends EObjectImpl implements Dependency {
  /**
   * The cached value of the '{@link #getSetter() <em>Setter</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSetter()
   * @generated
   * @ordered
   */
  protected ISetter setter;

  /**
   * The cached value of the '{@link #getGetter() <em>Getter</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getGetter()
   * @generated
   * @ordered
   */
  protected ConfigInputPort getter;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected DependencyImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.DEPENDENCY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ISetter getSetter() {
    if ((this.setter != null) && this.setter.eIsProxy()) {
      final InternalEObject oldSetter = (InternalEObject) this.setter;
      this.setter = (ISetter) eResolveProxy(oldSetter);
      if (this.setter != oldSetter) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__SETTER, oldSetter, this.setter));
        }
      }
    }
    return this.setter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ISetter basicGetSetter() {
    return this.setter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetSetter(final ISetter newSetter, NotificationChain msgs) {
    final ISetter oldSetter = this.setter;
    this.setter = newSetter;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, oldSetter, newSetter);
      if (msgs == null) {
        msgs = notification;
      } else {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setSetter(final ISetter newSetter) {
    if (newSetter != this.setter) {
      NotificationChain msgs = null;
      if (this.setter != null) {
        msgs = ((InternalEObject) this.setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
      }
      if (newSetter != null) {
        msgs = ((InternalEObject) newSetter).eInverseAdd(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
      }
      msgs = basicSetSetter(newSetter, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, newSetter, newSetter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ConfigInputPort getGetter() {
    if ((this.getter != null) && this.getter.eIsProxy()) {
      final InternalEObject oldGetter = (InternalEObject) this.getter;
      this.getter = (ConfigInputPort) eResolveProxy(oldGetter);
      if (this.getter != oldGetter) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__GETTER, oldGetter, this.getter));
        }
      }
    }
    return this.getter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ConfigInputPort basicGetGetter() {
    return this.getter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetGetter(final ConfigInputPort newGetter, NotificationChain msgs) {
    final ConfigInputPort oldGetter = this.getter;
    this.getter = newGetter;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, oldGetter, newGetter);
      if (msgs == null) {
        msgs = notification;
      } else {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setGetter(final ConfigInputPort newGetter) {
    if (newGetter != this.getter) {
      NotificationChain msgs = null;
      if (this.getter != null) {
        msgs = ((InternalEObject) this.getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
      }
      if (newGetter != null) {
        msgs = ((InternalEObject) newGetter).eInverseAdd(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
      }
      msgs = basicSetGetter(newGetter, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, newGetter, newGetter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        if (this.setter != null) {
          msgs = ((InternalEObject) this.setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
        }
        return basicSetSetter((ISetter) otherEnd, msgs);
      case PiMMPackage.DEPENDENCY__GETTER:
        if (this.getter != null) {
          msgs = ((InternalEObject) this.getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
        }
        return basicSetGetter((ConfigInputPort) otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        return basicSetSetter(null, msgs);
      case PiMMPackage.DEPENDENCY__GETTER:
        return basicSetGetter(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        if (resolve) {
          return getSetter();
        }
        return basicGetSetter();
      case PiMMPackage.DEPENDENCY__GETTER:
        if (resolve) {
          return getGetter();
        }
        return basicGetGetter();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        setSetter((ISetter) newValue);
        return;
      case PiMMPackage.DEPENDENCY__GETTER:
        setGetter((ConfigInputPort) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        setSetter((ISetter) null);
        return;
      case PiMMPackage.DEPENDENCY__GETTER:
        setGetter((ConfigInputPort) null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case PiMMPackage.DEPENDENCY__SETTER:
        return this.setter != null;
      case PiMMPackage.DEPENDENCY__GETTER:
        return this.getter != null;
    }
    return super.eIsSet(featureID);
  }

} // DependencyImpl
