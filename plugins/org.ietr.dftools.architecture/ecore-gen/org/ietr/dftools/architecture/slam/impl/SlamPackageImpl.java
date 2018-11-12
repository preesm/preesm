/**
 */
package org.ietr.dftools.architecture.slam.impl;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.ietr.dftools.architecture.slam.ComponentHolder;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.ParameterizedElement;
import org.ietr.dftools.architecture.slam.SlamFactory;
import org.ietr.dftools.architecture.slam.SlamPackage;
import org.ietr.dftools.architecture.slam.VLNVedElement;

import org.ietr.dftools.architecture.slam.attributes.AttributesPackage;

import org.ietr.dftools.architecture.slam.component.ComponentPackage;

import org.ietr.dftools.architecture.slam.component.impl.ComponentPackageImpl;

import org.ietr.dftools.architecture.slam.link.LinkPackage;

import org.ietr.dftools.architecture.slam.link.impl.LinkPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SlamPackageImpl extends EPackageImpl implements SlamPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass designEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentInstanceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass vlnVedElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass parameterizedElementEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentHolderEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iPathEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType stringEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType intEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType longEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType doubleEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.ietr.dftools.architecture.slam.SlamPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SlamPackageImpl() {
		super(eNS_URI, SlamFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link SlamPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SlamPackage init() {
		if (isInited) return (SlamPackage)EPackage.Registry.INSTANCE.getEPackage(SlamPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredSlamPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		SlamPackageImpl theSlamPackage = registeredSlamPackage instanceof SlamPackageImpl ? (SlamPackageImpl)registeredSlamPackage : new SlamPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();
		AttributesPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(ComponentPackage.eNS_URI);
		ComponentPackageImpl theComponentPackage = (ComponentPackageImpl)(registeredPackage instanceof ComponentPackageImpl ? registeredPackage : ComponentPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(LinkPackage.eNS_URI);
		LinkPackageImpl theLinkPackage = (LinkPackageImpl)(registeredPackage instanceof LinkPackageImpl ? registeredPackage : LinkPackage.eINSTANCE);

		// Create package meta-data objects
		theSlamPackage.createPackageContents();
		theComponentPackage.createPackageContents();
		theLinkPackage.createPackageContents();

		// Initialize created meta-data
		theSlamPackage.initializePackageContents();
		theComponentPackage.initializePackageContents();
		theLinkPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSlamPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SlamPackage.eNS_URI, theSlamPackage);
		return theSlamPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDesign() {
		return designEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDesign_ComponentInstances() {
		return (EReference)designEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDesign_Links() {
		return (EReference)designEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDesign_HierarchyPorts() {
		return (EReference)designEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDesign_Refined() {
		return (EReference)designEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDesign_Path() {
		return (EAttribute)designEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDesign_ComponentHolder() {
		return (EReference)designEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComponentInstance() {
		return componentInstanceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComponentInstance_Component() {
		return (EReference)componentInstanceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getComponentInstance_InstanceName() {
		return (EAttribute)componentInstanceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getVLNVedElement() {
		return vlnVedElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getVLNVedElement_Vlnv() {
		return (EReference)vlnVedElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getParameterizedElement() {
		return parameterizedElementEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getParameterizedElement_Parameters() {
		return (EReference)parameterizedElementEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComponentHolder() {
		return componentHolderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComponentHolder_Components() {
		return (EReference)componentHolderEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIPath() {
		return iPathEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getString() {
		return stringEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getint() {
		return intEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getlong() {
		return longEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getdouble() {
		return doubleEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SlamFactory getSlamFactory() {
		return (SlamFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		designEClass = createEClass(DESIGN);
		createEReference(designEClass, DESIGN__COMPONENT_INSTANCES);
		createEReference(designEClass, DESIGN__LINKS);
		createEReference(designEClass, DESIGN__HIERARCHY_PORTS);
		createEReference(designEClass, DESIGN__REFINED);
		createEAttribute(designEClass, DESIGN__PATH);
		createEReference(designEClass, DESIGN__COMPONENT_HOLDER);

		componentInstanceEClass = createEClass(COMPONENT_INSTANCE);
		createEReference(componentInstanceEClass, COMPONENT_INSTANCE__COMPONENT);
		createEAttribute(componentInstanceEClass, COMPONENT_INSTANCE__INSTANCE_NAME);

		vlnVedElementEClass = createEClass(VLN_VED_ELEMENT);
		createEReference(vlnVedElementEClass, VLN_VED_ELEMENT__VLNV);

		parameterizedElementEClass = createEClass(PARAMETERIZED_ELEMENT);
		createEReference(parameterizedElementEClass, PARAMETERIZED_ELEMENT__PARAMETERS);

		componentHolderEClass = createEClass(COMPONENT_HOLDER);
		createEReference(componentHolderEClass, COMPONENT_HOLDER__COMPONENTS);

		// Create data types
		iPathEDataType = createEDataType(IPATH);
		stringEDataType = createEDataType(STRING);
		intEDataType = createEDataType(INT);
		longEDataType = createEDataType(LONG);
		doubleEDataType = createEDataType(DOUBLE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		LinkPackage theLinkPackage = (LinkPackage)EPackage.Registry.INSTANCE.getEPackage(LinkPackage.eNS_URI);
		ComponentPackage theComponentPackage = (ComponentPackage)EPackage.Registry.INSTANCE.getEPackage(ComponentPackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		AttributesPackage theAttributesPackage = (AttributesPackage)EPackage.Registry.INSTANCE.getEPackage(AttributesPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		designEClass.getESuperTypes().add(this.getVLNVedElement());
		designEClass.getESuperTypes().add(this.getParameterizedElement());
		componentInstanceEClass.getESuperTypes().add(this.getParameterizedElement());

		// Initialize classes and features; add operations and parameters
		initEClass(designEClass, Design.class, "Design", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDesign_ComponentInstances(), this.getComponentInstance(), null, "componentInstances", null, 0, -1, Design.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
		initEReference(getDesign_Links(), theLinkPackage.getLink(), null, "links", null, 0, -1, Design.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDesign_HierarchyPorts(), theComponentPackage.getHierarchyPort(), null, "hierarchyPorts", null, 0, -1, Design.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDesign_Refined(), theComponentPackage.getComponent(), theComponentPackage.getComponent_Refinements(), "refined", null, 0, 1, Design.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDesign_Path(), this.getString(), "path", null, 0, 1, Design.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDesign_ComponentHolder(), this.getComponentHolder(), null, "componentHolder", null, 0, 1, Design.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(designEClass, theEcorePackage.getEBoolean(), "containsComponentInstance", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getString(), "name", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(designEClass, theEcorePackage.getEBoolean(), "containsComponent", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theAttributesPackage.getVLNV(), "name", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(designEClass, this.getComponentInstance(), "getComponentInstance", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getString(), "name", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(designEClass, theComponentPackage.getComponent(), "getComponent", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theAttributesPackage.getVLNV(), "name", 0, 1, !IS_UNIQUE, IS_ORDERED);

		initEClass(componentInstanceEClass, ComponentInstance.class, "ComponentInstance", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComponentInstance_Component(), theComponentPackage.getComponent(), theComponentPackage.getComponent_Instances(), "component", null, 0, 1, ComponentInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComponentInstance_InstanceName(), this.getString(), "instanceName", null, 0, 1, ComponentInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(componentInstanceEClass, theEcorePackage.getEBoolean(), "isHierarchical", 0, 1, !IS_UNIQUE, IS_ORDERED);

		initEClass(vlnVedElementEClass, VLNVedElement.class, "VLNVedElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getVLNVedElement_Vlnv(), theAttributesPackage.getVLNV(), null, "vlnv", null, 0, 1, VLNVedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(parameterizedElementEClass, ParameterizedElement.class, "ParameterizedElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getParameterizedElement_Parameters(), theAttributesPackage.getParameter(), null, "parameters", null, 0, -1, ParameterizedElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(componentHolderEClass, ComponentHolder.class, "ComponentHolder", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComponentHolder_Components(), theComponentPackage.getComponent(), null, "components", null, 0, -1, ComponentHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		// Initialize data types
		initEDataType(iPathEDataType, IPath.class, "IPath", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(stringEDataType, String.class, "String", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(intEDataType, int.class, "int", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(longEDataType, long.class, "long", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(doubleEDataType, double.class, "double", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //SlamPackageImpl
