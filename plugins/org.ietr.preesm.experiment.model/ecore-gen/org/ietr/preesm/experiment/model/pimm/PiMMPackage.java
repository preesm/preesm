/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 *        annotation="http://www.eclipse.org/emf/2002/GenModel modelName='PiMM' prefix='PiMM' modelDirectory='/org.ietr.preesm.experiment.model/ecore-gen' importerID='org.eclipse.emf.importer.ecore' loadInitialization='false' creationCommands='false' creationIcons='false' dataTypeConverters='false' operationReflection='false' basePackage='org.ietr.preesm.experiment.model'"
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
  String eNS_URI = "http://org.ietr.preesm/model/pimm";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "pimm";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PiMMPackage eINSTANCE = org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl.init();

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.VertexImpl <em>Vertex</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.VertexImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getVertex()
   * @generated
   */
  int VERTEX = 0;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERTEX__CONTAINING_GRAPH = 0;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERTEX__OUT_EDGES = 1;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERTEX__IN_EDGES = 2;

  /**
   * The number of structural features of the '<em>Vertex</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VERTEX_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl <em>Edge</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getEdge()
   * @generated
   */
  int EDGE = 1;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE__CONTAINING_GRAPH = 0;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE__SOURCE = 1;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE__TARGET = 2;

  /**
   * The number of structural features of the '<em>Edge</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EDGE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl <em>Graph</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.GraphImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getGraph()
   * @generated
   */
  int GRAPH = 2;

  /**
   * The feature id for the '<em><b>Vertices</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GRAPH__VERTICES = 0;

  /**
   * The feature id for the '<em><b>Edges</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GRAPH__EDGES = 1;

  /**
   * The number of structural features of the '<em>Graph</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GRAPH_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.Parameterizable <em>Parameterizable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.Parameterizable
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameterizable()
   * @generated
   */
  int PARAMETERIZABLE = 3;

  /**
   * The number of structural features of the '<em>Parameterizable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERIZABLE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ExpressionImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 4;

  /**
   * The feature id for the '<em><b>Holder</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__HOLDER = 0;

  /**
   * The feature id for the '<em><b>Expression String</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__EXPRESSION_STRING = 1;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder <em>Expression Holder</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.ExpressionHolder
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExpressionHolder()
   * @generated
   */
  int EXPRESSION_HOLDER = 5;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_HOLDER__EXPRESSION = PARAMETERIZABLE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Expression Holder</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_HOLDER_FEATURE_COUNT = PARAMETERIZABLE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex <em>Abstract Vertex</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.AbstractVertex
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractVertex()
   * @generated
   */
  int ABSTRACT_VERTEX = 6;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_VERTEX__CONTAINING_GRAPH = VERTEX__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_VERTEX__OUT_EDGES = VERTEX__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_VERTEX__IN_EDGES = VERTEX__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_VERTEX__NAME = VERTEX_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Abstract Vertex</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_VERTEX_FEATURE_COUNT = VERTEX_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl <em>Configurable</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigurable()
   * @generated
   */
  int CONFIGURABLE = 7;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE__CONTAINING_GRAPH = ABSTRACT_VERTEX__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE__OUT_EDGES = ABSTRACT_VERTEX__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE__IN_EDGES = ABSTRACT_VERTEX__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE__NAME = ABSTRACT_VERTEX__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE__CONFIG_INPUT_PORTS = ABSTRACT_VERTEX_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Configurable</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIGURABLE_FEATURE_COUNT = ABSTRACT_VERTEX_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl <em>Abstract Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getAbstractActor()
   * @generated
   */
  int ABSTRACT_ACTOR = 8;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__CONTAINING_GRAPH = CONFIGURABLE__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__OUT_EDGES = CONFIGURABLE__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__IN_EDGES = CONFIGURABLE__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__NAME = CONFIGURABLE__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__CONFIG_INPUT_PORTS = CONFIGURABLE__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__DATA_INPUT_PORTS = CONFIGURABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__DATA_OUTPUT_PORTS = CONFIGURABLE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS = CONFIGURABLE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Abstract Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_ACTOR_FEATURE_COUNT = CONFIGURABLE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl <em>Pi Graph</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPiGraph()
   * @generated
   */
  int PI_GRAPH = 9;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH__CONTAINING_GRAPH = ABSTRACT_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH__OUT_EDGES = ABSTRACT_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH__IN_EDGES = ABSTRACT_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH__NAME = ABSTRACT_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

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
   * The feature id for the '<em><b>Edges</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH__EDGES = ABSTRACT_ACTOR_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Pi Graph</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_GRAPH_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ExecutableActorImpl <em>Executable Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ExecutableActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExecutableActor()
   * @generated
   */
  int EXECUTABLE_ACTOR = 10;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__CONTAINING_GRAPH = ABSTRACT_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__OUT_EDGES = ABSTRACT_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__IN_EDGES = ABSTRACT_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__NAME = ABSTRACT_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__DATA_INPUT_PORTS = ABSTRACT_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS = ABSTRACT_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS = ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The number of structural features of the '<em>Executable Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXECUTABLE_ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl <em>Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getActor()
   * @generated
   */
  int ACTOR = 11;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__CONTAINING_GRAPH = EXECUTABLE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__OUT_EDGES = EXECUTABLE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__IN_EDGES = EXECUTABLE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__NAME = EXECUTABLE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__CONFIG_INPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__DATA_INPUT_PORTS = EXECUTABLE_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__DATA_OUTPUT_PORTS = EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__CONFIG_OUTPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__REFINEMENT = EXECUTABLE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Memory Script Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR__MEMORY_SCRIPT_PATH = EXECUTABLE_ACTOR_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTOR_FEATURE_COUNT = EXECUTABLE_ACTOR_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.BroadcastActorImpl <em>Broadcast Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.BroadcastActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getBroadcastActor()
   * @generated
   */
  int BROADCAST_ACTOR = 12;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__CONTAINING_GRAPH = EXECUTABLE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__OUT_EDGES = EXECUTABLE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__IN_EDGES = EXECUTABLE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__NAME = EXECUTABLE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__CONFIG_INPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__DATA_INPUT_PORTS = EXECUTABLE_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__DATA_OUTPUT_PORTS = EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR__CONFIG_OUTPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The number of structural features of the '<em>Broadcast Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BROADCAST_ACTOR_FEATURE_COUNT = EXECUTABLE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.JoinActorImpl <em>Join Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.JoinActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getJoinActor()
   * @generated
   */
  int JOIN_ACTOR = 13;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__CONTAINING_GRAPH = EXECUTABLE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__OUT_EDGES = EXECUTABLE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__IN_EDGES = EXECUTABLE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__NAME = EXECUTABLE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__CONFIG_INPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__DATA_INPUT_PORTS = EXECUTABLE_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__DATA_OUTPUT_PORTS = EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR__CONFIG_OUTPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The number of structural features of the '<em>Join Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int JOIN_ACTOR_FEATURE_COUNT = EXECUTABLE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ForkActorImpl <em>Fork Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ForkActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getForkActor()
   * @generated
   */
  int FORK_ACTOR = 14;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__CONTAINING_GRAPH = EXECUTABLE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__OUT_EDGES = EXECUTABLE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__IN_EDGES = EXECUTABLE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__NAME = EXECUTABLE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__CONFIG_INPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__DATA_INPUT_PORTS = EXECUTABLE_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__DATA_OUTPUT_PORTS = EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR__CONFIG_OUTPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The number of structural features of the '<em>Fork Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FORK_ACTOR_FEATURE_COUNT = EXECUTABLE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.RoundBufferActorImpl <em>Round Buffer Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.RoundBufferActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRoundBufferActor()
   * @generated
   */
  int ROUND_BUFFER_ACTOR = 15;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__CONTAINING_GRAPH = EXECUTABLE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__OUT_EDGES = EXECUTABLE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__IN_EDGES = EXECUTABLE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__NAME = EXECUTABLE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__CONFIG_INPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__DATA_INPUT_PORTS = EXECUTABLE_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__DATA_OUTPUT_PORTS = EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR__CONFIG_OUTPUT_PORTS = EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The number of structural features of the '<em>Round Buffer Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROUND_BUFFER_ACTOR_FEATURE_COUNT = EXECUTABLE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.NonExecutableActorImpl <em>Non Executable Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.NonExecutableActorImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getNonExecutableActor()
   * @generated
   */
  int NON_EXECUTABLE_ACTOR = 16;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__CONTAINING_GRAPH = ABSTRACT_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__OUT_EDGES = ABSTRACT_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__IN_EDGES = ABSTRACT_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__NAME = ABSTRACT_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__DATA_INPUT_PORTS = ABSTRACT_ACTOR__DATA_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Data Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__DATA_OUTPUT_PORTS = ABSTRACT_ACTOR__DATA_OUTPUT_PORTS;

  /**
   * The feature id for the '<em><b>Config Output Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR__CONFIG_OUTPUT_PORTS = ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS;

  /**
   * The number of structural features of the '<em>Non Executable Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NON_EXECUTABLE_ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.Port <em>Port</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.Port
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPort()
   * @generated
   */
  int PORT = 17;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PORT__NAME = 0;

  /**
   * The number of structural features of the '<em>Port</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl <em>Data Port</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataPort()
   * @generated
   */
  int DATA_PORT = 18;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_PORT__NAME = PORT__NAME;

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
  int DATA_INPUT_PORT = 19;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_INPUT_PORT__NAME = DATA_PORT__NAME;

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
  int DATA_OUTPUT_PORT = 20;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_PORT__NAME = DATA_PORT__NAME;

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
  int CONFIG_INPUT_PORT = 21;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_PORT__NAME = PORT__NAME;

  /**
   * The feature id for the '<em><b>Incoming Dependency</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_PORT__INCOMING_DEPENDENCY = PORT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Configurable</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_PORT__CONFIGURABLE = PORT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Config Input Port</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_PORT_FEATURE_COUNT = PORT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl <em>Config Output Port</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputPortImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputPort()
   * @generated
   */
  int CONFIG_OUTPUT_PORT = 22;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_PORT__NAME = DATA_OUTPUT_PORT__NAME;

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
  int FIFO = 23;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__CONTAINING_GRAPH = EDGE__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__SOURCE = EDGE__SOURCE;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__TARGET = EDGE__TARGET;

  /**
   * The feature id for the '<em><b>Source Port</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__SOURCE_PORT = EDGE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Target Port</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__TARGET_PORT = EDGE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Delay</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__DELAY = EDGE_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO__TYPE = EDGE_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Fifo</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FIFO_FEATURE_COUNT = EDGE_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor <em>Interface Actor</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.InterfaceActor
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInterfaceActor()
   * @generated
   */
  int INTERFACE_ACTOR = 24;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_ACTOR__CONTAINING_GRAPH = ABSTRACT_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_ACTOR__OUT_EDGES = ABSTRACT_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_ACTOR__IN_EDGES = ABSTRACT_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_ACTOR__NAME = ABSTRACT_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_ACTOR__CONFIG_INPUT_PORTS = ABSTRACT_ACTOR__CONFIG_INPUT_PORTS;

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
   * The number of structural features of the '<em>Interface Actor</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERFACE_ACTOR_FEATURE_COUNT = ABSTRACT_ACTOR_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputInterfaceImpl <em>Data Input Interface</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.DataInputInterfaceImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataInputInterface()
   * @generated
   */
  int DATA_INPUT_INTERFACE = 25;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_INPUT_INTERFACE__CONTAINING_GRAPH = INTERFACE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_INPUT_INTERFACE__OUT_EDGES = INTERFACE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_INPUT_INTERFACE__IN_EDGES = INTERFACE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_INPUT_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_INPUT_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

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
  int DATA_OUTPUT_INTERFACE = 26;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_INTERFACE__CONTAINING_GRAPH = INTERFACE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_INTERFACE__OUT_EDGES = INTERFACE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_INTERFACE__IN_EDGES = INTERFACE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

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
   * The number of structural features of the '<em>Data Output Interface</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_OUTPUT_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl <em>Parameter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameter()
   * @generated
   */
  int PARAMETER = 32;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__CONTAINING_GRAPH = VERTEX__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__OUT_EDGES = VERTEX__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__IN_EDGES = VERTEX__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__NAME = VERTEX_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__CONFIG_INPUT_PORTS = VERTEX_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__OUTGOING_DEPENDENCIES = VERTEX_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__EXPRESSION = VERTEX_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Parameter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER_FEATURE_COUNT = VERTEX_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl <em>Config Input Interface</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigInputInterface()
   * @generated
   */
  int CONFIG_INPUT_INTERFACE = 27;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__CONTAINING_GRAPH = PARAMETER__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__OUT_EDGES = PARAMETER__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__IN_EDGES = PARAMETER__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__NAME = PARAMETER__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__CONFIG_INPUT_PORTS = PARAMETER__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Outgoing Dependencies</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__OUTGOING_DEPENDENCIES = PARAMETER__OUTGOING_DEPENDENCIES;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__EXPRESSION = PARAMETER__EXPRESSION;

  /**
   * The feature id for the '<em><b>Graph Port</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE__GRAPH_PORT = PARAMETER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Config Input Interface</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_INPUT_INTERFACE_FEATURE_COUNT = PARAMETER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl <em>Config Output Interface</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigOutputInterfaceImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigOutputInterface()
   * @generated
   */
  int CONFIG_OUTPUT_INTERFACE = 28;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_INTERFACE__CONTAINING_GRAPH = INTERFACE_ACTOR__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_INTERFACE__OUT_EDGES = INTERFACE_ACTOR__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_INTERFACE__IN_EDGES = INTERFACE_ACTOR__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_INTERFACE__NAME = INTERFACE_ACTOR__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_INTERFACE__CONFIG_INPUT_PORTS = INTERFACE_ACTOR__CONFIG_INPUT_PORTS;

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
   * The number of structural features of the '<em>Config Output Interface</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONFIG_OUTPUT_INTERFACE_FEATURE_COUNT = INTERFACE_ACTOR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.Refinement <em>Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.Refinement
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRefinement()
   * @generated
   */
  int REFINEMENT = 29;

  /**
   * The feature id for the '<em><b>File Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENT__FILE_PATH = 0;

  /**
   * The number of structural features of the '<em>Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl <em>Pi SDF Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPiSDFRefinement()
   * @generated
   */
  int PI_SDF_REFINEMENT = 30;

  /**
   * The feature id for the '<em><b>File Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_SDF_REFINEMENT__FILE_PATH = REFINEMENT__FILE_PATH;

  /**
   * The number of structural features of the '<em>Pi SDF Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PI_SDF_REFINEMENT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl <em>CHeader Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getCHeaderRefinement()
   * @generated
   */
  int CHEADER_REFINEMENT = 31;

  /**
   * The feature id for the '<em><b>File Path</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHEADER_REFINEMENT__FILE_PATH = REFINEMENT__FILE_PATH;

  /**
   * The feature id for the '<em><b>Loop Prototype</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHEADER_REFINEMENT__LOOP_PROTOTYPE = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Init Prototype</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHEADER_REFINEMENT__INIT_PROTOTYPE = REFINEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>CHeader Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHEADER_REFINEMENT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl <em>Dependency</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDependency()
   * @generated
   */
  int DEPENDENCY = 33;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY__CONTAINING_GRAPH = EDGE__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Source</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY__SOURCE = EDGE__SOURCE;

  /**
   * The feature id for the '<em><b>Target</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY__TARGET = EDGE__TARGET;

  /**
   * The feature id for the '<em><b>Setter</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY__SETTER = EDGE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Getter</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY__GETTER = EDGE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Dependency</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEPENDENCY_FEATURE_COUNT = EDGE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.ISetter <em>ISetter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.ISetter
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getISetter()
   * @generated
   */
  int ISETTER = 34;

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
  int DELAY = 35;

  /**
   * The feature id for the '<em><b>Containing Graph</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__CONTAINING_GRAPH = CONFIGURABLE__CONTAINING_GRAPH;

  /**
   * The feature id for the '<em><b>Out Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__OUT_EDGES = CONFIGURABLE__OUT_EDGES;

  /**
   * The feature id for the '<em><b>In Edges</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__IN_EDGES = CONFIGURABLE__IN_EDGES;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__NAME = CONFIGURABLE__NAME;

  /**
   * The feature id for the '<em><b>Config Input Ports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__CONFIG_INPUT_PORTS = CONFIGURABLE__CONFIG_INPUT_PORTS;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__EXPRESSION = CONFIGURABLE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Containing Fifo</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY__CONTAINING_FIFO = CONFIGURABLE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Delay</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DELAY_FEATURE_COUNT = CONFIGURABLE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl <em>Function Prototype</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.FunctionPrototypeImpl
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getFunctionPrototype()
   * @generated
   */
  int FUNCTION_PROTOTYPE = 36;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_PROTOTYPE__NAME = 0;

  /**
   * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
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
  int FUNCTION_PARAMETER = 37;

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
  int DIRECTION = 38;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation <em>Port Memory Annotation</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPortMemoryAnnotation()
   * @generated
   */
  int PORT_MEMORY_ANNOTATION = 39;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.PortKind <em>Port Kind</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.PortKind
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPortKind()
   * @generated
   */
  int PORT_KIND = 40;

  /**
   * The meta object id for the '{@link org.ietr.preesm.experiment.model.pimm.InterfaceKind <em>Interface Kind</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.InterfaceKind
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInterfaceKind()
   * @generated
   */
  int INTERFACE_KIND = 41;

  /**
   * The meta object id for the '<em>IPath</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.core.runtime.IPath
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getIPath()
   * @generated
   */
  int IPATH = 42;

  /**
   * The meta object id for the '<em>String</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see java.lang.String
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getString()
   * @generated
   */
  int STRING = 43;

  /**
   * The meta object id for the '<em>int</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getint()
   * @generated
   */
  int INT = 44;

  /**
   * The meta object id for the '<em>long</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getlong()
   * @generated
   */
  int LONG = 45;

  /**
   * The meta object id for the '<em>double</em>' data type.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getdouble()
   * @generated
   */
  int DOUBLE = 46;


  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Vertex <em>Vertex</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Vertex</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Vertex
   * @generated
   */
  EClass getVertex();

  /**
   * Returns the meta object for the container reference '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getContainingGraph <em>Containing Graph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Containing Graph</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Vertex#getContainingGraph()
   * @see #getVertex()
   * @generated
   */
  EReference getVertex_ContainingGraph();

  /**
   * Returns the meta object for the reference list '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getOutEdges <em>Out Edges</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Out Edges</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Vertex#getOutEdges()
   * @see #getVertex()
   * @generated
   */
  EReference getVertex_OutEdges();

  /**
   * Returns the meta object for the reference list '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getInEdges <em>In Edges</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>In Edges</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Vertex#getInEdges()
   * @see #getVertex()
   * @generated
   */
  EReference getVertex_InEdges();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Edge <em>Edge</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Edge</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Edge
   * @generated
   */
  EClass getEdge();

  /**
   * Returns the meta object for the container reference '{@link org.ietr.preesm.experiment.model.pimm.Edge#getContainingGraph <em>Containing Graph</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Containing Graph</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Edge#getContainingGraph()
   * @see #getEdge()
   * @generated
   */
  EReference getEdge_ContainingGraph();

  /**
   * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Edge#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Source</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Edge#getSource()
   * @see #getEdge()
   * @generated
   */
  EReference getEdge_Source();

  /**
   * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.Edge#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Target</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Edge#getTarget()
   * @see #getEdge()
   * @generated
   */
  EReference getEdge_Target();

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
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Graph#getEdges <em>Edges</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Edges</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Graph#getEdges()
   * @see #getGraph()
   * @generated
   */
  EReference getGraph_Edges();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Expression
   * @generated
   */
  EClass getExpression();

  /**
   * Returns the meta object for the container reference '{@link org.ietr.preesm.experiment.model.pimm.Expression#getHolder <em>Holder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Holder</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Expression#getHolder()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Holder();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder <em>Expression Holder</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression Holder</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ExpressionHolder
   * @generated
   */
  EClass getExpressionHolder();

  /**
   * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ExpressionHolder#getExpression()
   * @see #getExpressionHolder()
   * @generated
   */
  EReference getExpressionHolder_Expression();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.Configurable <em>Configurable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Configurable</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Configurable
   * @generated
   */
  EClass getConfigurable();

  /**
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.Configurable#getConfigInputPorts <em>Config Input Ports</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Config Input Ports</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Configurable#getConfigInputPorts()
   * @see #getConfigurable()
   * @generated
   */
  EReference getConfigurable_ConfigInputPorts();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ExecutableActor <em>Executable Actor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Executable Actor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ExecutableActor
   * @generated
   */
  EClass getExecutableActor();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.BroadcastActor <em>Broadcast Actor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Broadcast Actor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.BroadcastActor
   * @generated
   */
  EClass getBroadcastActor();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.JoinActor <em>Join Actor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Join Actor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.JoinActor
   * @generated
   */
  EClass getJoinActor();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ForkActor <em>Fork Actor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Fork Actor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ForkActor
   * @generated
   */
  EClass getForkActor();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.RoundBufferActor <em>Round Buffer Actor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Round Buffer Actor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.RoundBufferActor
   * @generated
   */
  EClass getRoundBufferActor();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.NonExecutableActor <em>Non Executable Actor</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Non Executable Actor</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.NonExecutableActor
   * @generated
   */
  EClass getNonExecutableActor();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.DataPort <em>Data Port</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Data Port</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.DataPort
   * @generated
   */
  EClass getDataPort();

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
   * Returns the meta object for the container reference '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable <em>Configurable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Configurable</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputPort#getConfigurable()
   * @see #getConfigInputPort()
   * @generated
   */
  EReference getConfigInputPort_Configurable();

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
   * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Fifo#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Fifo#getType()
   * @see #getFifo()
   * @generated
   */
  EAttribute getFifo_Type();

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
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputInterface <em>Config Input Interface</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Config Input Interface</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputInterface
   * @generated
   */
  EClass getConfigInputInterface();

  /**
   * Returns the meta object for the reference '{@link org.ietr.preesm.experiment.model.pimm.ConfigInputInterface#getGraphPort <em>Graph Port</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Graph Port</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.ConfigInputInterface#getGraphPort()
   * @see #getConfigInputInterface()
   * @generated
   */
  EReference getConfigInputInterface_GraphPort();

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
   * Returns the meta object for the attribute '{@link org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath <em>File Path</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>File Path</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Refinement#getFilePath()
   * @see #getRefinement()
   * @generated
   */
  EAttribute getRefinement_FilePath();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.PiSDFRefinement <em>Pi SDF Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pi SDF Refinement</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.PiSDFRefinement
   * @generated
   */
  EClass getPiSDFRefinement();

  /**
   * Returns the meta object for class '{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement <em>CHeader Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>CHeader Refinement</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.CHeaderRefinement
   * @generated
   */
  EClass getCHeaderRefinement();

  /**
   * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getLoopPrototype <em>Loop Prototype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Loop Prototype</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getLoopPrototype()
   * @see #getCHeaderRefinement()
   * @generated
   */
  EReference getCHeaderRefinement_LoopPrototype();

  /**
   * Returns the meta object for the containment reference '{@link org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getInitPrototype <em>Init Prototype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Init Prototype</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.CHeaderRefinement#getInitPrototype()
   * @see #getCHeaderRefinement()
   * @generated
   */
  EReference getCHeaderRefinement_InitPrototype();

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
   * Returns the meta object for the container reference '{@link org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo <em>Containing Fifo</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Containing Fifo</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.Delay#getContainingFifo()
   * @see #getDelay()
   * @generated
   */
  EReference getDelay_ContainingFifo();

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
   * Returns the meta object for the containment reference list '{@link org.ietr.preesm.experiment.model.pimm.FunctionPrototype#getParameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Parameters</em>'.
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
   * Returns the meta object for enum '{@link org.ietr.preesm.experiment.model.pimm.PortKind <em>Port Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Port Kind</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.PortKind
   * @generated
   */
  EEnum getPortKind();

  /**
   * Returns the meta object for enum '{@link org.ietr.preesm.experiment.model.pimm.InterfaceKind <em>Interface Kind</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Interface Kind</em>'.
   * @see org.ietr.preesm.experiment.model.pimm.InterfaceKind
   * @generated
   */
  EEnum getInterfaceKind();

  /**
   * Returns the meta object for data type '{@link org.eclipse.core.runtime.IPath <em>IPath</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>IPath</em>'.
   * @see org.eclipse.core.runtime.IPath
   * @model instanceClass="org.eclipse.core.runtime.IPath"
   * @generated
   */
  EDataType getIPath();

  /**
   * Returns the meta object for data type '{@link java.lang.String <em>String</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>String</em>'.
   * @see java.lang.String
   * @model instanceClass="java.lang.String"
   * @generated
   */
  EDataType getString();

  /**
   * Returns the meta object for data type '<em>int</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>int</em>'.
   * @model instanceClass="int"
   * @generated
   */
  EDataType getint();

  /**
   * Returns the meta object for data type '<em>long</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>long</em>'.
   * @model instanceClass="long"
   * @generated
   */
  EDataType getlong();

  /**
   * Returns the meta object for data type '<em>double</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for data type '<em>double</em>'.
   * @model instanceClass="double"
   * @generated
   */
  EDataType getdouble();

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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.VertexImpl <em>Vertex</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.VertexImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getVertex()
     * @generated
     */
    EClass VERTEX = eINSTANCE.getVertex();

    /**
     * The meta object literal for the '<em><b>Containing Graph</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference VERTEX__CONTAINING_GRAPH = eINSTANCE.getVertex_ContainingGraph();

    /**
     * The meta object literal for the '<em><b>Out Edges</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference VERTEX__OUT_EDGES = eINSTANCE.getVertex_OutEdges();

    /**
     * The meta object literal for the '<em><b>In Edges</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference VERTEX__IN_EDGES = eINSTANCE.getVertex_InEdges();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl <em>Edge</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getEdge()
     * @generated
     */
    EClass EDGE = eINSTANCE.getEdge();

    /**
     * The meta object literal for the '<em><b>Containing Graph</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE__CONTAINING_GRAPH = eINSTANCE.getEdge_ContainingGraph();

    /**
     * The meta object literal for the '<em><b>Source</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE__SOURCE = eINSTANCE.getEdge_Source();

    /**
     * The meta object literal for the '<em><b>Target</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EDGE__TARGET = eINSTANCE.getEdge_Target();

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
     * The meta object literal for the '<em><b>Edges</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GRAPH__EDGES = eINSTANCE.getGraph_Edges();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.Parameterizable <em>Parameterizable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.Parameterizable
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getParameterizable()
     * @generated
     */
    EClass PARAMETERIZABLE = eINSTANCE.getParameterizable();

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
     * The meta object literal for the '<em><b>Holder</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__HOLDER = eINSTANCE.getExpression_Holder();

    /**
     * The meta object literal for the '<em><b>Expression String</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EXPRESSION__EXPRESSION_STRING = eINSTANCE.getExpression_ExpressionString();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.ExpressionHolder <em>Expression Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.ExpressionHolder
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExpressionHolder()
     * @generated
     */
    EClass EXPRESSION_HOLDER = eINSTANCE.getExpressionHolder();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION_HOLDER__EXPRESSION = eINSTANCE.getExpressionHolder_Expression();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.AbstractVertex <em>Abstract Vertex</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.AbstractVertex
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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl <em>Configurable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigurableImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigurable()
     * @generated
     */
    EClass CONFIGURABLE = eINSTANCE.getConfigurable();

    /**
     * The meta object literal for the '<em><b>Config Input Ports</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIGURABLE__CONFIG_INPUT_PORTS = eINSTANCE.getConfigurable_ConfigInputPorts();

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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ExecutableActorImpl <em>Executable Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.ExecutableActorImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getExecutableActor()
     * @generated
     */
    EClass EXECUTABLE_ACTOR = eINSTANCE.getExecutableActor();

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
     * The meta object literal for the '<em><b>Memory Script Path</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTOR__MEMORY_SCRIPT_PATH = eINSTANCE.getActor_MemoryScriptPath();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.BroadcastActorImpl <em>Broadcast Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.BroadcastActorImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getBroadcastActor()
     * @generated
     */
    EClass BROADCAST_ACTOR = eINSTANCE.getBroadcastActor();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.JoinActorImpl <em>Join Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.JoinActorImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getJoinActor()
     * @generated
     */
    EClass JOIN_ACTOR = eINSTANCE.getJoinActor();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ForkActorImpl <em>Fork Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.ForkActorImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getForkActor()
     * @generated
     */
    EClass FORK_ACTOR = eINSTANCE.getForkActor();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.RoundBufferActorImpl <em>Round Buffer Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.RoundBufferActorImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRoundBufferActor()
     * @generated
     */
    EClass ROUND_BUFFER_ACTOR = eINSTANCE.getRoundBufferActor();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.NonExecutableActorImpl <em>Non Executable Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.NonExecutableActorImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getNonExecutableActor()
     * @generated
     */
    EClass NON_EXECUTABLE_ACTOR = eINSTANCE.getNonExecutableActor();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.Port <em>Port</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.Port
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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl <em>Data Port</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getDataPort()
     * @generated
     */
    EClass DATA_PORT = eINSTANCE.getDataPort();

    /**
     * The meta object literal for the '<em><b>Annotation</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DATA_PORT__ANNOTATION = eINSTANCE.getDataPort_Annotation();

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
     * The meta object literal for the '<em><b>Configurable</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIG_INPUT_PORT__CONFIGURABLE = eINSTANCE.getConfigInputPort_Configurable();

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
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FIFO__TYPE = eINSTANCE.getFifo_Type();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.InterfaceActor <em>Interface Actor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.InterfaceActor
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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl <em>Config Input Interface</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.ConfigInputInterfaceImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getConfigInputInterface()
     * @generated
     */
    EClass CONFIG_INPUT_INTERFACE = eINSTANCE.getConfigInputInterface();

    /**
     * The meta object literal for the '<em><b>Graph Port</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONFIG_INPUT_INTERFACE__GRAPH_PORT = eINSTANCE.getConfigInputInterface_GraphPort();

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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.Refinement <em>Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.Refinement
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getRefinement()
     * @generated
     */
    EClass REFINEMENT = eINSTANCE.getRefinement();

    /**
     * The meta object literal for the '<em><b>File Path</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REFINEMENT__FILE_PATH = eINSTANCE.getRefinement_FilePath();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl <em>Pi SDF Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiSDFRefinementImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPiSDFRefinement()
     * @generated
     */
    EClass PI_SDF_REFINEMENT = eINSTANCE.getPiSDFRefinement();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl <em>CHeader Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getCHeaderRefinement()
     * @generated
     */
    EClass CHEADER_REFINEMENT = eINSTANCE.getCHeaderRefinement();

    /**
     * The meta object literal for the '<em><b>Loop Prototype</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHEADER_REFINEMENT__LOOP_PROTOTYPE = eINSTANCE.getCHeaderRefinement_LoopPrototype();

    /**
     * The meta object literal for the '<em><b>Init Prototype</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHEADER_REFINEMENT__INIT_PROTOTYPE = eINSTANCE.getCHeaderRefinement_InitPrototype();

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
     * The meta object literal for the '<em><b>Containing Fifo</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DELAY__CONTAINING_FIFO = eINSTANCE.getDelay_ContainingFifo();

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
     * The meta object literal for the '<em><b>Parameters</b></em>' containment reference list feature.
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
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.PortKind <em>Port Kind</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.PortKind
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getPortKind()
     * @generated
     */
    EEnum PORT_KIND = eINSTANCE.getPortKind();

    /**
     * The meta object literal for the '{@link org.ietr.preesm.experiment.model.pimm.InterfaceKind <em>Interface Kind</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.InterfaceKind
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getInterfaceKind()
     * @generated
     */
    EEnum INTERFACE_KIND = eINSTANCE.getInterfaceKind();

    /**
     * The meta object literal for the '<em>IPath</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IPath
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getIPath()
     * @generated
     */
    EDataType IPATH = eINSTANCE.getIPath();

    /**
     * The meta object literal for the '<em>String</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getString()
     * @generated
     */
    EDataType STRING = eINSTANCE.getString();

    /**
     * The meta object literal for the '<em>int</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getint()
     * @generated
     */
    EDataType INT = eINSTANCE.getint();

    /**
     * The meta object literal for the '<em>long</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getlong()
     * @generated
     */
    EDataType LONG = eINSTANCE.getlong();

    /**
     * The meta object literal for the '<em>double</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.ietr.preesm.experiment.model.pimm.impl.PiMMPackageImpl#getdouble()
     * @generated
     */
    EDataType DOUBLE = eINSTANCE.getdouble();

  }

} //PiMMPackage
