/**
 */
package org.ietr.dftools.architecture.slam.attributes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.attributes.Parameter#getKey <em>Key</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.attributes.Parameter#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends EObject {
	/**
	 * Returns the value of the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Key</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key</em>' attribute.
	 * @see #setKey(String)
	 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getParameter_Key()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.attributes.String"
	 * @generated
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.attributes.Parameter#getKey <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' attribute.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getParameter_Value()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.attributes.String"
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.attributes.Parameter#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

} // Parameter
