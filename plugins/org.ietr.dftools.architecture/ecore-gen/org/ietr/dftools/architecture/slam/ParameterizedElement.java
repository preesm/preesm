/**
 */
package org.ietr.dftools.architecture.slam;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.attributes.Parameter;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameterized Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.ParameterizedElement#getParameters <em>Parameters</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.SlamPackage#getParameterizedElement()
 * @model
 * @generated
 */
public interface ParameterizedElement extends EObject {
	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.ietr.dftools.architecture.slam.attributes.Parameter}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getParameterizedElement_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getParameters();

} // ParameterizedElement
