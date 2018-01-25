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
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.BroadcastActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputInterface;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.ForkActor;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.InterfaceKind;
import org.ietr.preesm.experiment.model.pimm.JoinActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PiSDFRefinement;
import org.ietr.preesm.experiment.model.pimm.PortKind;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;
import org.ietr.preesm.experiment.model.pimm.RoundBufferActor;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class PiMMFactoryImpl extends EFactoryImpl implements PiMMFactory {
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static PiMMFactory init() {
    try {
      final PiMMFactory thePiMMFactory = (PiMMFactory) EPackage.Registry.INSTANCE.getEFactory(PiMMPackage.eNS_URI);
      if (thePiMMFactory != null) {
        return thePiMMFactory;
      }
    } catch (final Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new PiMMFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public PiMMFactoryImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EObject create(final EClass eClass) {
    switch (eClass.getClassifierID()) {
      case PiMMPackage.PI_GRAPH:
        return createPiGraph();
      case PiMMPackage.ACTOR:
        return createActor();
      case PiMMPackage.BROADCAST_ACTOR:
        return createBroadcastActor();
      case PiMMPackage.JOIN_ACTOR:
        return createJoinActor();
      case PiMMPackage.FORK_ACTOR:
        return createForkActor();
      case PiMMPackage.ROUND_BUFFER_ACTOR:
        return createRoundBufferActor();
      case PiMMPackage.DATA_INPUT_PORT:
        return createDataInputPort();
      case PiMMPackage.DATA_OUTPUT_PORT:
        return createDataOutputPort();
      case PiMMPackage.CONFIG_INPUT_PORT:
        return createConfigInputPort();
      case PiMMPackage.CONFIG_OUTPUT_PORT:
        return createConfigOutputPort();
      case PiMMPackage.FIFO:
        return createFifo();
      case PiMMPackage.DATA_INPUT_INTERFACE:
        return createDataInputInterface();
      case PiMMPackage.DATA_OUTPUT_INTERFACE:
        return createDataOutputInterface();
      case PiMMPackage.CONFIG_INPUT_INTERFACE:
        return createConfigInputInterface();
      case PiMMPackage.CONFIG_OUTPUT_INTERFACE:
        return createConfigOutputInterface();
      case PiMMPackage.PI_SDF_REFINEMENT:
        return createPiSDFRefinement();
      case PiMMPackage.CHEADER_REFINEMENT:
        return createCHeaderRefinement();
      case PiMMPackage.PARAMETER:
        return createParameter();
      case PiMMPackage.DEPENDENCY:
        return createDependency();
      case PiMMPackage.DELAY:
        return createDelay();
      case PiMMPackage.EXPRESSION:
        return createExpression();
      case PiMMPackage.FUNCTION_PROTOTYPE:
        return createFunctionPrototype();
      case PiMMPackage.FUNCTION_PARAMETER:
        return createFunctionParameter();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object createFromString(final EDataType eDataType, final String initialValue) {
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
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertToString(final EDataType eDataType, final Object instanceValue) {
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
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PiGraph createPiGraph() {
    final PiGraphImpl piGraph = new PiGraphImpl();
    return piGraph;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Actor createActor() {
    final ActorImpl actor = new ActorImpl();
    return actor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public BroadcastActor createBroadcastActor() {
    final BroadcastActorImpl broadcastActor = new BroadcastActorImpl();
    return broadcastActor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public JoinActor createJoinActor() {
    final JoinActorImpl joinActor = new JoinActorImpl();
    return joinActor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ForkActor createForkActor() {
    final ForkActorImpl forkActor = new ForkActorImpl();
    return forkActor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public RoundBufferActor createRoundBufferActor() {
    final RoundBufferActorImpl roundBufferActor = new RoundBufferActorImpl();
    return roundBufferActor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DataInputPort createDataInputPort() {
    final DataInputPortImpl dataInputPort = new DataInputPortImpl();
    return dataInputPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DataOutputPort createDataOutputPort() {
    final DataOutputPortImpl dataOutputPort = new DataOutputPortImpl();
    return dataOutputPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ConfigInputPort createConfigInputPort() {
    final ConfigInputPortImpl configInputPort = new ConfigInputPortImpl();
    return configInputPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ConfigOutputPort createConfigOutputPort() {
    final ConfigOutputPortImpl configOutputPort = new ConfigOutputPortImpl();
    return configOutputPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Fifo createFifo() {
    final FifoImpl fifo = new FifoImpl();
    return fifo;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DataInputInterface createDataInputInterface() {
    final DataInputInterfaceImpl dataInputInterface = new DataInputInterfaceImpl();
    return dataInputInterface;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DataOutputInterface createDataOutputInterface() {
    final DataOutputInterfaceImpl dataOutputInterface = new DataOutputInterfaceImpl();
    return dataOutputInterface;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ConfigInputInterface createConfigInputInterface() {
    final ConfigInputInterfaceImpl configInputInterface = new ConfigInputInterfaceImpl();
    return configInputInterface;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public ConfigOutputInterface createConfigOutputInterface() {
    final ConfigOutputInterfaceImpl configOutputInterface = new ConfigOutputInterfaceImpl();
    return configOutputInterface;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PiSDFRefinement createPiSDFRefinement() {
    final PiSDFRefinementImpl piSDFRefinement = new PiSDFRefinementImpl();
    return piSDFRefinement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public CHeaderRefinement createCHeaderRefinement() {
    final CHeaderRefinementImpl cHeaderRefinement = new CHeaderRefinementImpl();
    return cHeaderRefinement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Parameter createParameter() {
    final ParameterImpl parameter = new ParameterImpl();
    return parameter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Dependency createDependency() {
    final DependencyImpl dependency = new DependencyImpl();
    return dependency;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Delay createDelay() {
    final DelayImpl delay = new DelayImpl();
    return delay;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Expression createExpression() {
    final ExpressionImpl expression = new ExpressionImpl();
    return expression;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public FunctionPrototype createFunctionPrototype() {
    final FunctionPrototypeImpl functionPrototype = new FunctionPrototypeImpl();
    return functionPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public FunctionParameter createFunctionParameter() {
    final FunctionParameterImpl functionParameter = new FunctionParameterImpl();
    return functionParameter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Direction createDirection(final String literal) {
    final Direction result = Direction.get(literal);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + PiMMPackage.Literals.DIRECTION.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public Direction createDirectionFromString(final EDataType eDataType, final String initialValue) {
    return createDirection(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertDirection(final Direction instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertDirectionToString(final EDataType eDataType, final Object instanceValue) {
    return convertDirection((Direction) instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PortMemoryAnnotation createPortMemoryAnnotation(final String literal) {
    final PortMemoryAnnotation result = PortMemoryAnnotation.get(literal);
    if (result == null) {
      throw new IllegalArgumentException(
          "The value '" + literal + "' is not a valid enumerator of '" + PiMMPackage.Literals.PORT_MEMORY_ANNOTATION.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public PortMemoryAnnotation createPortMemoryAnnotationFromString(final EDataType eDataType, final String initialValue) {
    return createPortMemoryAnnotation(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertPortMemoryAnnotation(final PortMemoryAnnotation instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertPortMemoryAnnotationToString(final EDataType eDataType, final Object instanceValue) {
    return convertPortMemoryAnnotation((PortMemoryAnnotation) instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PortKind createPortKind(final String literal) {
    final PortKind result = PortKind.get(literal);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + PiMMPackage.Literals.PORT_KIND.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public PortKind createPortKindFromString(final EDataType eDataType, final String initialValue) {
    return createPortKind(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertPortKind(final PortKind instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertPortKindToString(final EDataType eDataType, final Object instanceValue) {
    return convertPortKind((PortKind) instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public InterfaceKind createInterfaceKind(final String literal) {
    final InterfaceKind result = InterfaceKind.get(literal);
    if (result == null) {
      throw new IllegalArgumentException("The value '" + literal + "' is not a valid enumerator of '" + PiMMPackage.Literals.INTERFACE_KIND.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public InterfaceKind createInterfaceKindFromString(final EDataType eDataType, final String initialValue) {
    return createInterfaceKind(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertInterfaceKind(final InterfaceKind instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertInterfaceKindToString(final EDataType eDataType, final Object instanceValue) {
    return convertInterfaceKind((InterfaceKind) instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IPath createIPath(final String literal) {
    return (IPath) super.createFromString(PiMMPackage.Literals.IPATH, literal);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public IPath createIPathFromString(final EDataType eDataType, final String initialValue) {
    return createIPath(initialValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String convertIPath(final IPath instanceValue) {
    return super.convertToString(PiMMPackage.Literals.IPATH, instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String convertIPathToString(final EDataType eDataType, final Object instanceValue) {
    return convertIPath((IPath) instanceValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PiMMPackage getPiMMPackage() {
    return (PiMMPackage) getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static PiMMPackage getPackage() {
    return PiMMPackage.eINSTANCE;
  }

} // PiMMFactoryImpl
