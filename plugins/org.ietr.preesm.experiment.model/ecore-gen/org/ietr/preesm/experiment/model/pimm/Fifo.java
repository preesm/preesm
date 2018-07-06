/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Fifo</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo()
 * @model
 * @generated
 */
public interface Fifo extends Edge {
  /**
   * Returns the value of the '<em><b>Source Port</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source Port</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Port</em>' reference.
   * @see #setSourcePort(DataOutputPort)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_SourcePort()
   * @see org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo
   * @model opposite="outgoingFifo"
   * @generated
   */
  DataOutputPort getSourcePort();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Port</em>' reference.
   * @see #getSourcePort()
   * @generated
   */
  void setSourcePort(DataOutputPort value);

  /**
   * Returns the value of the '<em><b>Target Port</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo <em>Incoming Fifo</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target Port</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Port</em>' reference.
   * @see #setTargetPort(DataInputPort)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_TargetPort()
   * @see org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo
   * @model opposite="incomingFifo"
   * @generated
   */
  DataInputPort getTargetPort();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Port</em>' reference.
   * @see #getTargetPort()
   * @generated
   */
  void setTargetPort(DataInputPort value);

  /**
   * Returns the value of the '<em><b>Delay</b></em>' containment reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo <em>Containing Fifo</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Delay</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Delay</em>' containment reference.
   * @see #setDelay(Delay)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Delay()
   * @see org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo
   * @model opposite="containingFifo" containment="true"
   * @generated
   */
  Delay getDelay();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Delay</em>' containment reference.
   * @see #getDelay()
   * @generated
   */
  void setDelay(Delay value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute.
   * The default value is <code>"void"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' attribute.
   * @see #setType(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFifo_Type()
   * @model default="void" unique="false" dataType="org.ietr.preesm.experiment.model.pimm.String"
   * @generated
   */
  String getType();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' attribute.
   * @see #getType()
   * @generated
   */
  void setType(String value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return a {@link String} composed as follow:<br>
   * "&ltSourceName&gt[.&ltSourcePortName&gt]-&ltTargetName&gt[.&ltTargetPortName&gt]" <br>
   * <br>
   * This ID should be unique since each {@link Port} can only have one {@link Fifo} connected to them. Moreover, a {@link Port} with no name is always the
   * unique data {@link Port} of its owner.
   * <!-- end-model-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='try\n{\n\tfinal &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataOutputPort%&gt;, &lt;%java.lang.String%&gt;&gt; _function = new &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataOutputPort%&gt;, &lt;%java.lang.String%&gt;&gt;()\n\t{\n\t\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.DataOutputPort%&gt; it)\n\t\t{\n\t\t\treturn it.getId();\n\t\t}\n\t};\n\tfinal &lt;%java.util.function.Supplier%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt; _function_1 = new &lt;%java.util.function.Supplier%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt;()\n\t{\n\t\tpublic &lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt; get()\n\t\t{\n\t\t\treturn new &lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;(\"Fifo has no source port.\");\n\t\t}\n\t};\n\t&lt;%java.lang.String%&gt; _orElseThrow = &lt;%java.util.Optional%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataOutputPort%&gt;&gt;ofNullable(this.getSourcePort()).&lt;&lt;%java.lang.String%&gt;&gt;map(_function).&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt;orElseThrow(_function_1);\n\t&lt;%java.lang.String%&gt; _plus = (_orElseThrow + \n\t\t\"-\");\n\tfinal &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataInputPort%&gt;, &lt;%java.lang.String%&gt;&gt; _function_2 = new &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataInputPort%&gt;, &lt;%java.lang.String%&gt;&gt;()\n\t{\n\t\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.DataInputPort%&gt; it)\n\t\t{\n\t\t\treturn it.getId();\n\t\t}\n\t};\n\tfinal &lt;%java.util.function.Supplier%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt; _function_3 = new &lt;%java.util.function.Supplier%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt;()\n\t{\n\t\tpublic &lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt; get()\n\t\t{\n\t\t\treturn new &lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;(\"Fifo has no target port.\");\n\t\t}\n\t};\n\t&lt;%java.lang.String%&gt; _orElseThrow_1 = &lt;%java.util.Optional%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.DataInputPort%&gt;&gt;ofNullable(this.getTargetPort()).&lt;&lt;%java.lang.String%&gt;&gt;map(_function_2).&lt;&lt;%org.ietr.preesm.experiment.model.PiGraphException%&gt;&gt;orElseThrow(_function_3);\n\treturn (_plus + _orElseThrow_1);\n}\ncatch (Throwable _e)\n{\n\tthrow org.eclipse.xtext.xbase.lib.Exceptions.sneakyThrow(_e);\n}'"
   * @generated
   */
  String getId();

} // Fifo
