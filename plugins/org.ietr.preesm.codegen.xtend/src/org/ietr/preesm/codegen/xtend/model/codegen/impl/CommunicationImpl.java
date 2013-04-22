/**
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
 */
package org.ietr.preesm.codegen.xtend.model.codegen.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Communication</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getDirection
 * <em>Direction</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getDelimiter
 * <em>Delimiter</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getData
 * <em>Data</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getSendStart
 * <em>Send Start</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getSendEnd
 * <em>Send End</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getReceiveStart
 * <em>Receive Start</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getReceiveEnd
 * <em>Receive End</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CommunicationImpl extends CallImpl implements Communication {
	/**
	 * The default value of the '{@link #getDirection() <em>Direction</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDirection()
	 * @generated
	 * @ordered
	 */
	protected static final Direction DIRECTION_EDEFAULT = Direction.SEND;

	/**
	 * The cached value of the '{@link #getDirection() <em>Direction</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDirection()
	 * @generated
	 * @ordered
	 */
	protected Direction direction = DIRECTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDelimiter() <em>Delimiter</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDelimiter()
	 * @generated
	 * @ordered
	 */
	protected static final Delimiter DELIMITER_EDEFAULT = Delimiter.START;

	/**
	 * The cached value of the '{@link #getDelimiter() <em>Delimiter</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDelimiter()
	 * @generated
	 * @ordered
	 */
	protected Delimiter delimiter = DELIMITER_EDEFAULT;

	/**
	 * The cached value of the '{@link #getData() <em>Data</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getData()
	 * @generated
	 * @ordered
	 */
	protected Buffer data;

	/**
	 * The cached value of the '{@link #getSendStart() <em>Send Start</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSendStart()
	 * @generated
	 * @ordered
	 */
	protected Communication sendStart;

	/**
	 * The cached value of the '{@link #getSendEnd() <em>Send End</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSendEnd()
	 * @generated
	 * @ordered
	 */
	protected Communication sendEnd;

	/**
	 * The cached value of the '{@link #getReceiveStart()
	 * <em>Receive Start</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getReceiveStart()
	 * @generated
	 * @ordered
	 */
	protected Communication receiveStart;

	/**
	 * The cached value of the '{@link #getReceiveEnd() <em>Receive End</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getReceiveEnd()
	 * @generated
	 * @ordered
	 */
	protected Communication receiveEnd;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected CommunicationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.COMMUNICATION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDirection(Direction newDirection) {
		Direction oldDirection = direction;
		direction = newDirection == null ? DIRECTION_EDEFAULT : newDirection;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__DIRECTION, oldDirection,
					direction));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Delimiter getDelimiter() {
		return delimiter;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setDelimiter(Delimiter newDelimiter) {
		Delimiter oldDelimiter = delimiter;
		delimiter = newDelimiter == null ? DELIMITER_EDEFAULT : newDelimiter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__DELIMITER, oldDelimiter,
					delimiter));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Buffer getData() {
		if (data != null && data.eIsProxy()) {
			InternalEObject oldData = (InternalEObject) data;
			data = (Buffer) eResolveProxy(oldData);
			if (data != oldData) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.COMMUNICATION__DATA, oldData, data));
			}
		}
		return data;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Buffer basicGetData() {
		return data;
	}

	/**
	 * <!-- begin-user-doc --><!-- end-user-doc -->
	 * 
	 * 
	 */
	public void setData(Buffer newData) {
		Buffer oldData = data;
		data = newData;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__DATA, oldData, data));
		this.getParameters().clear();
		if (newData != null) {
			this.addParameter(newData);
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication getSendStart() {
		if (sendStart != null && sendStart.eIsProxy()) {
			InternalEObject oldSendStart = (InternalEObject) sendStart;
			sendStart = (Communication) eResolveProxy(oldSendStart);
			if (sendStart != oldSendStart) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.COMMUNICATION__SEND_START,
							oldSendStart, sendStart));
			}
		}
		return sendStart;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication basicGetSendStart() {
		return sendStart;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSendStart(Communication newSendStart) {
		Communication oldSendStart = sendStart;
		sendStart = newSendStart;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__SEND_START, oldSendStart,
					sendStart));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication getSendEnd() {
		if (sendEnd != null && sendEnd.eIsProxy()) {
			InternalEObject oldSendEnd = (InternalEObject) sendEnd;
			sendEnd = (Communication) eResolveProxy(oldSendEnd);
			if (sendEnd != oldSendEnd) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.COMMUNICATION__SEND_END, oldSendEnd,
							sendEnd));
			}
		}
		return sendEnd;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication basicGetSendEnd() {
		return sendEnd;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSendEnd(Communication newSendEnd) {
		Communication oldSendEnd = sendEnd;
		sendEnd = newSendEnd;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__SEND_END, oldSendEnd, sendEnd));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication getReceiveStart() {
		if (receiveStart != null && receiveStart.eIsProxy()) {
			InternalEObject oldReceiveStart = (InternalEObject) receiveStart;
			receiveStart = (Communication) eResolveProxy(oldReceiveStart);
			if (receiveStart != oldReceiveStart) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.COMMUNICATION__RECEIVE_START,
							oldReceiveStart, receiveStart));
			}
		}
		return receiveStart;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication basicGetReceiveStart() {
		return receiveStart;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setReceiveStart(Communication newReceiveStart) {
		Communication oldReceiveStart = receiveStart;
		receiveStart = newReceiveStart;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__RECEIVE_START,
					oldReceiveStart, receiveStart));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication getReceiveEnd() {
		if (receiveEnd != null && receiveEnd.eIsProxy()) {
			InternalEObject oldReceiveEnd = (InternalEObject) receiveEnd;
			receiveEnd = (Communication) eResolveProxy(oldReceiveEnd);
			if (receiveEnd != oldReceiveEnd) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.COMMUNICATION__RECEIVE_END,
							oldReceiveEnd, receiveEnd));
			}
		}
		return receiveEnd;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Communication basicGetReceiveEnd() {
		return receiveEnd;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setReceiveEnd(Communication newReceiveEnd) {
		Communication oldReceiveEnd = receiveEnd;
		receiveEnd = newReceiveEnd;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.COMMUNICATION__RECEIVE_END, oldReceiveEnd,
					receiveEnd));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case CodegenPackage.COMMUNICATION__DIRECTION:
			return getDirection();
		case CodegenPackage.COMMUNICATION__DELIMITER:
			return getDelimiter();
		case CodegenPackage.COMMUNICATION__DATA:
			if (resolve)
				return getData();
			return basicGetData();
		case CodegenPackage.COMMUNICATION__SEND_START:
			if (resolve)
				return getSendStart();
			return basicGetSendStart();
		case CodegenPackage.COMMUNICATION__SEND_END:
			if (resolve)
				return getSendEnd();
			return basicGetSendEnd();
		case CodegenPackage.COMMUNICATION__RECEIVE_START:
			if (resolve)
				return getReceiveStart();
			return basicGetReceiveStart();
		case CodegenPackage.COMMUNICATION__RECEIVE_END:
			if (resolve)
				return getReceiveEnd();
			return basicGetReceiveEnd();
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
		case CodegenPackage.COMMUNICATION__DIRECTION:
			setDirection((Direction) newValue);
			return;
		case CodegenPackage.COMMUNICATION__DELIMITER:
			setDelimiter((Delimiter) newValue);
			return;
		case CodegenPackage.COMMUNICATION__DATA:
			setData((Buffer) newValue);
			return;
		case CodegenPackage.COMMUNICATION__SEND_START:
			setSendStart((Communication) newValue);
			return;
		case CodegenPackage.COMMUNICATION__SEND_END:
			setSendEnd((Communication) newValue);
			return;
		case CodegenPackage.COMMUNICATION__RECEIVE_START:
			setReceiveStart((Communication) newValue);
			return;
		case CodegenPackage.COMMUNICATION__RECEIVE_END:
			setReceiveEnd((Communication) newValue);
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
		case CodegenPackage.COMMUNICATION__DIRECTION:
			setDirection(DIRECTION_EDEFAULT);
			return;
		case CodegenPackage.COMMUNICATION__DELIMITER:
			setDelimiter(DELIMITER_EDEFAULT);
			return;
		case CodegenPackage.COMMUNICATION__DATA:
			setData((Buffer) null);
			return;
		case CodegenPackage.COMMUNICATION__SEND_START:
			setSendStart((Communication) null);
			return;
		case CodegenPackage.COMMUNICATION__SEND_END:
			setSendEnd((Communication) null);
			return;
		case CodegenPackage.COMMUNICATION__RECEIVE_START:
			setReceiveStart((Communication) null);
			return;
		case CodegenPackage.COMMUNICATION__RECEIVE_END:
			setReceiveEnd((Communication) null);
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
		case CodegenPackage.COMMUNICATION__DIRECTION:
			return direction != DIRECTION_EDEFAULT;
		case CodegenPackage.COMMUNICATION__DELIMITER:
			return delimiter != DELIMITER_EDEFAULT;
		case CodegenPackage.COMMUNICATION__DATA:
			return data != null;
		case CodegenPackage.COMMUNICATION__SEND_START:
			return sendStart != null;
		case CodegenPackage.COMMUNICATION__SEND_END:
			return sendEnd != null;
		case CodegenPackage.COMMUNICATION__RECEIVE_START:
			return receiveStart != null;
		case CodegenPackage.COMMUNICATION__RECEIVE_END:
			return receiveEnd != null;
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
		result.append(" (direction: ");
		result.append(direction);
		result.append(", delimiter: ");
		result.append(delimiter);
		result.append(')');
		return result.toString();
	}

} // CommunicationImpl
