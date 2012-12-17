/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Config Input Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort()
 * @model
 * @generated
 */
public interface ConfigInputPort extends Port {

	/**
	 * Returns the value of the '<em><b>Incoming Dependency</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Incoming Dependency</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Dependency</em>' reference.
	 * @see #setIncomingDependency(Dependency)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort_IncomingDependency()
	 * @see org.ietr.preesm.experiment.model.pimm.Dependency#getGetter
	 * @model opposite="getter"
	 * @generated
	 */
	Dependency getIncomingDependency();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Incoming Dependency</em>' reference.
	 * @see #getIncomingDependency()
	 * @generated
	 */
	void setIncomingDependency(Dependency value);
} // ConfigInputPort
