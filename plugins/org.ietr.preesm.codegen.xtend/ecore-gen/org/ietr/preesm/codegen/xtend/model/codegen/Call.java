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
 * <!-- begin-user-doc --> A {@link Call} is a {@link CodeElt} used to represent a {@link Call} to a {@link FunctionCall function} or other primitive used in
 * the codegen such as {@link Communication} primitives. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameters <em>Parameters</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getEReference0 <em>EReference0</em>}</li>
 * <li>{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getParameterDirections <em>Parameter Directions</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCall()
 * @model abstract="true"
 * @generated
 */
public interface Call extends CodeElt {

  /**
   * Returns the value of the '<em><b>Parameters</b></em>' reference list. The list contents are of type
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.Variable}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameters</em>' reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parameters</em>' reference list.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCall_Parameters()
   * @model changeable="false"
   * @generated
   */
  EList<Variable> getParameters();

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCall_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getName <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>EReference0</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>EReference0</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>EReference0</em>' reference.
   * @see #setEReference0(Call)
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCall_EReference0()
   * @model
   * @generated
   */
  Call getEReference0();

  /**
   * Sets the value of the '{@link org.ietr.preesm.codegen.xtend.model.codegen.Call#getEReference0 <em>EReference0</em>}' reference. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>EReference0</em>' reference.
   * @see #getEReference0()
   * @generated
   */
  void setEReference0(Call value);

  /**
   * Returns the value of the '<em><b>Parameter Directions</b></em>' attribute list. The list contents are of type
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection}. The literals are from the enumeration
   * {@link org.ietr.preesm.codegen.xtend.model.codegen.PortDirection}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameter Directions</em>' attribute list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parameter Directions</em>' attribute list.
   * @see org.ietr.preesm.codegen.xtend.model.codegen.PortDirection
   * @see org.ietr.preesm.codegen.xtend.model.codegen.CodegenPackage#getCall_ParameterDirections()
   * @model unique="false"
   * @generated
   */
  EList<PortDirection> getParameterDirections();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param variable
   *          the variable
   * @param direction
   *          the direction
   * @model
   * @generated
   */
  void addParameter(Variable variable, PortDirection direction);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param variable
   *          the variable
   * @model
   * @generated
   */
  void removeParameter(Variable variable);

} // Call
