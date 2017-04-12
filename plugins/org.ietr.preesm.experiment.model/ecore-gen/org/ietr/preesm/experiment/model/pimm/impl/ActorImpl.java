/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2013)
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

import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.Parameterizable;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Actor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getRefinement <em>Refinement</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#isConfigurationActor <em>Configuration Actor</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ActorImpl extends ExecutableActorImpl implements Actor {
  /**
   * The cached value of the '{@link #getRefinement() <em>Refinement</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getRefinement()
   * @generated
   * @ordered
   */
  protected Refinement refinement;

  /**
   * The default value of the '{@link #isConfigurationActor() <em>Configuration Actor</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #isConfigurationActor()
   * @generated
   * @ordered
   */
  protected static final boolean CONFIGURATION_ACTOR_EDEFAULT = false;

  /**
   * The default value of the '{@link #getMemoryScriptPath() <em>Memory Script Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMemoryScriptPath()
   * @generated
   * @ordered
   */
  protected static final IPath MEMORY_SCRIPT_PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getMemoryScriptPath() <em>Memory Script Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getMemoryScriptPath()
   * @generated
   * @ordered
   */
  protected IPath memoryScriptPath = ActorImpl.MEMORY_SCRIPT_PATH_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   */
  protected ActorImpl() {
    super();
    setRefinement(PiMMFactory.eINSTANCE.createRefinement());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.ACTOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the refinement
   * @generated
   */
  @Override
  public Refinement getRefinement() {
    return this.refinement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newRefinement
   *          the new refinement
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetRefinement(final Refinement newRefinement, NotificationChain msgs) {
    final Refinement oldRefinement = this.refinement;
    this.refinement = newRefinement;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, oldRefinement, newRefinement);
      if (msgs == null) {
        msgs = notification;
      } else {
        msgs.add(notification);
      }
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newRefinement
   *          the new refinement
   * @generated
   */
  @Override
  public void setRefinement(final Refinement newRefinement) {
    if (newRefinement != this.refinement) {
      NotificationChain msgs = null;
      if (this.refinement != null) {
        msgs = ((InternalEObject) this.refinement).eInverseRemove(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
      }
      if (newRefinement != null) {
        msgs = ((InternalEObject) newRefinement).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
      }
      msgs = basicSetRefinement(newRefinement, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, newRefinement, newRefinement));
    }
  }

  /**
   * <!-- begin-user-doc --> Check whether the {@link Actor} is a configuration {@link Actor}.<br>
   * <br>
   * An {@link Actor} is a configuration {@link Actor} if it sets a {@link Parameter} value through a {@link ConfigOutputPort}.
   *
   * @return <code>true</code> if the {@link Actor} is a configuration {@link Actor} <code>false</code> else.<!-- end-user-doc -->
   *
   */
  @Override
  public boolean isConfigurationActor() {
    boolean result = false;

    final List<ConfigOutputPort> ports = getConfigOutputPorts();
    for (final ConfigOutputPort port : ports) {
      // If the port has an outgoing dependency
      if (!port.getOutgoingDependencies().isEmpty()) {
        // As soon as there is one dependency, the actor is a
        // configuration actor
        final Dependency dependency = port.getOutgoingDependencies().get(0);
        final Parameterizable parameterizable = (Parameterizable) dependency.getGetter().eContainer();

        // Should always be the case
        if (parameterizable instanceof Parameter) {
          result = true;
        } else {
          throw new RuntimeException(
              "Actor configuration output ports can" + " only set the value of a Parameter. " + parameterizable.eClass() + " cannot be set directly.");
        }
      }
    }

    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is sets the configuration actor
   * @generated
   */
  @Override
  public boolean isSetConfigurationActor() {
    // TODO: implement this method to return whether the 'Configuration Actor' attribute is set
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the memory script path
   * @generated
   */
  @Override
  public IPath getMemoryScriptPath() {
    return this.memoryScriptPath;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newMemoryScriptPath
   *          the new memory script path
   * @generated
   */
  @Override
  public void setMemoryScriptPath(final IPath newMemoryScriptPath) {
    final IPath oldMemoryScriptPath = this.memoryScriptPath;
    this.memoryScriptPath = newMemoryScriptPath;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH, oldMemoryScriptPath, this.memoryScriptPath));
    }
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
      case PiMMPackage.ACTOR__REFINEMENT:
        return basicSetRefinement(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case PiMMPackage.ACTOR__REFINEMENT:
        return getRefinement();
      case PiMMPackage.ACTOR__CONFIGURATION_ACTOR:
        return isConfigurationActor();
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        return getMemoryScriptPath();
    }
    return super.eGet(featureID, resolve, coreType);
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
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.ACTOR__REFINEMENT:
        setRefinement((Refinement) newValue);
        return;
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        setMemoryScriptPath((IPath) newValue);
        return;
    }
    super.eSet(featureID, newValue);
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
      case PiMMPackage.ACTOR__REFINEMENT:
        setRefinement((Refinement) null);
        return;
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        setMemoryScriptPath(ActorImpl.MEMORY_SCRIPT_PATH_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
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
      case PiMMPackage.ACTOR__REFINEMENT:
        return this.refinement != null;
      case PiMMPackage.ACTOR__CONFIGURATION_ACTOR:
        return isSetConfigurationActor();
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        return ActorImpl.MEMORY_SCRIPT_PATH_EDEFAULT == null ? this.memoryScriptPath != null
            : !ActorImpl.MEMORY_SCRIPT_PATH_EDEFAULT.equals(this.memoryScriptPath);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the string
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) {
      return super.toString();
    }

    final StringBuffer result = new StringBuffer(super.toString());
    result.append(" (memoryScriptPath: ");
    result.append(this.memoryScriptPath);
    result.append(')');
    return result.toString();
  }

  /**
   * Test if the actor is a hierarchical one.
   *
   * @return true, if it is.
   */
  @Override
  public boolean isHierarchical() {
    return !((getRefinement().getFilePath() == null) || getRefinement().getFilePath().isEmpty()) && !(getRefinement() instanceof HRefinement);
  }

  /**
   * Get the graph from hierarchy.
   *
   * @return The {@link PiGraph}
   */
  @Override
  public PiGraph getGraph() {
    final AbstractActor subgraph = getRefinement().getAbstractActor();
    if (subgraph instanceof PiGraph) {
      return (PiGraph) subgraph;
    } else {
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ietr.preesm.experiment.model.pimm.impl.ExecutableActorImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitActor(this);
  }

} // ActorImpl
