/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Config Output Port</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigOutputPort()
 * @model
 * @generated
 */
public interface ConfigOutputPort extends DataOutputPort, ISetter {
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return false;'"
   * @generated
   */
  boolean isLocallyStatic();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.ietr.preesm.experiment.model.pimm.PortKind%&gt;.CFG_OUTPUT;'"
   * @generated
   */
  PortKind getKind();

} // ConfigOutputPort
