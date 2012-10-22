/**
 */
package org.ietr.preesm.experiment.model.pimemoc;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Abstract Vertex</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getName
 * <em>Name</em>}</li>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getInputPorts
 * <em>Input Ports</em>}</li>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getOutputPorts
 * <em>Output Ports</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getAbstractVertex()
 * @model abstract="true"
 * @generated
 */
public interface AbstractVertex extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getAbstractVertex_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getName
	 * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Input Ports</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimemoc.InputPort}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Ports</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Input Ports</em>' containment reference
	 *         list.
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getAbstractVertex_InputPorts()
	 * @model containment="true"
	 * @generated
	 */
	EList<InputPort> getInputPorts();

	/**
	 * Returns the value of the '<em><b>Output Ports</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimemoc.OutputPort}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Ports</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Output Ports</em>' containment reference
	 *         list.
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getAbstractVertex_OutputPorts()
	 * @model containment="true"
	 * @generated
	 */
	EList<OutputPort> getOutputPorts();

	/**
	 * Get the {@link Port} with a given name from the {@link AbstractVertex}
	 * ports lists.
	 * 
	 * @param portName
	 *            the name of the desired {@link Port}
	 * @param direction
	 *            the direction of the desired {@link Port}:
	 *            <code>"input" or "output"</code>
	 * @return the requested port, or <code>null</code> if no such port exists.s
	 */
	public Port getPortNamed(String portName, String direction);

} // AbstractVertex
