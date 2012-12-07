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
			case PiMMPackage.FIFO: return createFifo();
			case PiMMPackage.INTERFACE_VERTEX: return createInterfaceVertex();
			case PiMMPackage.SOURCE_INTERFACE: return createSourceInterface();
			case PiMMPackage.SINK_INTERFACE: return createSinkInterface();
			case PiMMPackage.REFINEMENT: return createRefinement();
			case PiMMPackage.PARAMETER: return createParameter();
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
	public Fifo createFifo() {
		FifoImpl fifo = new FifoImpl();
		return fifo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterfaceVertex createInterfaceVertex() {
		InterfaceVertexImpl interfaceVertex = new InterfaceVertexImpl();
		return interfaceVertex;
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
