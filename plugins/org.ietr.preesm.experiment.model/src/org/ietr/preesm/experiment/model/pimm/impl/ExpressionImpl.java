/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import com.singularsys.jep.ParseException;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Expression</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl#getString
 * <em>String</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ExpressionImpl extends EObjectImpl implements Expression {

	/**
	 * The default value of the '{@link #getString() <em>String</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getString()
	 * @generated
	 * @ordered
	 */
	protected static final String STRING_EDEFAULT = "0";
	/**
	 * The cached value of the '{@link #getString() <em>String</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getString()
	 * @generated
	 * @ordered
	 */
	protected String string = STRING_EDEFAULT;

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
	public String getString() {
		return string;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setString(String newString) {
		String oldString = string;
		string = newString;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PiMMPackage.EXPRESSION__STRING, oldString, string));
	}

	/**
	 * <!-- begin-user-doc --> Evaluate expression of the class for which it is
	 * called. <!-- end-user-doc -->
	 * 
	 * @return the result of the expression evaluated as an int.
	 */
	public String evaluate() {
		String allExpression = getString();
		Jep jep = new Jep();

		Parameterizable parameterizableObj;
		if (this.eContainer() instanceof Parameterizable) {
			parameterizableObj = (Parameterizable) this.eContainer();
		} else if (this.eContainer().eContainer() instanceof Parameterizable) {
			parameterizableObj = (Parameterizable) this.eContainer()
					.eContainer();
		} else {
			return "Neither a child of Parameterizable nor a child of a child of Parameterizable";
		}

		try {
			for (ConfigInputPort port : parameterizableObj
					.getConfigInputPorts()) {
				if (port.getIncomingDependency() != null
						&& port.getIncomingDependency().getSetter() instanceof Parameter) {
					Parameter p = (Parameter) port.getIncomingDependency()
							.getSetter();

					String parameterName;
					if (parameterizableObj instanceof Parameter
							|| parameterizableObj instanceof Delay
							|| parameterizableObj instanceof InterfaceActor)
						parameterName = p.getName();
					else
						parameterName = port.getName();

					String evaluatedParam = p.getExpression().evaluate();

					jep.addVariable(parameterName,
							Double.parseDouble(evaluatedParam));
				}
			}

			jep.parse(allExpression);

			String evaluation = jep.evaluate().toString();

			return evaluation;

		} catch (ParseException e) {
			return "Parsing Error, check expression syntax" + " : "
					+ allExpression;
		} catch (EvaluationException e) {
			return "Evaluation Error, check parameter dependencies" + " : "
					+ allExpression;
		} catch (JepException e) {
			return "Error in parameter subtitution" + " : " + allExpression;
		} catch (NumberFormatException e) {
			return "Evaluation Error, check parameter dependencies" + " : "
					+ allExpression;
		}

	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PiMMPackage.EXPRESSION__STRING:
			setString((String) newValue);
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
	public void eUnset(int featureID) {
		switch (featureID) {
		case PiMMPackage.EXPRESSION__STRING:
			setString(STRING_EDEFAULT);
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
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case PiMMPackage.EXPRESSION__STRING:
			return STRING_EDEFAULT == null ? string != null : !STRING_EDEFAULT
					.equals(string);
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
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (string: ");
		result.append(string);
		result.append(')');
		return result.toString();
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitExpression(this);
	}

} // ExpressionImpl
