/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Function Parameter</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getDirection <em>Direction</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getType <em>Type</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#isIsConfigurationParameter <em>Is Configuration Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFunctionParameter()
 * @model
 * @generated
 */
public interface FunctionParameter extends EObject, PiMMVisitable {
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFunctionParameter_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getName <em>Name</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Direction</b></em>' attribute. The literals are from the enumeration
   * {@link org.ietr.preesm.experiment.model.pimm.Direction}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Direction</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Direction</em>' attribute.
   * @see org.ietr.preesm.experiment.model.pimm.Direction
   * @see #setDirection(Direction)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFunctionParameter_Direction()
   * @model
   * @generated
   */
  Direction getDirection();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getDirection <em>Direction</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Direction</em>' attribute.
   * @see org.ietr.preesm.experiment.model.pimm.Direction
   * @see #getDirection()
   * @generated
   */
  void setDirection(Direction value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Type</em>' attribute.
   * @see #setType(String)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFunctionParameter_Type()
   * @model
   * @generated
   */
  String getType();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getType <em>Type</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Type</em>' attribute.
   * @see #getType()
   * @generated
   */
  void setType(String value);

  /**
   * Returns the value of the '<em><b>Is Configuration Parameter</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Is Configuration Parameter</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Is Configuration Parameter</em>' attribute.
   * @see #setIsConfigurationParameter(boolean)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFunctionParameter_IsConfigurationParameter()
   * @model
   * @generated
   */
  boolean isIsConfigurationParameter();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#isIsConfigurationParameter <em>Is Configuration Parameter</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Is Configuration Parameter</em>' attribute.
   * @see #isIsConfigurationParameter()
   * @generated
   */
  void setIsConfigurationParameter(boolean value);

} // FunctionParameter
