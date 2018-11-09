/**
 */
package org.ietr.dftools.architecture.slam.component;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.ComponentInstance;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Hierarchy Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.HierarchyPort#getExternalInterface <em>External Interface</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.HierarchyPort#getInternalInterface <em>Internal Interface</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.HierarchyPort#getInternalComponentInstance <em>Internal Component Instance</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getHierarchyPort()
 * @model
 * @generated
 */
public interface HierarchyPort extends EObject {
	/**
	 * Returns the value of the '<em><b>External Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>External Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External Interface</em>' reference.
	 * @see #setExternalInterface(ComInterface)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getHierarchyPort_ExternalInterface()
	 * @model
	 * @generated
	 */
	ComInterface getExternalInterface();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.HierarchyPort#getExternalInterface <em>External Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>External Interface</em>' reference.
	 * @see #getExternalInterface()
	 * @generated
	 */
	void setExternalInterface(ComInterface value);

	/**
	 * Returns the value of the '<em><b>Internal Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Internal Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Internal Interface</em>' reference.
	 * @see #setInternalInterface(ComInterface)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getHierarchyPort_InternalInterface()
	 * @model
	 * @generated
	 */
	ComInterface getInternalInterface();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.HierarchyPort#getInternalInterface <em>Internal Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Internal Interface</em>' reference.
	 * @see #getInternalInterface()
	 * @generated
	 */
	void setInternalInterface(ComInterface value);

	/**
	 * Returns the value of the '<em><b>Internal Component Instance</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Internal Component Instance</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Internal Component Instance</em>' reference.
	 * @see #setInternalComponentInstance(ComponentInstance)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getHierarchyPort_InternalComponentInstance()
	 * @model
	 * @generated
	 */
	ComponentInstance getInternalComponentInstance();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.HierarchyPort#getInternalComponentInstance <em>Internal Component Instance</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Internal Component Instance</em>' reference.
	 * @see #getInternalComponentInstance()
	 * @generated
	 */
	void setInternalComponentInstance(ComponentInstance value);

} // HierarchyPort
