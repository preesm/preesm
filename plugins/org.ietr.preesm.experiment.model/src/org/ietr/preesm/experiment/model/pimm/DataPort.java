/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.DataPort#getExpression <em>Expression</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort()
 * @model abstract="true"
 * @generated
 */
public interface DataPort extends Port, PiMMVisitable {
	/**
	 * Returns the value of the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expression</em>' containment reference.
	 * @see #setExpression(Expression)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort_Expression()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Expression getExpression();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getExpression <em>Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expression</em>' containment reference.
	 * @see #getExpression()
	 * @generated
	 */
	void setExpression(Expression value);

	/**
	 * Returns the value of the '<em><b>Annotation</b></em>' attribute.
	 * The literals are from the enumeration {@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Annotation</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Annotation</em>' attribute.
	 * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
	 * @see #setAnnotation(PortMemoryAnnotation)
	 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort_Annotation()
	 * @model
	 * @generated
	 */
	PortMemoryAnnotation getAnnotation();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Annotation</em>' attribute.
	 * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
	 * @see #getAnnotation()
	 * @generated
	 */
	void setAnnotation(PortMemoryAnnotation value);

} // DataPort
