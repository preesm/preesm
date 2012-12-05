/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fifo</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo()
 * @model
 * @generated
 */
public interface Fifo extends EObject {
	/**
	 * Returns the value of the '<em><b>Source Port</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.OutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source Port</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source Port</em>' reference.
	 * @see #setSourcePort(OutputPort)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_SourcePort()
	 * @see org.ietr.preesm.experiment.model.pimm.OutputPort#getOutgoingFifo
	 * @model opposite="outgoingFifo" required="true"
	 * @generated
	 */
	OutputPort getSourcePort();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source Port</em>' reference.
	 * @see #getSourcePort()
	 * @generated
	 */
	void setSourcePort(OutputPort value);

	/**
	 * Returns the value of the '<em><b>Target Port</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.InputPort#getIncomingFifo <em>Incoming Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target Port</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Port</em>' reference.
	 * @see #setTargetPort(InputPort)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_TargetPort()
	 * @see org.ietr.preesm.experiment.model.pimm.InputPort#getIncomingFifo
	 * @model opposite="incomingFifo" required="true"
	 * @generated
	 */
	InputPort getTargetPort();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Port</em>' reference.
	 * @see #getTargetPort()
	 * @generated
	 */
	void setTargetPort(InputPort value);

} // Fifo
