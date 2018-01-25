/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Expression</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl#getExpressionString <em>Expression String</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ExpressionImpl extends EObjectImpl implements Expression {
  /**
   * The default value of the '{@link #getExpressionString() <em>Expression String</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getExpressionString()
   * @generated
   * @ordered
   */
  protected static final String EXPRESSION_STRING_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getExpressionString() <em>Expression String</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getExpressionString()
   * @generated
   * @ordered
   */
  protected String expressionString = ExpressionImpl.EXPRESSION_STRING_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ExpressionImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.EXPRESSION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getExpressionString() {
    return this.expressionString;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setExpressionString(final String newExpressionString) {
    final String oldExpressionString = this.expressionString;
    this.expressionString = newExpressionString;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EXPRESSION__EXPRESSION_STRING, oldExpressionString, this.expressionString));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        return getExpressionString();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        setExpressionString((String) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        setExpressionString(ExpressionImpl.EXPRESSION_STRING_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        return ExpressionImpl.EXPRESSION_STRING_EDEFAULT == null ? this.expressionString != null
            : !ExpressionImpl.EXPRESSION_STRING_EDEFAULT.equals(this.expressionString);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuffer result = new StringBuffer(super.toString());
    result.append(" (expressionString: ");
    result.append(this.expressionString);
    result.append(')');
    return result.toString();
  }

} // ExpressionImpl
