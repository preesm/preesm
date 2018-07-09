/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import com.google.common.base.Objects;

import com.google.common.collect.Iterables;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import org.eclipse.xtext.xbase.lib.IterableExtensions;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputInterface;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputInterface;
import org.ietr.preesm.experiment.model.pimm.Configurable;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Edge;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Graph;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Vertex;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Pi Graph</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl#getVertices <em>Vertices</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl#getEdges <em>Edges</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PiGraphImpl extends AbstractActorImpl implements PiGraph {
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
  protected PiGraphImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.PI_GRAPH;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Vertex> getVertices() {
    if (vertices == null) {
      vertices = new EObjectContainmentWithInverseEList<Vertex>(Vertex.class, this, PiMMPackage.PI_GRAPH__VERTICES, PiMMPackage.VERTEX__CONTAINING_GRAPH);
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
      edges = new EObjectContainmentWithInverseEList<Edge>(Edge.class, this, PiMMPackage.PI_GRAPH__EDGES, PiMMPackage.EDGE__CONTAINING_GRAPH);
    }
    return edges;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean addActor(final AbstractActor actor) {
    return this.getVertices().add(actor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean addParameter(final Parameter parameter) {
    return this.getVertices().add(parameter);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean addFifo(final Fifo fifo) {
    return this.getEdges().add(fifo);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean addDependency(final Dependency dependency) {
    return this.getEdges().add(dependency);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Parameter> getParameters() {
    return ECollections.<Parameter>unmodifiableEList(ECollections.<Parameter>toEList(Iterables.<Parameter>filter(this.getVertices(), Parameter.class)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AbstractActor> getActors() {
    return ECollections.<AbstractActor>unmodifiableEList(ECollections.<AbstractActor>toEList(Iterables.<AbstractActor>filter(this.getVertices(), AbstractActor.class)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Fifo> getFifos() {
    return ECollections.<Fifo>unmodifiableEList(ECollections.<Fifo>toEList(Iterables.<Fifo>filter(this.getEdges(), Fifo.class)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Dependency> getDependencies() {
    return ECollections.<Dependency>unmodifiableEList(ECollections.<Dependency>toEList(Iterables.<Dependency>filter(this.getEdges(), Dependency.class)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean removeActor(final AbstractActor actor) {
    return this.getVertices().remove(actor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Parameter> getOnlyParameters() {
    final Function1<Parameter, Boolean> _function = new Function1<Parameter, Boolean>() {
      public Boolean apply(final Parameter it) {
        return Boolean.valueOf((!((it instanceof ConfigInputInterface) || (it instanceof ConfigOutputInterface))));
      }
    };
    return ECollections.<Parameter>unmodifiableEList(ECollections.<Parameter>toEList(IterableExtensions.<Parameter>filter(this.getParameters(), _function)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getActorsNames() {
    final Function1<AbstractActor, String> _function = new Function1<AbstractActor, String>() {
      public String apply(final AbstractActor it) {
        return it.getName();
      }
    };
    return ECollections.<String>unmodifiableEList(XcoreEListExtensions.<AbstractActor, String>map(this.getActors(), _function));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getParametersNames() {
    final Function1<Parameter, String> _function = new Function1<Parameter, String>() {
      public String apply(final Parameter it) {
        return it.getName();
      }
    };
    return ECollections.<String>unmodifiableEList(XcoreEListExtensions.<Parameter, String>map(this.getParameters(), _function));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Actor> getActorsWithRefinement() {
    return ECollections.<Actor>unmodifiableEList(ECollections.<Actor>toEList(Iterables.<Actor>filter(this.getActors(), Actor.class)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Parameter> getAllParameters() {
    EList<Parameter> _parameters = this.getParameters();
    final Function1<PiGraph, EList<Parameter>> _function = new Function1<PiGraph, EList<Parameter>>() {
      public EList<Parameter> apply(final PiGraph it) {
        return it.getAllParameters();
      }
    };
    Iterable<Parameter> _flatten = Iterables.<Parameter>concat(XcoreEListExtensions.<PiGraph, EList<Parameter>>map(this.getChildrenGraphs(), _function));
    return ECollections.<Parameter>unmodifiableEList(ECollections.<Parameter>toEList(Iterables.<Parameter>concat(_parameters, _flatten)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AbstractActor> getOnlyActors() {
    final Function1<AbstractActor, Boolean> _function = new Function1<AbstractActor, Boolean>() {
      public Boolean apply(final AbstractActor it) {
        return Boolean.valueOf((!(it instanceof PiGraph)));
      }
    };
    return ECollections.<AbstractActor>unmodifiableEList(ECollections.<AbstractActor>toEList(IterableExtensions.<AbstractActor>filter(this.getActors(), _function)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<PiGraph> getChildrenGraphs() {
    Iterable<PiGraph> _filter = Iterables.<PiGraph>filter(this.getActors(), PiGraph.class);
    final Function1<Actor, Boolean> _function = new Function1<Actor, Boolean>() {
      public Boolean apply(final Actor it) {
        return Boolean.valueOf(it.isHierarchical());
      }
    };
    final Function1<Actor, PiGraph> _function_1 = new Function1<Actor, PiGraph>() {
      public PiGraph apply(final Actor it) {
        return it.getSubGraph();
      }
    };
    Iterable<PiGraph> _map = IterableExtensions.<Actor, PiGraph>map(IterableExtensions.<Actor>filter(this.getActorsWithRefinement(), _function), _function_1);
    return ECollections.<PiGraph>unmodifiableEList(ECollections.<PiGraph>toEList(Iterables.<PiGraph>concat(_filter, _map)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AbstractActor> getAllActors() {
    EList<AbstractActor> _actors = this.getActors();
    final Function1<PiGraph, EList<AbstractActor>> _function = new Function1<PiGraph, EList<AbstractActor>>() {
      public EList<AbstractActor> apply(final PiGraph it) {
        return it.getAllActors();
      }
    };
    Iterable<AbstractActor> _flatten = Iterables.<AbstractActor>concat(XcoreEListExtensions.<PiGraph, EList<AbstractActor>>map(this.getChildrenGraphs(), _function));
    return ECollections.<AbstractActor>unmodifiableEList(ECollections.<AbstractActor>toEList(Iterables.<AbstractActor>concat(_actors, _flatten)));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Parameter lookupParameterGivenGraph(final String parameterName, final String graphName) {
    final Function1<Parameter, Boolean> _function = new Function1<Parameter, Boolean>() {
      public Boolean apply(final Parameter it) {
        return Boolean.valueOf((Objects.equal(it.getName(), parameterName) && Objects.equal(((PiGraph) it.getContainingGraph()).getName(), graphName)));
      }
    };
    return IterableExtensions.<Parameter>findFirst(this.getAllParameters(), _function);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataPort lookupGraphDataPortForInterfaceActor(final InterfaceActor interfaceActor) {
    final Function1<DataPort, Boolean> _function = new Function1<DataPort, Boolean>() {
      public Boolean apply(final DataPort it) {
        String _name = it.getName();
        String _name_1 = interfaceActor.getName();
        return Boolean.valueOf(Objects.equal(_name, _name_1));
      }
    };
    return IterableExtensions.<DataPort>findFirst(this.getAllDataPorts(), _function);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractVertex lookupVertex(final String vertexName) {
    EList<AbstractActor> _actors = this.getActors();
    EList<Parameter> _parameters = this.getParameters();
    final Function1<Configurable, Boolean> _function = new Function1<Configurable, Boolean>() {
      public Boolean apply(final Configurable it) {
        String _name = it.getName();
        return Boolean.valueOf(Objects.equal(_name, vertexName));
      }
    };
    return IterableExtensions.<Configurable>findFirst(Iterables.<Configurable>concat(_actors, _parameters), _function);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Fifo lookupFifo(final String fifoId) {
    final Function1<Fifo, Boolean> _function = new Function1<Fifo, Boolean>() {
      public Boolean apply(final Fifo it) {
        String _id = it.getId();
        return Boolean.valueOf(Objects.equal(_id, fifoId));
      }
    };
    return IterableExtensions.<Fifo>findFirst(this.getFifos(), _function);
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
      case PiMMPackage.PI_GRAPH__VERTICES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getVertices()).basicAdd(otherEnd, msgs);
      case PiMMPackage.PI_GRAPH__EDGES:
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
      case PiMMPackage.PI_GRAPH__VERTICES:
        return ((InternalEList<?>)getVertices()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PI_GRAPH__EDGES:
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
      case PiMMPackage.PI_GRAPH__VERTICES:
        return getVertices();
      case PiMMPackage.PI_GRAPH__EDGES:
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
      case PiMMPackage.PI_GRAPH__VERTICES:
        getVertices().clear();
        getVertices().addAll((Collection<? extends Vertex>)newValue);
        return;
      case PiMMPackage.PI_GRAPH__EDGES:
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
      case PiMMPackage.PI_GRAPH__VERTICES:
        getVertices().clear();
        return;
      case PiMMPackage.PI_GRAPH__EDGES:
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
      case PiMMPackage.PI_GRAPH__VERTICES:
        return vertices != null && !vertices.isEmpty();
      case PiMMPackage.PI_GRAPH__EDGES:
        return edges != null && !edges.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
    if (baseClass == Graph.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PI_GRAPH__VERTICES: return PiMMPackage.GRAPH__VERTICES;
        case PiMMPackage.PI_GRAPH__EDGES: return PiMMPackage.GRAPH__EDGES;
        default: return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
    if (baseClass == Graph.class) {
      switch (baseFeatureID) {
        case PiMMPackage.GRAPH__VERTICES: return PiMMPackage.PI_GRAPH__VERTICES;
        case PiMMPackage.GRAPH__EDGES: return PiMMPackage.PI_GRAPH__EDGES;
        default: return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} //PiGraphImpl
