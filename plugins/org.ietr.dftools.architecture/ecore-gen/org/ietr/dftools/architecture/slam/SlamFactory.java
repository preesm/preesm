/**
 */
package org.ietr.dftools.architecture.slam;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.ietr.dftools.architecture.slam.SlamPackage
 * @generated
 */
public interface SlamFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SlamFactory eINSTANCE = org.ietr.dftools.architecture.slam.impl.SlamFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Design</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Design</em>'.
	 * @generated
	 */
	Design createDesign();

	/**
	 * Returns a new object of class '<em>Component Instance</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Component Instance</em>'.
	 * @generated
	 */
	ComponentInstance createComponentInstance();

	/**
	 * Returns a new object of class '<em>VLN Ved Element</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>VLN Ved Element</em>'.
	 * @generated
	 */
	VLNVedElement createVLNVedElement();

	/**
	 * Returns a new object of class '<em>Parameterized Element</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Parameterized Element</em>'.
	 * @generated
	 */
	ParameterizedElement createParameterizedElement();

	/**
	 * Returns a new object of class '<em>Component Holder</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Component Holder</em>'.
	 * @generated
	 */
	ComponentHolder createComponentHolder();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	SlamPackage getSlamPackage();

} //SlamFactory
