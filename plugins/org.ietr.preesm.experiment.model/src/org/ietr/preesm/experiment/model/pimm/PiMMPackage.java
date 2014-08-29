/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot
 * 
 * [mpelcat,jnezan,kdesnos,jheulot]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 ******************************************************************************/
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
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
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__DATA_INPUT_PORTS = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSTRACT_ACTOR__DATA_OUTPUT_PORTS = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl <em>Pi Graph</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPiGraph()
	 * @generated
	 */
	int PI_GRAPH = 3;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__NAME = ABSTRACT_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__DATA_INPUT_PORTS = ABSTRACT_ACTOR__DATA_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__DATA_OUTPUT_PORTS = ABSTRACT_ACTOR__DATA_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__CONFIG_OUTPUT_PORTS = ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__VERTICES = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Fifos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__FIFOS = ABSTRACT_ACTOR_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__PARAMETERS = ABSTRACT_ACTOR_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH__DEPENDENCIES = ABSTRACT_ACTOR_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Pi Graph</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PI_GRAPH_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 4;

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
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__DATA_INPUT_PORTS = ABSTRACT_ACTOR__DATA_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__DATA_OUTPUT_PORTS = ABSTRACT_ACTOR__DATA_OUTPUT_PORTS;

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
	 * The feature id for the '<em><b>Memory Script Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR__MEMORY_SCRIPT_PATH = ABSTRACT_ACTOR_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Actor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 3;


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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl <em>Data Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataPort()
	 * @generated
	 */
	int DATA_PORT = 25;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PORT__NAME = PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PORT__KIND = PORT__KIND;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PORT__EXPRESSION = PORT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Annotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PORT__ANNOTATION = PORT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Data Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_PORT_FEATURE_COUNT = PORT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputPortImpl <em>Data Input Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DataInputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataInputPort()
	 * @generated
	 */
	int DATA_INPUT_PORT = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_PORT__NAME = DATA_PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_PORT__KIND = DATA_PORT__KIND;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_PORT__EXPRESSION = DATA_PORT__EXPRESSION;

	/**
	 * The feature id for the '<em><b>Annotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_PORT__ANNOTATION = DATA_PORT__ANNOTATION;

	/**
	 * The feature id for the '<em><b>Incoming Fifo</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_PORT__INCOMING_FIFO = DATA_PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Data Input Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_PORT_FEATURE_COUNT = DATA_PORT_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataOutputPortImpl <em>Data Output Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DataOutputPortImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataOutputPort()
	 * @generated
	 */
	int DATA_OUTPUT_PORT = 7;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_PORT__NAME = DATA_PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_PORT__KIND = DATA_PORT__KIND;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_PORT__EXPRESSION = DATA_PORT__EXPRESSION;

	/**
	 * The feature id for the '<em><b>Annotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_PORT__ANNOTATION = DATA_PORT__ANNOTATION;

	/**
	 * The feature id for the '<em><b>Outgoing Fifo</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_PORT__OUTGOING_FIFO = DATA_PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Data Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_PORT_FEATURE_COUNT = DATA_PORT_FEATURE_COUNT + 1;

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
	int CONFIG_OUTPUT_PORT__NAME = DATA_OUTPUT_PORT__NAME;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__KIND = DATA_OUTPUT_PORT__KIND;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__EXPRESSION = DATA_OUTPUT_PORT__EXPRESSION;

	/**
	 * The feature id for the '<em><b>Annotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__ANNOTATION = DATA_OUTPUT_PORT__ANNOTATION;

	/**
	 * The feature id for the '<em><b>Outgoing Fifo</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__OUTGOING_FIFO = DATA_OUTPUT_PORT__OUTGOING_FIFO;

	/**
	 * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT__OUTGOING_DEPENDENCIES = DATA_OUTPUT_PORT_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Config Output Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_PORT_FEATURE_COUNT = DATA_OUTPUT_PORT_FEATURE_COUNT + 1;

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
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__DATA_INPUT_PORTS = ABSTRACT_ACTOR__DATA_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERFACE_ACTOR__DATA_OUTPUT_PORTS = ABSTRACT_ACTOR__DATA_OUTPUT_PORTS;

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
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputInterfaceImpl <em>Data Input Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DataInputInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataInputInterface()
	 * @generated
	 */
	int DATA_INPUT_INTERFACE = 12;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__DATA_INPUT_PORTS = INTERFACE_ACTOR__DATA_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__DATA_OUTPUT_PORTS = INTERFACE_ACTOR__DATA_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__CONFIG_OUTPUT_PORTS = INTERFACE_ACTOR__CONFIG_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__GRAPH_PORT = INTERFACE_ACTOR__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE__KIND = INTERFACE_ACTOR__KIND;

	/**
	 * The number of structural features of the '<em>Data Input Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_INPUT_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataOutputInterfaceImpl <em>Data Output Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DataOutputInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataOutputInterface()
	 * @generated
	 */
	int DATA_OUTPUT_INTERFACE = 13;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

	/**
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__DATA_INPUT_PORTS = INTERFACE_ACTOR__DATA_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__DATA_OUTPUT_PORTS = INTERFACE_ACTOR__DATA_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__CONFIG_OUTPUT_PORTS = INTERFACE_ACTOR__CONFIG_OUTPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__GRAPH_PORT = INTERFACE_ACTOR__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Kind</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE__KIND = INTERFACE_ACTOR__KIND;

	/**
	 * The number of structural features of the '<em>Data Output Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DATA_OUTPUT_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl <em>Config Output Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputInterface()
	 * @generated
	 */
	int CONFIG_OUTPUT_INTERFACE = 15;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl <em>Refinement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRefinement()
	 * @generated
	 */
	int REFINEMENT = 16;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl <em>Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameter()
	 * @generated
	 */
	int PARAMETER = 17;

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
	int DEPENDENCY = 18;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.ISetter <em>ISetter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.ISetter
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getISetter()
	 * @generated
	 */
	int ISETTER = 19;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DelayImpl <em>Delay</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.DelayImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDelay()
	 * @generated
	 */
	int DELAY = 20;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl <em>Expression</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExpression()
	 * @generated
	 */
	int EXPRESSION = 21;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl <em>Config Input Interface</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigInputInterface()
	 * @generated
	 */
	int CONFIG_INPUT_INTERFACE = 14;

	/**
	 * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__CONFIG_INPUT_PORTS = PARAMETER__CONFIG_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__NAME = PARAMETER__NAME;

	/**
	 * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__OUTGOING_DEPENDENCIES = PARAMETER__OUTGOING_DEPENDENCIES;

	/**
	 * The feature id for the '<em><b>Locally Static</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__LOCALLY_STATIC = PARAMETER__LOCALLY_STATIC;

	/**
	 * The feature id for the '<em><b>Configuration Interface</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__CONFIGURATION_INTERFACE = PARAMETER__CONFIGURATION_INTERFACE;

	/**
	 * The feature id for the '<em><b>Graph Port</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__GRAPH_PORT = PARAMETER__GRAPH_PORT;

	/**
	 * The feature id for the '<em><b>Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE__EXPRESSION = PARAMETER__EXPRESSION;

	/**
	 * The number of structural features of the '<em>Config Input Interface</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_INPUT_INTERFACE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 0;

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
	 * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__DATA_INPUT_PORTS = INTERFACE_ACTOR__DATA_INPUT_PORTS;

	/**
	 * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONFIG_OUTPUT_INTERFACE__DATA_OUTPUT_PORTS = INTERFACE_ACTOR__DATA_OUTPUT_PORTS;

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
	 * The feature id for the '<em><b>String</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION__STRING = 0;

	/**
	 * The number of structural features of the '<em>Expression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPRESSION_FEATURE_COUNT = 1;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl <em>HRefinement</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getHRefinement()
	 * @generated
	 */
	int HREFINEMENT = 22;

	/**
	 * The feature id for the '<em><b>File Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HREFINEMENT__FILE_NAME = REFINEMENT__FILE_NAME;

	/**
	 * The feature id for the '<em><b>Loop Prototype</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HREFINEMENT__LOOP_PROTOTYPE = REFINEMENT_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Init Prototype</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HREFINEMENT__INIT_PROTOTYPE = REFINEMENT_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>HRefinement</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int HREFINEMENT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl <em>Function Prototype</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFunctionPrototype()
	 * @generated
	 */
	int FUNCTION_PROTOTYPE = 23;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PROTOTYPE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Parameters</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PROTOTYPE__PARAMETERS = 1;

	/**
	 * The number of structural features of the '<em>Function Prototype</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PROTOTYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl <em>Function Parameter</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFunctionParameter()
	 * @generated
	 */
	int FUNCTION_PARAMETER = 24;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PARAMETER__NAME = 0;

	/**
	 * The feature id for the '<em><b>Direction</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PARAMETER__DIRECTION = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PARAMETER__TYPE = 2;

	/**
	 * The feature id for the '<em><b>Is Configuration Parameter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER = 3;

	/**
	 * The number of structural features of the '<em>Function Parameter</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FUNCTION_PARAMETER_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.Direction <em>Direction</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.Direction
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDirection()
	 * @generated
	 */
	int DIRECTION = 26;


	/**
	 * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation <em>Port Memory Annotation</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
	 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPortMemoryAnnotation()
	 * @generated
	 */
	int PORT_MEMORY_ANNOTATION = 27;


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
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataInputPorts <em>Data Input Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data Input Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataInputPorts()
	 * @see #getAbstractActor()
	 * @generated
	 */
	EReference getAbstractActor_DataInputPorts();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataOutputPorts <em>Data Output Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Data Output Ports</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getDataOutputPorts()
	 * @see #getAbstractActor()
	 * @generated
	 */
	EReference getAbstractActor_DataOutputPorts();

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
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.PiGraph <em>Pi Graph</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pi Graph</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.PiGraph
	 * @generated
	 */
	EClass getPiGraph();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getVertices <em>Vertices</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Vertices</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getVertices()
	 * @see #getPiGraph()
	 * @generated
	 */
	EReference getPiGraph_Vertices();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getFifos <em>Fifos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fifos</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getFifos()
	 * @see #getPiGraph()
	 * @generated
	 */
	EReference getPiGraph_Fifos();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Parameters</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getParameters()
	 * @see #getPiGraph()
	 * @generated
	 */
	EReference getPiGraph_Parameters();

	/**
	 * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getDependencies <em>Dependencies</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Dependencies</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getDependencies()
	 * @see #getPiGraph()
	 * @generated
	 */
	EReference getPiGraph_Dependencies();

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
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath <em>Memory Script Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Memory Script Path</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Actor#getMemoryScriptPath()
	 * @see #getActor()
	 * @generated
	 */
	EAttribute getActor_MemoryScriptPath();

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
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.DataInputPort <em>Data Input Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Input Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataInputPort
	 * @generated
	 */
	EClass getDataInputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo <em>Incoming Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Incoming Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataInputPort#getIncomingFifo()
	 * @see #getDataInputPort()
	 * @generated
	 */
	EReference getDataInputPort_IncomingFifo();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.DataOutputPort <em>Data Output Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Output Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataOutputPort
	 * @generated
	 */
	EClass getDataOutputPort();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo <em>Outgoing Fifo</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Outgoing Fifo</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataOutputPort#getOutgoingFifo()
	 * @see #getDataOutputPort()
	 * @generated
	 */
	EReference getDataOutputPort_OutgoingFifo();

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
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.DataInputInterface <em>Data Input Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Input Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataInputInterface
	 * @generated
	 */
	EClass getDataInputInterface();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.DataOutputInterface <em>Data Output Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Output Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataOutputInterface
	 * @generated
	 */
	EClass getDataOutputInterface();

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
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Expression#getString <em>String</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>String</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Expression#getString()
	 * @see #getExpression()
	 * @generated
	 */
	EAttribute getExpression_String();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.HRefinement <em>HRefinement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>HRefinement</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.HRefinement
	 * @generated
	 */
	EClass getHRefinement();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.HRefinement#getLoopPrototype <em>Loop Prototype</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Loop Prototype</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.HRefinement#getLoopPrototype()
	 * @see #getHRefinement()
	 * @generated
	 */
	EReference getHRefinement_LoopPrototype();

	/**
	 * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.HRefinement#getInitPrototype <em>Init Prototype</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Init Prototype</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.HRefinement#getInitPrototype()
	 * @see #getHRefinement()
	 * @generated
	 */
	EReference getHRefinement_InitPrototype();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.FunctionPrototype <em>Function Prototype</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Function Prototype</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionPrototype
	 * @generated
	 */
	EClass getFunctionPrototype();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.FunctionPrototype#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionPrototype#getName()
	 * @see #getFunctionPrototype()
	 * @generated
	 */
	EAttribute getFunctionPrototype_Name();

	/**
	 * Returns the meta object for the reference list '{@link org.ietr.preesm.experiment.model.pimm.FunctionPrototype#getParameters <em>Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Parameters</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionPrototype#getParameters()
	 * @see #getFunctionPrototype()
	 * @generated
	 */
	EReference getFunctionPrototype_Parameters();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter <em>Function Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Function Parameter</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionParameter
	 * @generated
	 */
	EClass getFunctionParameter();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionParameter#getName()
	 * @see #getFunctionParameter()
	 * @generated
	 */
	EAttribute getFunctionParameter_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getDirection <em>Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Direction</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionParameter#getDirection()
	 * @see #getFunctionParameter()
	 * @generated
	 */
	EAttribute getFunctionParameter_Direction();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionParameter#getType()
	 * @see #getFunctionParameter()
	 * @generated
	 */
	EAttribute getFunctionParameter_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.FunctionParameter#isIsConfigurationParameter <em>Is Configuration Parameter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Is Configuration Parameter</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.FunctionParameter#isIsConfigurationParameter()
	 * @see #getFunctionParameter()
	 * @generated
	 */
	EAttribute getFunctionParameter_IsConfigurationParameter();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.DataPort <em>Data Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Data Port</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataPort
	 * @generated
	 */
	EClass getDataPort();

	/**
	 * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getExpression <em>Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Expression</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataPort#getExpression()
	 * @see #getDataPort()
	 * @generated
	 */
	EReference getDataPort_Expression();

	/**
	 * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation <em>Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Annotation</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.DataPort#getAnnotation()
	 * @see #getDataPort()
	 * @generated
	 */
	EAttribute getDataPort_Annotation();

	/**
	 * Returns the meta object for enum '{@link org.ietr.preesm.experiment.model.pimm.Direction <em>Direction</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Direction</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.Direction
	 * @generated
	 */
	EEnum getDirection();

	/**
	 * Returns the meta object for enum '{@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation <em>Port Memory Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Port Memory Annotation</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
	 * @generated
	 */
	EEnum getPortMemoryAnnotation();

	/**
	 * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputInterface <em>Config Input Interface</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Config Input Interface</em>'.
	 * @see org.ietr.preesm.experiment.model.pimm.ConfigInputInterface
	 * @generated
	 */
	EClass getConfigInputInterface();

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
		 * The meta object literal for the '<em><b>Data Input Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_ACTOR__DATA_INPUT_PORTS = eINSTANCE.getAbstractActor_DataInputPorts();

		/**
		 * The meta object literal for the '<em><b>Data Output Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_ACTOR__DATA_OUTPUT_PORTS = eINSTANCE.getAbstractActor_DataOutputPorts();

		/**
		 * The meta object literal for the '<em><b>Config Output Ports</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS = eINSTANCE.getAbstractActor_ConfigOutputPorts();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl <em>Pi Graph</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPiGraph()
		 * @generated
		 */
		EClass PI_GRAPH = eINSTANCE.getPiGraph();

		/**
		 * The meta object literal for the '<em><b>Vertices</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PI_GRAPH__VERTICES = eINSTANCE.getPiGraph_Vertices();

		/**
		 * The meta object literal for the '<em><b>Fifos</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PI_GRAPH__FIFOS = eINSTANCE.getPiGraph_Fifos();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PI_GRAPH__PARAMETERS = eINSTANCE.getPiGraph_Parameters();

		/**
		 * The meta object literal for the '<em><b>Dependencies</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PI_GRAPH__DEPENDENCIES = eINSTANCE.getPiGraph_Dependencies();

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
		 * The meta object literal for the '<em><b>Memory Script Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACTOR__MEMORY_SCRIPT_PATH = eINSTANCE.getActor_MemoryScriptPath();

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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputPortImpl <em>Data Input Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DataInputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataInputPort()
		 * @generated
		 */
		EClass DATA_INPUT_PORT = eINSTANCE.getDataInputPort();

		/**
		 * The meta object literal for the '<em><b>Incoming Fifo</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_INPUT_PORT__INCOMING_FIFO = eINSTANCE.getDataInputPort_IncomingFifo();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataOutputPortImpl <em>Data Output Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DataOutputPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataOutputPort()
		 * @generated
		 */
		EClass DATA_OUTPUT_PORT = eINSTANCE.getDataOutputPort();

		/**
		 * The meta object literal for the '<em><b>Outgoing Fifo</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_OUTPUT_PORT__OUTGOING_FIFO = eINSTANCE.getDataOutputPort_OutgoingFifo();

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
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputInterfaceImpl <em>Data Input Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DataInputInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataInputInterface()
		 * @generated
		 */
		EClass DATA_INPUT_INTERFACE = eINSTANCE.getDataInputInterface();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataOutputInterfaceImpl <em>Data Output Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DataOutputInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataOutputInterface()
		 * @generated
		 */
		EClass DATA_OUTPUT_INTERFACE = eINSTANCE.getDataOutputInterface();

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
		 * The meta object literal for the '<em><b>String</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXPRESSION__STRING = eINSTANCE.getExpression_String();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl <em>HRefinement</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getHRefinement()
		 * @generated
		 */
		EClass HREFINEMENT = eINSTANCE.getHRefinement();

		/**
		 * The meta object literal for the '<em><b>Loop Prototype</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HREFINEMENT__LOOP_PROTOTYPE = eINSTANCE.getHRefinement_LoopPrototype();

		/**
		 * The meta object literal for the '<em><b>Init Prototype</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference HREFINEMENT__INIT_PROTOTYPE = eINSTANCE.getHRefinement_InitPrototype();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl <em>Function Prototype</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFunctionPrototype()
		 * @generated
		 */
		EClass FUNCTION_PROTOTYPE = eINSTANCE.getFunctionPrototype();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_PROTOTYPE__NAME = eINSTANCE.getFunctionPrototype_Name();

		/**
		 * The meta object literal for the '<em><b>Parameters</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FUNCTION_PROTOTYPE__PARAMETERS = eINSTANCE.getFunctionPrototype_Parameters();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl <em>Function Parameter</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.FunctionParameterImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFunctionParameter()
		 * @generated
		 */
		EClass FUNCTION_PARAMETER = eINSTANCE.getFunctionParameter();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_PARAMETER__NAME = eINSTANCE.getFunctionParameter_Name();

		/**
		 * The meta object literal for the '<em><b>Direction</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_PARAMETER__DIRECTION = eINSTANCE.getFunctionParameter_Direction();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_PARAMETER__TYPE = eINSTANCE.getFunctionParameter_Type();

		/**
		 * The meta object literal for the '<em><b>Is Configuration Parameter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FUNCTION_PARAMETER__IS_CONFIGURATION_PARAMETER = eINSTANCE.getFunctionParameter_IsConfigurationParameter();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl <em>Data Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataPort()
		 * @generated
		 */
		EClass DATA_PORT = eINSTANCE.getDataPort();

		/**
		 * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DATA_PORT__EXPRESSION = eINSTANCE.getDataPort_Expression();

		/**
		 * The meta object literal for the '<em><b>Annotation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DATA_PORT__ANNOTATION = eINSTANCE.getDataPort_Annotation();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.Direction <em>Direction</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.Direction
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDirection()
		 * @generated
		 */
		EEnum DIRECTION = eINSTANCE.getDirection();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation <em>Port Memory Annotation</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPortMemoryAnnotation()
		 * @generated
		 */
		EEnum PORT_MEMORY_ANNOTATION = eINSTANCE.getPortMemoryAnnotation();

		/**
		 * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl <em>Config Input Interface</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl
		 * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigInputInterface()
		 * @generated
		 */
		EClass CONFIG_INPUT_INTERFACE = eINSTANCE.getConfigInputInterface();

	}

} //PiMMPackage
