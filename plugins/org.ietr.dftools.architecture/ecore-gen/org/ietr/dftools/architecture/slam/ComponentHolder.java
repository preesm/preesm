/**
 */
package org.ietr.dftools.architecture.slam;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.component.Component;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.ComponentHolder#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.SlamPackage#getComponentHolder()
 * @model
 * @generated
 */
public interface ComponentHolder extends EObject {
	/**
	 * Returns the value of the '<em><b>Components</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.dftools.architecture.slam.component.Component}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Components</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Components</em>' containment reference list.
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getComponentHolder_Components()
	 * @model containment="true" ordered="false"
	 * @generated
	 */
	EList<Component> getComponents();

} // ComponentHolder
