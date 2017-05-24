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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
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
      this.parameters = new EObjectContainmentEList<>(Parameter.class, this, PiMMPackage.PI_GRAPH__PARAMETERS);
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

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getVertexNamed(java.lang.String)
   */
  @Override
  public AbstractVertex getVertexNamed(final String name) {
    for (final AbstractVertex vert : this.vertices) {
      if (vert.getName().equals(name)) {
        return vert;
      }
    }

    for (final AbstractVertex vert : this.parameters) {
      if (vert.getName().equals(name)) {
        return vert;
      }
    }

    return null;
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
      this.vertices = new EObjectContainmentEList<>(AbstractActor.class, this, PiMMPackage.PI_GRAPH__VERTICES);
    }
    return this.vertices;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getVerticesNames()
   */
  @Override
  public Set<String> getVerticesNames() {
    final Set<String> names = new HashSet<>(getVertices().size());
    for (final AbstractActor vertex : getVertices()) {
      names.add(vertex.getName());
    }
    return names;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getParametersNames()
   */
  @Override
  public Set<String> getParametersNames() {
    final Set<String> names = new HashSet<>(getVertices().size());
    for (final Parameter param : getParameters()) {
      names.add(param.getName());
    }
    return names;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getFifoIded(java.lang.String)
   */
  @Override
  public Fifo getFifoIded(final String id) {
    for (final Fifo fifo : getFifos()) {
      if (fifo.getId().equals(id)) {
        return fifo;
      }
    }
    return null;
  }

  /**
   * Get the set of {@link Actor} in the graph.
   *
   * @return the set of {@link Actor}
   */
  @Override
  public Set<Actor> getActors() {
    final HashSet<Actor> actors = new HashSet<>();
    for (final AbstractActor abactor : getVertices()) {
      if (abactor instanceof Actor) {
        actors.add((Actor) abactor);
      }
    }
    return actors;
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
  public AbstractActor getHierarchicalActorFromPath(final String path) {
    final String[] splitPath = path.split("/");
    int index = 0;
    // Get the first segment of the path, this is the name of the first
    // actor we will look for
    String currentName = splitPath[index];
    index++;
    String currentPath = "";
    // Handle the case where the first segment of path == name
    if (getName().equals(currentName)) {
      // If the first segment is the only one, then we are looking for
      // this
      if (splitPath.length == 1) {
        return this;
      }
      currentName = splitPath[index];
      index++;
    }
    // Compute the path for the next search (path minus currentName)
    for (int i = index; i < splitPath.length; i++) {
      if (i > index) {
        currentPath += "/";
      }
      currentPath += splitPath[i];
    }
    // Look for an actor named currentName
    for (final AbstractActor a : getVertices()) {
      if (a.getName().equals(currentName)) {
        // If currentPath is empty, then we are at the last hierarchy
        // level
        if (currentPath.equals("")) {
          // We found the actor
          return a;
          // Otherwise, we need to go deeper in the hierarchy, either
          // through a PiGraph object directly or through a Refinement
        } else if (a instanceof PiGraph) {
          return ((PiGraph) a).getHierarchicalActorFromPath(currentPath);
        } else if (a instanceof Actor) {
          final Refinement refinement = ((Actor) a).getRefinement();
          if (refinement != null) {
            final AbstractActor subGraph = refinement.getAbstractActor();
            if ((subGraph != null) && (subGraph instanceof PiGraph)) {
              return ((PiGraph) subGraph).getHierarchicalActorFromPath(currentPath);
            }
          }
        }
      }
    }
    // If we reach this point, no actor was found, return null
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getParameterNamedWithParent(java.lang.String, java.lang.String)
   */
  @Override
  public Parameter getParameterNamedWithParent(final String name, final String parent) {
    if (getName().equals(parent)) {
      for (final Parameter p : this.parameters) {
        if (p.getName().equals(name)) {
          return p;
        }
      }
    }
    for (final AbstractActor aa : this.vertices) {
      if ((aa instanceof Actor) && aa.getName().equals(parent)) {
        final Refinement refinement = ((Actor) aa).getRefinement();
        if (refinement != null) {
          final AbstractActor subGraph = refinement.getAbstractActor();
          if ((subGraph != null) && (subGraph instanceof PiGraph)) {
            final Parameter p = ((PiGraph) subGraph).getParameterNamedWithParent(name, parent);
            if (p != null) {
              return p;
            }
          }
        }
      } else if (aa instanceof PiGraph) {
        final Parameter p = ((PiGraph) aa).getParameterNamedWithParent(name, parent);
        if (p != null) {
          return p;
        }
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getAllParameters()
   */
  @Override
  public Set<Parameter> getAllParameters() {
    final Set<Parameter> result = new HashSet<>();
    for (final AbstractActor aa : this.vertices) {
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
    result.addAll(this.parameters);
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.PiGraph#getAllVertices()
   */
  @Override
  public EList<AbstractActor> getAllVertices() {
    final EList<AbstractActor> result = new BasicEList<>();
    for (final AbstractActor aa : getVertices()) {
      result.add(aa);
      if (aa instanceof PiGraph) {
        result.addAll(((PiGraph) aa).getAllVertices());
      } else if (aa instanceof Actor) {
        final Refinement refinement = ((Actor) aa).getRefinement();
        if (refinement != null) {
          final AbstractActor subGraph = refinement.getAbstractActor();
          if ((subGraph != null) && (subGraph instanceof PiGraph)) {
            result.addAll(((PiGraph) subGraph).getAllVertices());
          }
        }
      }
    }
    return result;
  }
} // GraphImpl
