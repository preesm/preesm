/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Vertex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface AbstractVertex extends Vertex {
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getAbstractVertex_Name()
   * @model unique="false" dataType="org.ietr.preesm.experiment.model.pimm.String"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.ietr.preesm.experiment.model.pimm.Graph%&gt; _containingGraph = super.getContainingGraph();\nreturn ((&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;) _containingGraph);'"
   * @generated
   */
  PiGraph getContainingPiGraph();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Port> getAllPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model unique="false" portNameDataType="org.ietr.preesm.experiment.model.pimm.String" portNameUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf((((it.getName() == null) &amp;&amp; (portName == null)) || ((it.getName() != null) &amp;&amp; it.getName().equals(portName))));\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;findFirst(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;filterNull(this.getAllPorts()), _function);'"
   * @generated
   */
  Port lookupPort(String portName);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%java.lang.String%&gt; actorName = this.getName();\nfinal &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%java.lang.String%&gt;&gt; _function = new &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;, &lt;%java.lang.String%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt; it)\n\t{\n\t\t&lt;%java.lang.String%&gt; _vertexPath = it.getVertexPath();\n\t\t&lt;%java.lang.String%&gt; _plus = (_vertexPath + \"/\");\n\t\treturn (_plus + actorName);\n\t}\n};\nreturn &lt;%java.util.Optional%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;&gt;ofNullable(this.getContainingPiGraph()).&lt;&lt;%java.lang.String%&gt;&gt;map(_function).orElse(actorName);'"
   * @generated
   */
  String getVertexPath();

} // AbstractVertex
