/**
 */
package org.ietr.preesm.experiment.model.pimm;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Vertex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex()
 * @model abstract="true"
 * @generated
 */
public interface AbstractVertex extends Parameterizable {
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
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Get the {@link Port} with a given name from the {@link AbstractActor}
	 * ports lists.
	 * 
	 * @param portName
	 *            the name of the desired {@link Port}
	 * @return the requested port, or <code>null</code> if no such port exists.s
	 */
	public Port getPortNamed(String portName);

} // AbstractVertex
