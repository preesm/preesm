/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameterizable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * ************************************
 *  * PiSDF Specification
 *  *************************************
 * <!-- end-model-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameterizable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Parameterizable extends EObject {
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Parameter> getInputParameters();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.Boolean%&gt;&gt; _function = new &lt;%org.eclipse.xtext.xbase.lib.Functions.Function1%&gt;&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;, &lt;%java.lang.Boolean%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.Boolean%&gt; apply(final &lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt; it)\n\t{\n\t\treturn &lt;%java.lang.Boolean%&gt;.valueOf(it.isLocallyStatic());\n\t}\n};\nreturn &lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;forall(&lt;%org.eclipse.xtext.xbase.lib.IterableExtensions%&gt;.&lt;&lt;%org.ietr.preesm.experiment.model.pimm.Parameter%&gt;&gt;filterNull(this.getInputParameters()), _function);'"
   * @generated
   */
  boolean isLocallyStatic();

} // Parameterizable
