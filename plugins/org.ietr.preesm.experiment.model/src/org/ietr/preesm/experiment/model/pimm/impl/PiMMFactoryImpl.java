/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.ecore.EClass;
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
			PiMMFactory thePiMMFactory = (PiMMFactory)EPackage.Registry.INSTANCE.getEFactory("http://org.ietr.preesm/experiment/model/pimm"); 
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
			case PiMMPackage.GRAPH: return createGraph();
			case PiMMPackage.ACTOR: return createActor();
			case PiMMPackage.INPUT_PORT: return createInputPort();
			case PiMMPackage.OUTPUT_PORT: return createOutputPort();
			case PiMMPackage.CONFIG_INPUT_PORT: return createConfigInputPort();
			case PiMMPackage.CONFIG_OUTPUT_PORT: return createConfigOutputPort();
			case PiMMPackage.FIFO: return createFifo();
			case PiMMPackage.INTERFACE_ACTOR: return createInterfaceActor();
			case PiMMPackage.SOURCE_INTERFACE: return createSourceInterface();
			case PiMMPackage.SINK_INTERFACE: return createSinkInterface();
			case PiMMPackage.CONFIG_OUTPUT_INTERFACE: return createConfigOutputInterface();
			case PiMMPackage.REFINEMENT: return createRefinement();
			case PiMMPackage.PARAMETER: return createParameter();
			case PiMMPackage.DEPENDENCY: return createDependency();
			case PiMMPackage.DELAY: return createDelay();
			case PiMMPackage.EXPRESSION: return createExpression();
			case PiMMPackage.CONFIG_INPUT_INTERFACE: return createConfigInputInterface();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
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
	public Actor createActor() {
		ActorImpl actor = new ActorImpl();
		return actor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InputPort createInputPort() {
		InputPortImpl inputPort = new InputPortImpl();
		return inputPort;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public OutputPort createOutputPort() {
		OutputPortImpl outputPort = new OutputPortImpl();
		return outputPort;
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
	public InterfaceActor createInterfaceActor() {
		InterfaceActorImpl interfaceActor = new InterfaceActorImpl();
		return interfaceActor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SourceInterface createSourceInterface() {
		SourceInterfaceImpl sourceInterface = new SourceInterfaceImpl();
		return sourceInterface;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SinkInterface createSinkInterface() {
		SinkInterfaceImpl sinkInterface = new SinkInterfaceImpl();
		return sinkInterface;
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
	public Refinement createRefinement() {
		RefinementImpl refinement = new RefinementImpl();
		return refinement;
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
	public Expression createExpression() {
		ExpressionImpl expression = new ExpressionImpl();
		return expression;
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
