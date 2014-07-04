/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>HRefinement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.HRefinement#getLoopPrototype <em>Loop Prototype</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.HRefinement#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getHRefinement()
 * @model
 * @generated
 */
public interface HRefinement extends Refinement, PiMMVisitable {

	/**
	 * Returns the value of the '<em><b>Loop Prototype</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Loop Prototype</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Loop Prototype</em>' reference.
	 * @see #setLoopPrototype(FunctionPrototype)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getHRefinement_LoopPrototype()
	 * @model required="true"
	 * @generated
	 */
	FunctionPrototype getLoopPrototype();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.HRefinement#getLoopPrototype <em>Loop Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Loop Prototype</em>' reference.
	 * @see #getLoopPrototype()
	 * @generated
	 */
	void setLoopPrototype(FunctionPrototype value);

	/**
	 * Returns the value of the '<em><b>Init Prototype</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Init Prototype</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Init Prototype</em>' reference.
	 * @see #setInitPrototype(FunctionPrototype)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getHRefinement_InitPrototype()
	 * @model
	 * @generated
	 */
	FunctionPrototype getInitPrototype();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.HRefinement#getInitPrototype <em>Init Prototype</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Init Prototype</em>' reference.
	 * @see #getInitPrototype()
	 * @generated
	 */
	void setInitPrototype(FunctionPrototype value);
} // HRefinement
