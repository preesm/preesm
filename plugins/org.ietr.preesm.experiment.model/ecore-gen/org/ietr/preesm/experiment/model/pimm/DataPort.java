/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort()
 * @model abstract="true"
 * @generated
 */
public interface DataPort extends Port, ExpressionHolder {
  /**
   * Returns the value of the '<em><b>Annotation</b></em>' attribute.
   * The literals are from the enumeration {@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Annotation</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Annotation</em>' attribute.
   * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
   * @see #setAnnotation(PortMemoryAnnotation)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getDataPort_Annotation()
   * @model unique="false"
   * @generated
   */
  PortMemoryAnnotation getAnnotation();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Annotation</em>' attribute.
   * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
   * @see #getAnnotation()
   * @generated
   */
  void setAnnotation(PortMemoryAnnotation value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getExpression();'"
   * @generated
   */
  Expression getPortRateExpression();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.eclipse.emf.ecore.EObject%&gt; _eContainer = this.eContainer();\nif ((_eContainer instanceof &lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;))\n{\n\t&lt;%org.eclipse.emf.ecore.EObject%&gt; _eContainer_1 = this.eContainer();\n\treturn ((&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;) _eContainer_1);\n}\nreturn null;'"
   * @generated
   */
  AbstractActor getContainingActor();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;unmodifiableEList(this.getContainingActor().getInputParameters());'"
   * @generated
   */
  EList<Parameter> getInputParameters();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='try\n{\n\tfinal &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.String%&gt;&gt; _function = new &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;, &lt;%java.lang.String%&gt;&gt;()\n\t{\n\t\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt; it)\n\t\t{\n\t\t\treturn it.getName();\n\t\t}\n\t};\n\tfinal &lt;%java.util.function.Supplier%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt; _function_1 = new &lt;%java.util.function.Supplier%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt;()\n\t{\n\t\tpublic &lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt; get()\n\t\t{\n\t\t\treturn new &lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;(((\"Data port \" + &lt;%this%&gt;) + \" is not contained in an AbstracytActor.\"));\n\t\t}\n\t};\n\tfinal &lt;%java.lang.String%&gt; actorName = &lt;%java.util.Optional%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt;&gt;ofNullable(this.getContainingActor()).&lt;&lt;%java.lang.String%&gt;&gt;map(_function).&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt;orElseThrow(_function_1);\n\tfinal &lt;%java.util.function.Predicate%&gt;&lt;&lt;%java.lang.String%&gt;&gt; _function_2 = new &lt;%java.util.function.Predicate%&gt;&lt;&lt;%java.lang.String%&gt;&gt;()\n\t{\n\t\tpublic boolean test(final &lt;%java.lang.String%&gt; it)\n\t\t{\n\t\t\tboolean _isEmpty = it.isEmpty();\n\t\t\treturn (!_isEmpty);\n\t\t}\n\t};\n\tfinal &lt;%java.util.function.Function%&gt;&lt;&lt;%java.lang.String%&gt;, &lt;%java.lang.String%&gt;&gt; _function_3 = new &lt;%java.util.function.Function%&gt;&lt;&lt;%java.lang.String%&gt;, &lt;%java.lang.String%&gt;&gt;()\n\t{\n\t\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%java.lang.String%&gt; it)\n\t\t{\n\t\t\treturn (\".\" + it);\n\t\t}\n\t};\n\tfinal &lt;%java.lang.String%&gt; portName = &lt;%java.util.Optional%&gt;.&lt;&lt;%java.lang.String%&gt;&gt;ofNullable(this.getName()).filter(_function_2).&lt;&lt;%java.lang.String%&gt;&gt;map(_function_3).orElse(\"\");\n\treturn (actorName + portName);\n}\ncatch (Throwable _e)\n{\n\tthrow org.eclipse.xtext.xbase.lib.Exceptions.sneakyThrow(_e);\n}'"
   * @generated
   */
  String getId();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Fifo getFifo();

} // DataPort
