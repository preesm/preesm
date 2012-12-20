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

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config Output Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConfigOutputPortImpl extends PortImpl implements ConfigOutputPort {
	/**
	 * The cached value of the '{@link #getOutgoingDependencies() <em>Outgoing Dependencies</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutgoingDependencies()
	 * @generated
	 * @ordered
	 */
	protected EList<Dependency> outgoingDependencies;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 */
	protected ConfigOutputPortImpl() {
		super();
		kind = "cfg_output";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.CONFIG_OUTPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Dependency> getOutgoingDependencies() {
		if (outgoingDependencies == null) {
			outgoingDependencies = new EObjectWithInverseResolvingEList<Dependency>(Dependency.class, this, PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES, PiMMPackage.DEPENDENCY__SETTER);
		}
		return outgoingDependencies;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingDependencies()).basicAdd(otherEnd, msgs);
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
			case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
				return ((InternalEList<?>)getOutgoingDependencies()).basicRemove(otherEnd, msgs);
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
			case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
				return getOutgoingDependencies();
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
			case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
				getOutgoingDependencies().clear();
				getOutgoingDependencies().addAll((Collection<? extends Dependency>)newValue);
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
			case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
				getOutgoingDependencies().clear();
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
			case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES:
				return outgoingDependencies != null && !outgoingDependencies.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == ISetter.class) {
			switch (derivedFeatureID) {
				case PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES: return PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == ISetter.class) {
			switch (baseFeatureID) {
				case PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES: return PiMMPackage.CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

} //ConfigOutputPortImpl
