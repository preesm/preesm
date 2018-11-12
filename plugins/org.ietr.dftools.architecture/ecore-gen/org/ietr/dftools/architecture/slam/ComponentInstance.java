/**
 */
package org.ietr.dftools.architecture.slam;

import org.ietr.dftools.architecture.slam.component.Component;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component Instance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.ComponentInstance#getComponent <em>Component</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.ComponentInstance#getInstanceName <em>Instance Name</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.SlamPackage#getComponentInstance()
 * @model
 * @generated
 */
public interface ComponentInstance extends ParameterizedElement {
	/**
	 * Returns the value of the '<em><b>Component</b></em>' reference.
	 * It is bidirectional and its opposite is '{@link org.ietr.dftools.architecture.slam.component.Component#getInstances <em>Instances</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Component</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Component</em>' reference.
	 * @see #setComponent(Component)
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getComponentInstance_Component()
	 * @see org.ietr.dftools.architecture.slam.component.Component#getInstances
	 * @model opposite="instances"
	 * @generated
	 */
	Component getComponent();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.ComponentInstance#getComponent <em>Component</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Component</em>' reference.
	 * @see #getComponent()
	 * @generated
	 */
	void setComponent(Component value);

	/**
	 * Returns the value of the '<em><b>Instance Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Instance Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Instance Name</em>' attribute.
	 * @see #setInstanceName(String)
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#getComponentInstance_InstanceName()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.String"
	 * @generated
	 */
	String getInstanceName();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.ComponentInstance#getInstanceName <em>Instance Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Instance Name</em>' attribute.
	 * @see #getInstanceName()
	 * @generated
	 */
	void setInstanceName(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation" unique="false"
	 * @generated
	 */
	boolean isHierarchical();

} // ComponentInstance
