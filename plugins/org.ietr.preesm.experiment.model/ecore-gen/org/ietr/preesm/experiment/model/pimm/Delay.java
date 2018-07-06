/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Delay</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo <em>Containing Fifo</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay()
 * @model
 * @generated
 */
public interface Delay extends Configurable, ExpressionHolder {
  /**
   * Returns the value of the '<em><b>Containing Fifo</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Fifo</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Containing Fifo</em>' container reference.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDelay_ContainingFifo()
   * @see org.ietr.preesm.experiment.model.pimm.Fifo#getDelay
   * @model opposite="delay" transient="false" changeable="false"
   * @generated
   */
  Fifo getContainingFifo();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getExpression();'"
   * @generated
   */
  Expression getSizeExpression();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.ietr.preesm.experiment.model.pimm.Graph%&gt; _containingGraph = this.getContainingFifo().getContainingGraph();\nreturn ((&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;) _containingGraph);'"
   * @generated
   */
  PiGraph getContainingGraph();

} // Delay
