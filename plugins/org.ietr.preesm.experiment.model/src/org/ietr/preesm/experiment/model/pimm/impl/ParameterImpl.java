/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#isLocallyStatic <em>Locally Static</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#isConfigurationInterface <em>Configuration Interface</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ParameterImpl extends AbstractVertexImpl implements Parameter {
	/**
	 * The default value of the '{@link #isLocallyStatic() <em>Locally Static</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isLocallyStatic()
	 * @generated
	 * @ordered
	 */
	protected static final boolean LOCALLY_STATIC_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isLocallyStatic() <em>Locally Static</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ParameterImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.PARAMETER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isLocallyStatic() {
		return locallyStatic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocallyStatic(boolean newLocallyStatic) {
		boolean oldLocallyStatic = locallyStatic;
		locallyStatic = newLocallyStatic;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__LOCALLY_STATIC, oldLocallyStatic, locallyStatic));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isConfigurationInterface() {
		return configurationInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConfigurationInterface(boolean newConfigurationInterface) {
		boolean oldConfigurationInterface = configurationInterface;
		configurationInterface = newConfigurationInterface;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE, oldConfigurationInterface, configurationInterface));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				return isLocallyStatic();
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				return isConfigurationInterface();
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				setLocallyStatic((Boolean)newValue);
				return;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				setConfigurationInterface((Boolean)newValue);
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				setLocallyStatic(LOCALLY_STATIC_EDEFAULT);
				return;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				setConfigurationInterface(CONFIGURATION_INTERFACE_EDEFAULT);
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
			case PiMMPackage.PARAMETER__LOCALLY_STATIC:
				return locallyStatic != LOCALLY_STATIC_EDEFAULT;
			case PiMMPackage.PARAMETER__CONFIGURATION_INTERFACE:
				return configurationInterface != CONFIGURATION_INTERFACE_EDEFAULT;
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
		result.append(" (locallyStatic: ");
		result.append(locallyStatic);
		result.append(", configurationInterface: ");
		result.append(configurationInterface);
		result.append(')');
		return result.toString();
	}

} //ParameterImpl
