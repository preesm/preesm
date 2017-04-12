/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
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
package org.ietr.preesm.codegen.xtend.model.codegen;

/**
 * <!-- begin-user-doc --> An {@link CoreBlock} is a {@link Block} containing
 * all information needed to generate the source code associated to a core. It
 * is the result of a mapping/scheduling process performed during an execution
 * of a workflow of Preesm. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getLoopBlock
 * <em>Loop Block</em>}</li>
 * <li>
 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getInitBlock
 * <em>Init Block</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreType
 * <em>Core Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCoreBlock()
 * @model
 * @generated
 */
public interface CoreBlock extends Block {

	/**
	 * Returns the value of the '<em><b>Loop Block</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Loop Block</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Loop Block</em>' reference.
	 * @see #setLoopBlock(LoopBlock)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCoreBlock_LoopBlock()
	 * @model required="true"
	 * @generated
	 */
	LoopBlock getLoopBlock();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getLoopBlock
	 * <em>Loop Block</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Loop Block</em>' reference.
	 * @see #getLoopBlock()
	 * @generated
	 */
	void setLoopBlock(LoopBlock value);

	/**
	 * Returns the value of the '<em><b>Init Block</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Init Block</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Init Block</em>' reference.
	 * @see #setInitBlock(CallBlock)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCoreBlock_InitBlock()
	 * @model required="true"
	 * @generated
	 */
	CallBlock getInitBlock();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getInitBlock
	 * <em>Init Block</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Init Block</em>' reference.
	 * @see #getInitBlock()
	 * @generated
	 */
	void setInitBlock(CallBlock value);

	/**
	 * Returns the value of the '<em><b>Core Type</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Core Type</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Core Type</em>' attribute.
	 * @see #setCoreType(String)
	 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCoreBlock_CoreType()
	 * @model required="true"
	 * @generated
	 */
	String getCoreType();

	/**
	 * Sets the value of the '
	 * {@link org.ietr.preesm.codegen.xtend.model.codegen.CoreBlock#getCoreType
	 * <em>Core Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Core Type</em>' attribute.
	 * @see #getCoreType()
	 * @generated
	 */
	void setCoreType(String value);
} // CoreBlock
