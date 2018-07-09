/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Graph</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Graph#getVertices <em>Vertices</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Graph#getEdges <em>Edges</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph()
 * @model
 * @generated
 */
public interface Graph extends EObject {
  /**
   * Returns the value of the '<em><b>Vertices</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.Vertex}.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getContainingGraph <em>Containing Graph</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Vertices</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Vertices</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph_Vertices()
   * @see org.ietr.preesm.experiment.model.pimm.Vertex#getContainingGraph
   * @model opposite="containingGraph" containment="true"
   * @generated
   */
  EList<Vertex> getVertices();

  /**
   * Returns the value of the '<em><b>Edges</b></em>' containment reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.Edge}.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Edge#getContainingGraph <em>Containing Graph</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Edges</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Edges</em>' containment reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getGraph_Edges()
   * @see org.ietr.preesm.experiment.model.pimm.Edge#getContainingGraph
   * @model opposite="containingGraph" containment="true"
   * @generated
   */
  EList<Edge> getEdges();

} // Graph
