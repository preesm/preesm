/**
 */
package org.ietr.preesm.experiment.model.pimm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Data Port</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.DataPort#getPortRateExpression <em>Port Rate Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort()
 * @model abstract="true"
 * @generated
 */
public interface DataPort extends Port {
  /**
   * Returns the value of the '<em><b>Port Rate Expression</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Port Rate Expression</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Port Rate Expression</em>' containment reference.
   * @see #setPortRateExpression(Expression)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort_PortRateExpression()
   * @model containment="true" required="true"
   * @generated
   */
  Expression getPortRateExpression();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getPortRateExpression <em>Port Rate Expression</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Port Rate Expression</em>' containment reference.
   * @see #getPortRateExpression()
   * @generated
   */
  void setPortRateExpression(Expression value);

  /**
   * Returns the value of the '<em><b>Annotation</b></em>' attribute. The literals are from the enumeration
   * {@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Annotation</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Annotation</em>' attribute.
   * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
   * @see #setAnnotation(PortMemoryAnnotation)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort_Annotation()
   * @model
   * @generated
   */
  PortMemoryAnnotation getAnnotation();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Annotation</em>' attribute.
   * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
   * @see #getAnnotation()
   * @generated
   */
  void setAnnotation(PortMemoryAnnotation value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='if (eContainer() instanceof AbstractActor) {\n return (AbstractActor)
   *        eContainer();\n}\nreturn null;'"
   * @generated
   */
  AbstractActor getContainingActor();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" required="true" annotation="http://www.eclipse.org/emf/2002/GenModel body='final String actorName =
   *        Optional.ofNullable(getContainingActor()).map(AbstractVertex::getName).orElseThrow(() -&gt; new PiGraphException(\"Data port \" + this + \" is not
   *        contained in an AbstracytActor.\"));\nfinal String portName = Optional.ofNullable(getName()).filter(s -&gt; !s.isEmpty()).map(s -&gt; \".\" +
   *        s).orElse(\"\");\nreturn actorName + portName;'"
   * @generated
   */
  String getId();

} // DataPort
