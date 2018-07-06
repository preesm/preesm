/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PortKind;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config Input Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl#getIncomingDependency <em>Incoming Dependency</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl#getConfigurable <em>Configurable</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConfigInputPortImpl extends MinimalEObjectImpl.Container implements ConfigInputPort {
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getIncomingDependency() <em>Incoming Dependency</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIncomingDependency()
   * @generated
   * @ordered
   */
  protected Dependency incomingDependency;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConfigInputPortImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIG_INPUT_PORT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName() {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName) {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Dependency getIncomingDependency() {
    if (incomingDependency != null && incomingDependency.eIsProxy()) {
      InternalEObject oldIncomingDependency = (InternalEObject)incomingDependency;
      incomingDependency = (Dependency)eResolveProxy(oldIncomingDependency);
      if (incomingDependency != oldIncomingDependency) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, oldIncomingDependency, incomingDependency));
      }
    }
    return incomingDependency;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Dependency basicGetIncomingDependency() {
    return incomingDependency;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetIncomingDependency(Dependency newIncomingDependency, NotificationChain msgs) {
    Dependency oldIncomingDependency = incomingDependency;
    incomingDependency = newIncomingDependency;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, oldIncomingDependency, newIncomingDependency);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setIncomingDependency(Dependency newIncomingDependency) {
    if (newIncomingDependency != incomingDependency) {
      NotificationChain msgs = null;
      if (incomingDependency != null)
        msgs = ((InternalEObject)incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
      if (newIncomingDependency != null)
        msgs = ((InternalEObject)newIncomingDependency).eInverseAdd(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
      msgs = basicSetIncomingDependency(newIncomingDependency, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, newIncomingDependency, newIncomingDependency));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configurable getConfigurable() {
    if (eContainerFeatureID() != PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE) return null;
    return (Configurable)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Configurable basicGetConfigurable() {
    if (eContainerFeatureID() != PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE) return null;
    return (Configurable)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetConfigurable(Configurable newConfigurable, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newConfigurable, PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConfigurable(Configurable newConfigurable) {
    if (newConfigurable != eInternalContainer() || (eContainerFeatureID() != PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE && newConfigurable != null)) {
      if (EcoreUtil.isAncestor(this, newConfigurable))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newConfigurable != null)
        msgs = ((InternalEObject)newConfigurable).eInverseAdd(this, PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS, Configurable.class, msgs);
      msgs = basicSetConfigurable(newConfigurable, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE, newConfigurable, newConfigurable));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PortKind getKind() {
    return PortKind.CFG_INPUT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        if (incomingDependency != null)
          msgs = ((InternalEObject)incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
        return basicSetIncomingDependency((Dependency)otherEnd, msgs);
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetConfigurable((Configurable)otherEnd, msgs);
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
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        return basicSetIncomingDependency(null, msgs);
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        return basicSetConfigurable(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS, Configurable.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.CONFIG_INPUT_PORT__NAME:
        return getName();
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        if (resolve) return getIncomingDependency();
        return basicGetIncomingDependency();
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        if (resolve) return getConfigurable();
        return basicGetConfigurable();
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
      case PiMMPackage.CONFIG_INPUT_PORT__NAME:
        setName((String)newValue);
        return;
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        setIncomingDependency((Dependency)newValue);
        return;
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        setConfigurable((Configurable)newValue);
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
      case PiMMPackage.CONFIG_INPUT_PORT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        setIncomingDependency((Dependency)null);
        return;
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        setConfigurable((Configurable)null);
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
      case PiMMPackage.CONFIG_INPUT_PORT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        return incomingDependency != null;
      case PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE:
        return basicGetConfigurable() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //ConfigInputPortImpl
