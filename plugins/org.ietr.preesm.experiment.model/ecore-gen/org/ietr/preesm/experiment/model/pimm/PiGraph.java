/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Pi Graph</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getActors <em>Actors</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getFifos <em>Fifos</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getParameters <em>Parameters</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.PiGraph#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph()
 * @model
 * @generated
 */
public interface PiGraph extends AbstractActor {
  /**
   * Returns the value of the '<em><b>Actors</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.AbstractActor}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.AbstractActor#getContainingGraph <em>Containing Graph</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Actors</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Actors</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Actors()
   * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getContainingGraph
   * @model opposite="containingGraph" containment="true"
   * @generated
   */
  EList<AbstractActor> getActors();

  /**
   * Returns the value of the '<em><b>Fifos</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Fifo}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Fifos</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Fifos</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Fifos()
   * @model containment="true"
   * @generated
   */
  EList<Fifo> getFifos();

  /**
   * Returns the value of the '<em><b>Parameters</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Parameter}. It is bidirectional and its opposite is
   * '{@link org.ietr.preesm.experiment.model.pimm.Parameter#getContainingGraph <em>Containing Graph</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Parameters</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Parameters()
   * @see org.ietr.preesm.experiment.model.pimm.Parameter#getContainingGraph
   * @model opposite="containingGraph" containment="true"
   * @generated
   */
  EList<Parameter> getParameters();

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' containment reference list. The list contents are of type
   * {@link org.ietr.preesm.experiment.model.pimm.Dependency}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dependencies</em>' containment reference list isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Dependencies</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getPiGraph_Dependencies()
   * @model containment="true"
   * @generated
   */
  EList<Dependency> getDependencies();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        ECollections.unmodifiableEList(getActors().stream().map(AbstractActor::getName).collect(Collectors.toList()));'"
   * @generated
   */
  EList<String> getActorsNames();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        ECollections.unmodifiableEList(getParameters().stream().map(Parameter::getName).collect(Collectors.toList()));'"
   * @generated
   */
  EList<String> getParametersNames();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='return
   *        ECollections.unmodifiableEList(getActors().stream().filter(Actor.class::isInstance).map(Actor.class::cast).collect(Collectors.toList()));'"
   * @generated
   */
  EList<Actor> getActorsWithRefinement();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final Stream&lt;Parameter&gt; currentGraphParameters =
   *        getParameters().stream();\nfinal Stream&lt;Parameter&gt; childrenGraphsParameters =
   *        getChildrenGraphs().stream().map(PiGraph::getAllParameters).flatMap(List::stream);\nreturn
   *        ECollections.unmodifiableEList(Stream.concat(currentGraphParameters, childrenGraphsParameters).collect(Collectors.toList()));'"
   * @generated
   */
  EList<Parameter> getAllParameters();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final Stream&lt;PiGraph&gt; directChildrenGraphs =
   *        getActors().stream().filter(PiGraph.class::isInstance).map(PiGraph.class::cast);\nfinal Stream&lt;PiGraph&gt; refinementChildrenGraphs =
   *        getActorsWithRefinement().stream().filter(Actor::isHierarchical).map(Actor::getSubGraph);\nreturn
   *        ECollections.unmodifiableEList(Stream.concat(directChildrenGraphs, refinementChildrenGraphs).collect(Collectors.toList()));'"
   * @generated
   */
  EList<PiGraph> getChildrenGraphs();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model kind="operation" annotation="http://www.eclipse.org/emf/2002/GenModel body='final Stream&lt;AbstractActor&gt; currentGraphActors =
   *        getActors().stream();\nfinal Stream&lt;AbstractActor&gt; chidrenGraphsActors =
   *        getChildrenGraphs().stream().map(PiGraph::getAllActors).flatMap(List::stream);\nreturn
   *        ECollections.unmodifiableEList(Stream.concat(currentGraphActors, chidrenGraphsActors).collect(Collectors.toList()));'"
   * @generated
   */
  EList<AbstractActor> getAllActors();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return getAllParameters().stream().filter(p -&gt; p.getName().equals(parameterName)
   *        &amp;&amp; p.getContainingGraph().getName().equals(graphName)).findFirst().orElse(null);'"
   * @generated
   */
  Parameter lookupParameterGivenGraph(String parameterName, String graphName);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return Stream.concat(getActors().stream(), getParameters().stream()).filter(v -&gt;
   *        v.getName().equals(vertexName)).findFirst().orElse(null);'"
   * @generated
   */
  AbstractVertex lookupVertex(String vertexName);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='return getFifos().stream().filter(f -&gt;
   *        f.getId().equals(fifoId)).findFirst().orElse(null);'"
   * @generated
   */
  Fifo lookupFifo(String fifoId);

} // PiGraph
