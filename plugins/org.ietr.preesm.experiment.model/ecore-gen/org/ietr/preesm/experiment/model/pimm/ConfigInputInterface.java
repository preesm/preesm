/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Config Input Interface</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.ConfigInputInterface#getGraphPort <em>Graph Port</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputInterface()
 * @model
 * @generated
 */
public interface ConfigInputInterface extends Parameter {
  /**
   * Returns the value of the '<em><b>Graph Port</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Graph Port</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Graph Port</em>' reference.
   * @see #setGraphPort(ConfigInputPort)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputInterface_GraphPort()
   * @model
   * @generated
   */
  ConfigInputPort getGraphPort();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputInterface#getGraphPort <em>Graph Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Graph Port</em>' reference.
   * @see #getGraphPort()
   * @generated
   */
  void setGraphPort(ConfigInputPort value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return true;'"
   * @generated
   */
  boolean isLocallyStatic();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return true;'"
   * @generated
   */
  boolean isConfigurationInterface();

} // ConfigInputInterface
