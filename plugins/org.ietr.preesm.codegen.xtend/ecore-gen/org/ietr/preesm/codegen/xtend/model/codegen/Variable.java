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

import org.eclipse.emf.common.util.EList;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A {@link Variable} is an object that will be used several time in the generated code.<br>
 * It usually is {@link Block#getDeclarations() declared} within a {@link Block code block} before being used as a
 * parameter to the {@link Block#getCodeElts() code elements} of this {@link Block code block}. <br>
 * Each {@link Variable} has a {@link #getName() name} and a {@link #getType() type}. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getType <em>Type</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator <em>Creator</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsers <em>Users</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getVariable()
 * @model abstract="true"
 * @generated
 */
public interface Variable extends Commentable {
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getVariable_Name()
   * @model id="true" required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Type</em>' attribute.
   * @see #setType(String)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getVariable_Type()
   * @model required="true"
   * @generated
   */
  String getType();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getType <em>Type</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Type</em>' attribute.
   * @see #getType()
   * @generated
   */
  void setType(String value);

  /**
   * Returns the value of the '<em><b>Creator</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions <em>Definitions</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * The {@link #getCreator() creator} of a {@link Variable} is the {@link Block} that declare and define the
   * {@link Variable}.
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Creator</em>' container reference.
   * @see #setCreator(Block)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getVariable_Creator()
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions
   * @model opposite="definitions" transient="false"
   * @generated
   */
  Block getCreator();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator <em>Creator</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Creator</em>' container reference.
   * @see #getCreator()
   * @generated
   */
  void setCreator(Block value);

  /**
   * Returns the value of the '<em><b>Users</b></em>' reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block}. It is bidirectional and its opposite is '
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations <em>Declarations</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * The {@link #getUsers() users} of a {@link Variable} are the {@link Block Blocks} using the {@link Variable}. One of
   * the {@link #getUsers() users} of the variable is also its {@link #getCreator() creator}.
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Users</em>' reference list.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getVariable_Users()
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations
   * @model opposite="declarations" required="true" changeable="false"
   * @generated
   */
  EList<Block> getUsers();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @model
   * @generated
   */
  void reaffectCreator(Block creator);

} // Variable
