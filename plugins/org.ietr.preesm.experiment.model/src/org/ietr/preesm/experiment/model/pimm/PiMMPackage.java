/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
 * @see org.ietr.preesm.experiment.model.pimm.PiMMFactory
 * @model kind="package"
 * @generated
 */
public interface PiMMPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pimm";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.ietr.preesm/experiment/model/pimm";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.ietr.preesm.experiment.pimm";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PiMMPackage eINSTANCE = org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl <em>Abstract Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractActor()
	 * @generated
	 */
	int ABSTRACT_ACTOR = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__NAME = 0;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__INPUT_PORTS = 1;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__OUTPUT_PORTS = 2;

	/**
	 * The number of structural features of the '<em>Abstract Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl <em>Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.GraphImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getGraph()
	 * @generated
	 */
	int GRAPH = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__NAME = ABSTRACT_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__INPUT_PORTS = ABSTRACT_ACTOR__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__OUTPUT_PORTS = ABSTRACT_ACTOR__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__VERTICES = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Fifos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__FIFOS = ABSTRACT_ACTOR_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__PARAMETERS = ABSTRACT_ACTOR_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl <em>Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ActorImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getActor()
	 * @generated
	 */
	int ACTOR = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__NAME = ABSTRACT_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__INPUT_PORTS = ABSTRACT_ACTOR__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__OUTPUT_PORTS = ABSTRACT_ACTOR__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Refinement</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__REFINEMENT = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 1;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PortImpl <em>Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__NAME = 0;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__KIND = 1;

	/**
	 * The number of structural features of the '<em>Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.InputPortImpl <em>Input Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.InputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInputPort()
	 * @generated
	 */
	int INPUT_PORT = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__NAME = PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__KIND = PORT__KIND;

	/**
	 * The feature id for the '<em><b>Incoming Fifo</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT__INCOMING_FIFO = PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Input Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INPUT_PORT_FEATURE_COUNT = PORT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.OutputPortImpl <em>Output Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.OutputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getOutputPort()
	 * @generated
	 */
	int OUTPUT_PORT = 5;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__NAME = PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__KIND = PORT__KIND;

	/**
	 * The feature id for the '<em><b>Outgoing Fifo</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__OUTGOING_FIFO = PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT_FEATURE_COUNT = PORT_FEATURE_COUNT + 1;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl <em>Fifo</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.FifoImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFifo()
	 * @generated
	 */
	int FIFO = 6;

	/**
	 * The feature id for the '<em><b>Source Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIFO__SOURCE_PORT = 0;

	/**
	 * The feature id for the '<em><b>Target Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIFO__TARGET_PORT = 1;

	/**
	 * The number of structural features of the '<em>Fifo</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIFO_FEATURE_COUNT = 2;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl <em>Interface Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInterfaceActor()
	 * @generated
	 */
	int INTERFACE_ACTOR = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__NAME = ABSTRACT_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__INPUT_PORTS = ABSTRACT_ACTOR__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__OUTPUT_PORTS = ABSTRACT_ACTOR__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__GRAPH_PORT = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__KIND = ABSTRACT_ACTOR_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Interface Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.SourceInterfaceImpl <em>Source Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.SourceInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getSourceInterface()
	 * @generated
	 */
	int SOURCE_INTERFACE = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__INPUT_PORTS = INTERFACE_ACTOR__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__OUTPUT_PORTS = INTERFACE_ACTOR__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__GRAPH_PORT = INTERFACE_ACTOR__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__KIND = INTERFACE_ACTOR__KIND;

	/**
	 * The number of structural features of the '<em>Source Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.SinkInterfaceImpl <em>Sink Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.SinkInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getSinkInterface()
	 * @generated
	 */
	int SINK_INTERFACE = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__INPUT_PORTS = INTERFACE_ACTOR__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__OUTPUT_PORTS = INTERFACE_ACTOR__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__GRAPH_PORT = INTERFACE_ACTOR__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__KIND = INTERFACE_ACTOR__KIND;

	/**
	 * The number of structural features of the '<em>Sink Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl <em>Refinement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRefinement()
	 * @generated
	 */
	int REFINEMENT = 10;

	/**
	 * The feature id for the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFINEMENT__FILE_NAME = 0;

	/**
	 * The number of structural features of the '<em>Refinement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFINEMENT_FEATURE_COUNT = 1;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 11;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = 0;

	/**
	 * The feature id for the '<em><b>Locally Static</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__LOCALLY_STATIC = 1;

	/**
	 * The feature id for the '<em><b>Configuration Interface</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__CONFIGURATION_INTERFACE = 2;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = 3;


	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor <em>Abstract Actor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Actor</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor
	 * @generated
	 */
	EClass getAbstractActor();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getName()
	 * @see #getAbstractActor()
	 * @generated
	 */
	EAttribute getAbstractActor_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getInputPorts <em>Input Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Input Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getInputPorts()
	 * @see #getAbstractActor()
	 * @generated
	 */
	EReference getAbstractActor_InputPorts();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getOutputPorts <em>Output Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Output Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getOutputPorts()
	 * @see #getAbstractActor()
	 * @generated
	 */
	EReference getAbstractActor_OutputPorts();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Graph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Graph</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Graph
	 * @generated
	 */
	EClass getGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Graph#getVertices <em>Vertices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vertices</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Graph#getVertices()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Vertices();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Graph#getFifos <em>Fifos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fifos</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Graph#getFifos()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Fifos();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Graph#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Graph#getParameters()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Parameters();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Actor <em>Actor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Actor</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Actor
	 * @generated
	 */
	EClass getActor();

	/**
	 * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.Actor#getRefinement <em>Refinement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Refinement</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Actor#getRefinement()
	 * @see #getActor()
	 * @generated
	 */
	EReference getActor_Refinement();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Port
	 * @generated
	 */
	EClass getPort();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Port#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Port#getName()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Port#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Port#getKind()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Kind();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.InputPort <em>Input Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Input Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.InputPort
	 * @generated
	 */
	EClass getInputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.InputPort#getIncomingFifo <em>Incoming Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Incoming Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.InputPort#getIncomingFifo()
	 * @see #getInputPort()
	 * @generated
	 */
	EReference getInputPort_IncomingFifo();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.OutputPort <em>Output Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Output Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.OutputPort
	 * @generated
	 */
	EClass getOutputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.OutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Outgoing Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.OutputPort#getOutgoingFifo()
	 * @see #getOutputPort()
	 * @generated
	 */
	EReference getOutputPort_OutgoingFifo();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Fifo <em>Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Fifo
	 * @generated
	 */
	EClass getFifo();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort <em>Source Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Fifo#getSourcePort()
	 * @see #getFifo()
	 * @generated
	 */
	EReference getFifo_SourcePort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort <em>Target Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Fifo#getTargetPort()
	 * @see #getFifo()
	 * @generated
	 */
	EReference getFifo_TargetPort();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor <em>Interface Actor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interface Actor</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.InterfaceActor
	 * @generated
	 */
	EClass getInterfaceActor();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor#getGraphPort <em>Graph Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Graph Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.InterfaceActor#getGraphPort()
	 * @see #getInterfaceActor()
	 * @generated
	 */
	EReference getInterfaceActor_GraphPort();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.InterfaceActor#getKind()
	 * @see #getInterfaceActor()
	 * @generated
	 */
	EAttribute getInterfaceActor_Kind();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.SourceInterface <em>Source Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Source Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.SourceInterface
	 * @generated
	 */
	EClass getSourceInterface();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.SinkInterface <em>Sink Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sink Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.SinkInterface
	 * @generated
	 */
	EClass getSinkInterface();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Refinement <em>Refinement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Refinement</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Refinement
	 * @generated
	 */
	EClass getRefinement();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Refinement#getFileName <em>File Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Refinement#getFileName()
	 * @see #getRefinement()
	 * @generated
	 */
	EAttribute getRefinement_FileName();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Parameter <em>Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameter</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameter
	 * @generated
	 */
	EClass getParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Parameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameter#getName()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Parameter#isLocallyStatic <em>Locally Static</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Locally Static</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameter#isLocallyStatic()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_LocallyStatic();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Parameter#isConfigurationInterface <em>Configuration Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Configuration Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameter#isConfigurationInterface()
	 * @see #getParameter()
	 * @generated
	 */
	EAttribute getParameter_ConfigurationInterface();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PiMMFactory getPiMMFactory();

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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl <em>Abstract Actor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractActor()
		 * @generated
		 */
		EClass ABSTRACT_ACTOR = eINSTANCE.getAbstractActor();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_ACTOR__NAME = eINSTANCE.getAbstractActor_Name();

		/**
		 * The meta object literal for the '<em><b>Input Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_ACTOR__INPUT_PORTS = eINSTANCE.getAbstractActor_InputPorts();

		/**
		 * The meta object literal for the '<em><b>Output Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_ACTOR__OUTPUT_PORTS = eINSTANCE.getAbstractActor_OutputPorts();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl <em>Graph</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.GraphImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getGraph()
		 * @generated
		 */
		EClass GRAPH = eINSTANCE.getGraph();

		/**
		 * The meta object literal for the '<em><b>Vertices</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__VERTICES = eINSTANCE.getGraph_Vertices();

		/**
		 * The meta object literal for the '<em><b>Fifos</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__FIFOS = eINSTANCE.getGraph_Fifos();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__PARAMETERS = eINSTANCE.getGraph_Parameters();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl <em>Actor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ActorImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getActor()
		 * @generated
		 */
		EClass ACTOR = eINSTANCE.getActor();

		/**
		 * The meta object literal for the '<em><b>Refinement</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACTOR__REFINEMENT = eINSTANCE.getActor_Refinement();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PortImpl <em>Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPort()
		 * @generated
		 */
		EClass PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__NAME = eINSTANCE.getPort_Name();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__KIND = eINSTANCE.getPort_Kind();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.InputPortImpl <em>Input Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.InputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInputPort()
		 * @generated
		 */
		EClass INPUT_PORT = eINSTANCE.getInputPort();

		/**
		 * The meta object literal for the '<em><b>Incoming Fifo</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INPUT_PORT__INCOMING_FIFO = eINSTANCE.getInputPort_IncomingFifo();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.OutputPortImpl <em>Output Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.OutputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getOutputPort()
		 * @generated
		 */
		EClass OUTPUT_PORT = eINSTANCE.getOutputPort();

		/**
		 * The meta object literal for the '<em><b>Outgoing Fifo</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_PORT__OUTGOING_FIFO = eINSTANCE.getOutputPort_OutgoingFifo();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl <em>Fifo</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.FifoImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFifo()
		 * @generated
		 */
		EClass FIFO = eINSTANCE.getFifo();

		/**
		 * The meta object literal for the '<em><b>Source Port</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FIFO__SOURCE_PORT = eINSTANCE.getFifo_SourcePort();

		/**
		 * The meta object literal for the '<em><b>Target Port</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FIFO__TARGET_PORT = eINSTANCE.getFifo_TargetPort();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl <em>Interface Actor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInterfaceActor()
		 * @generated
		 */
		EClass INTERFACE_ACTOR = eINSTANCE.getInterfaceActor();

		/**
		 * The meta object literal for the '<em><b>Graph Port</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_ACTOR__GRAPH_PORT = eINSTANCE.getInterfaceActor_GraphPort();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERFACE_ACTOR__KIND = eINSTANCE.getInterfaceActor_Kind();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.SourceInterfaceImpl <em>Source Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.SourceInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getSourceInterface()
		 * @generated
		 */
		EClass SOURCE_INTERFACE = eINSTANCE.getSourceInterface();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.SinkInterfaceImpl <em>Sink Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.SinkInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getSinkInterface()
		 * @generated
		 */
		EClass SINK_INTERFACE = eINSTANCE.getSinkInterface();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl <em>Refinement</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRefinement()
		 * @generated
		 */
		EClass REFINEMENT = eINSTANCE.getRefinement();

		/**
		 * The meta object literal for the '<em><b>File Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFINEMENT__FILE_NAME = eINSTANCE.getRefinement_FileName();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl <em>Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameter()
		 * @generated
		 */
		EClass PARAMETER = eINSTANCE.getParameter();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__NAME = eINSTANCE.getParameter_Name();

		/**
		 * The meta object literal for the '<em><b>Locally Static</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__LOCALLY_STATIC = eINSTANCE.getParameter_LocallyStatic();

		/**
		 * The meta object literal for the '<em><b>Configuration Interface</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PARAMETER__CONFIGURATION_INTERFACE = eINSTANCE.getParameter_ConfigurationInterface();

	}

} //PiMMPackage
