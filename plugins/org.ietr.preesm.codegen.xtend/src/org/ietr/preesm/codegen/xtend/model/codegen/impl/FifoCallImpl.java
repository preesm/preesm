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

import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.Delimiter;
import org.ietr.preesm.codegen.xtend.model.codegen.FifoCall;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Fifo Call</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl#getDelimiter
 * <em>Delimiter</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl#getFifoHead
 * <em>Fifo Head</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.FifoCallImpl#getFifoTail
 * <em>Fifo Tail</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class FifoCallImpl extends CallImpl implements FifoCall {
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
	 * The cached value of the '{@link #getFifoHead() <em>Fifo Head</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getFifoHead()
	 * @generated
	 * @ordered
	 */
	protected FifoCall fifoHead;

	/**
	 * The cached value of the '{@link #getFifoTail() <em>Fifo Tail</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getFifoTail()
	 * @generated
	 * @ordered
	 */
	protected FifoCall fifoTail;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected FifoCallImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.FIFO_CALL;
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
					CodegenPackage.FIFO_CALL__DELIMITER, oldDelimiter,
					delimiter));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FifoCall getFifoHead() {
		if (fifoHead != null && fifoHead.eIsProxy()) {
			InternalEObject oldFifoHead = (InternalEObject) fifoHead;
			fifoHead = (FifoCall) eResolveProxy(oldFifoHead);
			if (fifoHead != oldFifoHead) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.FIFO_CALL__FIFO_HEAD, oldFifoHead,
							fifoHead));
			}
		}
		return fifoHead;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FifoCall basicGetFifoHead() {
		return fifoHead;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFifoHead(FifoCall newFifoHead) {
		FifoCall oldFifoHead = fifoHead;
		fifoHead = newFifoHead;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.FIFO_CALL__FIFO_HEAD, oldFifoHead, fifoHead));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FifoCall getFifoTail() {
		if (fifoTail != null && fifoTail.eIsProxy()) {
			InternalEObject oldFifoTail = (InternalEObject) fifoTail;
			fifoTail = (FifoCall) eResolveProxy(oldFifoTail);
			if (fifoTail != oldFifoTail) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.FIFO_CALL__FIFO_TAIL, oldFifoTail,
							fifoTail));
			}
		}
		return fifoTail;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FifoCall basicGetFifoTail() {
		return fifoTail;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setFifoTail(FifoCall newFifoTail) {
		FifoCall oldFifoTail = fifoTail;
		fifoTail = newFifoTail;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.FIFO_CALL__FIFO_TAIL, oldFifoTail, fifoTail));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case CodegenPackage.FIFO_CALL__DELIMITER:
			return getDelimiter();
		case CodegenPackage.FIFO_CALL__FIFO_HEAD:
			if (resolve)
				return getFifoHead();
			return basicGetFifoHead();
		case CodegenPackage.FIFO_CALL__FIFO_TAIL:
			if (resolve)
				return getFifoTail();
			return basicGetFifoTail();
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
		case CodegenPackage.FIFO_CALL__DELIMITER:
			setDelimiter((Delimiter) newValue);
			return;
		case CodegenPackage.FIFO_CALL__FIFO_HEAD:
			setFifoHead((FifoCall) newValue);
			return;
		case CodegenPackage.FIFO_CALL__FIFO_TAIL:
			setFifoTail((FifoCall) newValue);
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
		case CodegenPackage.FIFO_CALL__DELIMITER:
			setDelimiter(DELIMITER_EDEFAULT);
			return;
		case CodegenPackage.FIFO_CALL__FIFO_HEAD:
			setFifoHead((FifoCall) null);
			return;
		case CodegenPackage.FIFO_CALL__FIFO_TAIL:
			setFifoTail((FifoCall) null);
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
		case CodegenPackage.FIFO_CALL__DELIMITER:
			return delimiter != DELIMITER_EDEFAULT;
		case CodegenPackage.FIFO_CALL__FIFO_HEAD:
			return fifoHead != null;
		case CodegenPackage.FIFO_CALL__FIFO_TAIL:
			return fifoTail != null;
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
		result.append(" (delimiter: ");
		result.append(delimiter);
		result.append(')');
		return result.toString();
	}

} // FifoCallImpl
