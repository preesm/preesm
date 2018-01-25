/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.core.runtime.IPath;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Actor</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor()
 * @model
 * @generated
 */
public interface Actor extends ExecutableActor {
  /**
   * Returns the value of the '<em><b>Refinement</b></em>' containment reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Refinement</em>' containment reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Refinement</em>' containment reference.
   * @see #setRefinement(Refinement)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_Refinement()
   * @model containment="true" required="true"
   * @generated
   */
  Refinement getRefinement();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}' containment reference. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Refinement</em>' containment reference.
   * @see #getRefinement()
   * @generated
   */
  void setRefinement(Refinement value);

  /**
   * Returns the value of the '<em><b>Memory Script Path</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Memory Script Path</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Memory Script Path</em>' attribute.
   * @see #setMemoryScriptPath(IPath)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getActor_MemoryScriptPath()
   * @model dataType="org.ietr.preesm.experiment.model.pimm.IPath"
   * @generated
   */
  IPath getMemoryScriptPath();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Memory Script Path</em>' attribute.
   * @see #getMemoryScriptPath()
   * @generated
   */
  void setMemoryScriptPath(IPath value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='// an Actor is considered as a Configuration Actor iff it has at least a
   *        ConfigOutputPort that is connected to a getter\nreturn
   *        getConfigOutputPorts().stream().filter(Objects::nonNull).map(ConfigOutputPort::getOutgoingDependencies).filter(l -&gt; !l.isEmpty()).map(l -&gt;
   *        l.get(0))\n .map(Dependency::getGetter).filter(Objects::nonNull).anyMatch(x -&gt; true);'"
   * @generated
   */
  boolean isConfigurationActor();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return getRefinement().isHierarchical();'"
   * @generated
   */
  boolean isHierarchical();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        Optional.ofNullable(getRefinement()).map(Refinement::getAbstractActor).orElse(null);'"
   * @generated
   */
  AbstractActor getChildAbstractActor();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='if (isHierarchical()) {\n\treturn (PiGraph) getChildAbstractActor();\n}
   *        else {\n\tthrow new UnsupportedOperationException(\"Cannot get the subgraph of a non hierarchical actor.\");\n}'"
   * @generated
   */
  PiGraph getSubGraph();

} // Actor
