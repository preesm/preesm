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
import org.ietr.preesm.codegen.xtend.model.codegen.BufferIterator;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Buffer Iterator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl#getBuffer <em>Buffer</em>}</li>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl#getIter <em>Iter</em>}</li>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferIteratorImpl#getIterStep <em>Iter Step</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BufferIteratorImpl extends VariableImpl implements BufferIterator {
	/**
	 * The cached value of the '{@link #getBuffer() <em>Buffer</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBuffer()
	 * @generated
	 * @ordered
	 */
	protected Buffer buffer;
	/**
	 * The default value of the '{@link #getIter() <em>Iter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIter()
	 * @generated
	 * @ordered
	 */
	protected static final int ITER_EDEFAULT = 0;
	/**
	 * The cached value of the '{@link #getIter() <em>Iter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIter()
	 * @generated
	 * @ordered
	 */
	protected int iter = ITER_EDEFAULT;
	/**
	 * The default value of the '{@link #getIterStep() <em>Iter Step</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIterStep()
	 * @generated
	 * @ordered
	 */
	protected static final int ITER_STEP_EDEFAULT = 0;
	/**
	 * The cached value of the '{@link #getIterStep() <em>Iter Step</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIterStep()
	 * @generated
	 * @ordered
	 */
	protected int iterStep = ITER_STEP_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BufferIteratorImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
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
	public Buffer getBuffer() {
		if (buffer != null && buffer.eIsProxy()) {
			InternalEObject oldBuffer = (InternalEObject)buffer;
			buffer = (Buffer)eResolveProxy(oldBuffer);
			if (buffer != oldBuffer) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, CodegenPackage.BUFFER_ITERATOR__BUFFER, oldBuffer, buffer));
			}
		}
		return buffer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Buffer basicGetBuffer() {
		return buffer;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBuffer(Buffer newBuffer) {
		Buffer oldBuffer = buffer;
		buffer = newBuffer;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER_ITERATOR__BUFFER, oldBuffer, buffer));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getIter() {
		return iter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIter(int newIter) {
		int oldIter = iter;
		iter = newIter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER_ITERATOR__ITER, oldIter, iter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getIterStep() {
		return iterStep;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIterStep(int newIterStep) {
		int oldIterStep = iterStep;
		iterStep = newIterStep;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.BUFFER_ITERATOR__ITER_STEP, oldIterStep, iterStep));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodegenPackage.BUFFER_ITERATOR__BUFFER:
				if (resolve) return getBuffer();
				return basicGetBuffer();
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				return getIter();
			case CodegenPackage.BUFFER_ITERATOR__ITER_STEP:
				return getIterStep();
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
			case CodegenPackage.BUFFER_ITERATOR__BUFFER:
				setBuffer((Buffer)newValue);
				return;
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				setIter((Integer)newValue);
				return;
			case CodegenPackage.BUFFER_ITERATOR__ITER_STEP:
				setIterStep((Integer)newValue);
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
			case CodegenPackage.BUFFER_ITERATOR__BUFFER:
				setBuffer((Buffer)null);
				return;
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				setIter(ITER_EDEFAULT);
				return;
			case CodegenPackage.BUFFER_ITERATOR__ITER_STEP:
				setIterStep(ITER_STEP_EDEFAULT);
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
			case CodegenPackage.BUFFER_ITERATOR__BUFFER:
				return buffer != null;
			case CodegenPackage.BUFFER_ITERATOR__ITER:
				return iter != ITER_EDEFAULT;
			case CodegenPackage.BUFFER_ITERATOR__ITER_STEP:
				return iterStep != ITER_STEP_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (iter: ");
		result.append(iter);
		result.append(", iterStep: ");
		result.append(iterStep);
		result.append(')');
		return result.toString();
	}

} //BufferIteratorImpl
