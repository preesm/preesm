/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
package org.ietr.preesm.codegen.xtend.model.codegen.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Communication;
import org.ietr.preesm.codegen.xtend.model.codegen.CommunicationNode;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.Direction;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Communication</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getDirection <em>Direction</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getDelimiter <em>Delimiter</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getData <em>Data</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getSendStart <em>Send Start</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getSendEnd <em>Send End</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getReceiveStart <em>Receive Start</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getReceiveEnd <em>Receive End</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getId <em>Id</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getNodes <em>Nodes</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getReceiveRelease <em>Receive Release</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CommunicationImpl#getSendReserve <em>Send Reserve</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CommunicationImpl extends CallImpl implements Communication {
  /**
   * The default value of the '{@link #getDirection() <em>Direction</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDirection()
   * @generated
   * @ordered
   */
  protected static final Direction DIRECTION_EDEFAULT = Direction.SEND;

  /**
   * The cached value of the '{@link #getDirection() <em>Direction</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDirection()
   * @generated
   * @ordered
   */
  protected Direction direction = CommunicationImpl.DIRECTION_EDEFAULT;

  /**
   * The default value of the '{@link #getDelimiter() <em>Delimiter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDelimiter()
   * @generated
   * @ordered
   */
  protected static final Delimiter DELIMITER_EDEFAULT = Delimiter.START;

  /**
   * The cached value of the '{@link #getDelimiter() <em>Delimiter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDelimiter()
   * @generated
   * @ordered
   */
  protected Delimiter delimiter = CommunicationImpl.DELIMITER_EDEFAULT;

  /**
   * The cached value of the '{@link #getData() <em>Data</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getData()
   * @generated
   * @ordered
   */
  protected Buffer data;

  /**
   * The cached value of the '{@link #getSendStart() <em>Send Start</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getSendStart()
   * @generated
   * @ordered
   */
  protected Communication sendStart;

  /**
   * The cached value of the '{@link #getSendEnd() <em>Send End</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getSendEnd()
   * @generated
   * @ordered
   */
  protected Communication sendEnd;

  /**
   * The cached value of the '{@link #getReceiveStart() <em>Receive Start</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getReceiveStart()
   * @generated
   * @ordered
   */
  protected Communication receiveStart;

  /**
   * The cached value of the '{@link #getReceiveEnd() <em>Receive End</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getReceiveEnd()
   * @generated
   * @ordered
   */
  protected Communication receiveEnd;

  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final int ID_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getId()
   * @generated
   * @ordered
   */
  protected int id = CommunicationImpl.ID_EDEFAULT;

  /**
   * The cached value of the '{@link #getNodes() <em>Nodes</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getNodes()
   * @generated
   * @ordered
   */
  protected EList<CommunicationNode> nodes;

  /**
   * The cached value of the '{@link #getReceiveRelease() <em>Receive Release</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getReceiveRelease()
   * @generated
   * @ordered
   */
  protected Communication receiveRelease;

  /**
   * The cached value of the '{@link #getSendReserve() <em>Send Reserve</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getSendReserve()
   * @generated
   * @ordered
   */
  protected Communication sendReserve;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected CommunicationImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return CodegenPackage.Literals.COMMUNICATION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the direction
   * @generated
   */
  @Override
  public Direction getDirection() {
    return this.direction;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newDirection
   *          the new direction
   * @generated
   */
  @Override
  public void setDirection(final Direction newDirection) {
    final Direction oldDirection = this.direction;
    this.direction = newDirection == null ? CommunicationImpl.DIRECTION_EDEFAULT : newDirection;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__DIRECTION, oldDirection, this.direction));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the delimiter
   * @generated
   */
  @Override
  public Delimiter getDelimiter() {
    return this.delimiter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newDelimiter
   *          the new delimiter
   * @generated
   */
  @Override
  public void setDelimiter(final Delimiter newDelimiter) {
    final Delimiter oldDelimiter = this.delimiter;
    this.delimiter = newDelimiter == null ? CommunicationImpl.DELIMITER_EDEFAULT : newDelimiter;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__DELIMITER, oldDelimiter, this.delimiter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data
   * @generated
   */
  @Override
  public Buffer getData() {
    if ((this.data != null) && this.data.eIsProxy()) {
      final InternalEObject oldData = (InternalEObject) this.data;
      this.data = (Buffer) eResolveProxy(oldData);
      if (this.data != oldData) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__DATA, oldData, this.data));
        }
      }
    }
    return this.data;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer
   * @generated
   */
  public Buffer basicGetData() {
    return this.data;
  }

  /**
   * <!-- begin-user-doc --><!-- end-user-doc -->.
   *
   * @param newData
   *          the new data
   */
  @Override
  public void setData(final Buffer newData) {
    final Buffer oldData = this.data;
    this.data = newData;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__DATA, oldData, this.data));
    }
    getParameters().clear();
    if (newData != null) {
      addParameter(newData, PortDirection.NONE);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the send start
   * @generated
   */
  @Override
  public Communication getSendStart() {
    if ((this.sendStart != null) && this.sendStart.eIsProxy()) {
      final InternalEObject oldSendStart = (InternalEObject) this.sendStart;
      this.sendStart = (Communication) eResolveProxy(oldSendStart);
      if (this.sendStart != oldSendStart) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__SEND_START, oldSendStart, this.sendStart));
        }
      }
    }
    return this.sendStart;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  public Communication basicGetSendStart() {
    return this.sendStart;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSendStart
   *          the new send start
   * @generated
   */
  @Override
  public void setSendStart(final Communication newSendStart) {
    final Communication oldSendStart = this.sendStart;
    this.sendStart = newSendStart;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__SEND_START, oldSendStart, this.sendStart));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the send end
   * @generated
   */
  @Override
  public Communication getSendEnd() {
    if ((this.sendEnd != null) && this.sendEnd.eIsProxy()) {
      final InternalEObject oldSendEnd = (InternalEObject) this.sendEnd;
      this.sendEnd = (Communication) eResolveProxy(oldSendEnd);
      if (this.sendEnd != oldSendEnd) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__SEND_END, oldSendEnd, this.sendEnd));
        }
      }
    }
    return this.sendEnd;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  public Communication basicGetSendEnd() {
    return this.sendEnd;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSendEnd
   *          the new send end
   * @generated
   */
  @Override
  public void setSendEnd(final Communication newSendEnd) {
    final Communication oldSendEnd = this.sendEnd;
    this.sendEnd = newSendEnd;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__SEND_END, oldSendEnd, this.sendEnd));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the receive start
   * @generated
   */
  @Override
  public Communication getReceiveStart() {
    if ((this.receiveStart != null) && this.receiveStart.eIsProxy()) {
      final InternalEObject oldReceiveStart = (InternalEObject) this.receiveStart;
      this.receiveStart = (Communication) eResolveProxy(oldReceiveStart);
      if (this.receiveStart != oldReceiveStart) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__RECEIVE_START, oldReceiveStart, this.receiveStart));
        }
      }
    }
    return this.receiveStart;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  public Communication basicGetReceiveStart() {
    return this.receiveStart;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newReceiveStart
   *          the new receive start
   * @generated
   */
  @Override
  public void setReceiveStart(final Communication newReceiveStart) {
    final Communication oldReceiveStart = this.receiveStart;
    this.receiveStart = newReceiveStart;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__RECEIVE_START, oldReceiveStart, this.receiveStart));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the receive end
   * @generated
   */
  @Override
  public Communication getReceiveEnd() {
    if ((this.receiveEnd != null) && this.receiveEnd.eIsProxy()) {
      final InternalEObject oldReceiveEnd = (InternalEObject) this.receiveEnd;
      this.receiveEnd = (Communication) eResolveProxy(oldReceiveEnd);
      if (this.receiveEnd != oldReceiveEnd) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__RECEIVE_END, oldReceiveEnd, this.receiveEnd));
        }
      }
    }
    return this.receiveEnd;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  public Communication basicGetReceiveEnd() {
    return this.receiveEnd;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newReceiveEnd
   *          the new receive end
   * @generated
   */
  @Override
  public void setReceiveEnd(final Communication newReceiveEnd) {
    final Communication oldReceiveEnd = this.receiveEnd;
    this.receiveEnd = newReceiveEnd;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__RECEIVE_END, oldReceiveEnd, this.receiveEnd));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the id
   * @generated
   */
  @Override
  public int getId() {
    return this.id;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newId
   *          the new id
   * @generated
   */
  @Override
  public void setId(final int newId) {
    final int oldId = this.id;
    this.id = newId;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__ID, oldId, this.id));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the nodes
   * @generated
   */
  @Override
  public EList<CommunicationNode> getNodes() {
    if (this.nodes == null) {
      this.nodes = new EObjectContainmentEList<>(CommunicationNode.class, this, CodegenPackage.COMMUNICATION__NODES);
    }
    return this.nodes;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the receive release
   * @generated
   */
  @Override
  public Communication getReceiveRelease() {
    if ((this.receiveRelease != null) && this.receiveRelease.eIsProxy()) {
      final InternalEObject oldReceiveRelease = (InternalEObject) this.receiveRelease;
      this.receiveRelease = (Communication) eResolveProxy(oldReceiveRelease);
      if (this.receiveRelease != oldReceiveRelease) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__RECEIVE_RELEASE, oldReceiveRelease, this.receiveRelease));
        }
      }
    }
    return this.receiveRelease;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  public Communication basicGetReceiveRelease() {
    return this.receiveRelease;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newReceiveRelease
   *          the new receive release
   * @generated
   */
  @Override
  public void setReceiveRelease(final Communication newReceiveRelease) {
    final Communication oldReceiveRelease = this.receiveRelease;
    this.receiveRelease = newReceiveRelease;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__RECEIVE_RELEASE, oldReceiveRelease, this.receiveRelease));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the send reserve
   * @generated
   */
  @Override
  public Communication getSendReserve() {
    if ((this.sendReserve != null) && this.sendReserve.eIsProxy()) {
      final InternalEObject oldSendReserve = (InternalEObject) this.sendReserve;
      this.sendReserve = (Communication) eResolveProxy(oldSendReserve);
      if (this.sendReserve != oldSendReserve) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.COMMUNICATION__SEND_RESERVE, oldSendReserve, this.sendReserve));
        }
      }
    }
    return this.sendReserve;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the communication
   * @generated
   */
  public Communication basicGetSendReserve() {
    return this.sendReserve;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSendReserve
   *          the new send reserve
   * @generated
   */
  @Override
  public void setSendReserve(final Communication newSendReserve) {
    final Communication oldSendReserve = this.sendReserve;
    this.sendReserve = newSendReserve;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.COMMUNICATION__SEND_RESERVE, oldSendReserve, this.sendReserve));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the core container
   */
  @Override
  public CoreBlock getCoreContainer() {
    EObject container = eContainer();
    while (!(container instanceof CoreBlock) && (container != null)) {
      container = container.eContainer();
    }

    if (container == null) {
      return null;
    } else {
      return (CoreBlock) container;
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature ID
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.COMMUNICATION__NODES:
        return ((InternalEList<?>) getNodes()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case CodegenPackage.COMMUNICATION__DIRECTION:
        return getDirection();
      case CodegenPackage.COMMUNICATION__DELIMITER:
        return getDelimiter();
      case CodegenPackage.COMMUNICATION__DATA:
        if (resolve) {
          return getData();
        }
        return basicGetData();
      case CodegenPackage.COMMUNICATION__SEND_START:
        if (resolve) {
          return getSendStart();
        }
        return basicGetSendStart();
      case CodegenPackage.COMMUNICATION__SEND_END:
        if (resolve) {
          return getSendEnd();
        }
        return basicGetSendEnd();
      case CodegenPackage.COMMUNICATION__RECEIVE_START:
        if (resolve) {
          return getReceiveStart();
        }
        return basicGetReceiveStart();
      case CodegenPackage.COMMUNICATION__RECEIVE_END:
        if (resolve) {
          return getReceiveEnd();
        }
        return basicGetReceiveEnd();
      case CodegenPackage.COMMUNICATION__ID:
        return getId();
      case CodegenPackage.COMMUNICATION__NODES:
        return getNodes();
      case CodegenPackage.COMMUNICATION__RECEIVE_RELEASE:
        if (resolve) {
          return getReceiveRelease();
        }
        return basicGetReceiveRelease();
      case CodegenPackage.COMMUNICATION__SEND_RESERVE:
        if (resolve) {
          return getSendReserve();
        }
        return basicGetSendReserve();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param newValue
   *          the new value
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
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
      case CodegenPackage.COMMUNICATION__ID:
        setId((Integer) newValue);
        return;
      case CodegenPackage.COMMUNICATION__NODES:
        getNodes().clear();
        getNodes().addAll((Collection<? extends CommunicationNode>) newValue);
        return;
      case CodegenPackage.COMMUNICATION__RECEIVE_RELEASE:
        setReceiveRelease((Communication) newValue);
        return;
      case CodegenPackage.COMMUNICATION__SEND_RESERVE:
        setSendReserve((Communication) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case CodegenPackage.COMMUNICATION__DIRECTION:
        setDirection(CommunicationImpl.DIRECTION_EDEFAULT);
        return;
      case CodegenPackage.COMMUNICATION__DELIMITER:
        setDelimiter(CommunicationImpl.DELIMITER_EDEFAULT);
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
      case CodegenPackage.COMMUNICATION__ID:
        setId(CommunicationImpl.ID_EDEFAULT);
        return;
      case CodegenPackage.COMMUNICATION__NODES:
        getNodes().clear();
        return;
      case CodegenPackage.COMMUNICATION__RECEIVE_RELEASE:
        setReceiveRelease((Communication) null);
        return;
      case CodegenPackage.COMMUNICATION__SEND_RESERVE:
        setSendReserve((Communication) null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @return true, if successful
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case CodegenPackage.COMMUNICATION__DIRECTION:
        return this.direction != CommunicationImpl.DIRECTION_EDEFAULT;
      case CodegenPackage.COMMUNICATION__DELIMITER:
        return this.delimiter != CommunicationImpl.DELIMITER_EDEFAULT;
      case CodegenPackage.COMMUNICATION__DATA:
        return this.data != null;
      case CodegenPackage.COMMUNICATION__SEND_START:
        return this.sendStart != null;
      case CodegenPackage.COMMUNICATION__SEND_END:
        return this.sendEnd != null;
      case CodegenPackage.COMMUNICATION__RECEIVE_START:
        return this.receiveStart != null;
      case CodegenPackage.COMMUNICATION__RECEIVE_END:
        return this.receiveEnd != null;
      case CodegenPackage.COMMUNICATION__ID:
        return this.id != CommunicationImpl.ID_EDEFAULT;
      case CodegenPackage.COMMUNICATION__NODES:
        return (this.nodes != null) && !this.nodes.isEmpty();
      case CodegenPackage.COMMUNICATION__RECEIVE_RELEASE:
        return this.receiveRelease != null;
      case CodegenPackage.COMMUNICATION__SEND_RESERVE:
        return this.sendReserve != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the string
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuffer result = new StringBuffer(super.toString());
    result.append(" (direction: ");
    result.append(this.direction);
    result.append(", delimiter: ");
    result.append(this.delimiter);
    result.append(", id: ");
    result.append(this.id);
    result.append(')');
    return result.toString();
  }

} // CommunicationImpl
