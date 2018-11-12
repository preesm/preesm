/**
 */
package org.ietr.dftools.architecture.slam;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.ietr.dftools.architecture.slam.SlamFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel modelName='Slam' prefix='Slam' modelDirectory='/org.ietr.dftools.architecture/ecore-gen' importerID='org.eclipse.emf.importer.ecore' loadInitialization='false' creationCommands='false' creationIcons='false' dataTypeConverters='false' operationReflection='false' basePackage='org.ietr.dftools.architecture'"
 * @generated
 */
public interface SlamPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "slam";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.ietr.preesm/architecture/slam";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "slam";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SlamPackage eINSTANCE = org.ietr.dftools.architecture.slam.impl.SlamPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.impl.VLNVedElementImpl <em>VLN Ved Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.VLNVedElementImpl
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getVLNVedElement()
	 * @generated
	 */
	int VLN_VED_ELEMENT = 2;

	/**
	 * The feature id for the '<em><b>Vlnv</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLN_VED_ELEMENT__VLNV = 0;

	/**
	 * The number of structural features of the '<em>VLN Ved Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VLN_VED_ELEMENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.impl.DesignImpl <em>Design</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.DesignImpl
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getDesign()
	 * @generated
	 */
	int DESIGN = 0;

	/**
	 * The feature id for the '<em><b>Vlnv</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__VLNV = VLN_VED_ELEMENT__VLNV;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__PARAMETERS = VLN_VED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Component Instances</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__COMPONENT_INSTANCES = VLN_VED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Links</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__LINKS = VLN_VED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Hierarchy Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__HIERARCHY_PORTS = VLN_VED_ELEMENT_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Refined</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__REFINED = VLN_VED_ELEMENT_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__PATH = VLN_VED_ELEMENT_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Component Holder</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN__COMPONENT_HOLDER = VLN_VED_ELEMENT_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Design</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DESIGN_FEATURE_COUNT = VLN_VED_ELEMENT_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.impl.ParameterizedElementImpl <em>Parameterized Element</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.ParameterizedElementImpl
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getParameterizedElement()
	 * @generated
	 */
	int PARAMETERIZED_ELEMENT = 3;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_ELEMENT__PARAMETERS = 0;

	/**
	 * The number of structural features of the '<em>Parameterized Element</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZED_ELEMENT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.impl.ComponentInstanceImpl <em>Component Instance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.ComponentInstanceImpl
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getComponentInstance()
	 * @generated
	 */
	int COMPONENT_INSTANCE = 1;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_INSTANCE__PARAMETERS = PARAMETERIZED_ELEMENT__PARAMETERS;

	/**
	 * The feature id for the '<em><b>Component</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_INSTANCE__COMPONENT = PARAMETERIZED_ELEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Instance Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_INSTANCE__INSTANCE_NAME = PARAMETERIZED_ELEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Component Instance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_INSTANCE_FEATURE_COUNT = PARAMETERIZED_ELEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.ietr.dftools.architecture.slam.impl.ComponentHolderImpl <em>Component Holder</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.ComponentHolderImpl
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getComponentHolder()
	 * @generated
	 */
	int COMPONENT_HOLDER = 4;

	/**
	 * The feature id for the '<em><b>Components</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_HOLDER__COMPONENTS = 0;

	/**
	 * The number of structural features of the '<em>Component Holder</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_HOLDER_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '<em>IPath</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.core.runtime.IPath
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getIPath()
	 * @generated
	 */
	int IPATH = 5;

	/**
	 * The meta object id for the '<em>String</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.String
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getString()
	 * @generated
	 */
	int STRING = 6;

	/**
	 * The meta object id for the '<em>int</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getint()
	 * @generated
	 */
	int INT = 7;

	/**
	 * The meta object id for the '<em>long</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getlong()
	 * @generated
	 */
	int LONG = 8;

	/**
	 * The meta object id for the '<em>double</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getdouble()
	 * @generated
	 */
	int DOUBLE = 9;


	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.Design <em>Design</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Design</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design
	 * @generated
	 */
	EClass getDesign();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.dftools.architecture.slam.Design#getComponentInstances <em>Component Instances</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Component Instances</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design#getComponentInstances()
	 * @see #getDesign()
	 * @generated
	 */
	EReference getDesign_ComponentInstances();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.dftools.architecture.slam.Design#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Links</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design#getLinks()
	 * @see #getDesign()
	 * @generated
	 */
	EReference getDesign_Links();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.dftools.architecture.slam.Design#getHierarchyPorts <em>Hierarchy Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Hierarchy Ports</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design#getHierarchyPorts()
	 * @see #getDesign()
	 * @generated
	 */
	EReference getDesign_HierarchyPorts();

	/**
	 * Returns the meta object for the container reference '{@link org.ietr.dftools.architecture.slam.Design#getRefined <em>Refined</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Refined</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design#getRefined()
	 * @see #getDesign()
	 * @generated
	 */
	EReference getDesign_Refined();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.Design#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design#getPath()
	 * @see #getDesign()
	 * @generated
	 */
	EAttribute getDesign_Path();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.dftools.architecture.slam.Design#getComponentHolder <em>Component Holder</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Component Holder</em>'.
	 * @see org.ietr.dftools.architecture.slam.Design#getComponentHolder()
	 * @see #getDesign()
	 * @generated
	 */
	EReference getDesign_ComponentHolder();

	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.ComponentInstance <em>Component Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Instance</em>'.
	 * @see org.ietr.dftools.architecture.slam.ComponentInstance
	 * @generated
	 */
	EClass getComponentInstance();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.dftools.architecture.slam.ComponentInstance#getComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Component</em>'.
	 * @see org.ietr.dftools.architecture.slam.ComponentInstance#getComponent()
	 * @see #getComponentInstance()
	 * @generated
	 */
	EReference getComponentInstance_Component();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.dftools.architecture.slam.ComponentInstance#getInstanceName <em>Instance Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Instance Name</em>'.
	 * @see org.ietr.dftools.architecture.slam.ComponentInstance#getInstanceName()
	 * @see #getComponentInstance()
	 * @generated
	 */
	EAttribute getComponentInstance_InstanceName();

	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.VLNVedElement <em>VLN Ved Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>VLN Ved Element</em>'.
	 * @see org.ietr.dftools.architecture.slam.VLNVedElement
	 * @generated
	 */
	EClass getVLNVedElement();

	/**
	 * Returns the meta object for the containment reference '{@link org.ietr.dftools.architecture.slam.VLNVedElement#getVlnv <em>Vlnv</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Vlnv</em>'.
	 * @see org.ietr.dftools.architecture.slam.VLNVedElement#getVlnv()
	 * @see #getVLNVedElement()
	 * @generated
	 */
	EReference getVLNVedElement_Vlnv();

	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.ParameterizedElement <em>Parameterized Element</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameterized Element</em>'.
	 * @see org.ietr.dftools.architecture.slam.ParameterizedElement
	 * @generated
	 */
	EClass getParameterizedElement();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.dftools.architecture.slam.ParameterizedElement#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.ietr.dftools.architecture.slam.ParameterizedElement#getParameters()
	 * @see #getParameterizedElement()
	 * @generated
	 */
	EReference getParameterizedElement_Parameters();

	/**
	 * Returns the meta object for class '{@link org.ietr.dftools.architecture.slam.ComponentHolder <em>Component Holder</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Holder</em>'.
	 * @see org.ietr.dftools.architecture.slam.ComponentHolder
	 * @generated
	 */
	EClass getComponentHolder();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.dftools.architecture.slam.ComponentHolder#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Components</em>'.
	 * @see org.ietr.dftools.architecture.slam.ComponentHolder#getComponents()
	 * @see #getComponentHolder()
	 * @generated
	 */
	EReference getComponentHolder_Components();

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
	SlamFactory getSlamFactory();

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
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.impl.DesignImpl <em>Design</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.DesignImpl
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getDesign()
		 * @generated
		 */
		EClass DESIGN = eINSTANCE.getDesign();

		/**
		 * The meta object literal for the '<em><b>Component Instances</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESIGN__COMPONENT_INSTANCES = eINSTANCE.getDesign_ComponentInstances();

		/**
		 * The meta object literal for the '<em><b>Links</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESIGN__LINKS = eINSTANCE.getDesign_Links();

		/**
		 * The meta object literal for the '<em><b>Hierarchy Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESIGN__HIERARCHY_PORTS = eINSTANCE.getDesign_HierarchyPorts();

		/**
		 * The meta object literal for the '<em><b>Refined</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESIGN__REFINED = eINSTANCE.getDesign_Refined();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DESIGN__PATH = eINSTANCE.getDesign_Path();

		/**
		 * The meta object literal for the '<em><b>Component Holder</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DESIGN__COMPONENT_HOLDER = eINSTANCE.getDesign_ComponentHolder();

		/**
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.impl.ComponentInstanceImpl <em>Component Instance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.ComponentInstanceImpl
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getComponentInstance()
		 * @generated
		 */
		EClass COMPONENT_INSTANCE = eINSTANCE.getComponentInstance();

		/**
		 * The meta object literal for the '<em><b>Component</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENT_INSTANCE__COMPONENT = eINSTANCE.getComponentInstance_Component();

		/**
		 * The meta object literal for the '<em><b>Instance Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT_INSTANCE__INSTANCE_NAME = eINSTANCE.getComponentInstance_InstanceName();

		/**
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.impl.VLNVedElementImpl <em>VLN Ved Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.VLNVedElementImpl
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getVLNVedElement()
		 * @generated
		 */
		EClass VLN_VED_ELEMENT = eINSTANCE.getVLNVedElement();

		/**
		 * The meta object literal for the '<em><b>Vlnv</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VLN_VED_ELEMENT__VLNV = eINSTANCE.getVLNVedElement_Vlnv();

		/**
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.impl.ParameterizedElementImpl <em>Parameterized Element</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.ParameterizedElementImpl
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getParameterizedElement()
		 * @generated
		 */
		EClass PARAMETERIZED_ELEMENT = eINSTANCE.getParameterizedElement();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETERIZED_ELEMENT__PARAMETERS = eINSTANCE.getParameterizedElement_Parameters();

		/**
		 * The meta object literal for the '{@link org.ietr.dftools.architecture.slam.impl.ComponentHolderImpl <em>Component Holder</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.ComponentHolderImpl
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getComponentHolder()
		 * @generated
		 */
		EClass COMPONENT_HOLDER = eINSTANCE.getComponentHolder();

		/**
		 * The meta object literal for the '<em><b>Components</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENT_HOLDER__COMPONENTS = eINSTANCE.getComponentHolder_Components();

		/**
		 * The meta object literal for the '<em>IPath</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.core.runtime.IPath
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getIPath()
		 * @generated
		 */
		EDataType IPATH = eINSTANCE.getIPath();

		/**
		 * The meta object literal for the '<em>String</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.String
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getString()
		 * @generated
		 */
		EDataType STRING = eINSTANCE.getString();

		/**
		 * The meta object literal for the '<em>int</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getint()
		 * @generated
		 */
		EDataType INT = eINSTANCE.getint();

		/**
		 * The meta object literal for the '<em>long</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getlong()
		 * @generated
		 */
		EDataType LONG = eINSTANCE.getlong();

		/**
		 * The meta object literal for the '<em>double</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.dftools.architecture.slam.impl.SlamPackageImpl#getdouble()
		 * @generated
		 */
		EDataType DOUBLE = eINSTANCE.getdouble();

	}

} //SlamPackage
