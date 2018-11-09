/**
 */
package org.ietr.dftools.architecture.slam.component;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mem</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.Mem#getSize <em>Size</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getMem()
 * @model
 * @generated
 */
public interface Mem extends Enabler {
	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Size</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see #setSize(int)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getMem_Size()
	 * @model default="1" unique="false" dataType="org.ietr.dftools.architecture.slam.component.int"
	 * @generated
	 */
	int getSize();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.Mem#getSize <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' attribute.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(int value);

} // Mem
