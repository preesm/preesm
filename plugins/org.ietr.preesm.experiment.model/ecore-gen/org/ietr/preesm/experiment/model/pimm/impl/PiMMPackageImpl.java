/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.Edge;
import org.ietr.preesm.experiment.model.pimm.ExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ExpressionHolder;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.InterfaceKind;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.NonExecutableActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.PortKind;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;
import org.ietr.preesm.experiment.model.pimm.Vertex;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PiMMPackageImpl extends EPackageImpl implements PiMMPackage {
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass vertexEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass edgeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass graphEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass parameterizableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionHolderEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass abstractVertexEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configurableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass abstractActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass piGraphEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass executableActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass actorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass broadcastActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass joinActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass forkActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass roundBufferActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass nonExecutableActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass portEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataPortEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataInputPortEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataOutputPortEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configInputPortEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configOutputPortEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass fifoEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass interfaceActorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataInputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataOutputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configInputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass configOutputInterfaceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass refinementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass piSDFRefinementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass cHeaderRefinementEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass parameterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dependencyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass iSetterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass delayEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass functionPrototypeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass functionParameterEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum directionEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum portMemoryAnnotationEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum portKindEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum interfaceKindEEnum = null;

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
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private PiMMPackageImpl() {
    super(eNS_URI, PiMMFactory.eINSTANCE);
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
   * <p>This method is used to initialize {@link PiMMPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static PiMMPackage init() {
    if (isInited) return (PiMMPackage)EPackage.Registry.INSTANCE.getEPackage(PiMMPackage.eNS_URI);

    // Obtain or create and register package
    Object registeredPiMMPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
    PiMMPackageImpl thePiMMPackage = registeredPiMMPackage instanceof PiMMPackageImpl ? (PiMMPackageImpl)registeredPiMMPackage : new PiMMPackageImpl();

    isInited = true;

    // Initialize simple dependencies
    EcorePackage.eINSTANCE.eClass();

    // Create package meta-data objects
    thePiMMPackage.createPackageContents();

    // Initialize created meta-data
    thePiMMPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    thePiMMPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(PiMMPackage.eNS_URI, thePiMMPackage);
    return thePiMMPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVertex() {
    return vertexEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVertex_ContainingGraph() {
    return (EReference)vertexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVertex_OutEdges() {
    return (EReference)vertexEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVertex_InEdges() {
    return (EReference)vertexEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getEdge() {
    return edgeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdge_ContainingGraph() {
    return (EReference)edgeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdge_Source() {
    return (EReference)edgeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getEdge_Target() {
    return (EReference)edgeEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGraph() {
    return graphEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGraph_Vertices() {
    return (EReference)graphEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGraph_Edges() {
    return (EReference)graphEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getParameterizable() {
    return parameterizableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExpression() {
    return expressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExpression_Holder() {
    return (EReference)expressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getExpression_ExpressionString() {
    return (EAttribute)expressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExpressionHolder() {
    return expressionHolderEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExpressionHolder_Expression() {
    return (EReference)expressionHolderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAbstractVertex() {
    return abstractVertexEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAbstractVertex_Name() {
    return (EAttribute)abstractVertexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfigurable() {
    return configurableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfigurable_ConfigInputPorts() {
    return (EReference)configurableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAbstractActor() {
    return abstractActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAbstractActor_DataInputPorts() {
    return (EReference)abstractActorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAbstractActor_DataOutputPorts() {
    return (EReference)abstractActorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAbstractActor_ConfigOutputPorts() {
    return (EReference)abstractActorEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPiGraph() {
    return piGraphEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExecutableActor() {
    return executableActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getActor() {
    return actorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getActor_Refinement() {
    return (EReference)actorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getActor_MemoryScriptPath() {
    return (EAttribute)actorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBroadcastActor() {
    return broadcastActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getJoinActor() {
    return joinActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getForkActor() {
    return forkActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRoundBufferActor() {
    return roundBufferActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getNonExecutableActor() {
    return nonExecutableActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPort() {
    return portEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPort_Name() {
    return (EAttribute)portEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataPort() {
    return dataPortEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDataPort_Annotation() {
    return (EAttribute)dataPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataInputPort() {
    return dataInputPortEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDataInputPort_IncomingFifo() {
    return (EReference)dataInputPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataOutputPort() {
    return dataOutputPortEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDataOutputPort_OutgoingFifo() {
    return (EReference)dataOutputPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfigInputPort() {
    return configInputPortEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfigInputPort_IncomingDependency() {
    return (EReference)configInputPortEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfigInputPort_Configurable() {
    return (EReference)configInputPortEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfigOutputPort() {
    return configOutputPortEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFifo() {
    return fifoEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFifo_SourcePort() {
    return (EReference)fifoEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFifo_TargetPort() {
    return (EReference)fifoEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFifo_Delay() {
    return (EReference)fifoEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFifo_Type() {
    return (EAttribute)fifoEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInterfaceActor() {
    return interfaceActorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getInterfaceActor_GraphPort() {
    return (EReference)interfaceActorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataInputInterface() {
    return dataInputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataOutputInterface() {
    return dataOutputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfigInputInterface() {
    return configInputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConfigInputInterface_GraphPort() {
    return (EReference)configInputInterfaceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConfigOutputInterface() {
    return configOutputInterfaceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRefinement() {
    return refinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getRefinement_FilePath() {
    return (EAttribute)refinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPiSDFRefinement() {
    return piSDFRefinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCHeaderRefinement() {
    return cHeaderRefinementEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCHeaderRefinement_LoopPrototype() {
    return (EReference)cHeaderRefinementEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCHeaderRefinement_InitPrototype() {
    return (EReference)cHeaderRefinementEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getParameter() {
    return parameterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDependency() {
    return dependencyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDependency_Setter() {
    return (EReference)dependencyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDependency_Getter() {
    return (EReference)dependencyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getISetter() {
    return iSetterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getISetter_OutgoingDependencies() {
    return (EReference)iSetterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDelay() {
    return delayEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDelay_ContainingFifo() {
    return (EReference)delayEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFunctionPrototype() {
    return functionPrototypeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFunctionPrototype_Name() {
    return (EAttribute)functionPrototypeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getFunctionPrototype_Parameters() {
    return (EReference)functionPrototypeEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFunctionParameter() {
    return functionParameterEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFunctionParameter_Name() {
    return (EAttribute)functionParameterEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFunctionParameter_Direction() {
    return (EAttribute)functionParameterEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFunctionParameter_Type() {
    return (EAttribute)functionParameterEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getFunctionParameter_IsConfigurationParameter() {
    return (EAttribute)functionParameterEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getDirection() {
    return directionEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getPortMemoryAnnotation() {
    return portMemoryAnnotationEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getPortKind() {
    return portKindEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getInterfaceKind() {
    return interfaceKindEEnum;
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
  public PiMMFactory getPiMMFactory() {
    return (PiMMFactory)getEFactoryInstance();
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
  @SuppressWarnings("deprecation")
  public void createPackageContents() {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    vertexEClass = createEClass(VERTEX);
    createEReference(vertexEClass, VERTEX__CONTAINING_GRAPH);
    createEReference(vertexEClass, VERTEX__OUT_EDGES);
    createEReference(vertexEClass, VERTEX__IN_EDGES);

    edgeEClass = createEClass(EDGE);
    createEReference(edgeEClass, EDGE__CONTAINING_GRAPH);
    createEReference(edgeEClass, EDGE__SOURCE);
    createEReference(edgeEClass, EDGE__TARGET);

    graphEClass = createEClass(GRAPH);
    createEReference(graphEClass, GRAPH__VERTICES);
    createEReference(graphEClass, GRAPH__EDGES);

    parameterizableEClass = createEClass(PARAMETERIZABLE);

    expressionEClass = createEClass(EXPRESSION);
    createEReference(expressionEClass, EXPRESSION__HOLDER);
    createEAttribute(expressionEClass, EXPRESSION__EXPRESSION_STRING);

    expressionHolderEClass = createEClass(EXPRESSION_HOLDER);
    createEReference(expressionHolderEClass, EXPRESSION_HOLDER__EXPRESSION);

    abstractVertexEClass = createEClass(ABSTRACT_VERTEX);
    createEAttribute(abstractVertexEClass, ABSTRACT_VERTEX__NAME);

    configurableEClass = createEClass(CONFIGURABLE);
    createEReference(configurableEClass, CONFIGURABLE__CONFIG_INPUT_PORTS);

    abstractActorEClass = createEClass(ABSTRACT_ACTOR);
    createEReference(abstractActorEClass, ABSTRACT_ACTOR__DATA_INPUT_PORTS);
    createEReference(abstractActorEClass, ABSTRACT_ACTOR__DATA_OUTPUT_PORTS);
    createEReference(abstractActorEClass, ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS);

    piGraphEClass = createEClass(PI_GRAPH);

    executableActorEClass = createEClass(EXECUTABLE_ACTOR);

    actorEClass = createEClass(ACTOR);
    createEReference(actorEClass, ACTOR__REFINEMENT);
    createEAttribute(actorEClass, ACTOR__MEMORY_SCRIPT_PATH);

    broadcastActorEClass = createEClass(BROADCAST_ACTOR);

    joinActorEClass = createEClass(JOIN_ACTOR);

    forkActorEClass = createEClass(FORK_ACTOR);

    roundBufferActorEClass = createEClass(ROUND_BUFFER_ACTOR);

    nonExecutableActorEClass = createEClass(NON_EXECUTABLE_ACTOR);

    portEClass = createEClass(PORT);
    createEAttribute(portEClass, PORT__NAME);

    dataPortEClass = createEClass(DATA_PORT);
    createEAttribute(dataPortEClass, DATA_PORT__ANNOTATION);

    dataInputPortEClass = createEClass(DATA_INPUT_PORT);
    createEReference(dataInputPortEClass, DATA_INPUT_PORT__INCOMING_FIFO);

    dataOutputPortEClass = createEClass(DATA_OUTPUT_PORT);
    createEReference(dataOutputPortEClass, DATA_OUTPUT_PORT__OUTGOING_FIFO);

    configInputPortEClass = createEClass(CONFIG_INPUT_PORT);
    createEReference(configInputPortEClass, CONFIG_INPUT_PORT__INCOMING_DEPENDENCY);
    createEReference(configInputPortEClass, CONFIG_INPUT_PORT__CONFIGURABLE);

    configOutputPortEClass = createEClass(CONFIG_OUTPUT_PORT);

    fifoEClass = createEClass(FIFO);
    createEReference(fifoEClass, FIFO__SOURCE_PORT);
    createEReference(fifoEClass, FIFO__TARGET_PORT);
    createEReference(fifoEClass, FIFO__DELAY);
    createEAttribute(fifoEClass, FIFO__TYPE);

    interfaceActorEClass = createEClass(INTERFACE_ACTOR);
    createEReference(interfaceActorEClass, INTERFACE_ACTOR__GRAPH_PORT);

    dataInputInterfaceEClass = createEClass(DATA_INPUT_INTERFACE);

    dataOutputInterfaceEClass = createEClass(DATA_OUTPUT_INTERFACE);

    configInputInterfaceEClass = createEClass(CONFIG_INPUT_INTERFACE);
    createEReference(configInputInterfaceEClass, CONFIG_INPUT_INTERFACE__GRAPH_PORT);

    configOutputInterfaceEClass = createEClass(CONFIG_OUTPUT_INTERFACE);

    refinementEClass = createEClass(REFINEMENT);
    createEAttribute(refinementEClass, REFINEMENT__FILE_PATH);

    piSDFRefinementEClass = createEClass(PI_SDF_REFINEMENT);

    cHeaderRefinementEClass = createEClass(CHEADER_REFINEMENT);
    createEReference(cHeaderRefinementEClass, CHEADER_REFINEMENT__LOOP_PROTOTYPE);
    createEReference(cHeaderRefinementEClass, CHEADER_REFINEMENT__INIT_PROTOTYPE);

    parameterEClass = createEClass(PARAMETER);

    dependencyEClass = createEClass(DEPENDENCY);
    createEReference(dependencyEClass, DEPENDENCY__SETTER);
    createEReference(dependencyEClass, DEPENDENCY__GETTER);

    iSetterEClass = createEClass(ISETTER);
    createEReference(iSetterEClass, ISETTER__OUTGOING_DEPENDENCIES);

    delayEClass = createEClass(DELAY);
    createEReference(delayEClass, DELAY__CONTAINING_FIFO);

    functionPrototypeEClass = createEClass(FUNCTION_PROTOTYPE);
    createEAttribute(functionPrototypeEClass, FUNCTION_PROTOTYPE__NAME);
    createEReference(functionPrototypeEClass, FUNCTION_PROTOTYPE__PARAMETERS);

    functionParameterEClass = createEClass(FUNCTION_PARAMETER);
    createEAttribute(functionParameterEClass, FUNCTION_PARAMETER__NAME);
    createEAttribute(functionParameterEClass, FUNCTION_PARAMETER__DIRECTION);
    createEAttribute(functionParameterEClass, FUNCTION_PARAMETER__TYPE);
    createEAttribute(functionParameterEClass, FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER);

    // Create enums
    directionEEnum = createEEnum(DIRECTION);
    portMemoryAnnotationEEnum = createEEnum(PORT_MEMORY_ANNOTATION);
    portKindEEnum = createEEnum(PORT_KIND);
    interfaceKindEEnum = createEEnum(INTERFACE_KIND);

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
    EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    expressionHolderEClass.getESuperTypes().add(this.getParameterizable());
    abstractVertexEClass.getESuperTypes().add(this.getVertex());
    configurableEClass.getESuperTypes().add(this.getAbstractVertex());
    configurableEClass.getESuperTypes().add(this.getParameterizable());
    abstractActorEClass.getESuperTypes().add(this.getConfigurable());
    piGraphEClass.getESuperTypes().add(this.getAbstractActor());
    piGraphEClass.getESuperTypes().add(this.getGraph());
    executableActorEClass.getESuperTypes().add(this.getAbstractActor());
    actorEClass.getESuperTypes().add(this.getExecutableActor());
    broadcastActorEClass.getESuperTypes().add(this.getExecutableActor());
    joinActorEClass.getESuperTypes().add(this.getExecutableActor());
    forkActorEClass.getESuperTypes().add(this.getExecutableActor());
    roundBufferActorEClass.getESuperTypes().add(this.getExecutableActor());
    nonExecutableActorEClass.getESuperTypes().add(this.getAbstractActor());
    dataPortEClass.getESuperTypes().add(this.getPort());
    dataPortEClass.getESuperTypes().add(this.getExpressionHolder());
    dataInputPortEClass.getESuperTypes().add(this.getDataPort());
    dataOutputPortEClass.getESuperTypes().add(this.getDataPort());
    configInputPortEClass.getESuperTypes().add(this.getPort());
    configOutputPortEClass.getESuperTypes().add(this.getDataOutputPort());
    configOutputPortEClass.getESuperTypes().add(this.getISetter());
    fifoEClass.getESuperTypes().add(this.getEdge());
    interfaceActorEClass.getESuperTypes().add(this.getAbstractActor());
    dataInputInterfaceEClass.getESuperTypes().add(this.getInterfaceActor());
    dataOutputInterfaceEClass.getESuperTypes().add(this.getInterfaceActor());
    configInputInterfaceEClass.getESuperTypes().add(this.getParameter());
    configOutputInterfaceEClass.getESuperTypes().add(this.getInterfaceActor());
    piSDFRefinementEClass.getESuperTypes().add(this.getRefinement());
    cHeaderRefinementEClass.getESuperTypes().add(this.getRefinement());
    parameterEClass.getESuperTypes().add(this.getVertex());
    parameterEClass.getESuperTypes().add(this.getConfigurable());
    parameterEClass.getESuperTypes().add(this.getISetter());
    parameterEClass.getESuperTypes().add(this.getExpressionHolder());
    dependencyEClass.getESuperTypes().add(this.getEdge());
    delayEClass.getESuperTypes().add(this.getConfigurable());
    delayEClass.getESuperTypes().add(this.getExpressionHolder());

    // Initialize classes and features; add operations and parameters
    initEClass(vertexEClass, Vertex.class, "Vertex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getVertex_ContainingGraph(), this.getGraph(), this.getGraph_Vertices(), "containingGraph", null, 0, 1, Vertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getVertex_OutEdges(), this.getEdge(), this.getEdge_Source(), "outEdges", null, 0, -1, Vertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getVertex_InEdges(), this.getEdge(), this.getEdge_Target(), "inEdges", null, 0, -1, Vertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(edgeEClass, Edge.class, "Edge", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getEdge_ContainingGraph(), this.getGraph(), this.getGraph_Edges(), "containingGraph", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdge_Source(), this.getVertex(), this.getVertex_OutEdges(), "source", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getEdge_Target(), this.getVertex(), this.getVertex_InEdges(), "target", null, 0, 1, Edge.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(graphEClass, Graph.class, "Graph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGraph_Vertices(), this.getVertex(), this.getVertex_ContainingGraph(), "vertices", null, 0, -1, Graph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGraph_Edges(), this.getEdge(), this.getEdge_ContainingGraph(), "edges", null, 0, -1, Graph.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(parameterizableEClass, Parameterizable.class, "Parameterizable", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(parameterizableEClass, this.getParameter(), "getInputParameters", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(parameterizableEClass, theEcorePackage.getEBoolean(), "isLocallyStatic", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(expressionEClass, Expression.class, "Expression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExpression_Holder(), this.getExpressionHolder(), this.getExpressionHolder_Expression(), "holder", null, 0, 1, Expression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getExpression_ExpressionString(), this.getString(), "expressionString", "0", 0, 1, Expression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(expressionHolderEClass, ExpressionHolder.class, "ExpressionHolder", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExpressionHolder_Expression(), this.getExpression(), this.getExpression_Holder(), "expression", null, 0, 1, ExpressionHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(abstractVertexEClass, AbstractVertex.class, "AbstractVertex", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAbstractVertex_Name(), this.getString(), "name", null, 0, 1, AbstractVertex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(abstractVertexEClass, this.getPiGraph(), "getContainingPiGraph", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(abstractVertexEClass, this.getPort(), "getAllPorts", 0, -1, IS_UNIQUE, IS_ORDERED);

    EOperation op = addEOperation(abstractVertexEClass, this.getPort(), "lookupPort", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getString(), "portName", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(abstractVertexEClass, this.getString(), "getVertexPath", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(configurableEClass, Configurable.class, "Configurable", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfigurable_ConfigInputPorts(), this.getConfigInputPort(), this.getConfigInputPort_Configurable(), "configInputPorts", null, 0, -1, Configurable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(configurableEClass, this.getParameter(), "getInputParameters", 0, -1, IS_UNIQUE, IS_ORDERED);

    op = addEOperation(configurableEClass, this.getConfigInputPort(), "lookupConfigInputPortConnectedWithParameter", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getParameter(), "parameter", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(configurableEClass, this.getPort(), "getAllConfigPorts", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(configurableEClass, this.getPort(), "getAllPorts", 0, -1, IS_UNIQUE, IS_ORDERED);

    initEClass(abstractActorEClass, AbstractActor.class, "AbstractActor", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAbstractActor_DataInputPorts(), this.getDataInputPort(), null, "dataInputPorts", null, 0, -1, AbstractActor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAbstractActor_DataOutputPorts(), this.getDataOutputPort(), null, "dataOutputPorts", null, 0, -1, AbstractActor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAbstractActor_ConfigOutputPorts(), this.getConfigOutputPort(), null, "configOutputPorts", null, 0, -1, AbstractActor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(abstractActorEClass, this.getDataPort(), "getAllDataPorts", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(abstractActorEClass, this.getPort(), "getAllConfigPorts", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(abstractActorEClass, this.getPort(), "getAllPorts", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(abstractActorEClass, this.getString(), "getActorPath", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(piGraphEClass, PiGraph.class, "PiGraph", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    op = addEOperation(piGraphEClass, theEcorePackage.getEBoolean(), "addActor", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getAbstractActor(), "actor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, theEcorePackage.getEBoolean(), "addParameter", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getParameter(), "parameter", 0, 1, !IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, theEcorePackage.getEBoolean(), "addFifo", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getFifo(), "fifo", 0, 1, !IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, theEcorePackage.getEBoolean(), "addDependency", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getDependency(), "dependency", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getParameter(), "getParameters", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getAbstractActor(), "getActors", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getFifo(), "getFifos", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getDependency(), "getDependencies", 0, -1, IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, theEcorePackage.getEBoolean(), "removeActor", 0, 1, IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getAbstractActor(), "actor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getParameter(), "getOnlyParameters", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getString(), "getActorsNames", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getString(), "getParametersNames", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getActor(), "getActorsWithRefinement", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getParameter(), "getAllParameters", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getAbstractActor(), "getOnlyActors", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getPiGraph(), "getChildrenGraphs", 0, -1, IS_UNIQUE, IS_ORDERED);

    addEOperation(piGraphEClass, this.getAbstractActor(), "getAllActors", 0, -1, IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, this.getParameter(), "lookupParameterGivenGraph", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getString(), "parameterName", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getString(), "graphName", 0, 1, !IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, this.getDataPort(), "lookupGraphDataPortForInterfaceActor", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getInterfaceActor(), "interfaceActor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, this.getAbstractVertex(), "lookupVertex", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getString(), "vertexName", 0, 1, !IS_UNIQUE, IS_ORDERED);

    op = addEOperation(piGraphEClass, this.getFifo(), "lookupFifo", 0, 1, !IS_UNIQUE, IS_ORDERED);
    addEParameter(op, this.getString(), "fifoId", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(executableActorEClass, ExecutableActor.class, "ExecutableActor", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(actorEClass, Actor.class, "Actor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getActor_Refinement(), this.getRefinement(), null, "refinement", null, 0, 1, Actor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getActor_MemoryScriptPath(), this.getIPath(), "memoryScriptPath", null, 0, 1, Actor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(actorEClass, theEcorePackage.getEBoolean(), "isConfigurationActor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(actorEClass, theEcorePackage.getEBoolean(), "isHierarchical", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(actorEClass, this.getAbstractActor(), "getChildAbstractActor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(actorEClass, this.getPiGraph(), "getSubGraph", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(broadcastActorEClass, BroadcastActor.class, "BroadcastActor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(joinActorEClass, JoinActor.class, "JoinActor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(forkActorEClass, ForkActor.class, "ForkActor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(roundBufferActorEClass, RoundBufferActor.class, "RoundBufferActor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(nonExecutableActorEClass, NonExecutableActor.class, "NonExecutableActor", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(portEClass, Port.class, "Port", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPort_Name(), this.getString(), "name", null, 0, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(portEClass, this.getPortKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(dataPortEClass, DataPort.class, "DataPort", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDataPort_Annotation(), this.getPortMemoryAnnotation(), "annotation", null, 0, 1, DataPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(dataPortEClass, this.getExpression(), "getPortRateExpression", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(dataPortEClass, this.getAbstractActor(), "getContainingActor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(dataPortEClass, this.getParameter(), "getInputParameters", 0, -1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(dataPortEClass, this.getString(), "getId", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(dataPortEClass, this.getFifo(), "getFifo", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(dataInputPortEClass, DataInputPort.class, "DataInputPort", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDataInputPort_IncomingFifo(), this.getFifo(), this.getFifo_TargetPort(), "incomingFifo", null, 0, 1, DataInputPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(dataInputPortEClass, this.getPortKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(dataInputPortEClass, this.getFifo(), "getFifo", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(dataOutputPortEClass, DataOutputPort.class, "DataOutputPort", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDataOutputPort_OutgoingFifo(), this.getFifo(), this.getFifo_SourcePort(), "outgoingFifo", null, 0, 1, DataOutputPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(dataOutputPortEClass, this.getPortKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(dataOutputPortEClass, this.getFifo(), "getFifo", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(configInputPortEClass, ConfigInputPort.class, "ConfigInputPort", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfigInputPort_IncomingDependency(), this.getDependency(), this.getDependency_Getter(), "incomingDependency", null, 0, 1, ConfigInputPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConfigInputPort_Configurable(), this.getConfigurable(), this.getConfigurable_ConfigInputPorts(), "configurable", null, 0, 1, ConfigInputPort.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(configInputPortEClass, this.getPortKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(configOutputPortEClass, ConfigOutputPort.class, "ConfigOutputPort", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(configOutputPortEClass, theEcorePackage.getEBoolean(), "isLocallyStatic", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(configOutputPortEClass, this.getPortKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(fifoEClass, Fifo.class, "Fifo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getFifo_SourcePort(), this.getDataOutputPort(), this.getDataOutputPort_OutgoingFifo(), "sourcePort", null, 0, 1, Fifo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFifo_TargetPort(), this.getDataInputPort(), this.getDataInputPort_IncomingFifo(), "targetPort", null, 0, 1, Fifo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFifo_Delay(), this.getDelay(), this.getDelay_ContainingFifo(), "delay", null, 0, 1, Fifo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFifo_Type(), this.getString(), "type", "void", 0, 1, Fifo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(fifoEClass, this.getString(), "getId", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(interfaceActorEClass, InterfaceActor.class, "InterfaceActor", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getInterfaceActor_GraphPort(), this.getPort(), null, "graphPort", null, 0, 1, InterfaceActor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(interfaceActorEClass, this.getDataPort(), "getDataPort", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(interfaceActorEClass, this.getInterfaceKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(dataInputInterfaceEClass, DataInputInterface.class, "DataInputInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(dataInputInterfaceEClass, this.getInterfaceKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(dataOutputInterfaceEClass, DataOutputInterface.class, "DataOutputInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(dataOutputInterfaceEClass, this.getInterfaceKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(configInputInterfaceEClass, ConfigInputInterface.class, "ConfigInputInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConfigInputInterface_GraphPort(), this.getConfigInputPort(), null, "graphPort", null, 0, 1, ConfigInputInterface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(configInputInterfaceEClass, theEcorePackage.getEBoolean(), "isLocallyStatic", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(configInputInterfaceEClass, theEcorePackage.getEBoolean(), "isConfigurationInterface", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(configOutputInterfaceEClass, ConfigOutputInterface.class, "ConfigOutputInterface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(configOutputInterfaceEClass, this.getInterfaceKind(), "getKind", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(refinementEClass, Refinement.class, "Refinement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getRefinement_FilePath(), this.getIPath(), "filePath", null, 0, 1, Refinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(refinementEClass, this.getAbstractActor(), "getAbstractActor", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(refinementEClass, this.getString(), "getFileName", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(refinementEClass, theEcorePackage.getEBoolean(), "isHierarchical", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(piSDFRefinementEClass, PiSDFRefinement.class, "PiSDFRefinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(piSDFRefinementEClass, theEcorePackage.getEBoolean(), "isHierarchical", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(cHeaderRefinementEClass, CHeaderRefinement.class, "CHeaderRefinement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCHeaderRefinement_LoopPrototype(), this.getFunctionPrototype(), null, "loopPrototype", null, 0, 1, CHeaderRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCHeaderRefinement_InitPrototype(), this.getFunctionPrototype(), null, "initPrototype", null, 0, 1, CHeaderRefinement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(cHeaderRefinementEClass, theEcorePackage.getEBoolean(), "isHierarchical", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    addEOperation(parameterEClass, this.getExpression(), "getValueExpression", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(parameterEClass, theEcorePackage.getEBoolean(), "isLocallyStatic", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(parameterEClass, theEcorePackage.getEBoolean(), "isDependent", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(parameterEClass, theEcorePackage.getEBoolean(), "isConfigurationInterface", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(dependencyEClass, Dependency.class, "Dependency", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDependency_Setter(), this.getISetter(), this.getISetter_OutgoingDependencies(), "setter", null, 0, 1, Dependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDependency_Getter(), this.getConfigInputPort(), this.getConfigInputPort_IncomingDependency(), "getter", null, 0, 1, Dependency.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(iSetterEClass, ISetter.class, "ISetter", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getISetter_OutgoingDependencies(), this.getDependency(), this.getDependency_Setter(), "outgoingDependencies", null, 0, -1, ISetter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(iSetterEClass, theEcorePackage.getEBoolean(), "isLocallyStatic", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(delayEClass, Delay.class, "Delay", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDelay_ContainingFifo(), this.getFifo(), this.getFifo_Delay(), "containingFifo", null, 0, 1, Delay.class, !IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    addEOperation(delayEClass, this.getExpression(), "getSizeExpression", 0, 1, !IS_UNIQUE, IS_ORDERED);

    addEOperation(delayEClass, this.getPiGraph(), "getContainingGraph", 0, 1, !IS_UNIQUE, IS_ORDERED);

    initEClass(functionPrototypeEClass, FunctionPrototype.class, "FunctionPrototype", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFunctionPrototype_Name(), this.getString(), "name", null, 0, 1, FunctionPrototype.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getFunctionPrototype_Parameters(), this.getFunctionParameter(), null, "parameters", null, 0, -1, FunctionPrototype.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(functionParameterEClass, FunctionParameter.class, "FunctionParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getFunctionParameter_Name(), this.getString(), "name", null, 0, 1, FunctionParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFunctionParameter_Direction(), this.getDirection(), "direction", null, 0, 1, FunctionParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFunctionParameter_Type(), this.getString(), "type", null, 0, 1, FunctionParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getFunctionParameter_IsConfigurationParameter(), theEcorePackage.getEBoolean(), "isConfigurationParameter", null, 0, 1, FunctionParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(directionEEnum, Direction.class, "Direction");
    addEEnumLiteral(directionEEnum, Direction.IN);
    addEEnumLiteral(directionEEnum, Direction.OUT);

    initEEnum(portMemoryAnnotationEEnum, PortMemoryAnnotation.class, "PortMemoryAnnotation");
    addEEnumLiteral(portMemoryAnnotationEEnum, PortMemoryAnnotation.NONE);
    addEEnumLiteral(portMemoryAnnotationEEnum, PortMemoryAnnotation.READ_ONLY);
    addEEnumLiteral(portMemoryAnnotationEEnum, PortMemoryAnnotation.WRITE_ONLY);
    addEEnumLiteral(portMemoryAnnotationEEnum, PortMemoryAnnotation.UNUSED);

    initEEnum(portKindEEnum, PortKind.class, "PortKind");
    addEEnumLiteral(portKindEEnum, PortKind.CFG_INPUT);
    addEEnumLiteral(portKindEEnum, PortKind.DATA_INPUT);
    addEEnumLiteral(portKindEEnum, PortKind.DATA_OUTPUT);
    addEEnumLiteral(portKindEEnum, PortKind.CFG_OUTPUT);

    initEEnum(interfaceKindEEnum, InterfaceKind.class, "InterfaceKind");
    addEEnumLiteral(interfaceKindEEnum, InterfaceKind.DATA_INPUT);
    addEEnumLiteral(interfaceKindEEnum, InterfaceKind.DATA_OUTPUT);
    addEEnumLiteral(interfaceKindEEnum, InterfaceKind.CFG_OUTPUT);
    addEEnumLiteral(interfaceKindEEnum, InterfaceKind.CFG_INPUT);

    // Initialize data types
    initEDataType(iPathEDataType, IPath.class, "IPath", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(stringEDataType, String.class, "String", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(intEDataType, int.class, "int", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(longEDataType, long.class, "long", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
    initEDataType(doubleEDataType, double.class, "double", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

    // Create resource
    createResource(eNS_URI);
  }

} //PiMMPackageImpl
