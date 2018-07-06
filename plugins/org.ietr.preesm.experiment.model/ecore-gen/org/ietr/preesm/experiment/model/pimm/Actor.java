/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.core.runtime.IPath;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Actor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor()
 * @model
 * @generated
 */
public interface Actor extends ExecutableActor {
  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Refinement</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(Refinement)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_Refinement()
   * @model containment="true"
   * @generated
   */
  Refinement getRefinement();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(Refinement value);

  /**
   * Returns the value of the '<em><b>Memory Script Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Memory Script Path</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Memory Script Path</em>' attribute.
   * @see #setMemoryScriptPath(IPath)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_MemoryScriptPath()
   * @model unique="false" dataType="org.ietr.preesm.experiment.model.pimm.IPath"
   * @generated
   */
  IPath getMemoryScriptPath();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Memory Script Path</em>' attribute.
   * @see #getMemoryScriptPath()
   * @generated
   */
  void setMemoryScriptPath(IPath value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigOutputPort%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigOutputPort%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;&gt;()\n{\n\tpublic &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.ConfigOutputPort%&gt; it)\n\t{\n\t\treturn it.getOutgoingDependencies();\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function_1 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; it)\n\t{\n\t\tboolean _isEmpty = it.isEmpty();\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf((!_isEmpty));\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; _function_2 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; apply(final &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt; it)\n\t{\n\t\treturn it.get(0);\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt; _function_3 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;()\n{\n\tpublic &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt; it)\n\t{\n\t\treturn it.getGetter();\n\t}\n};\nfinal &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function_4 = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(true);\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;exists(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.ConfigInputPort%&gt;&gt;map(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;, &lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;map(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;&gt;filter(&lt;%org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.ConfigOutputPort%&gt;, &lt;%org.eclipse.emf.common.util.EList%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Dependency%&gt;&gt;&gt;map(this.getConfigOutputPorts(), _function), _function_1), _function_2), _function_3), _function_4);'"
   * @generated
   */
  boolean isConfigurationActor();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return this.getRefinement().isHierarchical();'"
   * @generated
   */
  boolean isHierarchical();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='&lt;%org.ietr.preesm.experiment.model.pimm.Refinement%&gt; _refinement = this.getRefinement();\nboolean _tripleEquals = (_refinement == null);\nif (_tripleEquals)\n{\n\treturn null;\n}\nelse\n{\n\treturn this.getRefinement().getAbstractActor();\n}'"
   * @generated
   */
  AbstractActor getChildAbstractActor();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='boolean _isHierarchical = this.isHierarchical();\nif (_isHierarchical)\n{\n\t&lt;%org.ietr.preesm.experiment.model.pimm.AbstractActor%&gt; _childAbstractActor = this.getChildAbstractActor();\n\treturn ((&lt;%org.ietr.preesm.experiment.model.pimm.PiGraph%&gt;) _childAbstractActor);\n}\nelse\n{\n\tthrow new &lt;%java.lang.UnsupportedOperationException%&gt;(\"Cannot get the subgraph of a non hierarchical actor.\");\n}'"
   * @generated
   */
  PiGraph getSubGraph();

} // Actor
