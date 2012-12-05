/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Actor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor()
 * @model
 * @generated
 */
public interface Actor extends AbstractVertex {

	/**
	 * Returns the value of the '<em><b>Refinement</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refinement</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refinement</em>' containment reference.
	 * @see #setRefinement(Refinement)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_Refinement()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Refinement getRefinement();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Refinement</em>' containment reference.
	 * @see #getRefinement()
	 * @generated
	 */
	void setRefinement(Refinement value);
} // Actor
