/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.ietr.preesm.experiment.model.pimm.Edge;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Vertex;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Edge</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl#getContainingGraph <em>Containing Graph</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl#getSource <em>Source</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.EdgeImpl#getTarget <em>Target</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EdgeImpl extends MinimalEObjectImpl.Container implements Edge {
  /**
   * The cached value of the '{@link #getSource() <em>Source</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSource()
   * @generated
   * @ordered
   */
  protected Vertex source;

  /**
   * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTarget()
   * @generated
   * @ordered
   */
  protected Vertex target;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected EdgeImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.EDGE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Graph getContainingGraph() {
    if (eContainerFeatureID() != PiMMPackage.EDGE__CONTAINING_GRAPH) return null;
    return (Graph)eContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Graph basicGetContainingGraph() {
    if (eContainerFeatureID() != PiMMPackage.EDGE__CONTAINING_GRAPH) return null;
    return (Graph)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContainingGraph(Graph newContainingGraph, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject)newContainingGraph, PiMMPackage.EDGE__CONTAINING_GRAPH, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setContainingGraph(Graph newContainingGraph) {
    if (newContainingGraph != eInternalContainer() || (eContainerFeatureID() != PiMMPackage.EDGE__CONTAINING_GRAPH && newContainingGraph != null)) {
      if (EcoreUtil.isAncestor(this, newContainingGraph))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newContainingGraph != null)
        msgs = ((InternalEObject)newContainingGraph).eInverseAdd(this, PiMMPackage.GRAPH__EDGES, Graph.class, msgs);
      msgs = basicSetContainingGraph(newContainingGraph, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EDGE__CONTAINING_GRAPH, newContainingGraph, newContainingGraph));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Vertex getSource() {
    if (source != null && source.eIsProxy()) {
      InternalEObject oldSource = (InternalEObject)source;
      source = (Vertex)eResolveProxy(oldSource);
      if (source != oldSource) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.EDGE__SOURCE, oldSource, source));
      }
    }
    return source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Vertex basicGetSource() {
    return source;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSource(Vertex newSource, NotificationChain msgs) {
    Vertex oldSource = source;
    source = newSource;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.EDGE__SOURCE, oldSource, newSource);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSource(Vertex newSource) {
    if (newSource != source) {
      NotificationChain msgs = null;
      if (source != null)
        msgs = ((InternalEObject)source).eInverseRemove(this, PiMMPackage.VERTEX__OUT_EDGES, Vertex.class, msgs);
      if (newSource != null)
        msgs = ((InternalEObject)newSource).eInverseAdd(this, PiMMPackage.VERTEX__OUT_EDGES, Vertex.class, msgs);
      msgs = basicSetSource(newSource, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EDGE__SOURCE, newSource, newSource));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Vertex getTarget() {
    if (target != null && target.eIsProxy()) {
      InternalEObject oldTarget = (InternalEObject)target;
      target = (Vertex)eResolveProxy(oldTarget);
      if (target != oldTarget) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.EDGE__TARGET, oldTarget, target));
      }
    }
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Vertex basicGetTarget() {
    return target;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTarget(Vertex newTarget, NotificationChain msgs) {
    Vertex oldTarget = target;
    target = newTarget;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.EDGE__TARGET, oldTarget, newTarget);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTarget(Vertex newTarget) {
    if (newTarget != target) {
      NotificationChain msgs = null;
      if (target != null)
        msgs = ((InternalEObject)target).eInverseRemove(this, PiMMPackage.VERTEX__IN_EDGES, Vertex.class, msgs);
      if (newTarget != null)
        msgs = ((InternalEObject)newTarget).eInverseAdd(this, PiMMPackage.VERTEX__IN_EDGES, Vertex.class, msgs);
      msgs = basicSetTarget(newTarget, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.EDGE__TARGET, newTarget, newTarget));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetContainingGraph((Graph)otherEnd, msgs);
      case PiMMPackage.EDGE__SOURCE:
        if (source != null)
          msgs = ((InternalEObject)source).eInverseRemove(this, PiMMPackage.VERTEX__OUT_EDGES, Vertex.class, msgs);
        return basicSetSource((Vertex)otherEnd, msgs);
      case PiMMPackage.EDGE__TARGET:
        if (target != null)
          msgs = ((InternalEObject)target).eInverseRemove(this, PiMMPackage.VERTEX__IN_EDGES, Vertex.class, msgs);
        return basicSetTarget((Vertex)otherEnd, msgs);
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
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        return basicSetContainingGraph(null, msgs);
      case PiMMPackage.EDGE__SOURCE:
        return basicSetSource(null, msgs);
      case PiMMPackage.EDGE__TARGET:
        return basicSetTarget(null, msgs);
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
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.GRAPH__EDGES, Graph.class, msgs);
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
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        if (resolve) return getContainingGraph();
        return basicGetContainingGraph();
      case PiMMPackage.EDGE__SOURCE:
        if (resolve) return getSource();
        return basicGetSource();
      case PiMMPackage.EDGE__TARGET:
        if (resolve) return getTarget();
        return basicGetTarget();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue) {
    switch (featureID) {
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        setContainingGraph((Graph)newValue);
        return;
      case PiMMPackage.EDGE__SOURCE:
        setSource((Vertex)newValue);
        return;
      case PiMMPackage.EDGE__TARGET:
        setTarget((Vertex)newValue);
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
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        setContainingGraph((Graph)null);
        return;
      case PiMMPackage.EDGE__SOURCE:
        setSource((Vertex)null);
        return;
      case PiMMPackage.EDGE__TARGET:
        setTarget((Vertex)null);
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
      case PiMMPackage.EDGE__CONTAINING_GRAPH:
        return basicGetContainingGraph() != null;
      case PiMMPackage.EDGE__SOURCE:
        return source != null;
      case PiMMPackage.EDGE__TARGET:
        return target != null;
    }
    return super.eIsSet(featureID);
  }

} //EdgeImpl
