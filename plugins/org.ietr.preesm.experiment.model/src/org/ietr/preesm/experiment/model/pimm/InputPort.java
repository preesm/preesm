/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Input Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.InputPort#getIncomingFifo <em>Incoming Fifo</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getInputPort()
 * @model
 * @generated
 */
public interface InputPort extends Port {

	/**
	 * Returns the value of the '<em><b>Incoming Fifo</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Incoming Fifo</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Fifo</em>' reference.
	 * @see #setIncomingFifo(Fifo)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getInputPort_IncomingFifo()
	 * @see org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort
	 * @model opposite="targetPort"
	 * @generated
	 */
	Fifo getIncomingFifo();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.InputPort#getIncomingFifo <em>Incoming Fifo</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Incoming Fifo</em>' reference.
	 * @see #getIncomingFifo()
	 * @generated
	 */
	void setIncomingFifo(Fifo value);
} // InputPort
