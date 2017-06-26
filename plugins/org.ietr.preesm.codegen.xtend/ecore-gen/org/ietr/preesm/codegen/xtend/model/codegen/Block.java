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
 * <!-- begin-user-doc --> A {@link Block} represents a block of code of the code generation. In C language, a code block usually is delimited by a pair of
 * braces <code>{}</code>.<br>
 * Each {@link Block} contains a set of {@link #getDeclarations() declarations} and a set of {@link #getCodeElts() code elements} which are respectively similar
 * to variable declarations and statement in C. Since a {@link Block} is a {@link CodeElt code element} itself, {@link Block blocks} can contain other blocks
 * (e.g. a for-loop block).<!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getCodeElts <em>Code Elts</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDeclarations <em>Declarations</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getDefinitions <em>Definitions</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getBlock()
 * @model
 * @generated
 */
public interface Block extends CodeElt {
  /**
   * Returns the value of the '<em><b>Code Elts</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.CodeElt}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Code Elts</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Code Elts</em>' containment reference list.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getBlock_CodeElts()
   * @model containment="true" changeable="false"
   * @generated
   */
  EList<CodeElt> getCodeElts();

  /**
   * Returns the value of the '<em><b>Declarations</b></em>' reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsers <em>Users</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Declarations</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Declarations</em>' reference list.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getBlock_Declarations()
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getUsers
   * @model opposite="users" changeable="false"
   * @generated
   */
  EList<Variable> getDeclarations();

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getBlock_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Block#getName <em>Name</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Definitions</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator <em>Creator</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Definitions</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Definitions</em>' containment reference list.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getBlock_Definitions()
   * @see org.ietr.preesm.codegen.xtend.model.codegen.Variable#getCreator
   * @model opposite="creator" containment="true"
   * @generated
   */
  EList<Variable> getDefinitions();

} // Block
