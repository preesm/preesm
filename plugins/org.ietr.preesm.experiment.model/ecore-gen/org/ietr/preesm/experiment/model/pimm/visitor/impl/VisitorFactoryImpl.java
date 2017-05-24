/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.ietr.preesm.experiment.model.pimm.visitor.VisitorFactory;
import org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class VisitorFactoryImpl extends EFactoryImpl implements VisitorFactory {
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static VisitorFactory init() {
    try {
      final VisitorFactory theVisitorFactory = (VisitorFactory) EPackage.Registry.INSTANCE.getEFactory(VisitorPackage.eNS_URI);
      if (theVisitorFactory != null) {
        return theVisitorFactory;
      }
    } catch (final Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new VisitorFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public VisitorFactoryImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EObject create(final EClass eClass) {
    switch (eClass.getClassifierID()) {
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public VisitorPackage getVisitorPackage() {
    return (VisitorPackage) getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static VisitorPackage getPackage() {
    return VisitorPackage.eINSTANCE;
  }

} // VisitorFactoryImpl
