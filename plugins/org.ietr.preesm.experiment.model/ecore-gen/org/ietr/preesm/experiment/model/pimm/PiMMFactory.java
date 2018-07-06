/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage
 * @generated
 */
public interface PiMMFactory extends EFactory {
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  PiMMFactory eINSTANCE = org.ietr.preesm.experiment.model.pimm.impl.PiMMFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Vertex</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Vertex</em>'.
   * @generated
   */
  Vertex createVertex();

  /**
   * Returns a new object of class '<em>Edge</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Edge</em>'.
   * @generated
   */
  Edge createEdge();

  /**
   * Returns a new object of class '<em>Graph</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Graph</em>'.
   * @generated
   */
  Graph createGraph();

  /**
   * Returns a new object of class '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression</em>'.
   * @generated
   */
  Expression createExpression();

  /**
   * Returns a new object of class '<em>Pi Graph</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pi Graph</em>'.
   * @generated
   */
  PiGraph createPiGraph();

  /**
   * Returns a new object of class '<em>Actor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Actor</em>'.
   * @generated
   */
  Actor createActor();

  /**
   * Returns a new object of class '<em>Broadcast Actor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Broadcast Actor</em>'.
   * @generated
   */
  BroadcastActor createBroadcastActor();

  /**
   * Returns a new object of class '<em>Join Actor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Join Actor</em>'.
   * @generated
   */
  JoinActor createJoinActor();

  /**
   * Returns a new object of class '<em>Fork Actor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Fork Actor</em>'.
   * @generated
   */
  ForkActor createForkActor();

  /**
   * Returns a new object of class '<em>Round Buffer Actor</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Round Buffer Actor</em>'.
   * @generated
   */
  RoundBufferActor createRoundBufferActor();

  /**
   * Returns a new object of class '<em>Data Input Port</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Data Input Port</em>'.
   * @generated
   */
  DataInputPort createDataInputPort();

  /**
   * Returns a new object of class '<em>Data Output Port</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Data Output Port</em>'.
   * @generated
   */
  DataOutputPort createDataOutputPort();

  /**
   * Returns a new object of class '<em>Config Input Port</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Config Input Port</em>'.
   * @generated
   */
  ConfigInputPort createConfigInputPort();

  /**
   * Returns a new object of class '<em>Config Output Port</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Config Output Port</em>'.
   * @generated
   */
  ConfigOutputPort createConfigOutputPort();

  /**
   * Returns a new object of class '<em>Fifo</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Fifo</em>'.
   * @generated
   */
  Fifo createFifo();

  /**
   * Returns a new object of class '<em>Data Input Interface</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Data Input Interface</em>'.
   * @generated
   */
  DataInputInterface createDataInputInterface();

  /**
   * Returns a new object of class '<em>Data Output Interface</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Data Output Interface</em>'.
   * @generated
   */
  DataOutputInterface createDataOutputInterface();

  /**
   * Returns a new object of class '<em>Config Input Interface</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Config Input Interface</em>'.
   * @generated
   */
  ConfigInputInterface createConfigInputInterface();

  /**
   * Returns a new object of class '<em>Config Output Interface</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Config Output Interface</em>'.
   * @generated
   */
  ConfigOutputInterface createConfigOutputInterface();

  /**
   * Returns a new object of class '<em>Pi SDF Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pi SDF Refinement</em>'.
   * @generated
   */
  PiSDFRefinement createPiSDFRefinement();

  /**
   * Returns a new object of class '<em>CHeader Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>CHeader Refinement</em>'.
   * @generated
   */
  CHeaderRefinement createCHeaderRefinement();

  /**
   * Returns a new object of class '<em>Parameter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parameter</em>'.
   * @generated
   */
  Parameter createParameter();

  /**
   * Returns a new object of class '<em>Dependency</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Dependency</em>'.
   * @generated
   */
  Dependency createDependency();

  /**
   * Returns a new object of class '<em>Delay</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Delay</em>'.
   * @generated
   */
  Delay createDelay();

  /**
   * Returns a new object of class '<em>Function Prototype</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Function Prototype</em>'.
   * @generated
   */
  FunctionPrototype createFunctionPrototype();

  /**
   * Returns a new object of class '<em>Function Parameter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Function Parameter</em>'.
   * @generated
   */
  FunctionParameter createFunctionParameter();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  PiMMPackage getPiMMPackage();

} //PiMMFactory
