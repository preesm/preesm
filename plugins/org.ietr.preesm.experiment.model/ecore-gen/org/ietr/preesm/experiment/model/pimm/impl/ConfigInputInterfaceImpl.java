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
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config Input Interface</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl#getGraphPort <em>Graph Port</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConfigInputInterfaceImpl extends ParameterImpl implements ConfigInputInterface {
  /**
   * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGraphPort()
   * @generated
   * @ordered
   */
  protected ConfigInputPort graphPort;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConfigInputInterfaceImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIG_INPUT_INTERFACE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputPort getGraphPort() {
    if (graphPort != null && graphPort.eIsProxy()) {
      InternalEObject oldGraphPort = (InternalEObject)graphPort;
      graphPort = (ConfigInputPort)eResolveProxy(oldGraphPort);
      if (graphPort != oldGraphPort) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT, oldGraphPort, graphPort));
      }
    }
    return graphPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputPort basicGetGraphPort() {
    return graphPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGraphPort(ConfigInputPort newGraphPort) {
    ConfigInputPort oldGraphPort = graphPort;
    graphPort = newGraphPort;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT, oldGraphPort, graphPort));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isLocallyStatic() {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isConfigurationInterface() {
    return true;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        if (resolve) return getGraphPort();
        return basicGetGraphPort();
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
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        setGraphPort((ConfigInputPort)newValue);
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
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        setGraphPort((ConfigInputPort)null);
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
      case PiMMPackage.CONFIG_INPUT_INTERFACE__GRAPH_PORT:
        return graphPort != null;
    }
    return super.eIsSet(featureID);
  }

} //ConfigInputInterfaceImpl
