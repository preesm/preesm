/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model. <!-- end-user-doc -->
 * 
 * @see org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage
 * @generated
 */
public interface VisitorFactory extends EFactory {
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  VisitorFactory eINSTANCE = org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorFactoryImpl.init();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  VisitorPackage getVisitorPackage();

} // VisitorFactory
