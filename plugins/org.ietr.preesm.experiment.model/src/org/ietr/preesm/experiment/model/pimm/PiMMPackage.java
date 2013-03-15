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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterizableImpl <em>Parameterizable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterizableImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameterizable()
	 * @generated
	 */
	int PARAMETERIZABLE = 0;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZABLE__CONFIG_INPUT_PORTS = 0;

	/**
	 * The number of structural features of the '<em>Parameterizable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETERIZABLE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl <em>Abstract Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractVertex()
	 * @generated
	 */
	int ABSTRACT_VERTEX = 1;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX__CONFIG_INPUT_PORTS = PARAMETERIZABLE__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX__NAME = PARAMETERIZABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Abstract Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX_FEATURE_COUNT = PARAMETERIZABLE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl <em>Abstract Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractActor()
	 * @generated
	 */
	int ABSTRACT_ACTOR = 2;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__CONFIG_INPUT_PORTS = ABSTRACT_VERTEX__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__INPUT_PORTS = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__OUTPUT_PORTS = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS = ABSTRACT_VERTEX_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Abstract Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl <em>Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.GraphImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getGraph()
	 * @generated
	 */
	int GRAPH = 3;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

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
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__CONFIG_OUTPUT_PORTS = ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS;

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
	 * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__DEPENDENCIES = ABSTRACT_ACTOR_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl <em>Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ActorImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getActor()
	 * @generated
	 */
	int ACTOR = 4;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

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
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__CONFIG_OUTPUT_PORTS = ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Refinement</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__REFINEMENT = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Configuration Actor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__CONFIGURATION_ACTOR = ABSTRACT_ACTOR_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 2;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PortImpl <em>Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 5;

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
	int INPUT_PORT = 6;

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
	int OUTPUT_PORT = 7;

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
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT__EXPRESSION = PORT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OUTPUT_PORT_FEATURE_COUNT = PORT_FEATURE_COUNT + 2;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl <em>Config Input Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigInputPort()
	 * @generated
	 */
	int CONFIG_INPUT_PORT = 8;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_PORT__NAME = PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_PORT__KIND = PORT__KIND;

	/**
	 * The feature id for the '<em><b>Incoming Dependency</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_PORT__INCOMING_DEPENDENCY = PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Config Input Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_PORT_FEATURE_COUNT = PORT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl <em>Config Output Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputPort()
	 * @generated
	 */
	int CONFIG_OUTPUT_PORT = 9;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__NAME = OUTPUT_PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__KIND = OUTPUT_PORT__KIND;

	/**
	 * The feature id for the '<em><b>Outgoing Fifo</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__OUTGOING_FIFO = OUTPUT_PORT__OUTGOING_FIFO;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__EXPRESSION = OUTPUT_PORT__EXPRESSION;

	/**
	 * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES = OUTPUT_PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Config Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT_FEATURE_COUNT = OUTPUT_PORT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl <em>Fifo</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.FifoImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFifo()
	 * @generated
	 */
	int FIFO = 10;

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
	 * The feature id for the '<em><b>Delay</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIFO__DELAY = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIFO__ID = 3;

	/**
	 * The number of structural features of the '<em>Fifo</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FIFO_FEATURE_COUNT = 4;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl <em>Interface Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInterfaceActor()
	 * @generated
	 */
	int INTERFACE_ACTOR = 11;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

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
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__CONFIG_OUTPUT_PORTS = ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS;

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
	int SOURCE_INTERFACE = 12;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

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
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__CONFIG_OUTPUT_PORTS = INTERFACE_ACTOR__CONFIG_OUTPUT_PORTS;

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
	int SINK_INTERFACE = 13;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

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
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SINK_INTERFACE__CONFIG_OUTPUT_PORTS = INTERFACE_ACTOR__CONFIG_OUTPUT_PORTS;

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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl <em>Config Output Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputInterface()
	 * @generated
	 */
	int CONFIG_OUTPUT_INTERFACE = 14;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__INPUT_PORTS = INTERFACE_ACTOR__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__OUTPUT_PORTS = INTERFACE_ACTOR__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__CONFIG_OUTPUT_PORTS = INTERFACE_ACTOR__CONFIG_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__GRAPH_PORT = INTERFACE_ACTOR__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__KIND = INTERFACE_ACTOR__KIND;

	/**
	 * The number of structural features of the '<em>Config Output Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl <em>Refinement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRefinement()
	 * @generated
	 */
	int REFINEMENT = 15;

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
	int PARAMETER = 16;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__CONFIG_INPUT_PORTS = ABSTRACT_VERTEX__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__OUTGOING_DEPENDENCIES = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Locally Static</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__LOCALLY_STATIC = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Configuration Interface</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__CONFIGURATION_INTERFACE = ABSTRACT_VERTEX_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__GRAPH_PORT = ABSTRACT_VERTEX_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER__EXPRESSION = ABSTRACT_VERTEX_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PARAMETER_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 5;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl <em>Dependency</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDependency()
	 * @generated
	 */
	int DEPENDENCY = 17;

	/**
	 * The feature id for the '<em><b>Setter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY__SETTER = 0;

	/**
	 * The feature id for the '<em><b>Getter</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY__GETTER = 1;

	/**
	 * The number of structural features of the '<em>Dependency</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEPENDENCY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.ISetter <em>ISetter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.ISetter
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getISetter()
	 * @generated
	 */
	int ISETTER = 18;

	/**
	 * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ISETTER__OUTGOING_DEPENDENCIES = 0;

	/**
	 * The number of structural features of the '<em>ISetter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ISETTER_FEATURE_COUNT = 1;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DelayImpl <em>Delay</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DelayImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDelay()
	 * @generated
	 */
	int DELAY = 19;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELAY__CONFIG_INPUT_PORTS = PARAMETERIZABLE__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELAY__EXPRESSION = PARAMETERIZABLE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Delay</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELAY_FEATURE_COUNT = PARAMETERIZABLE_FEATURE_COUNT + 1;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl <em>Expression</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExpression()
	 * @generated
	 */
	int EXPRESSION = 20;

	/**
	 * The feature id for the '<em><b>Expression String</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION__EXPRESSION_STRING = 0;

	/**
	 * The number of structural features of the '<em>Expression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_FEATURE_COUNT = 1;


	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Parameterizable <em>Parameterizable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Parameterizable</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameterizable
	 * @generated
	 */
	EClass getParameterizable();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Parameterizable#getConfigInputPorts <em>Config Input Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Config Input Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameterizable#getConfigInputPorts()
	 * @see #getParameterizable()
	 * @generated
	 */
	EReference getParameterizable_ConfigInputPorts();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex <em>Abstract Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Vertex</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractVertex
	 * @generated
	 */
	EClass getAbstractVertex();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractVertex#getName()
	 * @see #getAbstractVertex()
	 * @generated
	 */
	EAttribute getAbstractVertex_Name();

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
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getConfigOutputPorts <em>Config Output Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Config Output Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getConfigOutputPorts()
	 * @see #getAbstractActor()
	 * @generated
	 */
	EReference getAbstractActor_ConfigOutputPorts();

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
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Graph#getDependencies <em>Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Dependencies</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Graph#getDependencies()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Dependencies();

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
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Actor#isConfigurationActor <em>Configuration Actor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Configuration Actor</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Actor#isConfigurationActor()
	 * @see #getActor()
	 * @generated
	 */
	EAttribute getActor_ConfigurationActor();

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
	 * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.OutputPort#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.OutputPort#getExpression()
	 * @see #getOutputPort()
	 * @generated
	 */
	EReference getOutputPort_Expression();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort <em>Config Input Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Config Input Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort
	 * @generated
	 */
	EClass getConfigInputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency <em>Incoming Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Incoming Dependency</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getIncomingDependency()
	 * @see #getConfigInputPort()
	 * @generated
	 */
	EReference getConfigInputPort_IncomingDependency();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ConfigOutputPort <em>Config Output Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Config Output Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigOutputPort
	 * @generated
	 */
	EClass getConfigOutputPort();

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
	 * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getDelay <em>Delay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Delay</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Fifo#getDelay()
	 * @see #getFifo()
	 * @generated
	 */
	EReference getFifo_Delay();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Fifo#getId()
	 * @see #getFifo()
	 * @generated
	 */
	EAttribute getFifo_Id();

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
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface <em>Config Output Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Config Output Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface
	 * @generated
	 */
	EClass getConfigOutputInterface();

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
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Parameter#getGraphPort <em>Graph Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Graph Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameter#getGraphPort()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_GraphPort();

	/**
	 * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.Parameter#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Parameter#getExpression()
	 * @see #getParameter()
	 * @generated
	 */
	EReference getParameter_Expression();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Dependency <em>Dependency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Dependency</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Dependency
	 * @generated
	 */
	EClass getDependency();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getSetter <em>Setter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Setter</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Dependency#getSetter()
	 * @see #getDependency()
	 * @generated
	 */
	EReference getDependency_Setter();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Dependency#getGetter <em>Getter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Getter</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Dependency#getGetter()
	 * @see #getDependency()
	 * @generated
	 */
	EReference getDependency_Getter();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ISetter <em>ISetter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>ISetter</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ISetter
	 * @generated
	 */
	EClass getISetter();

	/**
	 * Returns the meta object for the reference list '{@link org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies <em>Outgoing Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Outgoing Dependencies</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ISetter#getOutgoingDependencies()
	 * @see #getISetter()
	 * @generated
	 */
	EReference getISetter_OutgoingDependencies();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Delay <em>Delay</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Delay</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Delay
	 * @generated
	 */
	EClass getDelay();

	/**
	 * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.Delay#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Delay#getExpression()
	 * @see #getDelay()
	 * @generated
	 */
	EReference getDelay_Expression();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Expression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Expression</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Expression
	 * @generated
	 */
	EClass getExpression();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Expression#getExpressionString <em>Expression String</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expression String</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Expression#getExpressionString()
	 * @see #getExpression()
	 * @generated
	 */
	EAttribute getExpression_ExpressionString();

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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterizableImpl <em>Parameterizable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterizableImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameterizable()
		 * @generated
		 */
		EClass PARAMETERIZABLE = eINSTANCE.getParameterizable();

		/**
		 * The meta object literal for the '<em><b>Config Input Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETERIZABLE__CONFIG_INPUT_PORTS = eINSTANCE.getParameterizable_ConfigInputPorts();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl <em>Abstract Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractVertex()
		 * @generated
		 */
		EClass ABSTRACT_VERTEX = eINSTANCE.getAbstractVertex();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSTRACT_VERTEX__NAME = eINSTANCE.getAbstractVertex_Name();

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
		 * The meta object literal for the '<em><b>Config Output Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS = eINSTANCE.getAbstractActor_ConfigOutputPorts();

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
		 * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference GRAPH__DEPENDENCIES = eINSTANCE.getGraph_Dependencies();

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
		 * The meta object literal for the '<em><b>Configuration Actor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTOR__CONFIGURATION_ACTOR = eINSTANCE.getActor_ConfigurationActor();

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
		 * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference OUTPUT_PORT__EXPRESSION = eINSTANCE.getOutputPort_Expression();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl <em>Config Input Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigInputPort()
		 * @generated
		 */
		EClass CONFIG_INPUT_PORT = eINSTANCE.getConfigInputPort();

		/**
		 * The meta object literal for the '<em><b>Incoming Dependency</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CONFIG_INPUT_PORT__INCOMING_DEPENDENCY = eINSTANCE.getConfigInputPort_IncomingDependency();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl <em>Config Output Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputPort()
		 * @generated
		 */
		EClass CONFIG_OUTPUT_PORT = eINSTANCE.getConfigOutputPort();

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
		 * The meta object literal for the '<em><b>Delay</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FIFO__DELAY = eINSTANCE.getFifo_Delay();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FIFO__ID = eINSTANCE.getFifo_Id();

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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl <em>Config Output Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputInterface()
		 * @generated
		 */
		EClass CONFIG_OUTPUT_INTERFACE = eINSTANCE.getConfigOutputInterface();

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

		/**
		 * The meta object literal for the '<em><b>Graph Port</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__GRAPH_PORT = eINSTANCE.getParameter_GraphPort();

		/**
		 * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PARAMETER__EXPRESSION = eINSTANCE.getParameter_Expression();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl <em>Dependency</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDependency()
		 * @generated
		 */
		EClass DEPENDENCY = eINSTANCE.getDependency();

		/**
		 * The meta object literal for the '<em><b>Setter</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPENDENCY__SETTER = eINSTANCE.getDependency_Setter();

		/**
		 * The meta object literal for the '<em><b>Getter</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEPENDENCY__GETTER = eINSTANCE.getDependency_Getter();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.ISetter <em>ISetter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.ISetter
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getISetter()
		 * @generated
		 */
		EClass ISETTER = eINSTANCE.getISetter();

		/**
		 * The meta object literal for the '<em><b>Outgoing Dependencies</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ISETTER__OUTGOING_DEPENDENCIES = eINSTANCE.getISetter_OutgoingDependencies();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DelayImpl <em>Delay</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DelayImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDelay()
		 * @generated
		 */
		EClass DELAY = eINSTANCE.getDelay();

		/**
		 * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DELAY__EXPRESSION = eINSTANCE.getDelay_Expression();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl <em>Expression</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExpression()
		 * @generated
		 */
		EClass EXPRESSION = eINSTANCE.getExpression();

		/**
		 * The meta object literal for the '<em><b>Expression String</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXPRESSION__EXPRESSION_STRING = eINSTANCE.getExpression_ExpressionString();

	}

} //PiMMPackage
