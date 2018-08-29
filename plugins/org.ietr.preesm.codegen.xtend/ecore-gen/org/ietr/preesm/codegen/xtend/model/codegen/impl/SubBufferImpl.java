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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.Block;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Sub Buffer</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl#getContainer <em>Container</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SubBufferImpl#getOffset <em>Offset</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SubBufferImpl extends BufferImpl implements SubBuffer {
  /**
   * The cached value of the '{@link #getContainer() <em>Container</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getContainer()
   * @generated
   * @ordered
   */
  protected Buffer container;

  /**
   * The default value of the '{@link #getOffset() <em>Offset</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getOffset()
   * @generated
   * @ordered
   */
  protected static final int OFFSET_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getOffset() <em>Offset</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getOffset()
   * @generated
   * @ordered
   */
  protected int offset = SubBufferImpl.OFFSET_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected SubBufferImpl() {
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
    return CodegenPackage.Literals.SUB_BUFFER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the container
   * @generated
   */
  @Override
  public Buffer getContainer() {
    if ((this.container != null) && this.container.eIsProxy()) {
      final InternalEObject oldContainer = (InternalEObject) this.container;
      this.container = (Buffer) eResolveProxy(oldContainer);
      if (this.container != oldContainer) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.SUB_BUFFER__CONTAINER, oldContainer,
              this.container));
        }
      }
    }
    return this.container;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the buffer
   * @generated
   */
  public Buffer basicGetContainer() {
    return this.container;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newContainer
   *          the new container
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetContainer(final Buffer newContainer, NotificationChain msgs) {
    final Buffer oldContainer = this.container;
    this.container = newContainer;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
          CodegenPackage.SUB_BUFFER__CONTAINER, oldContainer, newContainer);
      if (msgs == null) {
        msgs = notification;
      } else {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newContainer
   *          the new container
   */
  @Override
  public void setContainer(final Buffer newContainer) {
    if (newContainer != this.container) {
      NotificationChain msgs = null;
      if (this.container != null) {
        msgs = ((InternalEObject) this.container).eInverseRemove(this, CodegenPackage.BUFFER__CHILDRENS, Buffer.class,
            msgs);
      }
      if (newContainer != null) {
        msgs = ((InternalEObject) newContainer).eInverseAdd(this, CodegenPackage.BUFFER__CHILDRENS, Buffer.class, msgs);
      }
      msgs = basicSetContainer(newContainer, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.SUB_BUFFER__CONTAINER, newContainer,
          newContainer));
    }
    if (getCreator() != null) {
      newContainer.getUsers().add(getCreator());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.codegen.xtend.model.codegen.impl.VariableImpl#setCreator(org.ietr.preesm.codegen.xtend.model.
   * codegen.Block)
   */
  @Override
  public void setCreator(final Block newCreator) {
    super.setCreator(newCreator);
    final Buffer container = getContainer();
    if (container != null) {
      container.getUsers().add(newCreator);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the offset
   * @generated
   */
  @Override
  public int getOffset() {
    return this.offset;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newOffset
   *          the new offset
   * @generated
   */
  @Override
  public void setOffset(final int newOffset) {
    final int oldOffset = this.offset;
    this.offset = newOffset;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.SUB_BUFFER__OFFSET, oldOffset, this.offset));
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
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.SUB_BUFFER__CONTAINER:
        if (this.container != null) {
          msgs = ((InternalEObject) this.container).eInverseRemove(this, CodegenPackage.BUFFER__CHILDRENS, Buffer.class,
              msgs);
        }
        return basicSetContainer((Buffer) otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
      final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.SUB_BUFFER__CONTAINER:
        return basicSetContainer(null, msgs);
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
      case CodegenPackage.SUB_BUFFER__CONTAINER:
        if (resolve) {
          return getContainer();
        }
        return basicGetContainer();
      case CodegenPackage.SUB_BUFFER__OFFSET:
        return getOffset();
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
      case CodegenPackage.SUB_BUFFER__CONTAINER:
        setContainer((Buffer) newValue);
        return;
      case CodegenPackage.SUB_BUFFER__OFFSET:
        setOffset((Integer) newValue);
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
      case CodegenPackage.SUB_BUFFER__CONTAINER:
        setContainer((Buffer) null);
        return;
      case CodegenPackage.SUB_BUFFER__OFFSET:
        setOffset(SubBufferImpl.OFFSET_EDEFAULT);
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
      case CodegenPackage.SUB_BUFFER__CONTAINER:
        return this.container != null;
      case CodegenPackage.SUB_BUFFER__OFFSET:
        return this.offset != SubBufferImpl.OFFSET_EDEFAULT;
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
    result.append(" (offset: ");
    result.append(this.offset);
    result.append(')');
    return result.toString();
  }
} // SubBufferImpl
