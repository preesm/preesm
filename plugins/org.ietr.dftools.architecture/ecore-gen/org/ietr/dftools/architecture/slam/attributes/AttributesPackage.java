/**
 */
package org.ietr.dftools.architecture.slam.attributes;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.ietr.dftools.architecture.slam.attributes.AttributesFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel modelName='Slam' prefix='Attributes' modelDirectory='/org.ietr.dftools.architecture/ecore-gen' importerID='org.eclipse.emf.importer.ecore' loadInitialization='false' creationCommands='false' creationIcons='false' dataTypeConverters='false' operationReflection='false' basePackage='org.ietr.dftools.architecture.slam'"
 * @generated
 */
public interface AttributesPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "attributes";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.ietr.preesm/architecture/slam/attributes";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "attributes";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AttributesPackage eINSTANCE = org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.attributes.impl.VLNVImpl <em>VLNV</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.VLNVImpl
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getVLNV()
	 * @generated
	 */
	int VLNV = 0;

	/**
	 * The feature id for the '<em><b>Vendor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLNV__VENDOR = 0;

	/**
	 * The feature id for the '<em><b>Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLNV__LIBRARY = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLNV__NAME = 2;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLNV__VERSION = 3;

	/**
	 * The number of structural features of the '<em>VLNV</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLNV_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.attributes.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.ParameterImpl
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 1;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '<em>IPath</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.core.runtime.IPath
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getIPath()
	 * @generated
	 */
	int IPATH = 2;

	/**
	 * The meta object id for the '<em>String</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getString()
	 * @generated
	 */
	int STRING = 3;

	/**
	 * The meta object id for the '<em>int</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getint()
	 * @generated
	 */
	int INT = 4;

	/**
	 * The meta object id for the '<em>long</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getlong()
	 * @generated
	 */
	int LONG = 5;

	/**
	 * The meta object id for the '<em>double</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getdouble()
	 * @generated
	 */
	int DOUBLE = 6;


	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.attributes.VLNV <em>VLNV</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>VLNV</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.VLNV
	 * @generated
	 */
	EClass getVLNV();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getVendor <em>Vendor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Vendor</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.VLNV#getVendor()
	 * @see #getVLNV()
	 * @generated
	 */
	EAttribute getVLNV_Vendor();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getLibrary <em>Library</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Library</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.VLNV#getLibrary()
	 * @see #getVLNV()
	 * @generated
	 */
	EAttribute getVLNV_Library();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.VLNV#getName()
	 * @see #getVLNV()
	 * @generated
	 */
	EAttribute getVLNV_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.attributes.VLNV#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.VLNV#getVersion()
	 * @see #getVLNV()
	 * @generated
	 */
	EAttribute getVLNV_Version();

	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.attributes.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.attributes.Parameter#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.Parameter#getKey()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.attributes.Parameter#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.ietr.dftools.architecture.slam.attributes.Parameter#getValue()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Value();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.core.runtime.IPath <em>IPath</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IPath</em>'.
	 * @see org.eclipse.core.runtime.IPath
	 * @model instanceClass="org.eclipse.core.runtime.IPath"
	 * @generated
	 */
	EDataType getIPath();

	/**
	 * Returns the meta object for data type '{@link java.lang.String <em>String</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>String</em>'.
	 * @see java.lang.String
	 * @model instanceClass="java.lang.String"
	 * @generated
	 */
	EDataType getString();

	/**
	 * Returns the meta object for data type '<em>int</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>int</em>'.
	 * @model instanceClass="int"
	 * @generated
	 */
	EDataType getint();

	/**
	 * Returns the meta object for data type '<em>long</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>long</em>'.
	 * @model instanceClass="long"
	 * @generated
	 */
	EDataType getlong();

	/**
	 * Returns the meta object for data type '<em>double</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>double</em>'.
	 * @model instanceClass="double"
	 * @generated
	 */
	EDataType getdouble();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	AttributesFactory getAttributesFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.attributes.impl.VLNVImpl <em>VLNV</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.VLNVImpl
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getVLNV()
		 * @generated
		 */
		EClass VLNV = eINSTANCE.getVLNV();

		/**
		 * The meta object literal for the '<em><b>Vendor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VLNV__VENDOR = eINSTANCE.getVLNV_Vendor();

		/**
		 * The meta object literal for the '<em><b>Library</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VLNV__LIBRARY = eINSTANCE.getVLNV_Library();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VLNV__NAME = eINSTANCE.getVLNV_Name();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VLNV__VERSION = eINSTANCE.getVLNV_Version();

		/**
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.attributes.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.ParameterImpl
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__KEY = eINSTANCE.getParameter_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__VALUE = eINSTANCE.getParameter_Value();

		/**
		 * The meta object literal for the '<em>IPath</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.core.runtime.IPath
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getIPath()
		 * @generated
		 */
		EDataType IPATH = eINSTANCE.getIPath();

		/**
		 * The meta object literal for the '<em>String</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getString()
		 * @generated
		 */
		EDataType STRING = eINSTANCE.getString();

		/**
		 * The meta object literal for the '<em>int</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getint()
		 * @generated
		 */
		EDataType INT = eINSTANCE.getint();

		/**
		 * The meta object literal for the '<em>long</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getlong()
		 * @generated
		 */
		EDataType LONG = eINSTANCE.getlong();

		/**
		 * The meta object literal for the '<em>double</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.attributes.impl.AttributesPackageImpl#getdouble()
		 * @generated
		 */
		EDataType DOUBLE = eINSTANCE.getdouble();

	}

} //AttributesPackage
