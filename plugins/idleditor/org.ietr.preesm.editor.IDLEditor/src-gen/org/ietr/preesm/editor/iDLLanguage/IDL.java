/**
 * <copyright>
 * </copyright>
 *

 */
package org.ietr.preesm.editor.iDLLanguage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IDL</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.ietr.preesm.editor.iDLLanguage.IDL#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getIDL()
 * @model
 * @generated
 */
public interface IDL extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.editor.iDLLanguage.Type}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.ietr.preesm.editor.iDLLanguage.IDLLanguagePackage#getIDL_Elements()
   * @model containment="true"
   * @generated
   */
  EList<Type> getElements();

} // IDL
