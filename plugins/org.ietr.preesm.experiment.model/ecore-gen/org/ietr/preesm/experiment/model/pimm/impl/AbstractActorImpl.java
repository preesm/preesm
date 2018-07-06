/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import com.google.common.collect.Iterables;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Actor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataInputPorts <em>Data Input Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataOutputPorts <em>Data Output Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getConfigOutputPorts <em>Config Output Ports</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AbstractActorImpl extends ConfigurableImpl implements AbstractActor {
  /**
   * The cached value of the '{@link #getDataInputPorts() <em>Data Input Ports</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDataInputPorts()
   * @generated
   * @ordered
   */
  protected EList<DataInputPort> dataInputPorts;

  /**
   * The cached value of the '{@link #getDataOutputPorts() <em>Data Output Ports</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDataOutputPorts()
   * @generated
   * @ordered
   */
  protected EList<DataOutputPort> dataOutputPorts;

  /**
   * The cached value of the '{@link #getConfigOutputPorts() <em>Config Output Ports</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConfigOutputPorts()
   * @generated
   * @ordered
   */
  protected EList<ConfigOutputPort> configOutputPorts;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AbstractActorImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.ABSTRACT_ACTOR;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DataInputPort> getDataInputPorts() {
    if (dataInputPorts == null) {
      dataInputPorts = new EObjectContainmentEList<DataInputPort>(DataInputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS);
    }
    return dataInputPorts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DataOutputPort> getDataOutputPorts() {
    if (dataOutputPorts == null) {
      dataOutputPorts = new EObjectContainmentEList<DataOutputPort>(DataOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS);
    }
    return dataOutputPorts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ConfigOutputPort> getConfigOutputPorts() {
    if (configOutputPorts == null) {
      configOutputPorts = new EObjectContainmentEList<ConfigOutputPort>(ConfigOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS);
    }
    return configOutputPorts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DataPort> getAllDataPorts() {
    EList<DataInputPort> _dataInputPorts = this.getDataInputPorts();
    EList<DataOutputPort> _dataOutputPorts = this.getDataOutputPorts();
    return ECollections.<DataPort>unmodifiableEList(ECollections.<DataPort>toEList(Iterables.<DataPort>concat(_dataInputPorts, _dataOutputPorts)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Port> getAllConfigPorts() {
    EList<Port> _allConfigPorts = super.getAllConfigPorts();
    EList<ConfigOutputPort> _configOutputPorts = this.getConfigOutputPorts();
    return ECollections.<Port>unmodifiableEList(ECollections.<Port>toEList(Iterables.<Port>concat(_allConfigPorts, _configOutputPorts)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Port> getAllPorts() {
    EList<Port> _allConfigPorts = this.getAllConfigPorts();
    EList<DataPort> _allDataPorts = this.getAllDataPorts();
    return ECollections.<Port>unmodifiableEList(ECollections.<Port>toEList(Iterables.<Port>concat(_allConfigPorts, _allDataPorts)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated See {@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getActorPath() model documentation} for details.
   * @generated
   */
  @Deprecated
  public String getActorPath() {
    return this.getVertexPath();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return ((InternalEList<?>)getDataInputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return ((InternalEList<?>)getDataOutputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return ((InternalEList<?>)getConfigOutputPorts()).basicRemove(otherEnd, msgs);
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return getDataInputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return getDataOutputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return getConfigOutputPorts();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        getDataInputPorts().clear();
        getDataInputPorts().addAll((Collection<? extends DataInputPort>)newValue);
        return;
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        getDataOutputPorts().clear();
        getDataOutputPorts().addAll((Collection<? extends DataOutputPort>)newValue);
        return;
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        getConfigOutputPorts().clear();
        getConfigOutputPorts().addAll((Collection<? extends ConfigOutputPort>)newValue);
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        getDataInputPorts().clear();
        return;
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        getDataOutputPorts().clear();
        return;
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        getConfigOutputPorts().clear();
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return dataInputPorts != null && !dataInputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return dataOutputPorts != null && !dataOutputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return configOutputPorts != null && !configOutputPorts.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //AbstractActorImpl
