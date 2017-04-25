/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;
import org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code> method for each class of the model. <!--
 * end-user-doc -->
 * 
 * @see org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage
 * @generated
 */
public class VisitorAdapterFactory extends AdapterFactoryImpl {
  /**
   * The cached model package. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static VisitorPackage modelPackage;

  /**
   * Creates an instance of the adapter factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public VisitorAdapterFactory() {
    if (VisitorAdapterFactory.modelPackage == null) {
      VisitorAdapterFactory.modelPackage = VisitorPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object. <!-- begin-user-doc --> This implementation returns <code>true</code> if the object
   * is either the model's package or is an instance object of the model. <!-- end-user-doc -->
   * 
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(final Object object) {
    if (object == VisitorAdapterFactory.modelPackage) {
      return true;
    }
    if (object instanceof EObject) {
      return ((EObject) object).eClass().getEPackage() == VisitorAdapterFactory.modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected VisitorSwitch<Adapter> modelSwitch = new VisitorSwitch<Adapter>() {
    @Override
    public Adapter casePiMMVisitable(final PiMMVisitable object) {
      return createPiMMVisitableAdapter();
    }

    @Override
    public Adapter casePiMMVisitor(final PiMMVisitor object) {
      return createPiMMVisitorAdapter();
    }

    @Override
    public Adapter defaultCase(final EObject object) {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param target
   *          the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(final Notifier target) {
    return this.modelSwitch.doSwitch((EObject) target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable <em>Pi MM Visitable</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable
   * @generated
   */
  public Adapter createPiMMVisitableAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor <em>Pi MM Visitor</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to ignore a case when inheritance will catch
   * all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor
   * @generated
   */
  public Adapter createPiMMVisitorAdapter() {
    return null;
  }

  /**
   * Creates a new adapter for the default case. <!-- begin-user-doc --> This default implementation returns null. <!-- end-user-doc -->
   * 
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter() {
    return null;
  }

} // VisitorAdapterFactory
