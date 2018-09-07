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
package org.ietr.preesm.codegen.model.codegen.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.codegen.model.codegen.Buffer;
import org.ietr.preesm.codegen.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.model.codegen.FifoCall;
import org.ietr.preesm.codegen.model.codegen.FifoOperation;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Fifo Call</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FifoCallImpl#getOperation <em>Operation</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FifoCallImpl#getFifoHead <em>Fifo Head</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FifoCallImpl#getFifoTail <em>Fifo Tail</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FifoCallImpl#getHeadBuffer <em>Head Buffer</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FifoCallImpl#getBodyBuffer <em>Body Buffer</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FifoCallImpl extends CallImpl implements FifoCall {
  /**
   * The default value of the '{@link #getOperation() <em>Operation</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getOperation()
   * @generated
   * @ordered
   */
  protected static final FifoOperation OPERATION_EDEFAULT = FifoOperation.PUSH;

  /**
   * The cached value of the '{@link #getOperation() <em>Operation</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getOperation()
   * @generated
   * @ordered
   */
  protected FifoOperation operation = FifoCallImpl.OPERATION_EDEFAULT;

  /**
   * The cached value of the '{@link #getFifoHead() <em>Fifo Head</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getFifoHead()
   * @generated
   * @ordered
   */
  protected FifoCall fifoHead;

  /**
   * The cached value of the '{@link #getFifoTail() <em>Fifo Tail</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getFifoTail()
   * @generated
   * @ordered
   */
  protected FifoCall fifoTail;

  /**
   * The cached value of the '{@link #getHeadBuffer() <em>Head Buffer</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getHeadBuffer()
   * @generated
   * @ordered
   */
  protected Buffer headBuffer;

  /**
   * The cached value of the '{@link #getBodyBuffer() <em>Body Buffer</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getBodyBuffer()
   * @generated
   * @ordered
   */
  protected Buffer bodyBuffer;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected FifoCallImpl() {
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
    return CodegenPackage.Literals.FIFO_CALL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the operation
   * @generated
   */
  @Override
  public FifoOperation getOperation() {
    return this.operation;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newOperation
   *          the new operation
   * @generated
   */
  @Override
  public void setOperation(final FifoOperation newOperation) {
    final FifoOperation oldOperation = this.operation;
    this.operation = newOperation == null ? FifoCallImpl.OPERATION_EDEFAULT : newOperation;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.FIFO_CALL__OPERATION, oldOperation,
          this.operation));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo head
   * @generated
   */
  @Override
  public FifoCall getFifoHead() {
    if ((this.fifoHead != null) && this.fifoHead.eIsProxy()) {
      final InternalEObject oldFifoHead = (InternalEObject) this.fifoHead;
      this.fifoHead = (FifoCall) eResolveProxy(oldFifoHead);
      if (this.fifoHead != oldFifoHead) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.FIFO_CALL__FIFO_HEAD, oldFifoHead,
              this.fifoHead));
        }
      }
    }
    return this.fifoHead;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call
   * @generated
   */
  public FifoCall basicGetFifoHead() {
    return this.fifoHead;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newFifoHead
   *          the new fifo head
   * @generated
   */
  @Override
  public void setFifoHead(final FifoCall newFifoHead) {
    final FifoCall oldFifoHead = this.fifoHead;
    this.fifoHead = newFifoHead;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.FIFO_CALL__FIFO_HEAD, oldFifoHead,
          this.fifoHead));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo tail
   * @generated
   */
  @Override
  public FifoCall getFifoTail() {
    if ((this.fifoTail != null) && this.fifoTail.eIsProxy()) {
      final InternalEObject oldFifoTail = (InternalEObject) this.fifoTail;
      this.fifoTail = (FifoCall) eResolveProxy(oldFifoTail);
      if (this.fifoTail != oldFifoTail) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.FIFO_CALL__FIFO_TAIL, oldFifoTail,
              this.fifoTail));
        }
      }
    }
    return this.fifoTail;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo call
   * @generated
   */
  public FifoCall basicGetFifoTail() {
    return this.fifoTail;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newFifoTail
   *          the new fifo tail
   * @generated
   */
  @Override
  public void setFifoTail(final FifoCall newFifoTail) {
    final FifoCall oldFifoTail = this.fifoTail;
    this.fifoTail = newFifoTail;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.FIFO_CALL__FIFO_TAIL, oldFifoTail,
          this.fifoTail));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the head buffer
   * @generated
   */
  @Override
  public Buffer getHeadBuffer() {
    if ((this.headBuffer != null) && this.headBuffer.eIsProxy()) {
      final InternalEObject oldHeadBuffer = (InternalEObject) this.headBuffer;
      this.headBuffer = (Buffer) eResolveProxy(oldHeadBuffer);
      if (this.headBuffer != oldHeadBuffer) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.FIFO_CALL__HEAD_BUFFER,
              oldHeadBuffer, this.headBuffer));
        }
      }
    }
    return this.headBuffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer
   * @generated
   */
  public Buffer basicGetHeadBuffer() {
    return this.headBuffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newHeadBuffer
   *          the new head buffer
   * @generated
   */
  @Override
  public void setHeadBuffer(final Buffer newHeadBuffer) {
    final Buffer oldHeadBuffer = this.headBuffer;
    this.headBuffer = newHeadBuffer;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.FIFO_CALL__HEAD_BUFFER, oldHeadBuffer,
          this.headBuffer));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the body buffer
   * @generated
   */
  @Override
  public Buffer getBodyBuffer() {
    if ((this.bodyBuffer != null) && this.bodyBuffer.eIsProxy()) {
      final InternalEObject oldBodyBuffer = (InternalEObject) this.bodyBuffer;
      this.bodyBuffer = (Buffer) eResolveProxy(oldBodyBuffer);
      if (this.bodyBuffer != oldBodyBuffer) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.FIFO_CALL__BODY_BUFFER,
              oldBodyBuffer, this.bodyBuffer));
        }
      }
    }
    return this.bodyBuffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer
   * @generated
   */
  public Buffer basicGetBodyBuffer() {
    return this.bodyBuffer;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newBodyBuffer
   *          the new body buffer
   * @generated
   */
  @Override
  public void setBodyBuffer(final Buffer newBodyBuffer) {
    final Buffer oldBodyBuffer = this.bodyBuffer;
    this.bodyBuffer = newBodyBuffer;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.FIFO_CALL__BODY_BUFFER, oldBodyBuffer,
          this.bodyBuffer));
    }
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
      case CodegenPackage.FIFO_CALL__OPERATION:
        return getOperation();
      case CodegenPackage.FIFO_CALL__FIFO_HEAD:
        if (resolve) {
          return getFifoHead();
        }
        return basicGetFifoHead();
      case CodegenPackage.FIFO_CALL__FIFO_TAIL:
        if (resolve) {
          return getFifoTail();
        }
        return basicGetFifoTail();
      case CodegenPackage.FIFO_CALL__HEAD_BUFFER:
        if (resolve) {
          return getHeadBuffer();
        }
        return basicGetHeadBuffer();
      case CodegenPackage.FIFO_CALL__BODY_BUFFER:
        if (resolve) {
          return getBodyBuffer();
        }
        return basicGetBodyBuffer();
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
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case CodegenPackage.FIFO_CALL__OPERATION:
        setOperation((FifoOperation) newValue);
        return;
      case CodegenPackage.FIFO_CALL__FIFO_HEAD:
        setFifoHead((FifoCall) newValue);
        return;
      case CodegenPackage.FIFO_CALL__FIFO_TAIL:
        setFifoTail((FifoCall) newValue);
        return;
      case CodegenPackage.FIFO_CALL__HEAD_BUFFER:
        setHeadBuffer((Buffer) newValue);
        return;
      case CodegenPackage.FIFO_CALL__BODY_BUFFER:
        setBodyBuffer((Buffer) newValue);
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
      case CodegenPackage.FIFO_CALL__OPERATION:
        setOperation(FifoCallImpl.OPERATION_EDEFAULT);
        return;
      case CodegenPackage.FIFO_CALL__FIFO_HEAD:
        setFifoHead((FifoCall) null);
        return;
      case CodegenPackage.FIFO_CALL__FIFO_TAIL:
        setFifoTail((FifoCall) null);
        return;
      case CodegenPackage.FIFO_CALL__HEAD_BUFFER:
        setHeadBuffer((Buffer) null);
        return;
      case CodegenPackage.FIFO_CALL__BODY_BUFFER:
        setBodyBuffer((Buffer) null);
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
      case CodegenPackage.FIFO_CALL__OPERATION:
        return this.operation != FifoCallImpl.OPERATION_EDEFAULT;
      case CodegenPackage.FIFO_CALL__FIFO_HEAD:
        return this.fifoHead != null;
      case CodegenPackage.FIFO_CALL__FIFO_TAIL:
        return this.fifoTail != null;
      case CodegenPackage.FIFO_CALL__HEAD_BUFFER:
        return this.headBuffer != null;
      case CodegenPackage.FIFO_CALL__BODY_BUFFER:
        return this.bodyBuffer != null;
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

    final StringBuilder result = new StringBuilder(super.toString());
    result.append(" (operation: ");
    result.append(this.operation);
    result.append(')');
    return result.toString();
  }

} // FifoCallImpl
