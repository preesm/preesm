/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Objects;
import java.util.Optional;
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
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Actor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getRefinement <em>Refinement</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ActorImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.ACTOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Refinement getRefinement() {
    return this.refinement;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IPath getMemoryScriptPath() {
    return this.memoryScriptPath;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isConfigurationActor() {
    // an Actor is considered as a Configuration Actor iff it has at least a ConfigOutputPort that is connected to a getter
    return getConfigOutputPorts().stream().filter(Objects::nonNull).map(ConfigOutputPort::getOutgoingDependencies).filter(l -> !l.isEmpty()).map(l -> l.get(0))
        .map(Dependency::getGetter).filter(Objects::nonNull).anyMatch(x -> true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isHierarchical() {
    return getRefinement().isHierarchical();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public AbstractActor getChildAbstractActor() {
    return Optional.ofNullable(getRefinement()).map(Refinement::getAbstractActor).orElse(null);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PiGraph getSubGraph() {
    if (isHierarchical()) {
      return (PiGraph) getChildAbstractActor();
    } else {
      throw new UnsupportedOperationException("Cannot get the subgraph of a non hierarchical actor.");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.ACTOR__REFINEMENT:
        return getRefinement();
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        return getMemoryScriptPath();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean eIsSet(final int featureID) {
    switch (featureID) {
      case PiMMPackage.ACTOR__REFINEMENT:
        return this.refinement != null;
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        return ActorImpl.MEMORY_SCRIPT_PATH_EDEFAULT == null ? this.memoryScriptPath != null
            : !ActorImpl.MEMORY_SCRIPT_PATH_EDEFAULT.equals(this.memoryScriptPath);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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

} // ActorImpl
