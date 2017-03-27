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

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Special Call</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl#getType
 * <em>Type</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl#getInputBuffers
 * <em>Input Buffers</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.impl.SpecialCallImpl#getOutputBuffers
 * <em>Output Buffers</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class SpecialCallImpl extends CallImpl implements SpecialCall {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final SpecialType TYPE_EDEFAULT = SpecialType.FORK;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected SpecialType type = TYPE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInputBuffers()
	 * <em>Input Buffers</em>}' reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getInputBuffers()
	 * @generated
	 * @ordered
	 */
	protected EList<Buffer> inputBuffers;

	/**
	 * The cached value of the '{@link #getOutputBuffers()
	 * <em>Output Buffers</em>}' reference list. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getOutputBuffers()
	 * @generated
	 * @ordered
	 */
	protected EList<Buffer> outputBuffers;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected SpecialCallImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.SPECIAL_CALL;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public SpecialType getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setType(SpecialType newType) {
		SpecialType oldType = type;
		type = newType == null ? TYPE_EDEFAULT : newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CodegenPackage.SPECIAL_CALL__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Buffer> getInputBuffers() {
		if (inputBuffers == null) {
			inputBuffers = new EObjectResolvingEList<Buffer>(Buffer.class,
					this, CodegenPackage.SPECIAL_CALL__INPUT_BUFFERS);
		}
		return inputBuffers;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Buffer> getOutputBuffers() {
		if (outputBuffers == null) {
			outputBuffers = new EObjectEList<Buffer>(Buffer.class, this,
					CodegenPackage.SPECIAL_CALL__OUTPUT_BUFFERS);
		}
		return outputBuffers;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public boolean isFork() {
		return this.getType().equals(SpecialType.FORK);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public boolean isJoin() {
		return this.getType().equals(SpecialType.JOIN);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public boolean isBroadcast() {
		return this.getType().equals(SpecialType.BROADCAST);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public boolean isRoundBuffer() {
		return this.getType().equals(SpecialType.ROUND_BUFFER);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public void addInputBuffer(Buffer buffer) {
		getInputBuffers().add(buffer);
		addParameter(buffer, PortDirection.INPUT);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public void addOutputBuffer(Buffer buffer) {
		getOutputBuffers().add(buffer);
		addParameter(buffer, PortDirection.OUTPUT);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public void removeInputBuffer(Buffer buffer) {
		getInputBuffers().remove(buffer);
		removeParameter(buffer);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public void removeOutputBuffer(Buffer buffer) {
		getOutputBuffers().remove(buffer);
		removeParameter(buffer);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case CodegenPackage.SPECIAL_CALL__TYPE:
			setType((SpecialType) newValue);
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
		case CodegenPackage.SPECIAL_CALL__TYPE:
			setType(TYPE_EDEFAULT);
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
		case CodegenPackage.SPECIAL_CALL__TYPE:
			return type != TYPE_EDEFAULT;
		case CodegenPackage.SPECIAL_CALL__INPUT_BUFFERS:
			return inputBuffers != null && !inputBuffers.isEmpty();
		case CodegenPackage.SPECIAL_CALL__OUTPUT_BUFFERS:
			return outputBuffers != null && !outputBuffers.isEmpty();
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
		result.append(" (type: ");
		result.append(type);
		result.append(')');
		return result.toString();
	}

} // SpecialCallImpl
