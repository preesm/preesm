/**
 */
package org.ietr.preesm.experiment.model.pimemoc;

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
 * @see org.ietr.preesm.experiment.model.pimemoc.PIMeMoCFactory
 * @model kind="package"
 * @generated
 */
public interface PIMeMoCPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "pimemoc";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://org.ietr.preesm/experiment/model/pimemoc";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "org.ietr.preesm.experiment.pimemoc";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	PIMeMoCPackage eINSTANCE = org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl <em>Abstract Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getAbstractVertex()
	 * @generated
	 */
	int ABSTRACT_VERTEX = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX__NAME = 0;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX__INPUT_PORTS = 1;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX__OUTPUT_PORTS = 2;

	/**
	 * The number of structural features of the '<em>Abstract Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_VERTEX_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl <em>Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getGraph()
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
	int GRAPH__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__INPUT_PORTS = ABSTRACT_VERTEX__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__OUTPUT_PORTS = ABSTRACT_VERTEX__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__VERTICES = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Fifos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH__FIFOS = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GRAPH_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl <em>Actor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getActor()
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
	int ACTOR__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__INPUT_PORTS = ABSTRACT_VERTEX__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__OUTPUT_PORTS = ABSTRACT_VERTEX__OUTPUT_PORTS;

	/**
	 * The number of structural features of the '<em>Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 0;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.PortImpl <em>Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PortImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getPort()
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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.InputPortImpl <em>Input Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.InputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getInputPort()
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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.OutputPortImpl <em>Output Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.OutputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getOutputPort()
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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.FifoImpl <em>Fifo</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.FifoImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getFifo()
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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.InterfaceVertexImpl <em>Interface Vertex</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.InterfaceVertexImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getInterfaceVertex()
	 * @generated
	 */
	int INTERFACE_VERTEX = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_VERTEX__NAME = ABSTRACT_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_VERTEX__INPUT_PORTS = ABSTRACT_VERTEX__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_VERTEX__OUTPUT_PORTS = ABSTRACT_VERTEX__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_VERTEX__GRAPH_PORT = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_VERTEX__KIND = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Interface Vertex</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_VERTEX_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 2;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.SourceInterfaceImpl <em>Source Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.SourceInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getSourceInterface()
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
	int SOURCE_INTERFACE__NAME = INTERFACE_VERTEX__NAME;

	/**
	 * The feature id for the '<em><b>Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__INPUT_PORTS = INTERFACE_VERTEX__INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__OUTPUT_PORTS = INTERFACE_VERTEX__OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__GRAPH_PORT = INTERFACE_VERTEX__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE__KIND = INTERFACE_VERTEX__KIND;

	/**
	 * The number of structural features of the '<em>Source Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOURCE_INTERFACE_FEATURE_COUNT = INTERFACE_VERTEX_FEATURE_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex <em>Abstract Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Abstract Vertex</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.AbstractVertex
	 * @generated
	 */
	EClass getAbstractVertex();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getName()
	 * @see #getAbstractVertex()
	 * @generated
	 */
	EAttribute getAbstractVertex_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getInputPorts <em>Input Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Input Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getInputPorts()
	 * @see #getAbstractVertex()
	 * @generated
	 */
	EReference getAbstractVertex_InputPorts();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getOutputPorts <em>Output Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Output Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.AbstractVertex#getOutputPorts()
	 * @see #getAbstractVertex()
	 * @generated
	 */
	EReference getAbstractVertex_OutputPorts();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.Graph <em>Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Graph</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Graph
	 * @generated
	 */
	EClass getGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimemoc.Graph#getVertices <em>Vertices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vertices</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Graph#getVertices()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Vertices();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimemoc.Graph#getFifos <em>Fifos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fifos</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Graph#getFifos()
	 * @see #getGraph()
	 * @generated
	 */
	EReference getGraph_Fifos();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.Actor <em>Actor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Actor</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Actor
	 * @generated
	 */
	EClass getActor();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Port
	 * @generated
	 */
	EClass getPort();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimemoc.Port#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Port#getName()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimemoc.Port#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Port#getKind()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Kind();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.InputPort <em>Input Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Input Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.InputPort
	 * @generated
	 */
	EClass getInputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimemoc.InputPort#getIncomingFifo <em>Incoming Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Incoming Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.InputPort#getIncomingFifo()
	 * @see #getInputPort()
	 * @generated
	 */
	EReference getInputPort_IncomingFifo();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.OutputPort <em>Output Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Output Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.OutputPort
	 * @generated
	 */
	EClass getOutputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimemoc.OutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Outgoing Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.OutputPort#getOutgoingFifo()
	 * @see #getOutputPort()
	 * @generated
	 */
	EReference getOutputPort_OutgoingFifo();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.Fifo <em>Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Fifo
	 * @generated
	 */
	EClass getFifo();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimemoc.Fifo#getSourcePort <em>Source Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Source Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Fifo#getSourcePort()
	 * @see #getFifo()
	 * @generated
	 */
	EReference getFifo_SourcePort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimemoc.Fifo#getTargetPort <em>Target Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.Fifo#getTargetPort()
	 * @see #getFifo()
	 * @generated
	 */
	EReference getFifo_TargetPort();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex <em>Interface Vertex</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interface Vertex</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex
	 * @generated
	 */
	EClass getInterfaceVertex();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex#getGraphPort <em>Graph Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Graph Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex#getGraphPort()
	 * @see #getInterfaceVertex()
	 * @generated
	 */
	EReference getInterfaceVertex_GraphPort();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex#getKind <em>Kind</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Kind</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.InterfaceVertex#getKind()
	 * @see #getInterfaceVertex()
	 * @generated
	 */
	EAttribute getInterfaceVertex_Kind();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimemoc.SourceInterface <em>Source Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Source Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimemoc.SourceInterface
	 * @generated
	 */
	EClass getSourceInterface();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	PIMeMoCFactory getPIMeMoCFactory();

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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl <em>Abstract Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.AbstractVertexImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getAbstractVertex()
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
		 * The meta object literal for the '<em><b>Input Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_VERTEX__INPUT_PORTS = eINSTANCE.getAbstractVertex_InputPorts();

		/**
		 * The meta object literal for the '<em><b>Output Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_VERTEX__OUTPUT_PORTS = eINSTANCE.getAbstractVertex_OutputPorts();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl <em>Graph</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.GraphImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getGraph()
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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl <em>Actor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.ActorImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getActor()
		 * @generated
		 */
		EClass ACTOR = eINSTANCE.getActor();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.PortImpl <em>Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PortImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getPort()
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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.InputPortImpl <em>Input Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.InputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getInputPort()
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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.OutputPortImpl <em>Output Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.OutputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getOutputPort()
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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.FifoImpl <em>Fifo</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.FifoImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getFifo()
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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.InterfaceVertexImpl <em>Interface Vertex</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.InterfaceVertexImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getInterfaceVertex()
		 * @generated
		 */
		EClass INTERFACE_VERTEX = eINSTANCE.getInterfaceVertex();

		/**
		 * The meta object literal for the '<em><b>Graph Port</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERFACE_VERTEX__GRAPH_PORT = eINSTANCE.getInterfaceVertex_GraphPort();

		/**
		 * The meta object literal for the '<em><b>Kind</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERFACE_VERTEX__KIND = eINSTANCE.getInterfaceVertex_Kind();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimemoc.impl.SourceInterfaceImpl <em>Source Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.SourceInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimemoc.impl.PIMeMoCPackageImpl#getSourceInterface()
		 * @generated
		 */
		EClass SOURCE_INTERFACE = eINSTANCE.getSourceInterface();

	}

} //PIMeMoCPackage
