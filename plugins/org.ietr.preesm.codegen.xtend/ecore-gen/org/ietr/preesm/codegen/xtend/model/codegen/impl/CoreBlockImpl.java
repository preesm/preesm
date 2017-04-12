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
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenFactory;
import org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage;
import org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.LoopBlock;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Core Block</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl#getLoopBlock
 * <em>Loop Block</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl#getInitBlock
 * <em>Init Block</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.CoreBlockImpl#getCoreType
 * <em>Core Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CoreBlockImpl extends BlockImpl implements CoreBlock {
	/**
	 * The cached value of the '{@link #getLoopBlock() <em>Loop Block</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getLoopBlock()
	 * @generated
	 * @ordered
	 */
	protected LoopBlock loopBlock;
	/**
	 * The cached value of the '{@link #getInitBlock() <em>Init Block</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getInitBlock()
	 * @generated
	 * @ordered
	 */
	protected CallBlock initBlock;

	/**
	 * The default value of the '{@link #getCoreType() <em>Core Type</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getCoreType()
	 * @generated
	 * @ordered
	 */
	protected static final String CORE_TYPE_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getCoreType() <em>Core Type</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getCoreType()
	 * @generated
	 * @ordered
	 */
	protected String coreType = CORE_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> Default Constructor also create the init and loop
	 * blocks and add them to the {@link CodeElt} list.<!-- end-user-doc -->
	 * 
	 */
	protected CoreBlockImpl() {
		super();
		initBlock = CodegenFactory.eINSTANCE.createCallBlock();
		this.getCodeElts().add(initBlock);
		loopBlock = CodegenFactory.eINSTANCE.createLoopBlock();
		this.getCodeElts().add(loopBlock);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.CORE_BLOCK;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public LoopBlock getLoopBlock() {
		if (loopBlock != null && loopBlock.eIsProxy()) {
			InternalEObject oldLoopBlock = (InternalEObject) loopBlock;
			loopBlock = (LoopBlock) eResolveProxy(oldLoopBlock);
			if (loopBlock != oldLoopBlock) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.CORE_BLOCK__LOOP_BLOCK,
							oldLoopBlock, loopBlock));
			}
		}
		return loopBlock;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public LoopBlock basicGetLoopBlock() {
		return loopBlock;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setLoopBlock(LoopBlock newLoopBlock) {
		LoopBlock oldLoopBlock = loopBlock;
		loopBlock = newLoopBlock;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.CORE_BLOCK__LOOP_BLOCK, oldLoopBlock,
					loopBlock));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public CallBlock getInitBlock() {
		if (initBlock != null && initBlock.eIsProxy()) {
			InternalEObject oldInitBlock = (InternalEObject) initBlock;
			initBlock = (CallBlock) eResolveProxy(oldInitBlock);
			if (initBlock != oldInitBlock) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CodegenPackage.CORE_BLOCK__INIT_BLOCK,
							oldInitBlock, initBlock));
			}
		}
		return initBlock;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public CallBlock basicGetInitBlock() {
		return initBlock;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setInitBlock(CallBlock newInitBlock) {
		CallBlock oldInitBlock = initBlock;
		initBlock = newInitBlock;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.CORE_BLOCK__INIT_BLOCK, oldInitBlock,
					initBlock));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getCoreType() {
		return coreType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setCoreType(String newCoreType) {
		String oldCoreType = coreType;
		coreType = newCoreType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.CORE_BLOCK__CORE_TYPE, oldCoreType, coreType));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case CodegenPackage.CORE_BLOCK__LOOP_BLOCK:
			if (resolve)
				return getLoopBlock();
			return basicGetLoopBlock();
		case CodegenPackage.CORE_BLOCK__INIT_BLOCK:
			if (resolve)
				return getInitBlock();
			return basicGetInitBlock();
		case CodegenPackage.CORE_BLOCK__CORE_TYPE:
			return getCoreType();
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
		case CodegenPackage.CORE_BLOCK__LOOP_BLOCK:
			setLoopBlock((LoopBlock) newValue);
			return;
		case CodegenPackage.CORE_BLOCK__INIT_BLOCK:
			setInitBlock((CallBlock) newValue);
			return;
		case CodegenPackage.CORE_BLOCK__CORE_TYPE:
			setCoreType((String) newValue);
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
		case CodegenPackage.CORE_BLOCK__LOOP_BLOCK:
			setLoopBlock((LoopBlock) null);
			return;
		case CodegenPackage.CORE_BLOCK__INIT_BLOCK:
			setInitBlock((CallBlock) null);
			return;
		case CodegenPackage.CORE_BLOCK__CORE_TYPE:
			setCoreType(CORE_TYPE_EDEFAULT);
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
		case CodegenPackage.CORE_BLOCK__LOOP_BLOCK:
			return loopBlock != null;
		case CodegenPackage.CORE_BLOCK__INIT_BLOCK:
			return initBlock != null;
		case CodegenPackage.CORE_BLOCK__CORE_TYPE:
			return CORE_TYPE_EDEFAULT == null ? coreType != null
					: !CORE_TYPE_EDEFAULT.equals(coreType);
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
		result.append(" (coreType: ");
		result.append(coreType);
		result.append(')');
		return result.toString();
	}

} // CoreBlockImpl
