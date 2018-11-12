/**
 */
package org.ietr.dftools.architecture.slam.component;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dma</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.Dma#getSetupTime <em>Setup Time</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getDma()
 * @model
 * @generated
 */
public interface Dma extends Enabler {
	/**
	 * Returns the value of the '<em><b>Setup Time</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Setup Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Setup Time</em>' attribute.
	 * @see #setSetupTime(int)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getDma_SetupTime()
	 * @model default="0" unique="false" dataType="org.ietr.dftools.architecture.slam.component.int"
	 * @generated
	 */
	int getSetupTime();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.Dma#getSetupTime <em>Setup Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Setup Time</em>' attribute.
	 * @see #getSetupTime()
	 * @generated
	 */
	void setSetupTime(int value);

} // Dma
