/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Pi MM Visitable</b></em>'. <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage#getPiMMVisitable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface PiMMVisitable extends EObject {
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model subjectRequired="true"
   * @generated
   */
  void accept(PiMMVisitor subject);

} // PiMMVisitable
