/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config Input Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl#getIncomingDependency <em>Incoming Dependency</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConfigInputPortImpl extends PortImpl implements ConfigInputPort {
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
	 * 
	 */
	protected ConfigInputPortImpl() {
		super();
		kind = "cfg_input";
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
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				if (incomingDependency != null)
					msgs = ((InternalEObject)incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
				return basicSetIncomingDependency((Dependency)otherEnd, msgs);
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
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				if (resolve) return getIncomingDependency();
				return basicGetIncomingDependency();
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
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				setIncomingDependency((Dependency)newValue);
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
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				setIncomingDependency((Dependency)null);
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
			case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
				return incomingDependency != null;
		}
		return super.eIsSet(featureID);
	}

} //ConfigInputPortImpl
