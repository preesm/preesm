/**
 */
package org.ietr.dftools.architecture.slam.component.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.ietr.dftools.architecture.slam.ComponentInstance;

import org.ietr.dftools.architecture.slam.component.ComInterface;
import org.ietr.dftools.architecture.slam.component.ComponentPackage;
import org.ietr.dftools.architecture.slam.component.HierarchyPort;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Hierarchy Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.HierarchyPortImpl#getExternalInterface <em>External Interface</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.HierarchyPortImpl#getInternalInterface <em>Internal Interface</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.impl.HierarchyPortImpl#getInternalComponentInstance <em>Internal Component Instance</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HierarchyPortImpl extends MinimalEObjectImpl.Container implements HierarchyPort {
	/**
	 * The cached value of the '{@link #getExternalInterface() <em>External Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExternalInterface()
	 * @generated
	 * @ordered
	 */
	protected ComInterface externalInterface;

	/**
	 * The cached value of the '{@link #getInternalInterface() <em>Internal Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalInterface()
	 * @generated
	 * @ordered
	 */
	protected ComInterface internalInterface;

	/**
	 * The cached value of the '{@link #getInternalComponentInstance() <em>Internal Component Instance</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInternalComponentInstance()
	 * @generated
	 * @ordered
	 */
	protected ComponentInstance internalComponentInstance;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected HierarchyPortImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ComponentPackage.Literals.HIERARCHY_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComInterface getExternalInterface() {
		if (externalInterface != null && externalInterface.eIsProxy()) {
			InternalEObject oldExternalInterface = (InternalEObject)externalInterface;
			externalInterface = (ComInterface)eResolveProxy(oldExternalInterface);
			if (externalInterface != oldExternalInterface) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ComponentPackage.HIERARCHY_PORT__EXTERNAL_INTERFACE, oldExternalInterface, externalInterface));
			}
		}
		return externalInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComInterface basicGetExternalInterface() {
		return externalInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExternalInterface(ComInterface newExternalInterface) {
		ComInterface oldExternalInterface = externalInterface;
		externalInterface = newExternalInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentPackage.HIERARCHY_PORT__EXTERNAL_INTERFACE, oldExternalInterface, externalInterface));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComInterface getInternalInterface() {
		if (internalInterface != null && internalInterface.eIsProxy()) {
			InternalEObject oldInternalInterface = (InternalEObject)internalInterface;
			internalInterface = (ComInterface)eResolveProxy(oldInternalInterface);
			if (internalInterface != oldInternalInterface) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ComponentPackage.HIERARCHY_PORT__INTERNAL_INTERFACE, oldInternalInterface, internalInterface));
			}
		}
		return internalInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComInterface basicGetInternalInterface() {
		return internalInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInternalInterface(ComInterface newInternalInterface) {
		ComInterface oldInternalInterface = internalInterface;
		internalInterface = newInternalInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentPackage.HIERARCHY_PORT__INTERNAL_INTERFACE, oldInternalInterface, internalInterface));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentInstance getInternalComponentInstance() {
		if (internalComponentInstance != null && internalComponentInstance.eIsProxy()) {
			InternalEObject oldInternalComponentInstance = (InternalEObject)internalComponentInstance;
			internalComponentInstance = (ComponentInstance)eResolveProxy(oldInternalComponentInstance);
			if (internalComponentInstance != oldInternalComponentInstance) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ComponentPackage.HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE, oldInternalComponentInstance, internalComponentInstance));
			}
		}
		return internalComponentInstance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentInstance basicGetInternalComponentInstance() {
		return internalComponentInstance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInternalComponentInstance(ComponentInstance newInternalComponentInstance) {
		ComponentInstance oldInternalComponentInstance = internalComponentInstance;
		internalComponentInstance = newInternalComponentInstance;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentPackage.HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE, oldInternalComponentInstance, internalComponentInstance));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ComponentPackage.HIERARCHY_PORT__EXTERNAL_INTERFACE:
				if (resolve) return getExternalInterface();
				return basicGetExternalInterface();
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_INTERFACE:
				if (resolve) return getInternalInterface();
				return basicGetInternalInterface();
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE:
				if (resolve) return getInternalComponentInstance();
				return basicGetInternalComponentInstance();
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
			case ComponentPackage.HIERARCHY_PORT__EXTERNAL_INTERFACE:
				setExternalInterface((ComInterface)newValue);
				return;
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_INTERFACE:
				setInternalInterface((ComInterface)newValue);
				return;
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE:
				setInternalComponentInstance((ComponentInstance)newValue);
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
			case ComponentPackage.HIERARCHY_PORT__EXTERNAL_INTERFACE:
				setExternalInterface((ComInterface)null);
				return;
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_INTERFACE:
				setInternalInterface((ComInterface)null);
				return;
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE:
				setInternalComponentInstance((ComponentInstance)null);
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
			case ComponentPackage.HIERARCHY_PORT__EXTERNAL_INTERFACE:
				return externalInterface != null;
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_INTERFACE:
				return internalInterface != null;
			case ComponentPackage.HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE:
				return internalComponentInstance != null;
		}
		return super.eIsSet(featureID);
	}

} //HierarchyPortImpl
