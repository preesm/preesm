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
package org.ietr.preesm.codegen.model.codegen.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.ietr.preesm.codegen.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.model.codegen.FiniteLoopBlock;
import org.ietr.preesm.codegen.model.codegen.IntVar;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Finite Loop Block</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FiniteLoopBlockImpl#getNbIter <em>Nb Iter</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FiniteLoopBlockImpl#getIter <em>Iter</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FiniteLoopBlockImpl#getInBuffers <em>In Buffers</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.model.codegen.impl.FiniteLoopBlockImpl#getOutBuffers <em>Out Buffers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FiniteLoopBlockImpl extends LoopBlockImpl implements FiniteLoopBlock {
  /**
   * The default value of the '{@link #getNbIter() <em>Nb Iter</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getNbIter()
   * @generated
   * @ordered
   */
  protected static final int NB_ITER_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getNbIter() <em>Nb Iter</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getNbIter()
   * @generated
   * @ordered
   */
  protected int nbIter = FiniteLoopBlockImpl.NB_ITER_EDEFAULT;

  /**
   * The cached value of the '{@link #getIter() <em>Iter</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getIter()
   * @generated
   * @ordered
   */
  protected IntVar iter;

  /**
   * The cached value of the '{@link #getInBuffers() <em>In Buffers</em>}' reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getInBuffers()
   * @generated
   * @ordered
   */
  protected EList<BufferIterator> inBuffers;

  /**
   * The cached value of the '{@link #getOutBuffers() <em>Out Buffers</em>}' reference list. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @see #getOutBuffers()
   * @generated
   * @ordered
   */
  protected EList<BufferIterator> outBuffers;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected FiniteLoopBlockImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return CodegenPackage.Literals.FINITE_LOOP_BLOCK;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int getNbIter() {
    return this.nbIter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setNbIter(final int newNbIter) {
    final int oldNbIter = this.nbIter;
    this.nbIter = newNbIter;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.FINITE_LOOP_BLOCK__NB_ITER, oldNbIter,
          this.nbIter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IntVar getIter() {
    if ((this.iter != null) && this.iter.eIsProxy()) {
      final InternalEObject oldIter = (InternalEObject) this.iter;
      this.iter = (IntVar) eResolveProxy(oldIter);
      if (this.iter != oldIter) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.FINITE_LOOP_BLOCK__ITER, oldIter,
              this.iter));
        }
      }
    }
    return this.iter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public IntVar basicGetIter() {
    return this.iter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setIter(final IntVar newIter) {
    final IntVar oldIter = this.iter;
    this.iter = newIter;
    if (eNotificationRequired()) {
      eNotify(
          new ENotificationImpl(this, Notification.SET, CodegenPackage.FINITE_LOOP_BLOCK__ITER, oldIter, this.iter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<BufferIterator> getInBuffers() {
    if (this.inBuffers == null) {
      this.inBuffers = new EObjectResolvingEList<>(BufferIterator.class, this,
          CodegenPackage.FINITE_LOOP_BLOCK__IN_BUFFERS);
    }
    return this.inBuffers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<BufferIterator> getOutBuffers() {
    if (this.outBuffers == null) {
      this.outBuffers = new EObjectResolvingEList<>(BufferIterator.class, this,
          CodegenPackage.FINITE_LOOP_BLOCK__OUT_BUFFERS);
    }
    return this.outBuffers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case CodegenPackage.FINITE_LOOP_BLOCK__NB_ITER:
        return getNbIter();
      case CodegenPackage.FINITE_LOOP_BLOCK__ITER:
        if (resolve) {
          return getIter();
        }
        return basicGetIter();
      case CodegenPackage.FINITE_LOOP_BLOCK__IN_BUFFERS:
        return getInBuffers();
      case CodegenPackage.FINITE_LOOP_BLOCK__OUT_BUFFERS:
        return getOutBuffers();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case CodegenPackage.FINITE_LOOP_BLOCK__NB_ITER:
        setNbIter((Integer) newValue);
        return;
      case CodegenPackage.FINITE_LOOP_BLOCK__ITER:
        setIter((IntVar) newValue);
        return;
      case CodegenPackage.FINITE_LOOP_BLOCK__IN_BUFFERS:
        getInBuffers().clear();
        getInBuffers().addAll((Collection<? extends BufferIterator>) newValue);
        return;
      case CodegenPackage.FINITE_LOOP_BLOCK__OUT_BUFFERS:
        getOutBuffers().clear();
        getOutBuffers().addAll((Collection<? extends BufferIterator>) newValue);
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
  public void eUnset(final int featureID) {
    switch (featureID) {
      case CodegenPackage.FINITE_LOOP_BLOCK__NB_ITER:
        setNbIter(FiniteLoopBlockImpl.NB_ITER_EDEFAULT);
        return;
      case CodegenPackage.FINITE_LOOP_BLOCK__ITER:
        setIter((IntVar) null);
        return;
      case CodegenPackage.FINITE_LOOP_BLOCK__IN_BUFFERS:
        getInBuffers().clear();
        return;
      case CodegenPackage.FINITE_LOOP_BLOCK__OUT_BUFFERS:
        getOutBuffers().clear();
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
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case CodegenPackage.FINITE_LOOP_BLOCK__NB_ITER:
        return this.nbIter != FiniteLoopBlockImpl.NB_ITER_EDEFAULT;
      case CodegenPackage.FINITE_LOOP_BLOCK__ITER:
        return this.iter != null;
      case CodegenPackage.FINITE_LOOP_BLOCK__IN_BUFFERS:
        return (this.inBuffers != null) && !this.inBuffers.isEmpty();
      case CodegenPackage.FINITE_LOOP_BLOCK__OUT_BUFFERS:
        return (this.outBuffers != null) && !this.outBuffers.isEmpty();
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
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuilder result = new StringBuilder(super.toString());
    result.append(" (nbIter: ");
    result.append(this.nbIter);
    result.append(')');
    return result.toString();
  }

} // FiniteLoopBlockImpl
