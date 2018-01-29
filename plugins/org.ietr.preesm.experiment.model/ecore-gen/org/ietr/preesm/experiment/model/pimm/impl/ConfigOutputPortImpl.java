/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PortKind;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Config Output Port</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConfigOutputPortImpl extends DataOutputPortImpl implements ConfigOutputPort {
  /**
   * The cached value of the '{@link #getOutgoingDependencies() <em>Outgoing Dependencies</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getOutgoingDependencies()
   * @generated
   * @ordered
   */
  protected EList<Dependency> outgoingDependencies;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ConfigOutputPortImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIG_OUTPUT_PORT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Dependency> getOutgoingDependencies() {
    if (this.outgoingDependencies == null) {
      this.outgoingDependencies = new EObjectWithInverseResolvingEList<>(Dependency.class, this, PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES,
          PiMMPackage.DEPENDENCY__SETTER);
    }
    return this.outgoingDependencies;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isLocallyStatic() {
    // config output ports are never considered static
    return false;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PortKind getKind() {
    return PortKind.CFG_OUTPUT;
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
      case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getOutgoingDependencies()).basicAdd(otherEnd, msgs);
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
      case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
        return ((InternalEList<?>) getOutgoingDependencies()).basicRemove(otherEnd, msgs);
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
      case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
        return getOutgoingDependencies();
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
      case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        getOutgoingDependencies().addAll((Collection<? extends Dependency>) newValue);
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
      case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
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
      case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
        return (this.outgoingDependencies != null) && !this.outgoingDependencies.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(final int derivedFeatureID, final Class<?> baseClass) {
    if (baseClass == ISetter.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
          return PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES;
        default:
          return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(final int baseFeatureID, final Class<?> baseClass) {
    if (baseClass == ISetter.class) {
      switch (baseFeatureID) {
        case PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES:
          return PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES;
        default:
          return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ConfigOutputPortImpl
