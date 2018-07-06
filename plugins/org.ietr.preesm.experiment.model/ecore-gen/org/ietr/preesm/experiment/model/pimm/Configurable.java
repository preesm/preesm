/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Configurable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Configurable#getConfigInputPorts <em>Config Input Ports</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigurable()
 * @model abstract="true"
 * @generated
 */
public interface Configurable extends AbstractVertex, Parameterizable {
  /**
   * Returns the value of the '<em><b>Config Input Ports</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort}.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable <em>Configurable</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Config Input Ports</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Config Input Ports</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getConfigurable_ConfigInputPorts()
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable
   * @model opposite="configurable" containment="true"
   * @generated
   */
  EList<ConfigInputPort> getConfigInputPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt; it)\n\t{\n\t\treturn it.getIncomingDependency();\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt; _function_1 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; it)\n\t{\n\t\treturn it.getSetter();\n\t}\n};\nreturn &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;unmodifiableEList(&lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;toEList(&lt;%com.google.common.collect.Iterables%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;filter(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt;&gt;map(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;filterNull(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;map(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;filterNull(this.getConfigInputPorts()), _function)), _function_1), &lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;.class)));'"
   * @generated
   */
  EList<Parameter> getInputParameters();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Lookup within all the ConfigInputPort and return the first one connected to the given
   * Parameter, or null if the actor is not connected to it.
   * <!-- end-model-doc -->
   * @model unique="false" parameterUnique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt; it)\n\t{\n\t\treturn it.getIncomingDependency();\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function_1 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; it)\n\t{\n\t\t&lt;%org.ietr.preesm.experiment.model.pimm.ISetter%&gt; _setter = it.getSetter();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf((_setter == parameter));\n\t}\n};\nfinal &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt; _function_2 = new &lt;%java.util.function.Function%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; it)\n\t{\n\t\treturn it.getGetter();\n\t}\n};\nreturn &lt;%java.util.Optional%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;ofNullable(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;findFirst(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;filterNull(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;map(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;filterNull(this.getConfigInputPorts()), _function)), _function_1)).&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;map(_function_2).orElse(null);'"
   * @generated
   */
  ConfigInputPort lookupConfigInputPortConnectedWithParameter(Parameter parameter);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;unmodifiableEList(this.getConfigInputPorts());'"
   * @generated
   */
  EList<Port> getAllConfigPorts();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.eclipse.emf.common.util.ECollections%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Port%&gt;&gt;unmodifiableEList(this.getAllConfigPorts());'"
   * @generated
   */
  EList<Port> getAllPorts();

} // Configurable
