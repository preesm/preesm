/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InterfaceVertex;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Interface Vertex</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceVertexImpl#getGraphPort <em>Graph Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceVertexImpl#getKind <em>Kind</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InterfaceVertexImpl extends AbstractVertexImpl implements
		InterfaceVertex {
	/**
	 * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference.
	 * <!-- begin-user-doc --> This {@link Port} is the corresponding
	 * {@link Port} of the {@link Graph} containing this {@link Interface}
	 * instance. <!-- end-user-doc -->
	 * @see #getGraphPort()
	 * @generated
	 * @ordered
	 */
	protected Port graphPort;

	/**
	 * The default value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected static final String KIND_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getKind()
	 * @generated
	 * @ordered
	 */
	protected String kind = KIND_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected InterfaceVertexImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Port basicGetGraphPort() {
		return graphPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.INTERFACE_VERTEX__GRAPH_PORT:
				if (resolve) return getGraphPort();
				return basicGetGraphPort();
			case PiMMPackage.INTERFACE_VERTEX__KIND:
				return getKind();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PiMMPackage.INTERFACE_VERTEX__GRAPH_PORT:
				return graphPort != null;
			case PiMMPackage.INTERFACE_VERTEX__KIND:
				return KIND_EDEFAULT == null ? kind != null : !KIND_EDEFAULT.equals(kind);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PiMMPackage.INTERFACE_VERTEX__GRAPH_PORT:
				setGraphPort((Port)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.INTERFACE_VERTEX;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PiMMPackage.INTERFACE_VERTEX__GRAPH_PORT:
				setGraphPort((Port)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Port getGraphPort() {
		if (graphPort != null && graphPort.eIsProxy()) {
			InternalEObject oldGraphPort = (InternalEObject)graphPort;
			graphPort = (Port)eResolveProxy(oldGraphPort);
			if (graphPort != oldGraphPort) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.INTERFACE_VERTEX__GRAPH_PORT, oldGraphPort, graphPort));
			}
		}
		return graphPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setGraphPort(Port newGraphPort) {
		Port oldGraphPort = graphPort;
		graphPort = newGraphPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.INTERFACE_VERTEX__GRAPH_PORT, oldGraphPort, graphPort));
	}

	@Override
	public void setName(String newName) {
		super.setName(newName);
		if (this.getGraphPort() != null) {
			this.getGraphPort().setName(newName);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (kind: ");
		result.append(kind);
		result.append(')');
		return result.toString();
	}

} // InterfaceVertexImpl
