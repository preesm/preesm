/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 *
 * @see org.ietr.preesm.experiment.model.pimm.visitor.VisitorFactory
 * @model kind="package"
 * @generated
 */
public interface VisitorPackage extends EPackage {
  /**
   * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNAME = "visitor";

  /**
   * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_URI = "http://org.ietr.preesm/experiment/model/pimm/visitor";

  /**
   * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  String eNS_PREFIX = "org.ietr.preesm.experiment.pimm.visitor";

  /**
   * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  VisitorPackage eINSTANCE = org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorPackageImpl.init();

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable <em>Pi MM Visitable</em>}' class. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable
   * @see org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorPackageImpl#getPiMMVisitable()
   * @generated
   */
  int PI_MM_VISITABLE = 0;

  /**
   * The number of structural features of the '<em>Pi MM Visitable</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PI_MM_VISITABLE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.visitor.impl.PiMMVisitorImpl <em>Pi MM Visitor</em>}' class. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @see org.ietr.preesm.experiment.model.pimm.visitor.impl.PiMMVisitorImpl
   * @see org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorPackageImpl#getPiMMVisitor()
   * @generated
   */
  int PI_MM_VISITOR = 1;

  /**
   * The number of structural features of the '<em>Pi MM Visitor</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   * @ordered
   */
  int PI_MM_VISITOR_FEATURE_COUNT = 0;

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable <em>Pi MM Visitable</em>}'. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @return the meta object for class '<em>Pi MM Visitable</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable
   * @generated
   */
  EClass getPiMMVisitable();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor <em>Pi MM Visitor</em>}'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @return the meta object for class '<em>Pi MM Visitor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor
   * @generated
   */
  EClass getPiMMVisitor();

  /**
   * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the factory that creates the instances of the model.
   * @generated
   */
  VisitorFactory getVisitorFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   *
   * @generated
   */
  interface Literals {
    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable <em>Pi MM Visitable</em>}' class. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable
     * @see org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorPackageImpl#getPiMMVisitable()
     * @generated
     */
    EClass PI_MM_VISITABLE = VisitorPackage.eINSTANCE.getPiMMVisitable();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.visitor.impl.PiMMVisitorImpl <em>Pi MM Visitor</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     *
     * @see org.ietr.preesm.experiment.model.pimm.visitor.impl.PiMMVisitorImpl
     * @see org.ietr.preesm.experiment.model.pimm.visitor.impl.VisitorPackageImpl#getPiMMVisitor()
     * @generated
     */
    EClass PI_MM_VISITOR = VisitorPackage.eINSTANCE.getPiMMVisitor();

  }

} // VisitorPackage
