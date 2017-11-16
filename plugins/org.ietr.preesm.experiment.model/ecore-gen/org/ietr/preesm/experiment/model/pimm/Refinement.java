/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.core.runtime.IPath;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Refinement</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath <em>File Path</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getRefinement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Refinement extends PiMMVisitable {
  /**
   * Returns the value of the '<em><b>File Path</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>File Path</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>File Path</em>' attribute.
   * @see #setFilePath(IPath)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getRefinement_FilePath()
   * @model dataType="org.ietr.preesm.experiment.model.pimm.IPath"
   * @generated
   */
  IPath getFilePath();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath <em>File Path</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>File Path</em>' attribute.
   * @see #getFilePath()
   * @generated
   */
  void setFilePath(IPath value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation"
   * @generated
   */
  AbstractActor getAbstractActor();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return (this.getFilePath() == null) ? null :
   *        this.getFilePath().lastSegment();'"
   * @generated
   */
  String getFileName();

} // Refinement
