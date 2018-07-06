/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import org.ietr.preesm.experiment.model.pimm.Edge;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Vertex;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Vertex</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.VertexImpl#getContainingGraph <em>Containing Graph</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.VertexImpl#getOutEdges <em>Out Edges</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.VertexImpl#getInEdges <em>In Edges</em>}</li>
 * </ul>
 *
 * @generated
 */
public class VertexImpl extends MinimalEObjectImpl.Container implements Vertex {
  /**
   * The cached value of the '{@link #getOutEdges() <em>Out Edges</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutEdges()
   * @generated
   * @ordered
   */
  protected EList<Edge> outEdges;

  /**
   * The cached value of the '{@link #getInEdges() <em>In Edges</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInEdges()
   * @generated
   * @ordered
   */
  protected EList<Edge> inEdges;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected VertexImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.VERTEX;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Graph getContainingGraph() {
    if (eContainerFeatureID() != PiMMPackage.VERTEX__CONTAINING_GRAPH) return null;
    return (Graph)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Graph basicGetContainingGraph() {
    if (eContainerFeatureID() != PiMMPackage.VERTEX__CONTAINING_GRAPH) return null;
    return (Graph)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContainingGraph(Graph newContainingGraph, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newContainingGraph, PiMMPackage.VERTEX__CONTAINING_GRAPH, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContainingGraph(Graph newContainingGraph) {
    if (newContainingGraph != eInternalContainer() || (eContainerFeatureID() != PiMMPackage.VERTEX__CONTAINING_GRAPH && newContainingGraph != null)) {
      if (EcoreUtil.isAncestor(this, newContainingGraph))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newContainingGraph != null)
        msgs = ((InternalEObject)newContainingGraph).eInverseAdd(this, PiMMPackage.GRAPH__VERTICES, Graph.class, msgs);
      msgs = basicSetContainingGraph(newContainingGraph, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.VERTEX__CONTAINING_GRAPH, newContainingGraph, newContainingGraph));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Edge> getOutEdges() {
    if (outEdges == null) {
      outEdges = new EObjectWithInverseResolvingEList<Edge>(Edge.class, this, PiMMPackage.VERTEX__OUT_EDGES, PiMMPackage.EDGE__SOURCE);
    }
    return outEdges;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Edge> getInEdges() {
    if (inEdges == null) {
      inEdges = new EObjectWithInverseResolvingEList<Edge>(Edge.class, this, PiMMPackage.VERTEX__IN_EDGES, PiMMPackage.EDGE__TARGET);
    }
    return inEdges;
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
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetContainingGraph((Graph)otherEnd, msgs);
      case PiMMPackage.VERTEX__OUT_EDGES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutEdges()).basicAdd(otherEnd, msgs);
      case PiMMPackage.VERTEX__IN_EDGES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getInEdges()).basicAdd(otherEnd, msgs);
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
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        return basicSetContainingGraph(null, msgs);
      case PiMMPackage.VERTEX__OUT_EDGES:
        return ((InternalEList<?>)getOutEdges()).basicRemove(otherEnd, msgs);
      case PiMMPackage.VERTEX__IN_EDGES:
        return ((InternalEList<?>)getInEdges()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.GRAPH__VERTICES, Graph.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        if (resolve) return getContainingGraph();
        return basicGetContainingGraph();
      case PiMMPackage.VERTEX__OUT_EDGES:
        return getOutEdges();
      case PiMMPackage.VERTEX__IN_EDGES:
        return getInEdges();
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
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        setContainingGraph((Graph)newValue);
        return;
      case PiMMPackage.VERTEX__OUT_EDGES:
        getOutEdges().clear();
        getOutEdges().addAll((Collection<? extends Edge>)newValue);
        return;
      case PiMMPackage.VERTEX__IN_EDGES:
        getInEdges().clear();
        getInEdges().addAll((Collection<? extends Edge>)newValue);
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
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        setContainingGraph((Graph)null);
        return;
      case PiMMPackage.VERTEX__OUT_EDGES:
        getOutEdges().clear();
        return;
      case PiMMPackage.VERTEX__IN_EDGES:
        getInEdges().clear();
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
      case PiMMPackage.VERTEX__CONTAINING_GRAPH:
        return basicGetContainingGraph() != null;
      case PiMMPackage.VERTEX__OUT_EDGES:
        return outEdges != null && !outEdges.isEmpty();
      case PiMMPackage.VERTEX__IN_EDGES:
        return inEdges != null && !inEdges.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //VertexImpl
