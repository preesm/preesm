/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Abstract Vertex</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getInputPorts <em>Input Ports</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getOutputPorts <em>Output Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractActor()
 * @model abstract="true"
 * @generated
 */
public interface AbstractActor extends AbstractVertex {
	/**
	 * Returns the value of the '<em><b>Input Ports</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimm.InputPort}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Ports</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Input Ports</em>' containment reference
	 *         list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex_InputPorts()
	 * @model containment="true"
	 * @generated
	 */
	EList<InputPort> getInputPorts();

	/**
	 * Returns the value of the '<em><b>Output Ports</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link org.ietr.preesm.experiment.model.pimm.OutputPort}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Ports</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Output Ports</em>' containment reference
	 *         list.
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex_OutputPorts()
	 * @model containment="true"
	 * @generated
	 */
	EList<OutputPort> getOutputPorts();

	/**
	 * Get the {@link Port} with a given name from the {@link AbstractActor}
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
