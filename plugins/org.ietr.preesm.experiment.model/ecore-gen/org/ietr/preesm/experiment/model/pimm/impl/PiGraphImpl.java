/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2014)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.AbstractVertex;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.adapter.GraphInterfaceObserver;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Graph</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl#getVertices <em>Vertices</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl#getFifos <em>Fifos</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl#getParameters <em>Parameters</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.PiGraphImpl#getDependencies <em>Dependencies</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PiGraphImpl extends AbstractActorImpl implements PiGraph {
  /**
   * The cached value of the '{@link #getVertices() <em>Vertices</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getVertices()
   * @generated
   * @ordered
   */
  protected EList<AbstractActor> vertices;

  /**
   * The cached value of the '{@link #getFifos() <em>Fifos</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFifos()
   * @generated
   * @ordered
   */
  protected EList<Fifo> fifos;

  /**
   * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getParameters()
   * @generated
   * @ordered
   */
  protected EList<Parameter> parameters;

  /**
   * The cached value of the '{@link #getDependencies() <em>Dependencies</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDependencies()
   * @generated
   * @ordered
   */
  protected EList<Dependency> dependencies;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   */
  protected PiGraphImpl() {
    super();
    // Add an observer to apply all the changes required when adding an
    // interface
    eAdapters().add(new GraphInterfaceObserver());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param resolve
   *          the resolve
   * @param coreType
   *          the core type
   * @return the object
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.PI_GRAPH__VERTICES:
        return getVertices();
      case PiMMPackage.PI_GRAPH__FIFOS:
        return getFifos();
      case PiMMPackage.PI_GRAPH__PARAMETERS:
        return getParameters();
      case PiMMPackage.PI_GRAPH__DEPENDENCIES:
        return getDependencies();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param otherEnd
   *          the other end
   * @param featureID
   *          the feature ID
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.PI_GRAPH__VERTICES:
        return ((InternalEList<?>) getVertices()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PI_GRAPH__FIFOS:
        return ((InternalEList<?>) getFifos()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PI_GRAPH__PARAMETERS:
        return ((InternalEList<?>) getParameters()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PI_GRAPH__DEPENDENCIES:
        return ((InternalEList<?>) getDependencies()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @return true, if successful
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case PiMMPackage.PI_GRAPH__VERTICES:
        return (this.vertices != null) && !this.vertices.isEmpty();
      case PiMMPackage.PI_GRAPH__FIFOS:
        return (this.fifos != null) && !this.fifos.isEmpty();
      case PiMMPackage.PI_GRAPH__PARAMETERS:
        return (this.parameters != null) && !this.parameters.isEmpty();
      case PiMMPackage.PI_GRAPH__DEPENDENCIES:
        return (this.dependencies != null) && !this.dependencies.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @param newValue
   *          the new value
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.PI_GRAPH__VERTICES:
        getVertices().clear();
        getVertices().addAll((Collection<? extends AbstractActor>) newValue);
        return;
      case PiMMPackage.PI_GRAPH__FIFOS:
        getFifos().clear();
        getFifos().addAll((Collection<? extends Fifo>) newValue);
        return;
      case PiMMPackage.PI_GRAPH__PARAMETERS:
        getParameters().clear();
        getParameters().addAll((Collection<? extends Parameter>) newValue);
        return;
      case PiMMPackage.PI_GRAPH__DEPENDENCIES:
        getDependencies().clear();
        getDependencies().addAll((Collection<? extends Dependency>) newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.PI_GRAPH;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
   * @generated
   */
  @Override
  public void eUnset(final int featureID) {
    switch (featureID) {
      case PiMMPackage.PI_GRAPH__VERTICES:
        getVertices().clear();
        return;
      case PiMMPackage.PI_GRAPH__FIFOS:
        getFifos().clear();
        return;
      case PiMMPackage.PI_GRAPH__PARAMETERS:
        getParameters().clear();
        return;
      case PiMMPackage.PI_GRAPH__DEPENDENCIES:
        getDependencies().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifos
   * @generated
   */
  @Override
  public EList<Fifo> getFifos() {
    if (this.fifos == null) {
      this.fifos = new EObjectContainmentEList<>(Fifo.class, this, PiMMPackage.PI_GRAPH__FIFOS);
    }
    return this.fifos;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the parameters
   * @generated
   */
  @Override
  public EList<Parameter> getParameters() {
    if (this.parameters == null) {
      this.parameters = new EObjectContainmentWithInverseEList<>(Parameter.class, this, PiMMPackage.PI_GRAPH__PARAMETERS,
          PiMMPackage.PARAMETER__CONTAINING_GRAPH);
    }
    return this.parameters;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the dependencies
   * @generated
   */
  @Override
  public EList<Dependency> getDependencies() {
    if (this.dependencies == null) {
      this.dependencies = new EObjectContainmentEList<>(Dependency.class, this, PiMMPackage.PI_GRAPH__DEPENDENCIES);
    }
    return this.dependencies;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<String> getVerticesNames() {
    return ECollections.newBasicEList(getVertices().stream().map(AbstractActor::getName).collect(Collectors.toList()));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<String> getParametersNames() {
    return ECollections.newBasicEList(getParameters().stream().map(Parameter::getName).collect(Collectors.toList()));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Actor> getActorsWithRefinement() {
    return ECollections.newBasicEList(getVertices().stream().filter(Actor.class::isInstance).map(Actor.class::cast).collect(Collectors.toList()));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Parameter> getAllParameters() {
    final EList<Parameter> result = ECollections.newBasicEList();
    for (final AbstractActor aa : getVertices()) {
      if (aa instanceof PiGraph) {
        result.addAll(((PiGraph) aa).getAllParameters());
      } else if (aa instanceof Actor) {
        final Refinement refinement = ((Actor) aa).getRefinement();
        if (refinement != null) {
          final AbstractActor subGraph = refinement.getAbstractActor();
          if ((subGraph != null) && (subGraph instanceof PiGraph)) {
            result.addAll(((PiGraph) subGraph).getAllParameters());
          }
        }
      }
    }
    result.addAll(getParameters());
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.PI_GRAPH__VERTICES:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getVertices()).basicAdd(otherEnd, msgs);
      case PiMMPackage.PI_GRAPH__PARAMETERS:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getParameters()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getVertexNamed(java.lang.String)
   */
  @Override
  public AbstractVertex lookupVertex(final String name) {
    return Stream.concat(getVertices().stream(), getParameters().stream()).filter(v -> v.getName().equals(name)).findFirst().orElse(null);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the vertices
   * @generated
   */
  @Override
  public EList<AbstractActor> getVertices() {
    if (this.vertices == null) {
      this.vertices = new EObjectContainmentWithInverseEList<>(AbstractActor.class, this, PiMMPackage.PI_GRAPH__VERTICES,
          PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH);
    }
    return this.vertices;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getFifoIded(java.lang.String)
   */
  @Override
  public Fifo lookupFifo(final String id) {
    return getFifos().stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitPiGraph(this);
  }

  /**
   * Returns an Actor indicated through a path where separators are "/".
   *
   * @param path
   *          the path
   * @return the hierarchical actor from path
   */
  @Override
  public AbstractActor lookupActorFromPath(final String path) {
    final String safePath = path.replaceAll("/+", "/").replaceAll("^/*" + getName(), "").replaceAll("^/", "").replaceAll("/$", "");
    if (safePath.isEmpty()) {
      return this;
    }
    final List<String> pathFragments = new ArrayList<>(Arrays.asList(safePath.split("/")));
    final String firstFragment = pathFragments.remove(0);
    final AbstractActor current = getVertices().stream().filter(a -> firstFragment.equals(a.getName())).findFirst().orElse(null);
    if (pathFragments.isEmpty()) {
      return current;
    } else {
      if (current instanceof PiGraph) {
        return ((PiGraph) current).lookupActorFromPath(String.join("/", pathFragments));
      } else if (current instanceof Actor) {
        final AbstractActor aa = ((Actor) current).getRefinement().getAbstractActor();
        if (aa instanceof PiGraph) {
          return ((PiGraph) aa).lookupActorFromPath(String.join("/", pathFragments));
        } else {
          return null;
        }
      } else {
        return null;
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getParameterNamedWithParent(java.lang.String, java.lang.String)
   */
  @Override
  public Parameter lookupParameterGivenGraph(final String parameterName, final String graphName) {
    return getAllParameters().stream().filter(p -> p.getName().equals(parameterName) && p.getContainingGraph().getName().equals(graphName)).findFirst()
        .orElse(null);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getAllVertices()
   */
  @Override
  public EList<AbstractActor> getAllActors() {
    final EList<AbstractActor> result = new BasicEList<>();
    for (final AbstractActor aa : getVertices()) {
      result.add(aa);
      if (aa instanceof PiGraph) {
        result.addAll(((PiGraph) aa).getAllActors());
      } else if (aa instanceof Actor) {
        final Refinement refinement = ((Actor) aa).getRefinement();
        if (refinement != null) {
          final AbstractActor subGraph = refinement.getAbstractActor();
          if ((subGraph != null) && (subGraph instanceof PiGraph)) {
            result.addAll(((PiGraph) subGraph).getAllActors());
          }
        }
      }
    }
    return ECollections.unmodifiableEList(result);
  }
} // GraphImpl
