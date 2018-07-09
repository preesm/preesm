/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.ietr.preesm.experiment.model.pimm.Edge;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Vertex;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Graph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getVertices <em>Vertices</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.GraphImpl#getEdges <em>Edges</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GraphImpl extends MinimalEObjectImpl.Container implements Graph {
  /**
   * The cached value of the '{@link #getVertices() <em>Vertices</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getVertices()
   * @generated
   * @ordered
   */
  protected EList<Vertex> vertices;

  /**
   * The cached value of the '{@link #getEdges() <em>Edges</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEdges()
   * @generated
   * @ordered
   */
  protected EList<Edge> edges;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GraphImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.GRAPH;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Vertex> getVertices() {
    if (vertices == null) {
      vertices = new EObjectContainmentWithInverseEList<Vertex>(Vertex.class, this, PiMMPackage.GRAPH__VERTICES, PiMMPackage.VERTEX__CONTAINING_GRAPH);
    }
    return vertices;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Edge> getEdges() {
    if (edges == null) {
      edges = new EObjectContainmentWithInverseEList<Edge>(Edge.class, this, PiMMPackage.GRAPH__EDGES, PiMMPackage.EDGE__CONTAINING_GRAPH);
    }
    return edges;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.GRAPH__VERTICES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getVertices()).basicAdd(otherEnd, msgs);
      case PiMMPackage.GRAPH__EDGES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getEdges()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.GRAPH__VERTICES:
        return ((InternalEList<?>)getVertices()).basicRemove(otherEnd, msgs);
      case PiMMPackage.GRAPH__EDGES:
        return ((InternalEList<?>)getEdges()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.GRAPH__VERTICES:
        return getVertices();
      case PiMMPackage.GRAPH__EDGES:
        return getEdges();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.GRAPH__VERTICES:
        getVertices().clear();
        getVertices().addAll((Collection<? extends Vertex>)newValue);
        return;
      case PiMMPackage.GRAPH__EDGES:
        getEdges().clear();
        getEdges().addAll((Collection<? extends Edge>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID) {
    switch (featureID) {
      case PiMMPackage.GRAPH__VERTICES:
        getVertices().clear();
        return;
      case PiMMPackage.GRAPH__EDGES:
        getEdges().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID) {
    switch (featureID) {
      case PiMMPackage.GRAPH__VERTICES:
        return vertices != null && !vertices.isEmpty();
      case PiMMPackage.GRAPH__EDGES:
        return edges != null && !edges.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //GraphImpl
