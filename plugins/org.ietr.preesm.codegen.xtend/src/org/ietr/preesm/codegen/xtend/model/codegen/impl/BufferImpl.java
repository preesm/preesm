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

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Buffer</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getSize
 * <em>Size</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getChildrens
 * <em>Childrens</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getTypeSize
 * <em>Type Size</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.BufferImpl#getMergedRange
 * <em>Merged Range</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class BufferImpl extends VariableImpl implements Buffer {
	/**
	 * The default value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected static final int SIZE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected int size = SIZE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChildrens() <em>Childrens</em>}'
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getChildrens()
	 * @generated
	 * @ordered
	 */
	protected EList<SubBuffer> childrens;

	/**
	 * The default value of the '{@link #getTypeSize() <em>Type Size</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTypeSize()
	 * @generated
	 * @ordered
	 */
	protected static final int TYPE_SIZE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getTypeSize() <em>Type Size</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTypeSize()
	 * @generated
	 * @ordered
	 */
	protected int typeSize = TYPE_SIZE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMergedRange() <em>Merged Range</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMergedRange()
	 * @generated
	 * @ordered
	 */
	protected EList<org.ietr.preesm.memory.script.Range> mergedRange;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected BufferImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.BUFFER;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getSize() {
		return size;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setSize(int newSize) {
		int oldSize = size;
		size = newSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.BUFFER__SIZE, oldSize, size));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<SubBuffer> getChildrens() {
		if (childrens == null) {
			childrens = new EObjectWithInverseResolvingEList<SubBuffer>(
					SubBuffer.class, this, CodegenPackage.BUFFER__CHILDRENS,
					CodegenPackage.SUB_BUFFER__CONTAINER);
		}
		return childrens;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public int getTypeSize() {
		return typeSize;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setTypeSize(int newTypeSize) {
		int oldTypeSize = typeSize;
		typeSize = newTypeSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.BUFFER__TYPE_SIZE, oldTypeSize, typeSize));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public EList<org.ietr.preesm.memory.script.Range> getMergedRange() {
		return mergedRange;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setMergedRange(
			EList<org.ietr.preesm.memory.script.Range> newMergedRange) {
		EList<org.ietr.preesm.memory.script.Range> oldMergedRange = mergedRange;
		mergedRange = newMergedRange;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.BUFFER__MERGED_RANGE, oldMergedRange,
					mergedRange));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case CodegenPackage.BUFFER__CHILDRENS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getChildrens())
					.basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case CodegenPackage.BUFFER__CHILDRENS:
			return ((InternalEList<?>) getChildrens()).basicRemove(otherEnd,
					msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case CodegenPackage.BUFFER__SIZE:
			return getSize();
		case CodegenPackage.BUFFER__CHILDRENS:
			return getChildrens();
		case CodegenPackage.BUFFER__TYPE_SIZE:
			return getTypeSize();
		case CodegenPackage.BUFFER__MERGED_RANGE:
			return getMergedRange();
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
	public void eSet(int featureID, Object newValue) {
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
			setMergedRange((EList<org.ietr.preesm.memory.script.Range>) newValue);
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
		case CodegenPackage.BUFFER__SIZE:
			setSize(SIZE_EDEFAULT);
			return;
		case CodegenPackage.BUFFER__CHILDRENS:
			getChildrens().clear();
			return;
		case CodegenPackage.BUFFER__TYPE_SIZE:
			setTypeSize(TYPE_SIZE_EDEFAULT);
			return;
		case CodegenPackage.BUFFER__MERGED_RANGE:
			setMergedRange((EList<org.ietr.preesm.memory.script.Range>) null);
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
		case CodegenPackage.BUFFER__SIZE:
			return size != SIZE_EDEFAULT;
		case CodegenPackage.BUFFER__CHILDRENS:
			return childrens != null && !childrens.isEmpty();
		case CodegenPackage.BUFFER__TYPE_SIZE:
			return typeSize != TYPE_SIZE_EDEFAULT;
		case CodegenPackage.BUFFER__MERGED_RANGE:
			return mergedRange != null;
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
		result.append(" (size: ");
		result.append(size);
		result.append(", typeSize: ");
		result.append(typeSize);
		result.append(", mergedRange: ");
		result.append(mergedRange);
		result.append(')');
		return result.toString();
	}

} // BufferImpl
