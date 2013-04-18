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
import org.ietr.preesm.codegen.xtend.model.codegen.CallBlock;
import org.ietr.preesm.codegen.xtend.model.codegen.CodeElt;
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
		}
		return super.eIsSet(featureID);
	}

} // CoreBlockImpl
