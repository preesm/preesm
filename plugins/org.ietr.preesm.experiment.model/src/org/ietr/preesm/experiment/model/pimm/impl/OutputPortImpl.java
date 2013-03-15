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
import org.ietr.preesm.experiment.model.pimm.OutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Output Port</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.OutputPortImpl#getOutgoingFifo <em>Outgoing Fifo</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.OutputPortImpl#getExpression <em>Expression</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OutputPortImpl extends PortImpl implements OutputPort {
	/**
	 * The cached value of the '{@link #getOutgoingFifo() <em>Outgoing Fifo</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutgoingFifo()
	 * @generated
	 * @ordered
	 */
	protected Fifo outgoingFifo;

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
	protected OutputPortImpl() {
		super();
		kind = "output";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PiMMPackage.Literals.OUTPUT_PORT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo getOutgoingFifo() {
		if (outgoingFifo != null && outgoingFifo.eIsProxy()) {
			InternalEObject oldOutgoingFifo = (InternalEObject)outgoingFifo;
			outgoingFifo = (Fifo)eResolveProxy(oldOutgoingFifo);
			if (outgoingFifo != oldOutgoingFifo) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO, oldOutgoingFifo, outgoingFifo));
			}
		}
		return outgoingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Fifo basicGetOutgoingFifo() {
		return outgoingFifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetOutgoingFifo(Fifo newOutgoingFifo, NotificationChain msgs) {
		Fifo oldOutgoingFifo = outgoingFifo;
		outgoingFifo = newOutgoingFifo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO, oldOutgoingFifo, newOutgoingFifo);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOutgoingFifo(Fifo newOutgoingFifo) {
		if (newOutgoingFifo != outgoingFifo) {
			NotificationChain msgs = null;
			if (outgoingFifo != null)
				msgs = ((InternalEObject)outgoingFifo).eInverseRemove(this, PiMMPackage.FIFO__SOURCE_PORT, Fifo.class, msgs);
			if (newOutgoingFifo != null)
				msgs = ((InternalEObject)newOutgoingFifo).eInverseAdd(this, PiMMPackage.FIFO__SOURCE_PORT, Fifo.class, msgs);
			msgs = basicSetOutgoingFifo(newOutgoingFifo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO, newOutgoingFifo, newOutgoingFifo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * Returns an expression and if the expression is null creates.
	 * <!-- end-user-doc -->
	 */
	public Expression getExpression() {
		//TODO View because it automatically creates the instance of expression.
		if (expression == null) {
			expression = new ExpressionImpl();
		}else{
			InternalEObject oldExpression = (InternalEObject) expression;
			expression =  (Expression) eResolveProxy(oldExpression);
			if (expression != oldExpression) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.OUTPUT_PORT__EXPRESSION, oldExpression, expression));
			}
		}
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
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.OUTPUT_PORT__EXPRESSION, oldExpression, newExpression);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void setExpression(Expression newExpression) {
		Expression oldExpression = expression;
		expression = newExpression;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.OUTPUT_PORT__EXPRESSION, oldExpression, expression));
		
	}

	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO:
				if (outgoingFifo != null)
					msgs = ((InternalEObject)outgoingFifo).eInverseRemove(this, PiMMPackage.FIFO__SOURCE_PORT, Fifo.class, msgs);
				return basicSetOutgoingFifo((Fifo)otherEnd, msgs);
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
			case PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO:
				return basicSetOutgoingFifo(null, msgs);
			case PiMMPackage.OUTPUT_PORT__EXPRESSION:
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
			case PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO:
				if (resolve) return getOutgoingFifo();
				return basicGetOutgoingFifo();
			case PiMMPackage.OUTPUT_PORT__EXPRESSION:
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
			case PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO:
				setOutgoingFifo((Fifo)newValue);
				return;
			case PiMMPackage.OUTPUT_PORT__EXPRESSION:
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
			case PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO:
				setOutgoingFifo((Fifo)null);
				return;
			case PiMMPackage.OUTPUT_PORT__EXPRESSION:
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
			case PiMMPackage.OUTPUT_PORT__OUTGOING_FIFO:
				return outgoingFifo != null;
			case PiMMPackage.OUTPUT_PORT__EXPRESSION:
				return expression != null;
		}
		return super.eIsSet(featureID);
	}

} //OutputPortImpl
