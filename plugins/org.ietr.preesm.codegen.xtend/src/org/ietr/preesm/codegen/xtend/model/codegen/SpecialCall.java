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
package org.ietr.preesm.codegen.xtend.model.codegen;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Special Call</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getType <em>Type</em>}</li>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getInputBuffers <em>Input Buffers</em>}</li>
 *   <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getOutputBuffers <em>Output Buffers</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSpecialCall()
 * @model
 * @generated
 */
public interface SpecialCall extends Call {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute. The literals
	 * are from the enumeration
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialType}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
	 * @see #setType(SpecialType)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSpecialCall_Type()
	 * @model required="true"
	 * @generated
	 */
	SpecialType getType();

	/**
	 * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.SpecialCall#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.SpecialType
	 * @see #getType()
	 * @generated
	 */
	void setType(SpecialType value);

	/**
	 * Returns the value of the '<em><b>Input Buffers</b></em>' reference list.
	 * The list contents are of type
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Input Buffers</em>' reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Input Buffers</em>' reference list.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSpecialCall_InputBuffers()
	 * @model required="true" changeable="false"
	 * @generated
	 * 
	 */
	EList<Buffer> getInputBuffers();

	/**
	 * Returns the value of the '<em><b>Output Buffers</b></em>' reference list.
	 * The list contents are of type
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.Buffer}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Buffers</em>' reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Output Buffers</em>' reference list.
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getSpecialCall_OutputBuffers()
	 * @model resolveProxies="false" required="true" changeable="false"
	 * @generated
	 * 
	 */
	EList<Buffer> getOutputBuffers();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isFork();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isJoin();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isBroadcast();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isRoundBuffer();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void addInputBuffer(Buffer buffer);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void addOutputBuffer(Buffer buffer);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void removeInputBuffer(Buffer buffer);

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void removeOutputBuffer(Buffer buffer);

} // SpecialCall
