/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interface Vertex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor#getGraphPort <em>Graph Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getInterfaceActor()
 * @model
 * @generated
 */
public interface InterfaceActor extends AbstractActor {
	/**
	 * Returns the value of the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Graph Port</em>' reference.
	 * @see #setGraphPort(Port)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getInterfaceActor_GraphPort()
	 * @model required="true"
	 * @generated
	 */
	Port getGraphPort();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor#getGraphPort <em>Graph Port</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Graph Port</em>' reference.
	 * @see #getGraphPort()
	 * @generated
	 */
	void setGraphPort(Port value);

	/**
	 * Returns the value of the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Kind</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Kind</em>' attribute.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getInterfaceActor_Kind()
	 * @model required="true" changeable="false"
	 * @generated
	 */
	String getKind();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void setName(String newName);

} // InterfaceVertex
