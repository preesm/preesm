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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.ietr.preesm.codegen.xtend.model.codegen.Buffer;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.PortDirection;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall;
import org.ietr.preesm.codegen.xtend.model.codegen.SpecialType;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Special Call</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl#getType <em>Type</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl#getInputBuffers <em>Input
 * Buffers</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl#getOutputBuffers <em>Output
 * Buffers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SpecialCallImpl extends CallImpl implements SpecialCall {
  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final SpecialType TYPE_EDEFAULT = SpecialType.FORK;

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getType()
   * @generated
   * @ordered
   */
  protected SpecialType type = SpecialCallImpl.TYPE_EDEFAULT;

  /**
   * The cached value of the '{@link #getInputBuffers() <em>Input Buffers</em>}' reference list. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getInputBuffers()
   * @generated
   * @ordered
   */
  protected EList<Buffer> inputBuffers;

  /**
   * The cached value of the '{@link #getOutputBuffers() <em>Output Buffers</em>}' reference list. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getOutputBuffers()
   * @generated
   * @ordered
   */
  protected EList<Buffer> outputBuffers;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected SpecialCallImpl() {
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
    return CodegenPackage.Literals.SPECIAL_CALL;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the type
   * @generated
   */
  @Override
  public SpecialType getType() {
    return this.type;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newType
   *          the new type
   * @generated
   */
  @Override
  public void setType(final SpecialType newType) {
    final SpecialType oldType = this.type;
    this.type = newType == null ? SpecialCallImpl.TYPE_EDEFAULT : newType;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.SPECIAL_CALL__TYPE, oldType, this.type));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the input buffers
   * @generated
   */
  @Override
  public EList<Buffer> getInputBuffers() {
    if (this.inputBuffers == null) {
      this.inputBuffers = new EObjectResolvingEList<>(Buffer.class, this, CodegenPackage.SPECIAL_CALL__INPUT_BUFFERS);
    }
    return this.inputBuffers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the output buffers
   * @generated
   */
  @Override
  public EList<Buffer> getOutputBuffers() {
    if (this.outputBuffers == null) {
      this.outputBuffers = new EObjectEList<>(Buffer.class, this, CodegenPackage.SPECIAL_CALL__OUTPUT_BUFFERS);
    }
    return this.outputBuffers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is fork
   */
  @Override
  public boolean isFork() {
    return getType().equals(SpecialType.FORK);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is join
   */
  @Override
  public boolean isJoin() {
    return getType().equals(SpecialType.JOIN);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is broadcast
   */
  @Override
  public boolean isBroadcast() {
    return getType().equals(SpecialType.BROADCAST);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is round buffer
   */
  @Override
  public boolean isRoundBuffer() {
    return getType().equals(SpecialType.ROUND_BUFFER);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param buffer
   *          the buffer
   */
  @Override
  public void addInputBuffer(final Buffer buffer) {
    getInputBuffers().add(buffer);
    addParameter(buffer, PortDirection.INPUT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param buffer
   *          the buffer
   */
  @Override
  public void addOutputBuffer(final Buffer buffer) {
    getOutputBuffers().add(buffer);
    addParameter(buffer, PortDirection.OUTPUT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param buffer
   *          the buffer
   */
  @Override
  public void removeInputBuffer(final Buffer buffer) {
    getInputBuffers().remove(buffer);
    removeParameter(buffer);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param buffer
   *          the buffer
   */
  @Override
  public void removeOutputBuffer(final Buffer buffer) {
    getOutputBuffers().remove(buffer);
    removeParameter(buffer);
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
      case CodegenPackage.SPECIAL_CALL__TYPE:
        return getType();
      case CodegenPackage.SPECIAL_CALL__INPUT_BUFFERS:
        return getInputBuffers();
      case CodegenPackage.SPECIAL_CALL__OUTPUT_BUFFERS:
        return getOutputBuffers();
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
      case CodegenPackage.SPECIAL_CALL__TYPE:
        setType((SpecialType) newValue);
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
      case CodegenPackage.SPECIAL_CALL__TYPE:
        setType(SpecialCallImpl.TYPE_EDEFAULT);
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
      case CodegenPackage.SPECIAL_CALL__TYPE:
        return this.type != SpecialCallImpl.TYPE_EDEFAULT;
      case CodegenPackage.SPECIAL_CALL__INPUT_BUFFERS:
        return (this.inputBuffers != null) && !this.inputBuffers.isEmpty();
      case CodegenPackage.SPECIAL_CALL__OUTPUT_BUFFERS:
        return (this.outputBuffers != null) && !this.outputBuffers.isEmpty();
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
    result.append(" (type: ");
    result.append(this.type);
    result.append(')');
    return result.toString();
  }

} // SpecialCallImpl
