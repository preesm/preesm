/**
 */
package org.ietr.dftools.architecture.slam;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.attributes.VLNV;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>VLN Ved Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.VLNVedElement#getVlnv <em>Vlnv</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.SlamPackage#getVLNVedElement()
 * @model
 * @generated
 */
public interface VLNVedElement extends EObject {
	/**
	 * Returns the value of the '<em><b>Vlnv</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vlnv</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vlnv</em>' containment reference.
	 * @see #setVlnv(VLNV)
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getVLNVedElement_Vlnv()
	 * @model containment="true"
	 * @generated
	 */
	VLNV getVlnv();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.VLNVedElement#getVlnv <em>Vlnv</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Vlnv</em>' containment reference.
	 * @see #getVlnv()
	 * @generated
	 */
	void setVlnv(VLNV value);

} // VLNVedElement
