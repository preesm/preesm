/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Expression#getHolder <em>Holder</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Expression#getExpressionString <em>Expression String</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getExpression()
 * @model
 * @generated
 */
public interface Expression extends EObject {
  /**
   * Returns the value of the '<em><b>Holder</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Holder</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Holder</em>' container reference.
   * @see #setHolder(ExpressionHolder)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getExpression_Holder()
   * @see org.ietr.preesm.experiment.model.pimm.ExpressionHolder#getExpression
   * @model opposite="expression" transient="false"
   * @generated
   */
  ExpressionHolder getHolder();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Expression#getHolder <em>Holder</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Holder</em>' container reference.
   * @see #getHolder()
   * @generated
   */
  void setHolder(ExpressionHolder value);

  /**
   * Returns the value of the '<em><b>Expression String</b></em>' attribute.
   * The default value is <code>"0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Expression String</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Expression String</em>' attribute.
   * @see #setExpressionString(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getExpression_ExpressionString()
   * @model default="0" unique="false" dataType="org.ietr.preesm.experiment.model.pimm.String"
   * @generated
   */
  String getExpressionString();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Expression#getExpressionString <em>Expression String</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Expression String</em>' attribute.
   * @see #getExpressionString()
   * @generated
   */
  void setExpressionString(String value);

} // Expression
