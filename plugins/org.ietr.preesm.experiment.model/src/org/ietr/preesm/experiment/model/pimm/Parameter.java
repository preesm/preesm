/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Parameter#isLocallyStatic <em>Locally Static</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Parameter#isConfigurationInterface <em>Configuration Interface</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends AbstractVertex {
	/**
	 * Returns the value of the '<em><b>Locally Static</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Locally Static</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Locally Static</em>' attribute.
	 * @see #setLocallyStatic(boolean)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter_LocallyStatic()
	 * @model required="true"
	 * @generated
	 */
	boolean isLocallyStatic();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Parameter#isLocallyStatic <em>Locally Static</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Locally Static</em>' attribute.
	 * @see #isLocallyStatic()
	 * @generated
	 */
	void setLocallyStatic(boolean value);

	/**
	 * Returns the value of the '<em><b>Configuration Interface</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Configuration Interface</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Configuration Interface</em>' attribute.
	 * @see #setConfigurationInterface(boolean)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter_ConfigurationInterface()
	 * @model required="true"
	 * @generated
	 */
	boolean isConfigurationInterface();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Parameter#isConfigurationInterface <em>Configuration Interface</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Configuration Interface</em>' attribute.
	 * @see #isConfigurationInterface()
	 * @generated
	 */
	void setConfigurationInterface(boolean value);

} // Parameter
