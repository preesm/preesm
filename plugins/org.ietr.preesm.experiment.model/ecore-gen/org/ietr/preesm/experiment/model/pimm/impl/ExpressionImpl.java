/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ExpressionHolder;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl#getHolder <em>Holder</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl#getExpressionString <em>Expression String</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ExpressionImpl extends MinimalEObjectImpl.Container implements Expression {
  /**
   * The default value of the '{@link #getExpressionString() <em>Expression String</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpressionString()
   * @generated
   * @ordered
   */
  protected static final String EXPRESSION_STRING_EDEFAULT = "0";

  /**
   * The cached value of the '{@link #getExpressionString() <em>Expression String</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpressionString()
   * @generated
   * @ordered
   */
  protected String expressionString = EXPRESSION_STRING_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ExpressionImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionHolder getHolder() {
    if (eContainerFeatureID() != PiMMPackage.EXPRESSION__HOLDER) return null;
    return (ExpressionHolder)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExpressionHolder basicGetHolder() {
    if (eContainerFeatureID() != PiMMPackage.EXPRESSION__HOLDER) return null;
    return (ExpressionHolder)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetHolder(ExpressionHolder newHolder, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newHolder, PiMMPackage.EXPRESSION__HOLDER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setHolder(ExpressionHolder newHolder) {
    if (newHolder != eInternalContainer() || (eContainerFeatureID() != PiMMPackage.EXPRESSION__HOLDER && newHolder != null)) {
      if (EcoreUtil.isAncestor(this, newHolder))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newHolder != null)
        msgs = ((InternalEObject)newHolder).eInverseAdd(this, PiMMPackage.EXPRESSION_HOLDER__EXPRESSION, ExpressionHolder.class, msgs);
      msgs = basicSetHolder(newHolder, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EXPRESSION__HOLDER, newHolder, newHolder));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getExpressionString() {
    return expressionString;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setExpressionString(String newExpressionString) {
    String oldExpressionString = expressionString;
    expressionString = newExpressionString;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EXPRESSION__EXPRESSION_STRING, oldExpressionString, expressionString));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__HOLDER:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetHolder((ExpressionHolder)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__HOLDER:
        return basicSetHolder(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case PiMMPackage.EXPRESSION__HOLDER:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.EXPRESSION_HOLDER__EXPRESSION, ExpressionHolder.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__HOLDER:
        if (resolve) return getHolder();
        return basicGetHolder();
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        return getExpressionString();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__HOLDER:
        setHolder((ExpressionHolder)newValue);
        return;
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        setExpressionString((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__HOLDER:
        setHolder((ExpressionHolder)null);
        return;
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        setExpressionString(EXPRESSION_STRING_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case PiMMPackage.EXPRESSION__HOLDER:
        return basicGetHolder() != null;
      case PiMMPackage.EXPRESSION__EXPRESSION_STRING:
        return EXPRESSION_STRING_EDEFAULT == null ? expressionString != null : !EXPRESSION_STRING_EDEFAULT.equals(expressionString);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (expressionString: ");
    result.append(expressionString);
    result.append(')');
    return result.toString();
  }

} //ExpressionImpl
