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
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.IntVar;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Buffer Iterator</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl#getIterSize <em>Iter Size</em>}</li>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl#getIter <em>Iter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BufferIteratorImpl extends SubBufferImpl implements BufferIterator {
	/**
	 * The default value of the '{@link #getIterSize() <em>Iter Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIterSize()
	 * @generated
	 * @ordered
	 */
	protected static final int ITER_SIZE_EDEFAULT = 0;
	/**
	 * The cached value of the '{@link #getIterSize() <em>Iter Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIterSize()
	 * @generated
	 * @ordered
	 */
	protected int iterSize = ITER_SIZE_EDEFAULT;
	/**
	 * The cached value of the '{@link #getIter() <em>Iter</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getIter()
	 * @generated
	 * @ordered
	 */
	protected IntVar iter;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected BufferIteratorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.BUFFER_ITERATOR;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getIterSize() {
		return iterSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIterSize(int newIterSize) {
		int oldIterSize = iterSize;
		iterSize = newIterSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER_ITERATOR__ITER_SIZE, oldIterSize, iterSize));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public IntVar getIter() {
		if (iter != null && iter.eIsProxy()) {
			InternalEObject oldIter = (InternalEObject)iter;
			iter = (IntVar)eResolveProxy(oldIter);
			if (iter != oldIter) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.BUFFER_ITERATOR__ITER, oldIter, iter));
			}
		}
		return iter;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public IntVar basicGetIter() {
		return iter;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setIter(IntVar newIter) {
		IntVar oldIter = iter;
		iter = newIter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER_ITERATOR__ITER, oldIter, iter));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodegenPackage.BUFFER_ITERATOR__ITER_SIZE:
				return getIterSize();
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				if (resolve) return getIter();
				return basicGetIter();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CodegenPackage.BUFFER_ITERATOR__ITER_SIZE:
				setIterSize((Integer)newValue);
				return;
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				setIter((IntVar)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case CodegenPackage.BUFFER_ITERATOR__ITER_SIZE:
				setIterSize(ITER_SIZE_EDEFAULT);
				return;
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				setIter((IntVar)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case CodegenPackage.BUFFER_ITERATOR__ITER_SIZE:
				return iterSize != ITER_SIZE_EDEFAULT;
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				return iter != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (iterSize: ");
		result.append(iterSize);
		result.append(')');
		return result.toString();
	}

} // BufferIteratorImpl
