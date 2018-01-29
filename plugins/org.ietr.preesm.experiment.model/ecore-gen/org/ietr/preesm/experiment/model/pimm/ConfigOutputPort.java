/**
 */
package org.ietr.preesm.experiment.model.pimm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Config Output Port</b></em>'. <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigOutputPort()
 * @model
 * @generated
 */
public interface ConfigOutputPort extends DataOutputPort, ISetter {
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='// config output ports are never considered static\nreturn false;'"
   * @generated
   */
  @Override
  boolean isLocallyStatic();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" required="true" annotation="http://www.eclipse.org/emf/2002/GenModel body='return PortKind.CFG_OUTPUT;'"
   * @generated
   */
  @Override
  PortKind getKind();

} // ConfigOutputPort
