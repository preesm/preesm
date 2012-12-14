/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameterizable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterizableImpl#getConfigInputPorts <em>Config Input Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ParameterizableImpl extends EObjectImpl implements Parameterizable {
	/**
	 * The cached value of the '{@link #getConfigInputPorts() <em>Config Input Ports</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConfigInputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<ConfigInputPort> configInputPorts;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterizableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.PARAMETERIZABLE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ConfigInputPort> getConfigInputPorts() {
		if (configInputPorts == null) {
			configInputPorts = new EObjectContainmentEList<ConfigInputPort>(ConfigInputPort.class, this, PiMMPackage.PARAMETERIZABLE__CONFIG_INPUT_PORTS);
		}
		return configInputPorts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.PARAMETERIZABLE__CONFIG_INPUT_PORTS:
				return ((InternalEList<?>)getConfigInputPorts()).basicRemove(otherEnd, msgs);
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
			case PiMMPackage.PARAMETERIZABLE__CONFIG_INPUT_PORTS:
				return getConfigInputPorts();
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
			case PiMMPackage.PARAMETERIZABLE__CONFIG_INPUT_PORTS:
				getConfigInputPorts().clear();
				getConfigInputPorts().addAll((Collection<? extends ConfigInputPort>)newValue);
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
			case PiMMPackage.PARAMETERIZABLE__CONFIG_INPUT_PORTS:
				getConfigInputPorts().clear();
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
			case PiMMPackage.PARAMETERIZABLE__CONFIG_INPUT_PORTS:
				return configInputPorts != null && !configInputPorts.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //ParameterizableImpl
