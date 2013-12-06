/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.InputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Input Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.InputPortImpl#getIncomingFifo <em>Incoming Fifo</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.InputPortImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InputPortImpl extends PortImpl implements InputPort {
	/**
	 * The cached value of the '{@link #getIncomingFifo() <em>Incoming Fifo</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIncomingFifo()
	 * @generated
	 * @ordered
	 */
	protected Fifo incomingFifo;

	/**
	 * The cached value of the '{@link #getExpression() <em>Expression</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpression()
	 * @generated
	 * @ordered
	 */
	protected Expression expression;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 */
	protected InputPortImpl() {
		super();
		kind = "input";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.INPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo getIncomingFifo() {
		if (incomingFifo != null && incomingFifo.eIsProxy()) {
			InternalEObject oldIncomingFifo = (InternalEObject)incomingFifo;
			incomingFifo = (Fifo)eResolveProxy(oldIncomingFifo);
			if (incomingFifo != oldIncomingFifo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.INPUT_PORT__INCOMING_FIFO, oldIncomingFifo, incomingFifo));
			}
		}
		return incomingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo basicGetIncomingFifo() {
		return incomingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetIncomingFifo(Fifo newIncomingFifo, NotificationChain msgs) {
		Fifo oldIncomingFifo = incomingFifo;
		incomingFifo = newIncomingFifo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.INPUT_PORT__INCOMING_FIFO, oldIncomingFifo, newIncomingFifo);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIncomingFifo(Fifo newIncomingFifo) {
		if (newIncomingFifo != incomingFifo) {
			NotificationChain msgs = null;
			if (incomingFifo != null)
				msgs = ((InternalEObject)incomingFifo).eInverseRemove(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
			if (newIncomingFifo != null)
				msgs = ((InternalEObject)newIncomingFifo).eInverseAdd(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
			msgs = basicSetIncomingFifo(newIncomingFifo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.INPUT_PORT__INCOMING_FIFO, newIncomingFifo, newIncomingFifo));
	}

/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Expression getExpression() {
		return expression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetExpression(Expression newExpression, NotificationChain msgs) {
		Expression oldExpression = expression;
		expression = newExpression;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.INPUT_PORT__EXPRESSION, oldExpression, newExpression);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpression(Expression newExpression) {
		if (newExpression != expression) {
			NotificationChain msgs = null;
			if (expression != null)
				msgs = ((InternalEObject)expression).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.INPUT_PORT__EXPRESSION, null, msgs);
			if (newExpression != null)
				msgs = ((InternalEObject)newExpression).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.INPUT_PORT__EXPRESSION, null, msgs);
			msgs = basicSetExpression(newExpression, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.INPUT_PORT__EXPRESSION, newExpression, newExpression));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.INPUT_PORT__INCOMING_FIFO:
				if (incomingFifo != null)
					msgs = ((InternalEObject)incomingFifo).eInverseRemove(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
				return basicSetIncomingFifo((Fifo)otherEnd, msgs);
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
			case PiMMPackage.INPUT_PORT__INCOMING_FIFO:
				return basicSetIncomingFifo(null, msgs);
			case PiMMPackage.INPUT_PORT__EXPRESSION:
				return basicSetExpression(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PiMMPackage.INPUT_PORT__INCOMING_FIFO:
				if (resolve) return getIncomingFifo();
				return basicGetIncomingFifo();
			case PiMMPackage.INPUT_PORT__EXPRESSION:
				return getExpression();
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
			case PiMMPackage.INPUT_PORT__INCOMING_FIFO:
				setIncomingFifo((Fifo)newValue);
				return;
			case PiMMPackage.INPUT_PORT__EXPRESSION:
				setExpression((Expression)newValue);
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
			case PiMMPackage.INPUT_PORT__INCOMING_FIFO:
				setIncomingFifo((Fifo)null);
				return;
			case PiMMPackage.INPUT_PORT__EXPRESSION:
				setExpression((Expression)null);
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
			case PiMMPackage.INPUT_PORT__INCOMING_FIFO:
				return incomingFifo != null;
			case PiMMPackage.INPUT_PORT__EXPRESSION:
				return expression != null;
		}
		return super.eIsSet(featureID);
	}

} //InputPortImpl
