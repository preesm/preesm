/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder#getExpression <em>Expression</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getExpressionHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ExpressionHolder extends Parameterizable {
  /**
   * Returns the value of the '<em><b>Expression</b></em>' containment reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Expression#getHolder <em>Holder</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression</em>' containment reference.
   * @see #setExpression(Expression)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getExpressionHolder_Expression()
   * @see org.ietr.preesm.experiment.model.pimm.Expression#getHolder
   * @model opposite="holder" containment="true"
   * @generated
   */
  Expression getExpression();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder#getExpression <em>Expression</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression</em>' containment reference.
   * @see #getExpression()
   * @generated
   */
  void setExpression(Expression value);

} // ExpressionHolder
