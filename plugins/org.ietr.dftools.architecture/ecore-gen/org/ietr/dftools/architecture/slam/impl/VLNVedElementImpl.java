/**
 */
package org.ietr.dftools.architecture.slam.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.VLNVedElement;

import org.ietr.dftools.architecture.slam.attributes.VLNV;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>VLN Ved Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.impl.VLNVedElementImpl#getVlnv <em>Vlnv</em>}</li>
 * </ul>
 *
 * @generated
 */
public class VLNVedElementImpl extends MinimalEObjectImpl.Container implements VLNVedElement {
	/**
	 * The cached value of the '{@link #getVlnv() <em>Vlnv</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVlnv()
	 * @generated
	 * @ordered
	 */
	protected VLNV vlnv;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected VLNVedElementImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SlamPackage.Literals.VLN_VED_ELEMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VLNV getVlnv() {
		return vlnv;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetVlnv(VLNV newVlnv, NotificationChain msgs) {
		VLNV oldVlnv = vlnv;
		vlnv = newVlnv;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SlamPackage.VLN_VED_ELEMENT__VLNV, oldVlnv, newVlnv);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVlnv(VLNV newVlnv) {
		if (newVlnv != vlnv) {
			NotificationChain msgs = null;
			if (vlnv != null)
				msgs = ((InternalEObject)vlnv).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SlamPackage.VLN_VED_ELEMENT__VLNV, null, msgs);
			if (newVlnv != null)
				msgs = ((InternalEObject)newVlnv).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - SlamPackage.VLN_VED_ELEMENT__VLNV, null, msgs);
			msgs = basicSetVlnv(newVlnv, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SlamPackage.VLN_VED_ELEMENT__VLNV, newVlnv, newVlnv));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case SlamPackage.VLN_VED_ELEMENT__VLNV:
				return basicSetVlnv(null, msgs);
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
			case SlamPackage.VLN_VED_ELEMENT__VLNV:
				return getVlnv();
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
			case SlamPackage.VLN_VED_ELEMENT__VLNV:
				setVlnv((VLNV)newValue);
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
			case SlamPackage.VLN_VED_ELEMENT__VLNV:
				setVlnv((VLNV)null);
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
			case SlamPackage.VLN_VED_ELEMENT__VLNV:
				return vlnv != null;
		}
		return super.eIsSet(featureID);
	}

} //VLNVedElementImpl
