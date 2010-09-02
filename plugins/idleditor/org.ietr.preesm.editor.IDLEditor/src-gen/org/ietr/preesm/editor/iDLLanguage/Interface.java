/**
 * <copyright>
 * </copyright>
 *

 */
package org.ietr.preesm.editor.iDLLanguage;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interface</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.Interface#getName <em>Name</em>}</li>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.Interface#getFunction <em>Function</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getInterface()
 * @model
 * @generated
 */
public interface Interface extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * The literals are from the enumeration {@link org.ietr.preesm.editor.iDLLanguage.InterfaceName}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see org.ietr.preesm.editor.iDLLanguage.InterfaceName
   * @see #setName(InterfaceName)
   * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getInterface_Name()
   * @model
   * @generated
   */
  InterfaceName getName();

  /**
   * Sets the value of the '{@link org.ietr.preesm.editor.iDLLanguage.Interface#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see org.ietr.preesm.editor.iDLLanguage.InterfaceName
   * @see #getName()
   * @generated
   */
  void setName(InterfaceName value);

  /**
   * Returns the value of the '<em><b>Function</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Function</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Function</em>' containment reference.
   * @see #setFunction(Function)
   * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getInterface_Function()
   * @model containment="true"
   * @generated
   */
  Function getFunction();

  /**
   * Sets the value of the '{@link org.ietr.preesm.editor.iDLLanguage.Interface#getFunction <em>Function</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Function</em>' containment reference.
   * @see #getFunction()
   * @generated
   */
  void setFunction(Function value);

} // Interface
