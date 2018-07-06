/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Vertex</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * ************************************
 *  * Generic Graph Definition
 *  *************************************
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Vertex#getContainingGraph <em>Containing Graph</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Vertex#getOutEdges <em>Out Edges</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Vertex#getInEdges <em>In Edges</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getVertex()
 * @model
 * @generated
 */
public interface Vertex extends EObject {
  /**
   * Returns the value of the '<em><b>Containing Graph</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Graph#getVertices <em>Vertices</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Graph</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Containing Graph</em>' container reference.
   * @see #setContainingGraph(Graph)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getVertex_ContainingGraph()
   * @see org.ietr.preesm.experiment.model.pimm.Graph#getVertices
   * @model opposite="vertices" transient="false"
   * @generated
   */
  Graph getContainingGraph();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getContainingGraph <em>Containing Graph</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Containing Graph</em>' container reference.
   * @see #getContainingGraph()
   * @generated
   */
  void setContainingGraph(Graph value);

  /**
   * Returns the value of the '<em><b>Out Edges</b></em>' reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.Edge}.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Edge#getSource <em>Source</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Out Edges</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Out Edges</em>' reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getVertex_OutEdges()
   * @see org.ietr.preesm.experiment.model.pimm.Edge#getSource
   * @model opposite="source"
   * @generated
   */
  EList<Edge> getOutEdges();

  /**
   * Returns the value of the '<em><b>In Edges</b></em>' reference list.
   * The list contents are of type {@link org.ietr.preesm.experiment.model.pimm.Edge}.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Edge#getTarget <em>Target</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>In Edges</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>In Edges</em>' reference list.
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getVertex_InEdges()
   * @see org.ietr.preesm.experiment.model.pimm.Edge#getTarget
   * @model opposite="target"
   * @generated
   */
  EList<Edge> getInEdges();

} // Vertex
