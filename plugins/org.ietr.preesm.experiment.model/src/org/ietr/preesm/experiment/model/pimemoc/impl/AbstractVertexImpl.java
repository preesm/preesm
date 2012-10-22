/**
 */
package org.ietr.preesm.experiment.model.pimemoc.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimemoc.AbstractVertex;
import org.ietr.preesm.experiment.model.pimemoc.InputPort;
import org.ietr.preesm.experiment.model.pimemoc.OutputPort;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage;
import org.ietr.preesm.experiment.model.pimemoc.Port;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Abstract Vertex</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl#getName
 * <em>Name</em>}</li>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl#getInputPorts
 * <em>Input Ports</em>}</li>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl#getOutputPorts
 * <em>Output Ports</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class AbstractVertexImpl extends EObjectImpl implements
		AbstractVertex {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInputPorts() <em>Input Ports</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getInputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<InputPort> inputPorts;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOutputPorts() <em>Output Ports</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getOutputPorts()
	 * @generated
	 * @ordered
	 */
	protected EList<OutputPort> outputPorts;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected AbstractVertexImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PIMeMoCPackage.ABSTRACT_VERTEX__NAME:
			return getName();
		case PIMeMoCPackage.ABSTRACT_VERTEX__INPUT_PORTS:
			return getInputPorts();
		case PIMeMoCPackage.ABSTRACT_VERTEX__OUTPUT_PORTS:
			return getOutputPorts();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case PIMeMoCPackage.ABSTRACT_VERTEX__INPUT_PORTS:
			return ((InternalEList<?>) getInputPorts()).basicRemove(otherEnd,
					msgs);
		case PIMeMoCPackage.ABSTRACT_VERTEX__OUTPUT_PORTS:
			return ((InternalEList<?>) getOutputPorts()).basicRemove(otherEnd,
					msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case PIMeMoCPackage.ABSTRACT_VERTEX__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case PIMeMoCPackage.ABSTRACT_VERTEX__INPUT_PORTS:
			return inputPorts != null && !inputPorts.isEmpty();
		case PIMeMoCPackage.ABSTRACT_VERTEX__OUTPUT_PORTS:
			return outputPorts != null && !outputPorts.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case PIMeMoCPackage.ABSTRACT_VERTEX__NAME:
			setName((String) newValue);
			return;
		case PIMeMoCPackage.ABSTRACT_VERTEX__INPUT_PORTS:
			getInputPorts().clear();
			getInputPorts().addAll((Collection<? extends InputPort>) newValue);
			return;
		case PIMeMoCPackage.ABSTRACT_VERTEX__OUTPUT_PORTS:
			getOutputPorts().clear();
			getOutputPorts()
					.addAll((Collection<? extends OutputPort>) newValue);
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
	protected EClass eStaticClass() {
		return PIMeMoCPackage.Literals.ABSTRACT_VERTEX;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case PIMeMoCPackage.ABSTRACT_VERTEX__NAME:
			setName(NAME_EDEFAULT);
			return;
		case PIMeMoCPackage.ABSTRACT_VERTEX__INPUT_PORTS:
			getInputPorts().clear();
			return;
		case PIMeMoCPackage.ABSTRACT_VERTEX__OUTPUT_PORTS:
			getOutputPorts().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<InputPort> getInputPorts() {
		if (inputPorts == null) {
			inputPorts = new EObjectContainmentEList<InputPort>(
					InputPort.class, this,
					PIMeMoCPackage.ABSTRACT_VERTEX__INPUT_PORTS);
		}
		return inputPorts;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<OutputPort> getOutputPorts() {
		if (outputPorts == null) {
			outputPorts = new EObjectContainmentEList<OutputPort>(
					OutputPort.class, this,
					PIMeMoCPackage.ABSTRACT_VERTEX__OUTPUT_PORTS);
		}
		return outputPorts;
	}

	@Override
	public Port getPortNamed(String portName, String direction) {
		EList<?> ports = null;

		switch (direction) {
		case "input":
			ports = getInputPorts();
			break;
		case "output":
			ports = getOutputPorts();
			break;
		default:
			return null;
		}

		for (Object port : ports) {
			if (((Port) port).getName().equals(portName)) {
				return (Port) port;
			}
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PIMeMoCPackage.ABSTRACT_VERTEX__NAME, oldName, name));
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
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} // AbstractVertexImpl
