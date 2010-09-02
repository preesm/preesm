/**
 * <copyright>
 * </copyright>
 *

 */
package org.ietr.preesm.editor.iDLLanguage;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getDirection <em>Direction</em>}</li>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getType <em>Type</em>}</li>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends EObject
{
  /**
   * Returns the value of the '<em><b>Direction</b></em>' attribute.
   * The literals are from the enumeration {@link org.ietr.preesm.editor.iDLLanguage.Direction}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Direction</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Direction</em>' attribute.
   * @see org.ietr.preesm.editor.iDLLanguage.Direction
   * @see #setDirection(Direction)
   * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getParameter_Direction()
   * @model
   * @generated
   */
  Direction getDirection();

  /**
   * Sets the value of the '{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getDirection <em>Direction</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Direction</em>' attribute.
   * @see org.ietr.preesm.editor.iDLLanguage.Direction
   * @see #getDirection()
   * @generated
   */
  void setDirection(Direction value);

  /**
   * Returns the value of the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' containment reference.
   * @see #setType(TypeStar)
   * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getParameter_Type()
   * @model containment="true"
   * @generated
   */
  TypeStar getType();

  /**
   * Sets the value of the '{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getType <em>Type</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' containment reference.
   * @see #getType()
   * @generated
   */
  void setType(TypeStar value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getParameter_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.editor.iDLLanguage.Parameter#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Parameter
