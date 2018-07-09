/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceKind;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config Output Interface</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl#getGraphPort <em>Graph Port</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConfigOutputInterfaceImpl extends AbstractActorImpl implements ConfigOutputInterface {
  /**
   * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGraphPort()
   * @generated
   * @ordered
   */
  protected Port graphPort;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ConfigOutputInterfaceImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIG_OUTPUT_INTERFACE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Port getGraphPort() {
    if (graphPort != null && graphPort.eIsProxy()) {
      InternalEObject oldGraphPort = (InternalEObject)graphPort;
      graphPort = (Port)eResolveProxy(oldGraphPort);
      if (graphPort != oldGraphPort) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.CONFIG_OUTPUT_INTERFACE__GRAPH_PORT, oldGraphPort, graphPort));
      }
    }
    return graphPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Port basicGetGraphPort() {
    return graphPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGraphPort(Port newGraphPort) {
    Port oldGraphPort = graphPort;
    graphPort = newGraphPort;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_OUTPUT_INTERFACE__GRAPH_PORT, oldGraphPort, graphPort));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InterfaceKind getKind() {
    return InterfaceKind.CFG_OUTPUT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataPort getDataPort() {
    return this.getAllDataPorts().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.CONFIG_OUTPUT_INTERFACE__GRAPH_PORT:
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
      case PiMMPackage.CONFIG_OUTPUT_INTERFACE__GRAPH_PORT:
        setGraphPort((Port)newValue);
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
      case PiMMPackage.CONFIG_OUTPUT_INTERFACE__GRAPH_PORT:
        setGraphPort((Port)null);
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
      case PiMMPackage.CONFIG_OUTPUT_INTERFACE__GRAPH_PORT:
        return graphPort != null;
    }
    return super.eIsSet(featureID);
  }

} //ConfigOutputInterfaceImpl
