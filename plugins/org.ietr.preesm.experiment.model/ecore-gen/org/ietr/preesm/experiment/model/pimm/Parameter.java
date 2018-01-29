/**
 */
package org.ietr.preesm.experiment.model.pimm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Parameter</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Parameter#getValueExpression <em>Value Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Parameter#getContainingGraph <em>Containing Graph</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends Configurable, ISetter {
  /**
   * Returns the value of the '<em><b>Value Expression</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value Expression</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Value Expression</em>' containment reference.
   * @see #setValueExpression(Expression)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter_ValueExpression()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getValueExpression();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Parameter#getValueExpression <em>Value Expression</em>}' containment reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Value Expression</em>' containment reference.
   * @see #getValueExpression()
   * @generated
   */
  void setValueExpression(Expression value);

  /**
   * Returns the value of the '<em><b>Containing Graph</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getParameters <em>Parameters</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Graph</em>' container reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Containing Graph</em>' container reference.
   * @see #setContainingGraph(PiGraph)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter_ContainingGraph()
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getParameters
   * @model opposite="parameters" transient="false"
   * @generated
   */
  PiGraph getContainingGraph();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Parameter#getContainingGraph <em>Containing Graph</em>}' container reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Containing Graph</em>' container reference.
   * @see #getContainingGraph()
   * @generated
   */
  void setContainingGraph(PiGraph value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='// a parameter is static if all its setters are static (or it has no
   *        setter)\nreturn getConfigInputPorts().stream().filter(Objects::nonNull).map(ConfigInputPort::getIncomingDependency).filter(Objects::nonNull)\n
   *        .map(Dependency::getSetter).filter(Objects::nonNull).allMatch(ISetter::isLocallyStatic);'"
   * @generated
   */
  @Override
  boolean isLocallyStatic();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return !getConfigInputPorts().isEmpty();'"
   * @generated
   */
  boolean isDependent();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return false;'"
   * @generated
   */
  boolean isConfigurationInterface();

} // Parameter
