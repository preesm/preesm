/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dependency</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Dependency#getSetter <em>Setter</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDependency()
 * @model
 * @generated
 */
public interface Dependency extends EObject {
	/**
	 * Returns the value of the '<em><b>Setter</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies <em>Outgoing Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Setter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Setter</em>' reference.
	 * @see #setSetter(ISetter)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDependency_Setter()
	 * @see org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies
	 * @model opposite="outgoingDependencies" required="true"
	 * @generated
	 */
	ISetter getSetter();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getSetter <em>Setter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Setter</em>' reference.
	 * @see #getSetter()
	 * @generated
	 */
	void setSetter(ISetter value);

	/**
	 * Returns the value of the '<em><b>Getter</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Getter</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Getter</em>' reference.
	 * @see #setGetter(ConfigInputPort)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDependency_Getter()
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency
	 * @model opposite="incomingDependency" required="true"
	 * @generated
	 */
	ConfigInputPort getGetter();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Getter</em>' reference.
	 * @see #getGetter()
	 * @generated
	 */
	void setGetter(ConfigInputPort value);

} // Dependency
