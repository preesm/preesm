/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.ParseException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl#getExpressionString <em>Expression String</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExpressionImpl extends EObjectImpl implements Expression {
	
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
	 * Evaluate expression of the class for which it is called.
	 * <!-- end-user-doc -->
	 * @return the result of the expression evaluated as an int.
	 */
	public int evaluate() {
		Float fl = null;
		Jep jep = new Jep();
		try {
			jep.parse(getExpressionString());
			String val = jep.evaluate().toString();
			fl = Float.valueOf(val);
		} catch (ParseException e) {
			e.printStackTrace();
		}catch (EvaluationException e) {
			e.printStackTrace();
		}
		return fl.intValue();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
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

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (expressionString: ");
		result.append(expressionString);
		result.append(')');
		return result.toString();
	}
	
} //ExpressionImpl
