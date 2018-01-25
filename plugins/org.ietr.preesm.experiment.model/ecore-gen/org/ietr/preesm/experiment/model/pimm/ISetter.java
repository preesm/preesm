/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>ISetter</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getISetter()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ISetter extends EObject {
  /**
   * Returns the value of the '<em><b>Outgoing Dependencies</b></em>' reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Dependency}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getSetter <em>Setter</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Outgoing Dependencies</em>' reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Outgoing Dependencies</em>' reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getISetter_OutgoingDependencies()
   * @see org.ietr.preesm.experiment.model.pimm.Dependency#getSetter
   * @model opposite="setter"
   * @generated
   */
  EList<Dependency> getOutgoingDependencies();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation"
   * @generated
   */
  boolean isLocallyStatic();

} // ISetter
