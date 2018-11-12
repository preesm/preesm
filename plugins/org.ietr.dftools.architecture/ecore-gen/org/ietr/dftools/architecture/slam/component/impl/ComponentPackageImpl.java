/**
 */
package org.ietr.dftools.architecture.slam.component.impl;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.ietr.dftools.architecture.slam.SlamPackage;

import org.ietr.dftools.architecture.slam.attributes.AttributesPackage;

import org.ietr.dftools.architecture.slam.component.ComInterface;
import org.ietr.dftools.architecture.slam.component.ComNode;
import org.ietr.dftools.architecture.slam.component.Component;
import org.ietr.dftools.architecture.slam.component.ComponentFactory;
import org.ietr.dftools.architecture.slam.component.ComponentPackage;
import org.ietr.dftools.architecture.slam.component.Dma;
import org.ietr.dftools.architecture.slam.component.Enabler;
import org.ietr.dftools.architecture.slam.component.HierarchyPort;
import org.ietr.dftools.architecture.slam.component.Mem;
import org.ietr.dftools.architecture.slam.component.Operator;

import org.ietr.dftools.architecture.slam.impl.SlamPackageImpl;

import org.ietr.dftools.architecture.slam.link.LinkPackage;

import org.ietr.dftools.architecture.slam.link.impl.LinkPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ComponentPackageImpl extends EPackageImpl implements ComponentPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass operatorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass comNodeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass enablerEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass dmaEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass memEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass hierarchyPortEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass comInterfaceEClass = null;

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
	 * @see org.ietr.dftools.architecture.slam.component.ComponentPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ComponentPackageImpl() {
		super(eNS_URI, ComponentFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link ComponentPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ComponentPackage init() {
		if (isInited) return (ComponentPackage)EPackage.Registry.INSTANCE.getEPackage(ComponentPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredComponentPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		ComponentPackageImpl theComponentPackage = registeredComponentPackage instanceof ComponentPackageImpl ? (ComponentPackageImpl)registeredComponentPackage : new ComponentPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();
		AttributesPackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(SlamPackage.eNS_URI);
		SlamPackageImpl theSlamPackage = (SlamPackageImpl)(registeredPackage instanceof SlamPackageImpl ? registeredPackage : SlamPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(LinkPackage.eNS_URI);
		LinkPackageImpl theLinkPackage = (LinkPackageImpl)(registeredPackage instanceof LinkPackageImpl ? registeredPackage : LinkPackage.eINSTANCE);

		// Create package meta-data objects
		theComponentPackage.createPackageContents();
		theSlamPackage.createPackageContents();
		theLinkPackage.createPackageContents();

		// Initialize created meta-data
		theComponentPackage.initializePackageContents();
		theSlamPackage.initializePackageContents();
		theLinkPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theComponentPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ComponentPackage.eNS_URI, theComponentPackage);
		return theComponentPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComponent() {
		return componentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComponent_Interfaces() {
		return (EReference)componentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComponent_Instances() {
		return (EReference)componentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComponent_Refinements() {
		return (EReference)componentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getOperator() {
		return operatorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComNode() {
		return comNodeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getComNode_Parallel() {
		return (EAttribute)comNodeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getComNode_Speed() {
		return (EAttribute)comNodeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getEnabler() {
		return enablerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDma() {
		return dmaEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDma_SetupTime() {
		return (EAttribute)dmaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMem() {
		return memEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMem_Size() {
		return (EAttribute)memEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getHierarchyPort() {
		return hierarchyPortEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHierarchyPort_ExternalInterface() {
		return (EReference)hierarchyPortEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHierarchyPort_InternalInterface() {
		return (EReference)hierarchyPortEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getHierarchyPort_InternalComponentInstance() {
		return (EReference)hierarchyPortEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComInterface() {
		return comInterfaceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComInterface_Component() {
		return (EReference)comInterfaceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComInterface_BusType() {
		return (EReference)comInterfaceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getComInterface_Name() {
		return (EAttribute)comInterfaceEClass.getEStructuralFeatures().get(2);
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
	public ComponentFactory getComponentFactory() {
		return (ComponentFactory)getEFactoryInstance();
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
		componentEClass = createEClass(COMPONENT);
		createEReference(componentEClass, COMPONENT__INTERFACES);
		createEReference(componentEClass, COMPONENT__INSTANCES);
		createEReference(componentEClass, COMPONENT__REFINEMENTS);

		operatorEClass = createEClass(OPERATOR);

		comNodeEClass = createEClass(COM_NODE);
		createEAttribute(comNodeEClass, COM_NODE__PARALLEL);
		createEAttribute(comNodeEClass, COM_NODE__SPEED);

		enablerEClass = createEClass(ENABLER);

		dmaEClass = createEClass(DMA);
		createEAttribute(dmaEClass, DMA__SETUP_TIME);

		memEClass = createEClass(MEM);
		createEAttribute(memEClass, MEM__SIZE);

		hierarchyPortEClass = createEClass(HIERARCHY_PORT);
		createEReference(hierarchyPortEClass, HIERARCHY_PORT__EXTERNAL_INTERFACE);
		createEReference(hierarchyPortEClass, HIERARCHY_PORT__INTERNAL_INTERFACE);
		createEReference(hierarchyPortEClass, HIERARCHY_PORT__INTERNAL_COMPONENT_INSTANCE);

		comInterfaceEClass = createEClass(COM_INTERFACE);
		createEReference(comInterfaceEClass, COM_INTERFACE__COMPONENT);
		createEReference(comInterfaceEClass, COM_INTERFACE__BUS_TYPE);
		createEAttribute(comInterfaceEClass, COM_INTERFACE__NAME);

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
		SlamPackage theSlamPackage = (SlamPackage)EPackage.Registry.INSTANCE.getEPackage(SlamPackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		AttributesPackage theAttributesPackage = (AttributesPackage)EPackage.Registry.INSTANCE.getEPackage(AttributesPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		componentEClass.getESuperTypes().add(theSlamPackage.getVLNVedElement());
		componentEClass.getESuperTypes().add(theSlamPackage.getParameterizedElement());
		operatorEClass.getESuperTypes().add(this.getComponent());
		comNodeEClass.getESuperTypes().add(this.getComponent());
		enablerEClass.getESuperTypes().add(this.getComponent());
		dmaEClass.getESuperTypes().add(this.getEnabler());
		memEClass.getESuperTypes().add(this.getEnabler());

		// Initialize classes and features; add operations and parameters
		initEClass(componentEClass, Component.class, "Component", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComponent_Interfaces(), this.getComInterface(), this.getComInterface_Component(), "interfaces", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponent_Instances(), theSlamPackage.getComponentInstance(), theSlamPackage.getComponentInstance_Component(), "instances", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComponent_Refinements(), theSlamPackage.getDesign(), theSlamPackage.getDesign_Refined(), "refinements", null, 0, -1, Component.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(componentEClass, this.getComInterface(), "getInterface", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getString(), "name", 0, 1, !IS_UNIQUE, IS_ORDERED);

		initEClass(operatorEClass, Operator.class, "Operator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(comNodeEClass, ComNode.class, "ComNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getComNode_Parallel(), theEcorePackage.getEBoolean(), "parallel", "true", 0, 1, ComNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComNode_Speed(), theEcorePackage.getEFloat(), "speed", "1", 0, 1, ComNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(enablerEClass, Enabler.class, "Enabler", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(dmaEClass, Dma.class, "Dma", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDma_SetupTime(), this.getint(), "setupTime", "0", 0, 1, Dma.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(memEClass, Mem.class, "Mem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMem_Size(), this.getint(), "size", "1", 0, 1, Mem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(hierarchyPortEClass, HierarchyPort.class, "HierarchyPort", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getHierarchyPort_ExternalInterface(), this.getComInterface(), null, "externalInterface", null, 0, 1, HierarchyPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHierarchyPort_InternalInterface(), this.getComInterface(), null, "internalInterface", null, 0, 1, HierarchyPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getHierarchyPort_InternalComponentInstance(), theSlamPackage.getComponentInstance(), null, "internalComponentInstance", null, 0, 1, HierarchyPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(comInterfaceEClass, ComInterface.class, "ComInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComInterface_Component(), this.getComponent(), this.getComponent_Interfaces(), "component", null, 0, 1, ComInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getComInterface_BusType(), theAttributesPackage.getVLNV(), null, "busType", null, 0, 1, ComInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComInterface_Name(), this.getString(), "name", "", 0, 1, ComInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(iPathEDataType, IPath.class, "IPath", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(stringEDataType, String.class, "String", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(intEDataType, int.class, "int", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(longEDataType, long.class, "long", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(doubleEDataType, double.class, "double", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //ComponentPackageImpl
