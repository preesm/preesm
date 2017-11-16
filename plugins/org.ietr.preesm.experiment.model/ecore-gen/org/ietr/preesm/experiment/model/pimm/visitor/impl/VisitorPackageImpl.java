/**
 */
package org.ietr.preesm.experiment.model.pimm.visitor.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitable;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;
import org.ietr.preesm.experiment.model.pimm.visitor.VisitorFactory;
import org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class VisitorPackageImpl extends EPackageImpl implements VisitorPackage {
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass piMMVisitableEClass = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private EClass piMMVisitorEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package package
   * URI value.
   * <p>
   * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also performs initialization of the package, or
   * returns the registered package, if one already exists. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.ietr.preesm.experiment.model.pimm.visitor.VisitorPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private VisitorPackageImpl() {
    super(VisitorPackage.eNS_URI, VisitorFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   *
   * <p>
   * This method is used to initialize {@link VisitorPackage#eINSTANCE} when that field is accessed. Clients should not invoke it directly. Instead, they should
   * simply access that field to obtain the package. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static VisitorPackage init() {
    if (VisitorPackageImpl.isInited) {
      return (VisitorPackage) EPackage.Registry.INSTANCE.getEPackage(VisitorPackage.eNS_URI);
    }

    // Obtain or create and register package
    final VisitorPackageImpl theVisitorPackage = (VisitorPackageImpl) (EPackage.Registry.INSTANCE.get(VisitorPackage.eNS_URI) instanceof VisitorPackageImpl
        ? EPackage.Registry.INSTANCE.get(VisitorPackage.eNS_URI)
        : new VisitorPackageImpl());

    VisitorPackageImpl.isInited = true;

    // Obtain or create and register interdependencies
    final PiMMPackageImpl thePiMMPackage = (PiMMPackageImpl) (EPackage.Registry.INSTANCE.getEPackage(PiMMPackage.eNS_URI) instanceof PiMMPackageImpl
        ? EPackage.Registry.INSTANCE.getEPackage(PiMMPackage.eNS_URI)
        : PiMMPackage.eINSTANCE);

    // Create package meta-data objects
    theVisitorPackage.createPackageContents();
    thePiMMPackage.createPackageContents();

    // Initialize created meta-data
    theVisitorPackage.initializePackageContents();
    thePiMMPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theVisitorPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(VisitorPackage.eNS_URI, theVisitorPackage);
    return theVisitorPackage;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getPiMMVisitable() {
    return this.piMMVisitableEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EClass getPiMMVisitor() {
    return this.piMMVisitorEClass;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public VisitorFactory getVisitorFactory() {
    return (VisitorFactory) getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its first. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @generated
   */
  public void createPackageContents() {
    if (this.isCreated) {
      return;
    }
    this.isCreated = true;

    // Create classes and their features
    this.piMMVisitableEClass = createEClass(VisitorPackage.PI_MM_VISITABLE);

    this.piMMVisitorEClass = createEClass(VisitorPackage.PI_MM_VISITOR);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any invocation but its first. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void initializePackageContents() {
    if (this.isInitialized) {
      return;
    }
    this.isInitialized = true;

    // Initialize package
    setName(VisitorPackage.eNAME);
    setNsPrefix(VisitorPackage.eNS_PREFIX);
    setNsURI(VisitorPackage.eNS_URI);

    // Obtain other dependent packages
    final PiMMPackage thePiMMPackage = (PiMMPackage) EPackage.Registry.INSTANCE.getEPackage(PiMMPackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(this.piMMVisitableEClass, PiMMVisitable.class, "PiMMVisitable", EPackageImpl.IS_ABSTRACT, EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    EOperation op = addEOperation(this.piMMVisitableEClass, null, "accept", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, getPiMMVisitor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    initEClass(this.piMMVisitorEClass, PiMMVisitor.class, "PiMMVisitor", EPackageImpl.IS_ABSTRACT, !EPackageImpl.IS_INTERFACE,
        EPackageImpl.IS_GENERATED_INSTANCE_CLASS);

    op = addEOperation(this.piMMVisitorEClass, null, "visit", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, getPiMMVisitable(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitAbstractActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getAbstractActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitAbstractVertex", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getAbstractVertex(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitConfigInputInterface", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getConfigInputInterface(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitConfigInputPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getConfigInputPort(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitConfigOutputInterface", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getConfigOutputInterface(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitConfigOutputPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getConfigOutputPort(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDataInputInterface", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDataInputInterface(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDataInputPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDataInputPort(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDataOutputInterface", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDataOutputInterface(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDataOutputPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDataOutputPort(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDelay", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDelay(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDependency", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDependency(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitExpression", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getExpression(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitFifo", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getFifo(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitInterfaceActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getInterfaceActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitISetter", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getISetter(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitParameter", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getParameter(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitParameterizable", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getParameterizable(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitPiGraph", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getPiGraph(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getPort(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitRefinement", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getRefinement(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitFunctionParameter", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getFunctionParameter(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitFunctionPrototype", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getFunctionPrototype(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitHRefinement", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getCHeaderRefinement(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitDataPort", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getDataPort(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitBroadcastActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getBroadcastActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitJoinActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getJoinActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitForkActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getForkActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitRoundBufferActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getRoundBufferActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);

    op = addEOperation(this.piMMVisitorEClass, null, "visitExecutableActor", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
    addEParameter(op, thePiMMPackage.getExecutableActor(), "subject", 1, 1, EPackageImpl.IS_UNIQUE, EPackageImpl.IS_ORDERED);
  }

} // VisitorPackageImpl
