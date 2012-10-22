/**
 */
package org.ietr.preesm.experiment.model.pimemoc;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Output Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimemoc.OutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getOutputPort()
 * @model
 * @generated
 */
public interface OutputPort extends Port {

	/**
	 * Returns the value of the '<em><b>Outgoing Fifo</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimemoc.Fifo#getSourcePort <em>Source Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outgoing Fifo</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outgoing Fifo</em>' reference.
	 * @see #setOutgoingFifo(Fifo)
	 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage#getOutputPort_OutgoingFifo()
	 * @see org.ietr.preesm.experiment.model.pimemoc.Fifo#getSourcePort
	 * @model opposite="sourcePort"
	 * @generated
	 */
	Fifo getOutgoingFifo();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimemoc.OutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Outgoing Fifo</em>' reference.
	 * @see #getOutgoingFifo()
	 * @generated
	 */
	void setOutgoingFifo(Fifo value);
} // OutputPort
