/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Configurable</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Configurable#getConfigInputPorts <em>Config Input Ports</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigurable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Configurable extends Parameterizable {
  /**
   * Returns the value of the '<em><b>Config Input Ports</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable <em>Configurable</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Config Input Ports</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Config Input Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigurable_ConfigInputPorts()
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable
   * @model opposite="configurable" containment="true"
   * @generated
   */
  EList<ConfigInputPort> getConfigInputPorts();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final EList&lt;Parameter&gt; result = ECollections.newBasicEList();\nfor
   *        (final ConfigInputPort in : getConfigInputPorts()) {\n final ISetter setter = in.getIncomingDependency().getSetter();\n if (setter instanceof
   *        Parameter) {\n\tresult.add((Parameter) setter);\n }\n}\nreturn result;'"
   * @generated
   */
  @Override
  EList<Parameter> getInputParameters();

} // Configurable
