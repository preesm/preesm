/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.ietr.preesm.experiment.model.pimm.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PiMMFactoryImpl extends EFactoryImpl implements PiMMFactory {
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static PiMMFactory init() {
    try {
      PiMMFactory thePiMMFactory = (PiMMFactory)EPackage.Registry.INSTANCE.getEFactory(PiMMPackage.eNS_URI);
      if (thePiMMFactory != null) {
        return thePiMMFactory;
      }
    }
    catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new PiMMFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PiMMFactoryImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass) {
    switch (eClass.getClassifierID()) {
      case PiMMPackage.VERTEX: return createVertex();
      case PiMMPackage.EDGE: return createEdge();
      case PiMMPackage.GRAPH: return createGraph();
      case PiMMPackage.EXPRESSION: return createExpression();
      case PiMMPackage.PI_GRAPH: return createPiGraph();
      case PiMMPackage.ACTOR: return createActor();
      case PiMMPackage.BROADCAST_ACTOR: return createBroadcastActor();
      case PiMMPackage.JOIN_ACTOR: return createJoinActor();
      case PiMMPackage.FORK_ACTOR: return createForkActor();
      case PiMMPackage.ROUND_BUFFER_ACTOR: return createRoundBufferActor();
      case PiMMPackage.DATA_INPUT_PORT: return createDataInputPort();
      case PiMMPackage.DATA_OUTPUT_PORT: return createDataOutputPort();
      case PiMMPackage.CONFIG_INPUT_PORT: return createConfigInputPort();
      case PiMMPackage.CONFIG_OUTPUT_PORT: return createConfigOutputPort();
      case PiMMPackage.FIFO: return createFifo();
      case PiMMPackage.DATA_INPUT_INTERFACE: return createDataInputInterface();
      case PiMMPackage.DATA_OUTPUT_INTERFACE: return createDataOutputInterface();
      case PiMMPackage.CONFIG_INPUT_INTERFACE: return createConfigInputInterface();
      case PiMMPackage.CONFIG_OUTPUT_INTERFACE: return createConfigOutputInterface();
      case PiMMPackage.PI_SDF_REFINEMENT: return createPiSDFRefinement();
      case PiMMPackage.CHEADER_REFINEMENT: return createCHeaderRefinement();
      case PiMMPackage.PARAMETER: return createParameter();
      case PiMMPackage.DEPENDENCY: return createDependency();
      case PiMMPackage.DELAY: return createDelay();
      case PiMMPackage.FUNCTION_PROTOTYPE: return createFunctionPrototype();
      case PiMMPackage.FUNCTION_PARAMETER: return createFunctionParameter();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue) {
    switch (eDataType.getClassifierID()) {
      case PiMMPackage.DIRECTION:
        return createDirectionFromString(eDataType, initialValue);
      case PiMMPackage.PORT_MEMORY_ANNOTATION:
        return createPortMemoryAnnotationFromString(eDataType, initialValue);
      case PiMMPackage.PORT_KIND:
        return createPortKindFromString(eDataType, initialValue);
      case PiMMPackage.INTERFACE_KIND:
        return createInterfaceKindFromString(eDataType, initialValue);
      case PiMMPackage.IPATH:
        return createIPathFromString(eDataType, initialValue);
      case PiMMPackage.STRING:
        return createStringFromString(eDataType, initialValue);
      case PiMMPackage.INT:
        return createintFromString(eDataType, initialValue);
      case PiMMPackage.LONG:
        return createlongFromString(eDataType, initialValue);
      case PiMMPackage.DOUBLE:
        return createdoubleFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue) {
    switch (eDataType.getClassifierID()) {
      case PiMMPackage.DIRECTION:
        return convertDirectionToString(eDataType, instanceValue);
      case PiMMPackage.PORT_MEMORY_ANNOTATION:
        return convertPortMemoryAnnotationToString(eDataType, instanceValue);
      case PiMMPackage.PORT_KIND:
        return convertPortKindToString(eDataType, instanceValue);
      case PiMMPackage.INTERFACE_KIND:
        return convertInterfaceKindToString(eDataType, instanceValue);
      case PiMMPackage.IPATH:
        return convertIPathToString(eDataType, instanceValue);
      case PiMMPackage.STRING:
        return convertStringToString(eDataType, instanceValue);
      case PiMMPackage.INT:
        return convertintToString(eDataType, instanceValue);
      case PiMMPackage.LONG:
        return convertlongToString(eDataType, instanceValue);
      case PiMMPackage.DOUBLE:
        return convertdoubleToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Vertex createVertex() {
    VertexImpl vertex = new VertexImpl();
    return vertex;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Edge createEdge() {
    EdgeImpl edge = new EdgeImpl();
    return edge;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Graph createGraph() {
    GraphImpl graph = new GraphImpl();
    return graph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression createExpression() {
    ExpressionImpl expression = new ExpressionImpl();
    return expression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PiGraph createPiGraph() {
    PiGraphImpl piGraph = new PiGraphImpl();
    return piGraph;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Actor createActor() {
    ActorImpl actor = new ActorImpl();
    return actor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BroadcastActor createBroadcastActor() {
    BroadcastActorImpl broadcastActor = new BroadcastActorImpl();
    return broadcastActor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public JoinActor createJoinActor() {
    JoinActorImpl joinActor = new JoinActorImpl();
    return joinActor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ForkActor createForkActor() {
    ForkActorImpl forkActor = new ForkActorImpl();
    return forkActor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RoundBufferActor createRoundBufferActor() {
    RoundBufferActorImpl roundBufferActor = new RoundBufferActorImpl();
    return roundBufferActor;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataInputPort createDataInputPort() {
    DataInputPortImpl dataInputPort = new DataInputPortImpl();
    return dataInputPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataOutputPort createDataOutputPort() {
    DataOutputPortImpl dataOutputPort = new DataOutputPortImpl();
    return dataOutputPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputPort createConfigInputPort() {
    ConfigInputPortImpl configInputPort = new ConfigInputPortImpl();
    return configInputPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigOutputPort createConfigOutputPort() {
    ConfigOutputPortImpl configOutputPort = new ConfigOutputPortImpl();
    return configOutputPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Fifo createFifo() {
    FifoImpl fifo = new FifoImpl();
    return fifo;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataInputInterface createDataInputInterface() {
    DataInputInterfaceImpl dataInputInterface = new DataInputInterfaceImpl();
    return dataInputInterface;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataOutputInterface createDataOutputInterface() {
    DataOutputInterfaceImpl dataOutputInterface = new DataOutputInterfaceImpl();
    return dataOutputInterface;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigInputInterface createConfigInputInterface() {
    ConfigInputInterfaceImpl configInputInterface = new ConfigInputInterfaceImpl();
    return configInputInterface;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConfigOutputInterface createConfigOutputInterface() {
    ConfigOutputInterfaceImpl configOutputInterface = new ConfigOutputInterfaceImpl();
    return configOutputInterface;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PiSDFRefinement createPiSDFRefinement() {
    PiSDFRefinementImpl piSDFRefinement = new PiSDFRefinementImpl();
    return piSDFRefinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CHeaderRefinement createCHeaderRefinement() {
    CHeaderRefinementImpl cHeaderRefinement = new CHeaderRefinementImpl();
    return cHeaderRefinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Parameter createParameter() {
    ParameterImpl parameter = new ParameterImpl();
    return parameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Dependency createDependency() {
    DependencyImpl dependency = new DependencyImpl();
    return dependency;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Delay createDelay() {
    DelayImpl delay = new DelayImpl();
    return delay;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionPrototype createFunctionPrototype() {
    FunctionPrototypeImpl functionPrototype = new FunctionPrototypeImpl();
    return functionPrototype;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionParameter createFunctionParameter() {
    FunctionParameterImpl functionParameter = new FunctionParameterImpl();
    return functionParameter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Direction createDirectionFromString(EDataType eDataType, String initialValue) {
    Direction result = Direction.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertDirectionToString(EDataType eDataType, Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PortMemoryAnnotation createPortMemoryAnnotationFromString(EDataType eDataType, String initialValue) {
    PortMemoryAnnotation result = PortMemoryAnnotation.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertPortMemoryAnnotationToString(EDataType eDataType, Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PortKind createPortKindFromString(EDataType eDataType, String initialValue) {
    PortKind result = PortKind.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertPortKindToString(EDataType eDataType, Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public InterfaceKind createInterfaceKindFromString(EDataType eDataType, String initialValue) {
    InterfaceKind result = InterfaceKind.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertInterfaceKindToString(EDataType eDataType, Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IPath createIPathFromString(EDataType eDataType, String initialValue) {
    return (IPath)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertIPathToString(EDataType eDataType, Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String createStringFromString(EDataType eDataType, String initialValue) {
    return (String)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertStringToString(EDataType eDataType, Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Integer createintFromString(EDataType eDataType, String initialValue) {
    return (Integer)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertintToString(EDataType eDataType, Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Long createlongFromString(EDataType eDataType, String initialValue) {
    return (Long)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertlongToString(EDataType eDataType, Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Double createdoubleFromString(EDataType eDataType, String initialValue) {
    return (Double)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertdoubleToString(EDataType eDataType, Object instanceValue) {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PiMMPackage getPiMMPackage() {
    return (PiMMPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static PiMMPackage getPackage() {
    return PiMMPackage.eINSTANCE;
  }

} //PiMMFactoryImpl
