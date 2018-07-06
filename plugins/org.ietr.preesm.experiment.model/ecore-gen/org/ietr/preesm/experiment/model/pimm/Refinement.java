/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Refinement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath <em>File Path</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getRefinement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Refinement extends EObject {
  /**
   * Returns the value of the '<em><b>File Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>File Path</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>File Path</em>' attribute.
   * @see #setFilePath(IPath)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getRefinement_FilePath()
   * @model unique="false" dataType="org.ietr.preesm.experiment.model.pimm.IPath"
   * @generated
   */
  IPath getFilePath();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath <em>File Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>File Path</em>' attribute.
   * @see #getFilePath()
   * @generated
   */
  void setFilePath(IPath value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='return &lt;%org.ietr.preesm.experiment.model.pimm.util.RefinementResolver%&gt;.resolveAbstractActor(this);'"
   * @generated
   */
  AbstractActor getAbstractActor();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" dataType="org.ietr.preesm.experiment.model.pimm.String" unique="false"
   *        annotation="http://www.eclipse.org/emf/2002/GenModel body='final &lt;%java.util.function.Function%&gt;&lt;&lt;%org.eclipse.core.runtime.IPath%&gt;, &lt;%java.lang.String%&gt;&gt; _function = new &lt;%java.util.function.Function%&gt;&lt;&lt;%org.eclipse.core.runtime.IPath%&gt;, &lt;%java.lang.String%&gt;&gt;()\n{\n\tpublic &lt;%java.lang.String%&gt; apply(final &lt;%org.eclipse.core.runtime.IPath%&gt; it)\n\t{\n\t\treturn it.lastSegment();\n\t}\n};\nreturn &lt;%java.util.Optional%&gt;.&lt;&lt;%org.eclipse.core.runtime.IPath%&gt;&gt;ofNullable(this.getFilePath()).&lt;&lt;%java.lang.String%&gt;&gt;map(_function).orElse(null);'"
   * @generated
   */
  String getFileName();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  boolean isHierarchical();

} // Refinement
