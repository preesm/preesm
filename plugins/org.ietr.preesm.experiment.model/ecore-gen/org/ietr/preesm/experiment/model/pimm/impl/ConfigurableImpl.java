/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import java.util.Objects;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Configurable</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl#getConfigInputPorts <em>Config Input Ports</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ConfigurableImpl extends AbstractVertexImpl implements Configurable {
  /**
   * The cached value of the '{@link #getConfigInputPorts() <em>Config Input Ports</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getConfigInputPorts()
   * @generated
   * @ordered
   */
  protected EList<ConfigInputPort> configInputPorts;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ConfigurableImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIGURABLE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<ConfigInputPort> getConfigInputPorts() {
    if (this.configInputPorts == null) {
      this.configInputPorts = new EObjectContainmentWithInverseEList<>(ConfigInputPort.class, this, PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS,
          PiMMPackage.CONFIG_INPUT_PORT__CONFIGURABLE);
    }
    return this.configInputPorts;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Parameter> getInputParameters() {
    final EList<Parameter> result = ECollections.newBasicEList();
    for (final ConfigInputPort in : getConfigInputPorts()) {
      final ISetter setter = in.getIncomingDependency().getSetter();
      if (setter instanceof Parameter) {
        result.add((Parameter) setter);
      }
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Port lookupPortConnectedWithParameter(final Parameter parameter) {
    return getConfigInputPorts().stream().filter(Objects::nonNull).map(ConfigInputPort::getIncomingDependency).filter(Objects::nonNull)
        .filter(it -> it.getSetter() == parameter).findFirst().map(Dependency::getGetter).orElse(null);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Port> getAllConfigPorts() {
    final BasicEList<Port> result = ECollections.newBasicEList();
    result.addAll(getConfigInputPorts());
    return ECollections.unmodifiableEList(result);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Port> getAllPorts() {
    final BasicEList<Port> result = ECollections.newBasicEList();
    result.addAll(super.getAllPorts());
    result.addAll(getAllConfigPorts());
    return ECollections.unmodifiableEList(result);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isLocallyStatic() {
    // a Parameterizable is static if all its parameters are static (or it has no parameter)
    return getInputParameters().stream().filter(Objects::nonNull).allMatch(Parameter::isLocallyStatic);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getConfigInputPorts()).basicAdd(otherEnd, msgs);
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
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return ((InternalEList<?>) getConfigInputPorts()).basicRemove(otherEnd, msgs);
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
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return getConfigInputPorts();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        getConfigInputPorts().clear();
        getConfigInputPorts().addAll((Collection<? extends ConfigInputPort>) newValue);
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
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        getConfigInputPorts().clear();
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
      case PiMMPackage.CONFIGURABLE__CONFIG_INPUT_PORTS:
        return (this.configInputPorts != null) && !this.configInputPorts.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ConfigurableImpl
