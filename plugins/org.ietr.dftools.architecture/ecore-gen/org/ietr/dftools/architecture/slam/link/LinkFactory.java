/**
 */
package org.ietr.dftools.architecture.slam.link;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.ietr.dftools.architecture.slam.link.LinkPackage
 * @generated
 */
public interface LinkFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LinkFactory eINSTANCE = org.ietr.dftools.architecture.slam.link.impl.LinkFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Data Link</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Data Link</em>'.
	 * @generated
	 */
	DataLink createDataLink();

	/**
	 * Returns a new object of class '<em>Control Link</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Control Link</em>'.
	 * @generated
	 */
	ControlLink createControlLink();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	LinkPackage getLinkPackage();

} //LinkFactory
