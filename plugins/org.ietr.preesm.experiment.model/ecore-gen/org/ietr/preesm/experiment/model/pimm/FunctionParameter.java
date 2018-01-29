/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Function Parameter</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getName <em>Name</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getDirection <em>Direction</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getType <em>Type</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#isIsConfigurationParameter <em>Is Configuration Parameter</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getFunctionParameter()
 * @model
 * @generated
 */
public interface FunctionParameter extends EObject {
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
