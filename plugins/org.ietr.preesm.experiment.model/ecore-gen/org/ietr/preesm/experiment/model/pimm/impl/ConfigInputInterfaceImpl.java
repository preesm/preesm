/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Config Input Interface</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl#getGraphPort <em>Graph Port</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConfigInputInterfaceImpl extends ParameterImpl implements ConfigInputInterface {
  /**
   * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getGraphPort()
   * @generated
   * @ordered
   */
  protected ConfigInputPort graphPort;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ConfigInputInterfaceImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIG_INPUT_INTERFACE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ConfigInputPort getGraphPort() {
    if ((this.graphPort != null) && this.graphPort.eIsProxy()) {
      final InternalEObject oldGraphPort = (InternalEObject) this.graphPort;
      this.graphPort = (ConfigInputPort) eResolveProxy(oldGraphPort);
      if (this.graphPort != oldGraphPort) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT, oldGraphPort, this.graphPort));
        }
      }
    }
    return this.graphPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public ConfigInputPort basicGetGraphPort() {
    return this.graphPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setGraphPort(final ConfigInputPort newGraphPort) {
    final ConfigInputPort oldGraphPort = this.graphPort;
    this.graphPort = newGraphPort;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT, oldGraphPort, this.graphPort));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isLocallyStatic() {
    // a ConfigInputInterface gets its value from the parent graph once per execution
    // during one iteration, its value does not change, thus is locally static
    return true;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isConfigurationInterface() {
    return true;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        if (resolve) {
          return getGraphPort();
        }
        return basicGetGraphPort();
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
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        setGraphPort((ConfigInputPort) newValue);
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
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        setGraphPort((ConfigInputPort) null);
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
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        return this.graphPort != null;
    }
    return super.eIsSet(featureID);
  }

} // ConfigInputInterfaceImpl
