/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
 * Romina Racca <romina.racca@gmail.com> (2013)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
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
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#isConfigurationInterface <em>Configuration Interface</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getGraphPort <em>Graph Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ParameterImpl extends AbstractVertexImpl implements Parameter {
	/**
	 * The cached value of the '{@link #getOutgoingDependencies() <em>Outgoing Dependencies</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutgoingDependencies()
	 * @generated
	 * @ordered
	 */
	protected EList<Dependency> outgoingDependencies;

	protected static final boolean LOCALLY_STATIC_EDEFAULT = false;

	/**
	 * The default value of the '{@link #isConfigurationInterface() <em>Configuration Interface</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfigurationInterface()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CONFIGURATION_INTERFACE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isConfigurationInterface() <em>Configuration Interface</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfigurationInterface()
	 * @generated
	 * @ordered
	 */
	protected boolean configurationInterface = CONFIGURATION_INTERFACE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getGraphPort()
	 * @generated
	 * @ordered
	 */
	protected ConfigInputPort graphPort;

	/**
	 * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getExpression()
	 * @generated
	 * @ordered
	 */
	protected Expression expression;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	protected ParameterImpl() {
		super();		
		this.setExpression(PiMMFactory.eINSTANCE.createExpression());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.PARAMETER;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Dependency> getOutgoingDependencies() {
		if (outgoingDependencies == null) {
			outgoingDependencies = new EObjectWithInverseResolvingEList<Dependency>(Dependency.class, this, PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES, PiMMPackage.DEPENDENCY__SETTER);
		}
		return outgoingDependencies;
	}

	/**
	 * <!-- begin-user-doc --> Check whether the {@link Parameter} is a locally
	 * static {@link Parameter} or a dynamically configurable {@link Parameter}.<br>
	 * <br>
	 * A {@link Parameter} is locally static if its value only depends on
	 * locally static {@link Parameter}s. If the value of a {@link Parameter}
	 * depends on a {@link Actor#isConfigurationActor() configuration actor} or
	 * a configurable {@link Parameter}, the {@link Parameter} becomes a
	 * configurable {@link Parameter}. <br>
	 * <br>
	 * <b>This method should only be called on an acyclic {@link Dependency}
	 * tree otherwise the call will result in an infinite loop. Use
	 * {@link DependencyCycleDetector} to check that the {@link Dependency} tree
	 * is acyclic.</b>
	 * 
	 * @return <code>true</code> if the {@link Parameter} is locally static,
	 *         <code>false</code> if the {@link Parameter} is configurable. <!--
	 *         end-user-doc -->
	 * 
	 */
	@Override
	public boolean isLocallyStatic() {

		// Retrieve all incoming dependencies
		List<ConfigInputPort> ports = getConfigInputPorts();
		for (ConfigInputPort port : ports) {
			if (port.getIncomingDependency() != null) {
				// For each dependency, check if the setter is configurable or
				// an actor
				ISetter setter = port.getIncomingDependency().getSetter();
				if (setter instanceof ConfigOutputPort) {
					// The setter is an actor, the parameter is configurable
					return false;
				}

				if (setter instanceof Parameter
						&& !((Parameter) setter).isLocallyStatic()) {
					// The setter is a configurable parameter
					return false;
				}

				if (!(setter instanceof ConfigOutputPort)
						&& !(setter instanceof Parameter)) {
					throw new RuntimeException(
							"Can not check if the parameter is locally static"
									+ " because the setter class is unknown.");
				}
			}
		}

		// If this code is reached, the parameter is locally static
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public boolean isDependent() {
		return !this.getConfigInputPorts().isEmpty();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isConfigurationInterface() {
		return configurationInterface;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConfigurationInterface(boolean newConfigurationInterface) {
		boolean oldConfigurationInterface = configurationInterface;
		configurationInterface = newConfigurationInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE, oldConfigurationInterface, configurationInterface));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ConfigInputPort getGraphPort() {
		if (graphPort != null && graphPort.eIsProxy()) {
			InternalEObject oldGraphPort = (InternalEObject)graphPort;
			graphPort = (ConfigInputPort)eResolveProxy(oldGraphPort);
			if (graphPort != oldGraphPort) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.PARAMETER__GRAPH_PORT, oldGraphPort, graphPort));
			}
		}
		return graphPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ConfigInputPort basicGetGraphPort() {
		return graphPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGraphPort(ConfigInputPort newGraphPort) {
		ConfigInputPort oldGraphPort = graphPort;
		graphPort = newGraphPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__GRAPH_PORT, oldGraphPort, graphPort));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Expression getExpression() {
		return expression;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExpression(Expression newExpression,
			NotificationChain msgs) {
		Expression oldExpression = expression;
		expression = newExpression;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__EXPRESSION, oldExpression, newExpression);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpression(Expression newExpression) {
		if (newExpression != expression) {
			NotificationChain msgs = null;
			if (expression != null)
				msgs = ((InternalEObject)expression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__EXPRESSION, null, msgs);
			if (newExpression != null)
				msgs = ((InternalEObject)newExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__EXPRESSION, null, msgs);
			msgs = basicSetExpression(newExpression, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__EXPRESSION, newExpression, newExpression));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	@Override
	public int getValue() {
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingDependencies()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
				return ((InternalEList<?>)getOutgoingDependencies()).basicRemove(otherEnd, msgs);
			case PiMMPackage.PARAMETER__EXPRESSION:
				return basicSetExpression(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
				return getOutgoingDependencies();
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				return isConfigurationInterface();
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				if (resolve) return getGraphPort();
				return basicGetGraphPort();
			case PiMMPackage.PARAMETER__EXPRESSION:
				return getExpression();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
				getOutgoingDependencies().clear();
				getOutgoingDependencies().addAll((Collection<? extends Dependency>)newValue);
				return;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				setConfigurationInterface((Boolean)newValue);
				return;
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				setGraphPort((ConfigInputPort)newValue);
				return;
			case PiMMPackage.PARAMETER__EXPRESSION:
				setExpression((Expression)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
				getOutgoingDependencies().clear();
				return;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				setConfigurationInterface(CONFIGURATION_INTERFACE_EDEFAULT);
				return;
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				setGraphPort((ConfigInputPort)null);
				return;
			case PiMMPackage.PARAMETER__EXPRESSION:
				setExpression((Expression)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
				return outgoingDependencies != null && !outgoingDependencies.isEmpty();
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				return configurationInterface != CONFIGURATION_INTERFACE_EDEFAULT;
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				return graphPort != null;
			case PiMMPackage.PARAMETER__EXPRESSION:
				return expression != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == ISetter.class) {
			switch (derivedFeatureID) {
				case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES: return PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	@Override
	public void setName(String newName) {
		super.setName(newName);
		if (isConfigurationInterface() && getGraphPort() != null) {
			this.getGraphPort().setName(newName);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == ISetter.class) {
			switch (baseFeatureID) {
				case PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES: return PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (configurationInterface: ");
		result.append(configurationInterface);
		result.append(')');
		return result.toString();
	}

	@Override
	public void accept(PiMMVisitor v) {
		v.visitParameter(this);
	}

} // ParameterImpl
