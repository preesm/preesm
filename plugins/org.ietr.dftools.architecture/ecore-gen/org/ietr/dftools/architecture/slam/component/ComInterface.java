/**
 */
package org.ietr.dftools.architecture.slam.component;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.attributes.VLNV;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Com Interface</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.ComInterface#getComponent <em>Component</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.ComInterface#getBusType <em>Bus Type</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.component.ComInterface#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getComInterface()
 * @model
 * @generated
 */
public interface ComInterface extends EObject {
	/**
	 * Returns the value of the '<em><b>Component</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.dftools.architecture.slam.component.Component#getInterfaces <em>Interfaces</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component</em>' container reference.
	 * @see #setComponent(Component)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getComInterface_Component()
	 * @see org.ietr.dftools.architecture.slam.component.Component#getInterfaces
	 * @model opposite="interfaces" transient="false"
	 * @generated
	 */
	Component getComponent();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.ComInterface#getComponent <em>Component</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component</em>' container reference.
	 * @see #getComponent()
	 * @generated
	 */
	void setComponent(Component value);

	/**
	 * Returns the value of the '<em><b>Bus Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bus Type</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bus Type</em>' containment reference.
	 * @see #setBusType(VLNV)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getComInterface_BusType()
	 * @model containment="true"
	 * @generated
	 */
	VLNV getBusType();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.ComInterface#getBusType <em>Bus Type</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bus Type</em>' containment reference.
	 * @see #getBusType()
	 * @generated
	 */
	void setBusType(VLNV value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#getComInterface_Name()
	 * @model default="" unique="false" id="true" dataType="org.ietr.dftools.architecture.slam.component.String"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.component.ComInterface#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

} // ComInterface
