/**
 */
package org.ietr.dftools.architecture.slam.link;

import org.eclipse.emf.ecore.EObject;

import org.ietr.dftools.architecture.slam.ComponentInstance;

import org.ietr.dftools.architecture.slam.component.ComInterface;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.dftools.architecture.slam.link.Link#getSourceInterface <em>Source Interface</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.link.Link#getDestinationInterface <em>Destination Interface</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.link.Link#getSourceComponentInstance <em>Source Component Instance</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.link.Link#getDestinationComponentInstance <em>Destination Component Instance</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.link.Link#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.ietr.dftools.architecture.slam.link.Link#isDirected <em>Directed</em>}</li>
 * </ul>
 *
 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink()
 * @model abstract="true"
 * @generated
 */
public interface Link extends EObject {
	/**
	 * Returns the value of the '<em><b>Source Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source Interface</em>' reference.
	 * @see #setSourceInterface(ComInterface)
	 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink_SourceInterface()
	 * @model
	 * @generated
	 */
	ComInterface getSourceInterface();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.link.Link#getSourceInterface <em>Source Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source Interface</em>' reference.
	 * @see #getSourceInterface()
	 * @generated
	 */
	void setSourceInterface(ComInterface value);

	/**
	 * Returns the value of the '<em><b>Destination Interface</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Destination Interface</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Destination Interface</em>' reference.
	 * @see #setDestinationInterface(ComInterface)
	 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink_DestinationInterface()
	 * @model
	 * @generated
	 */
	ComInterface getDestinationInterface();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.link.Link#getDestinationInterface <em>Destination Interface</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Destination Interface</em>' reference.
	 * @see #getDestinationInterface()
	 * @generated
	 */
	void setDestinationInterface(ComInterface value);

	/**
	 * Returns the value of the '<em><b>Source Component Instance</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source Component Instance</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source Component Instance</em>' reference.
	 * @see #setSourceComponentInstance(ComponentInstance)
	 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink_SourceComponentInstance()
	 * @model
	 * @generated
	 */
	ComponentInstance getSourceComponentInstance();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.link.Link#getSourceComponentInstance <em>Source Component Instance</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source Component Instance</em>' reference.
	 * @see #getSourceComponentInstance()
	 * @generated
	 */
	void setSourceComponentInstance(ComponentInstance value);

	/**
	 * Returns the value of the '<em><b>Destination Component Instance</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Destination Component Instance</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Destination Component Instance</em>' reference.
	 * @see #setDestinationComponentInstance(ComponentInstance)
	 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink_DestinationComponentInstance()
	 * @model
	 * @generated
	 */
	ComponentInstance getDestinationComponentInstance();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.link.Link#getDestinationComponentInstance <em>Destination Component Instance</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Destination Component Instance</em>' reference.
	 * @see #getDestinationComponentInstance()
	 * @generated
	 */
	void setDestinationComponentInstance(ComponentInstance value);

	/**
	 * Returns the value of the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uuid</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uuid</em>' attribute.
	 * @see #setUuid(String)
	 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink_Uuid()
	 * @model unique="false" dataType="org.ietr.dftools.architecture.slam.link.String"
	 * @generated
	 */
	String getUuid();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.link.Link#getUuid <em>Uuid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uuid</em>' attribute.
	 * @see #getUuid()
	 * @generated
	 */
	void setUuid(String value);

	/**
	 * Returns the value of the '<em><b>Directed</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Directed</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Directed</em>' attribute.
	 * @see #setDirected(boolean)
	 * @see org.ietr.dftools.architecture.slam.link.LinkPackage#getLink_Directed()
	 * @model default="false" unique="false"
	 * @generated
	 */
	boolean isDirected();

	/**
	 * Sets the value of the '{@link org.ietr.dftools.architecture.slam.link.Link#isDirected <em>Directed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Directed</em>' attribute.
	 * @see #isDirected()
	 * @generated
	 */
	void setDirected(boolean value);

} // Link
