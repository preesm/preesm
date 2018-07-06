/**
 */
package org.ietr.preesm.experiment.model.pimm;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Edge#getContainingGraph <em>Containing Graph</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Edge#getSource <em>Source</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.Edge#getTarget <em>Target</em>}</li>
 * </ul>
 *
 * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getEdge()
 * @model
 * @generated
 */
public interface Edge extends EObject {
  /**
   * Returns the value of the '<em><b>Containing Graph</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Graph#getEdges <em>Edges</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containing Graph</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Containing Graph</em>' container reference.
   * @see #setContainingGraph(Graph)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getEdge_ContainingGraph()
   * @see org.ietr.preesm.experiment.model.pimm.Graph#getEdges
   * @model opposite="edges" transient="false"
   * @generated
   */
  Graph getContainingGraph();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Edge#getContainingGraph <em>Containing Graph</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Containing Graph</em>' container reference.
   * @see #getContainingGraph()
   * @generated
   */
  void setContainingGraph(Graph value);

  /**
   * Returns the value of the '<em><b>Source</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getOutEdges <em>Out Edges</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Source</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source</em>' reference.
   * @see #setSource(Vertex)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getEdge_Source()
   * @see org.ietr.preesm.experiment.model.pimm.Vertex#getOutEdges
   * @model opposite="outEdges"
   * @generated
   */
  Vertex getSource();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Edge#getSource <em>Source</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source</em>' reference.
   * @see #getSource()
   * @generated
   */
  void setSource(Vertex value);

  /**
   * Returns the value of the '<em><b>Target</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.ietr.preesm.experiment.model.pimm.Vertex#getInEdges <em>In Edges</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target</em>' reference.
   * @see #setTarget(Vertex)
   * @see org.ietr.preesm.experiment.model.pimm.PiMMPackage#getEdge_Target()
   * @see org.ietr.preesm.experiment.model.pimm.Vertex#getInEdges
   * @model opposite="inEdges"
   * @generated
   */
  Vertex getTarget();

  /**
   * Sets the value of the '{@link org.ietr.preesm.experiment.model.pimm.Edge#getTarget <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target</em>' reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(Vertex value);

} // Edge
