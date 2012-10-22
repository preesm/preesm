/**
 */
package org.ietr.preesm.experiment.model.pimemoc.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimemoc.Fifo;
import org.ietr.preesm.experiment.model.pimemoc.InputPort;
import org.ietr.preesm.experiment.model.pimemoc.OutputPort;
import org.ietr.preesm.experiment.model.pimemoc.PIMeMoCPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Fifo</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.FifoImpl#getSourcePort
 * <em>Source Port</em>}</li>
 * <li>
 * {@link org.ietr.preesm.experiment.model.pimemoc.impl.FifoImpl#getTargetPort
 * <em>Target Port</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class FifoImpl extends EObjectImpl implements Fifo {
	/**
	 * The cached value of the '{@link #getSourcePort() <em>Source Port</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSourcePort()
	 * @generated
	 * @ordered
	 */
	protected OutputPort sourcePort;

	/**
	 * The cached value of the '{@link #getTargetPort() <em>Target Port</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTargetPort()
	 * @generated
	 * @ordered
	 */
	protected InputPort targetPort;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FifoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PIMeMoCPackage.Literals.FIFO;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public OutputPort getSourcePort() {
		if (sourcePort != null && sourcePort.eIsProxy()) {
			InternalEObject oldSourcePort = (InternalEObject) sourcePort;
			sourcePort = (OutputPort) eResolveProxy(oldSourcePort);
			if (sourcePort != oldSourcePort) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							PIMeMoCPackage.FIFO__SOURCE_PORT, oldSourcePort,
							sourcePort));
			}
		}
		return sourcePort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public OutputPort basicGetSourcePort() {
		return sourcePort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetSourcePort(OutputPort newSourcePort,
			NotificationChain msgs) {
		OutputPort oldSourcePort = sourcePort;
		sourcePort = newSourcePort;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET, PIMeMoCPackage.FIFO__SOURCE_PORT,
					oldSourcePort, newSourcePort);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSourcePort(OutputPort newSourcePort) {
		if (newSourcePort != sourcePort) {
			NotificationChain msgs = null;
			if (sourcePort != null)
				msgs = ((InternalEObject) sourcePort).eInverseRemove(this,
						PIMeMoCPackage.OUTPUT_PORT__OUTGOING_FIFO,
						OutputPort.class, msgs);
			if (newSourcePort != null)
				msgs = ((InternalEObject) newSourcePort).eInverseAdd(this,
						PIMeMoCPackage.OUTPUT_PORT__OUTGOING_FIFO,
						OutputPort.class, msgs);
			msgs = basicSetSourcePort(newSourcePort, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PIMeMoCPackage.FIFO__SOURCE_PORT, newSourcePort,
					newSourcePort));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public InputPort getTargetPort() {
		if (targetPort != null && targetPort.eIsProxy()) {
			InternalEObject oldTargetPort = (InternalEObject) targetPort;
			targetPort = (InputPort) eResolveProxy(oldTargetPort);
			if (targetPort != oldTargetPort) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							PIMeMoCPackage.FIFO__TARGET_PORT, oldTargetPort,
							targetPort));
			}
		}
		return targetPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public InputPort basicGetTargetPort() {
		return targetPort;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetTargetPort(InputPort newTargetPort,
			NotificationChain msgs) {
		InputPort oldTargetPort = targetPort;
		targetPort = newTargetPort;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET, PIMeMoCPackage.FIFO__TARGET_PORT,
					oldTargetPort, newTargetPort);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTargetPort(InputPort newTargetPort) {
		if (newTargetPort != targetPort) {
			NotificationChain msgs = null;
			if (targetPort != null)
				msgs = ((InternalEObject) targetPort).eInverseRemove(this,
						PIMeMoCPackage.INPUT_PORT__INCOMING_FIFO,
						InputPort.class, msgs);
			if (newTargetPort != null)
				msgs = ((InternalEObject) newTargetPort).eInverseAdd(this,
						PIMeMoCPackage.INPUT_PORT__INCOMING_FIFO,
						InputPort.class, msgs);
			msgs = basicSetTargetPort(newTargetPort, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					PIMeMoCPackage.FIFO__TARGET_PORT, newTargetPort,
					newTargetPort));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case PIMeMoCPackage.FIFO__SOURCE_PORT:
			if (sourcePort != null)
				msgs = ((InternalEObject) sourcePort).eInverseRemove(this,
						PIMeMoCPackage.OUTPUT_PORT__OUTGOING_FIFO,
						OutputPort.class, msgs);
			return basicSetSourcePort((OutputPort) otherEnd, msgs);
		case PIMeMoCPackage.FIFO__TARGET_PORT:
			if (targetPort != null)
				msgs = ((InternalEObject) targetPort).eInverseRemove(this,
						PIMeMoCPackage.INPUT_PORT__INCOMING_FIFO,
						InputPort.class, msgs);
			return basicSetTargetPort((InputPort) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
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
		case PIMeMoCPackage.FIFO__SOURCE_PORT:
			return basicSetSourcePort(null, msgs);
		case PIMeMoCPackage.FIFO__TARGET_PORT:
			return basicSetTargetPort(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case PIMeMoCPackage.FIFO__SOURCE_PORT:
			if (resolve)
				return getSourcePort();
			return basicGetSourcePort();
		case PIMeMoCPackage.FIFO__TARGET_PORT:
			if (resolve)
				return getTargetPort();
			return basicGetTargetPort();
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
		case PIMeMoCPackage.FIFO__SOURCE_PORT:
			setSourcePort((OutputPort) newValue);
			return;
		case PIMeMoCPackage.FIFO__TARGET_PORT:
			setTargetPort((InputPort) newValue);
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
		case PIMeMoCPackage.FIFO__SOURCE_PORT:
			setSourcePort((OutputPort) null);
			return;
		case PIMeMoCPackage.FIFO__TARGET_PORT:
			setTargetPort((InputPort) null);
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
		case PIMeMoCPackage.FIFO__SOURCE_PORT:
			return sourcePort != null;
		case PIMeMoCPackage.FIFO__TARGET_PORT:
			return targetPort != null;
		}
		return super.eIsSet(featureID);
	}

} // FifoImpl
