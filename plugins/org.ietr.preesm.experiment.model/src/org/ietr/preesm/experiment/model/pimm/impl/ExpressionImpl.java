/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
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
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl#getString <em>String</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ExpressionImpl extends EObjectImpl implements Expression {
	
	/**
	 * The default value of the '{@link #getString() <em>String</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getString()
	 * @generated
	 * @ordered
	 */
	protected static final String STRING_EDEFAULT = "0";
	/**
	 * The cached value of the '{@link #getString() <em>String</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getString()
	 * @generated
	 * @ordered
	 */
	protected String string = STRING_EDEFAULT;
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
	public String getString() {
		return string;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setString(String newString) {
		String oldString = string;
		string = newString;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EXPRESSION__STRING, oldString, string));
	}

	/**
	 * <!-- begin-user-doc -->
	 * Evaluate expression of the class for which it is called.
	 * <!-- end-user-doc -->
	 * @return the result of the expression evaluated as an int.
	 * @throws ParseException 
	 */
	public String evaluate(){
		String allExpression = getString();
		Jep jep = new Jep();
		
		Parameterizable parameterizableObj;
		if(this.eContainer() instanceof Parameterizable){
			parameterizableObj = (Parameterizable) this.eContainer();
		}else if(this.eContainer().eContainer() instanceof Parameterizable){
			parameterizableObj = (Parameterizable) this.eContainer().eContainer();
		}else{
			return "Neither a child of Parameterizable nor a child of a child of Parameterizable";
		}
		
		if(parameterizableObj.getConfigInputPorts().isEmpty()){

		}else{
			for (ConfigInputPort port : parameterizableObj.getConfigInputPorts()) {
				if(port.getIncomingDependency() != null
						&& port.getIncomingDependency().getSetter() instanceof Parameter){
					Parameter p = (Parameter) port.getIncomingDependency().getSetter();
					
					String parameterName;
					if(parameterizableObj instanceof Parameter || parameterizableObj instanceof Delay)
						parameterName = p.getName();
					else
						parameterName = port.getName();
					
					int startingIndex=0;
					String operators = "*+-/^";
					while(startingIndex<allExpression.length()){
						if(allExpression.substring(startingIndex).contains(parameterName)){
							int index = allExpression.substring(startingIndex).indexOf(parameterName) + startingIndex;
							
							// Verify that the parameter is surrounded by operators.
							if(index == 0 || operators.contains(""+allExpression.charAt(index-1))){
								if (index+parameterName.length() == allExpression.length() 
										|| operators.contains(""+allExpression.charAt(index+parameterName.length()))){
									
									String evaluatedParam;
									if(p.isConfigurationInterface())
										// TODO Handle config input interface
										evaluatedParam =  "0";
									else
										evaluatedParam =  p.getExpression().evaluate();
									allExpression = allExpression.substring(0, index) 
											+ allExpression.substring(index).replaceFirst(parameterName, "("+evaluatedParam+")");
								}
							}
							startingIndex = index+1;
						} else {
							break;
						}
					}
				}
			}
		}
		
		try {
			jep.parse(allExpression);
			return jep.evaluate().toString();
		} catch (ParseException e) {
//			e.printStackTrace();
			return "Parsing Error, check expression syntax"+" : "+allExpression;
		} catch (EvaluationException e) {
//			e.printStackTrace();
			return "Evaluation Error, check parameter dependecies"+" : "+allExpression; 
		}
				
	}
	
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.EXPRESSION__STRING:
				return getString();
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
			case PiMMPackage.EXPRESSION__STRING:
				setString((String)newValue);
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
			case PiMMPackage.EXPRESSION__STRING:
				setString(STRING_EDEFAULT);
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
			case PiMMPackage.EXPRESSION__STRING:
				return STRING_EDEFAULT == null ? string != null : !STRING_EDEFAULT.equals(string);
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
		result.append(" (string: ");
		result.append(string);
		result.append(')');
		return result.toString();
	}

	
	
} //ExpressionImpl
