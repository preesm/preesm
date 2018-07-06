/**
 */
package org.ietr.preesm.experiment.model.pimm;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends Vertex, Configurable, ISetter, ExpressionHolder {
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getExpression();'"
   * @generated
   */
  Expression getValueExpression();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt; it)\n\t{\n\t\treturn it.getIncomingDependency();\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt; _function_1 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; it)\n\t{\n\t\treturn it.getSetter();\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function_2 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(it.isLocallyStatic());\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt;forall(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt;map(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;map(this.getConfigInputPorts(), _function), _function_1), _function_2);'"
   * @generated
   */
  boolean isLocallyStatic();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='boolean _isEmpty = this.getConfigInputPorts().isEmpty();\nreturn (!_isEmpty);'"
   * @generated
   */
  boolean isDependent();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return false;'"
   * @generated
   */
  boolean isConfigurationInterface();

} // Parameter
