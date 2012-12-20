/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#isLocallyStatic <em>Locally Static</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#isConfigurationInterface <em>Configuration Interface</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getGraphPort <em>Graph Port</em>}</li>
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
	 * The cached value of the '{@link #isLocallyStatic() <em>Locally Static</em>}' attribute.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @see #isLocallyStatic()
	 * @generated
	 * @ordered
	 */
	protected boolean locallyStatic = LOCALLY_STATIC_EDEFAULT;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterImpl() {
		super();
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
	public EList<Dependency> getOutgoingDependencies() {
		if (outgoingDependencies == null) {
			outgoingDependencies = new EObjectWithInverseResolvingEList<Dependency>(Dependency.class, this, PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES, PiMMPackage.DEPENDENCY__SETTER);
		}
		return outgoingDependencies;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isLocallyStatic() {
		return locallyStatic;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocallyStatic(boolean newLocallyStatic) {
		boolean oldLocallyStatic = locallyStatic;
		locallyStatic = newLocallyStatic;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__LOCALLY_STATIC, oldLocallyStatic, locallyStatic));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isConfigurationInterface() {
		return configurationInterface;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				return isLocallyStatic();
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				return isConfigurationInterface();
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				if (resolve) return getGraphPort();
				return basicGetGraphPort();
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				setLocallyStatic((Boolean)newValue);
				return;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				setConfigurationInterface((Boolean)newValue);
				return;
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				setGraphPort((ConfigInputPort)newValue);
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				setLocallyStatic(LOCALLY_STATIC_EDEFAULT);
				return;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				setConfigurationInterface(CONFIGURATION_INTERFACE_EDEFAULT);
				return;
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				setGraphPort((ConfigInputPort)null);
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				return locallyStatic != LOCALLY_STATIC_EDEFAULT;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				return configurationInterface != CONFIGURATION_INTERFACE_EDEFAULT;
			case PiMMPackage.PARAMETER__GRAPH_PORT:
				return graphPort != null;
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
		result.append(" (locallyStatic: ");
		result.append(locallyStatic);
		result.append(", configurationInterface: ");
		result.append(configurationInterface);
		result.append(')');
		return result.toString();
	}

} // ParameterImpl
