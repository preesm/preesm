/**
 */
package org.ietr.preesm.experiment.model.pimm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Delay</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Delay#getSizeExpression <em>Size Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo <em>Containing Fifo</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay()
 * @model
 * @generated
 */
public interface Delay extends Configurable {
  /**
   * Returns the value of the '<em><b>Size Expression</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Size Expression</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Size Expression</em>' containment reference.
   * @see #setSizeExpression(Expression)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay_SizeExpression()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getSizeExpression();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Delay#getSizeExpression <em>Size Expression</em>}' containment reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Size Expression</em>' containment reference.
   * @see #getSizeExpression()
   * @generated
   */
  void setSizeExpression(Expression value);

  /**
   * Returns the value of the '<em><b>Containing Fifo</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Fifo</em>' container reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Containing Fifo</em>' container reference.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay_ContainingFifo()
   * @see org.ietr.preesm.experiment.model.pimm.Fifo#getDelay
   * @model opposite="delay" required="true" transient="false" changeable="false"
   * @generated
   */
  Fifo getContainingFifo();

} // Delay
