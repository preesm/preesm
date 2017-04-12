/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013 - 2015)
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
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.SubBuffer;
import org.ietr.preesm.memory.script.Range;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Buffer</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getSize <em>Size</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getChildrens <em>Childrens</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getTypeSize <em>Type Size</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getMergedRange <em>Merged Range</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#isLocal <em>Local</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BufferImpl extends VariableImpl implements Buffer {
  /**
   * The default value of the '{@link #getSize() <em>Size</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSize()
   * @generated
   * @ordered
   */
  protected static final int SIZE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getSize() <em>Size</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSize()
   * @generated
   * @ordered
   */
  protected int size = BufferImpl.SIZE_EDEFAULT;

  /**
   * The cached value of the '{@link #getChildrens() <em>Childrens</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getChildrens()
   * @generated
   * @ordered
   */
  protected EList<SubBuffer> childrens;

  /**
   * The default value of the '{@link #getTypeSize() <em>Type Size</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getTypeSize()
   * @generated
   * @ordered
   */
  protected static final int TYPE_SIZE_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getTypeSize() <em>Type Size</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getTypeSize()
   * @generated
   * @ordered
   */
  protected int typeSize = BufferImpl.TYPE_SIZE_EDEFAULT;

  /**
   * The cached value of the '{@link #getMergedRange() <em>Merged Range</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMergedRange()
   * @generated
   * @ordered
   */
  protected EList<Range> mergedRange;

  /**
   * The default value of the '{@link #isLocal() <em>Local</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isLocal()
   * @generated
   * @ordered
   */
  protected static final boolean LOCAL_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isLocal() <em>Local</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isLocal()
   * @generated
   * @ordered
   */
  protected boolean local = BufferImpl.LOCAL_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected BufferImpl() {
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
    return CodegenPackage.Literals.BUFFER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the size
   * @generated
   */
  @Override
  public int getSize() {
    return this.size;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSize
   *          the new size
   * @generated
   */
  @Override
  public void setSize(final int newSize) {
    final int oldSize = this.size;
    this.size = newSize;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER__SIZE, oldSize, this.size));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the childrens
   * @generated
   */
  @Override
  public EList<SubBuffer> getChildrens() {
    if (this.childrens == null) {
      this.childrens = new EObjectWithInverseResolvingEList<>(SubBuffer.class, this, CodegenPackage.BUFFER__CHILDRENS, CodegenPackage.SUB_BUFFER__CONTAINER);
    }
    return this.childrens;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the type size
   * @generated
   */
  @Override
  public int getTypeSize() {
    return this.typeSize;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newTypeSize
   *          the new type size
   * @generated
   */
  @Override
  public void setTypeSize(final int newTypeSize) {
    final int oldTypeSize = this.typeSize;
    this.typeSize = newTypeSize;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER__TYPE_SIZE, oldTypeSize, this.typeSize));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the merged range
   * @generated
   */
  @Override
  public EList<Range> getMergedRange() {
    return this.mergedRange;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newMergedRange
   *          the new merged range
   * @generated
   */
  @Override
  public void setMergedRange(final EList<Range> newMergedRange) {
    final EList<Range> oldMergedRange = this.mergedRange;
    this.mergedRange = newMergedRange;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER__MERGED_RANGE, oldMergedRange, this.mergedRange));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is local
   * @generated
   */
  @Override
  public boolean isLocal() {
    return this.local;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newLocal
   *          the new local
   * @generated
   */
  @Override
  public void setLocal(final boolean newLocal) {
    final boolean oldLocal = this.local;
    this.local = newLocal;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER__LOCAL, oldLocal, this.local));
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
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.BUFFER__CHILDRENS:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getChildrens()).basicAdd(otherEnd, msgs);
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
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case CodegenPackage.BUFFER__CHILDRENS:
        return ((InternalEList<?>) getChildrens()).basicRemove(otherEnd, msgs);
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
      case CodegenPackage.BUFFER__SIZE:
        return getSize();
      case CodegenPackage.BUFFER__CHILDRENS:
        return getChildrens();
      case CodegenPackage.BUFFER__TYPE_SIZE:
        return getTypeSize();
      case CodegenPackage.BUFFER__MERGED_RANGE:
        return getMergedRange();
      case CodegenPackage.BUFFER__LOCAL:
        return isLocal();
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
      case CodegenPackage.BUFFER__SIZE:
        setSize((Integer) newValue);
        return;
      case CodegenPackage.BUFFER__CHILDRENS:
        getChildrens().clear();
        getChildrens().addAll((Collection<? extends SubBuffer>) newValue);
        return;
      case CodegenPackage.BUFFER__TYPE_SIZE:
        setTypeSize((Integer) newValue);
        return;
      case CodegenPackage.BUFFER__MERGED_RANGE:
        setMergedRange((EList<Range>) newValue);
        return;
      case CodegenPackage.BUFFER__LOCAL:
        setLocal((Boolean) newValue);
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
      case CodegenPackage.BUFFER__SIZE:
        setSize(BufferImpl.SIZE_EDEFAULT);
        return;
      case CodegenPackage.BUFFER__CHILDRENS:
        getChildrens().clear();
        return;
      case CodegenPackage.BUFFER__TYPE_SIZE:
        setTypeSize(BufferImpl.TYPE_SIZE_EDEFAULT);
        return;
      case CodegenPackage.BUFFER__MERGED_RANGE:
        setMergedRange((EList<Range>) null);
        return;
      case CodegenPackage.BUFFER__LOCAL:
        setLocal(BufferImpl.LOCAL_EDEFAULT);
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
      case CodegenPackage.BUFFER__SIZE:
        return this.size != BufferImpl.SIZE_EDEFAULT;
      case CodegenPackage.BUFFER__CHILDRENS:
        return (this.childrens != null) && !this.childrens.isEmpty();
      case CodegenPackage.BUFFER__TYPE_SIZE:
        return this.typeSize != BufferImpl.TYPE_SIZE_EDEFAULT;
      case CodegenPackage.BUFFER__MERGED_RANGE:
        return this.mergedRange != null;
      case CodegenPackage.BUFFER__LOCAL:
        return this.local != BufferImpl.LOCAL_EDEFAULT;
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
    result.append(" (size: ");
    result.append(this.size);
    result.append(", typeSize: ");
    result.append(this.typeSize);
    result.append(", mergedRange: ");
    result.append(this.mergedRange);
    result.append(", local: ");
    result.append(this.local);
    result.append(')');
    return result.toString();
  }

} // BufferImpl
