/**
 */
package org.ietr.dftools.architecture.slam.attributes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>VLNV</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getVendor <em>Vendor</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getLibrary <em>Library</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getName <em>Name</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getVersion <em>Version</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getVLNV()
 * @model
 * @generated
 */
public interface VLNV extends EObject {
	/**
	 * Returns the value of the '<em><b>Vendor</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vendor</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vendor</em>' attribute.
	 * @see #setVendor(String)
	 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getVLNV_Vendor()
	 * @model default="" unique="false" dataType="org.ietr.dftools.architecture.slam.attributes.String"
	 * @generated
	 */
	String getVendor();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getVendor <em>Vendor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Vendor</em>' attribute.
	 * @see #getVendor()
	 * @generated
	 */
	void setVendor(String value);

	/**
	 * Returns the value of the '<em><b>Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Library</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Library</em>' attribute.
	 * @see #setLibrary(String)
	 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getVLNV_Library()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.attributes.String"
	 * @generated
	 */
	String getLibrary();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getLibrary <em>Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Library</em>' attribute.
	 * @see #getLibrary()
	 * @generated
	 */
	void setLibrary(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getVLNV_Name()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.attributes.String"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see org.ietr.dftools.architecture.slam.attributes.AttributesPackage#getVLNV_Version()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.attributes.String"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

} // VLNV
