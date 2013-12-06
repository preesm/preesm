/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependency</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getSetter <em>Setter</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getGetter <em>Getter</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DependencyImpl extends EObjectImpl implements Dependency {
	/**
	 * The cached value of the '{@link #getSetter() <em>Setter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSetter()
	 * @generated
	 * @ordered
	 */
	protected ISetter setter;

	/**
	 * The cached value of the '{@link #getGetter() <em>Getter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGetter()
	 * @generated
	 * @ordered
	 */
	protected ConfigInputPort getter;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DependencyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.DEPENDENCY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ISetter getSetter() {
		if (setter != null && setter.eIsProxy()) {
			InternalEObject oldSetter = (InternalEObject)setter;
			setter = (ISetter)eResolveProxy(oldSetter);
			if (setter != oldSetter) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__SETTER, oldSetter, setter));
			}
		}
		return setter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ISetter basicGetSetter() {
		return setter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSetter(ISetter newSetter, NotificationChain msgs) {
		ISetter oldSetter = setter;
		setter = newSetter;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, oldSetter, newSetter);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSetter(ISetter newSetter) {
		if (newSetter != setter) {
			NotificationChain msgs = null;
			if (setter != null)
				msgs = ((InternalEObject)setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
			if (newSetter != null)
				msgs = ((InternalEObject)newSetter).eInverseAdd(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
			msgs = basicSetSetter(newSetter, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, newSetter, newSetter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigInputPort getGetter() {
		if (getter != null && getter.eIsProxy()) {
			InternalEObject oldGetter = (InternalEObject)getter;
			getter = (ConfigInputPort)eResolveProxy(oldGetter);
			if (getter != oldGetter) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__GETTER, oldGetter, getter));
			}
		}
		return getter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigInputPort basicGetGetter() {
		return getter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGetter(ConfigInputPort newGetter, NotificationChain msgs) {
		ConfigInputPort oldGetter = getter;
		getter = newGetter;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, oldGetter, newGetter);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGetter(ConfigInputPort newGetter) {
		if (newGetter != getter) {
			NotificationChain msgs = null;
			if (getter != null)
				msgs = ((InternalEObject)getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
			if (newGetter != null)
				msgs = ((InternalEObject)newGetter).eInverseAdd(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
			msgs = basicSetGetter(newGetter, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, newGetter, newGetter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.DEPENDENCY__SETTER:
				if (setter != null)
					msgs = ((InternalEObject)setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
				return basicSetSetter((ISetter)otherEnd, msgs);
			case PiMMPackage.DEPENDENCY__GETTER:
				if (getter != null)
					msgs = ((InternalEObject)getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
				return basicSetGetter((ConfigInputPort)otherEnd, msgs);
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
			case PiMMPackage.DEPENDENCY__SETTER:
				return basicSetSetter(null, msgs);
			case PiMMPackage.DEPENDENCY__GETTER:
				return basicSetGetter(null, msgs);
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
			case PiMMPackage.DEPENDENCY__SETTER:
				if (resolve) return getSetter();
				return basicGetSetter();
			case PiMMPackage.DEPENDENCY__GETTER:
				if (resolve) return getGetter();
				return basicGetGetter();
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
			case PiMMPackage.DEPENDENCY__SETTER:
				setSetter((ISetter)newValue);
				return;
			case PiMMPackage.DEPENDENCY__GETTER:
				setGetter((ConfigInputPort)newValue);
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
			case PiMMPackage.DEPENDENCY__SETTER:
				setSetter((ISetter)null);
				return;
			case PiMMPackage.DEPENDENCY__GETTER:
				setGetter((ConfigInputPort)null);
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
			case PiMMPackage.DEPENDENCY__SETTER:
				return setter != null;
			case PiMMPackage.DEPENDENCY__GETTER:
				return getter != null;
		}
		return super.eIsSet(featureID);
	}

} //DependencyImpl
