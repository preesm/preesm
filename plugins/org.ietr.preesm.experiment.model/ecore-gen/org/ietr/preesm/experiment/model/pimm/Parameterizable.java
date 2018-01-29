/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Parameterizable</b></em>'. <!-- end-user-doc -->
 *
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getParameterizable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Parameterizable extends EObject {
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation"
   * @generated
   */
  EList<Parameter> getInputParameters();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='// a Parameterizable is static if all its parameters are static (or it
   *        has no parameter)\nreturn getInputParameters().stream().filter(Objects::nonNull).allMatch(Parameter::isLocallyStatic);'"
   * @generated
   */
  boolean isLocallyStatic();

} // Parameterizable
