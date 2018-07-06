/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Config Input Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable <em>Configurable</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort()
 * @model
 * @generated
 */
public interface ConfigInputPort extends Port {
  /**
   * Returns the value of the '<em><b>Incoming Dependency</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Incoming Dependency</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Incoming Dependency</em>' reference.
   * @see #setIncomingDependency(Dependency)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort_IncomingDependency()
   * @see org.ietr.preesm.experiment.model.pimm.Dependency#getGetter
   * @model opposite="getter"
   * @generated
   */
  Dependency getIncomingDependency();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Incoming Dependency</em>' reference.
   * @see #getIncomingDependency()
   * @generated
   */
  void setIncomingDependency(Dependency value);

  /**
   * Returns the value of the '<em><b>Configurable</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Configurable#getConfigInputPorts <em>Config Input Ports</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Configurable</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Configurable</em>' container reference.
   * @see #setConfigurable(Configurable)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigInputPort_Configurable()
   * @see org.ietr.preesm.experiment.model.pimm.Configurable#getConfigInputPorts
   * @model opposite="configInputPorts" transient="false"
   * @generated
   */
  Configurable getConfigurable();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable <em>Configurable</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Configurable</em>' container reference.
   * @see #getConfigurable()
   * @generated
   */
  void setConfigurable(Configurable value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.ietr.preesm.experiment.model.pimm.PortKind%&gt;.CFG_INPUT;'"
   * @generated
   */
  PortKind getKind();

} // ConfigInputPort
