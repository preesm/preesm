/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.xcore.lib.XcoreEListExtensions;

import org.eclipse.xtext.xbase.lib.Functions.Function1;

import org.eclipse.xtext.xbase.lib.IterableExtensions;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Actor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Refinement;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Actor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getRefinement <em>Refinement</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ActorImpl#getMemoryScriptPath <em>Memory Script Path</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ActorImpl extends ExecutableActorImpl implements Actor {
  /**
   * The cached value of the '{@link #getRefinement() <em>Refinement</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRefinement()
   * @generated
   * @ordered
   */
  protected Refinement refinement;

  /**
   * The default value of the '{@link #getMemoryScriptPath() <em>Memory Script Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMemoryScriptPath()
   * @generated
   * @ordered
   */
  protected static final IPath MEMORY_SCRIPT_PATH_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getMemoryScriptPath() <em>Memory Script Path</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMemoryScriptPath()
   * @generated
   * @ordered
   */
  protected IPath memoryScriptPath = MEMORY_SCRIPT_PATH_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ActorImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.ACTOR;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Refinement getRefinement() {
    return refinement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetRefinement(Refinement newRefinement, NotificationChain msgs) {
    Refinement oldRefinement = refinement;
    refinement = newRefinement;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, oldRefinement, newRefinement);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRefinement(Refinement newRefinement) {
    if (newRefinement != refinement) {
      NotificationChain msgs = null;
      if (refinement != null)
        msgs = ((InternalEObject)refinement).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
      if (newRefinement != null)
        msgs = ((InternalEObject)newRefinement).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.ACTOR__REFINEMENT, null, msgs);
      msgs = basicSetRefinement(newRefinement, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__REFINEMENT, newRefinement, newRefinement));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IPath getMemoryScriptPath() {
    return memoryScriptPath;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMemoryScriptPath(IPath newMemoryScriptPath) {
    IPath oldMemoryScriptPath = memoryScriptPath;
    memoryScriptPath = newMemoryScriptPath;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH, oldMemoryScriptPath, memoryScriptPath));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isConfigurationActor() {
    final Function1<ConfigOutputPort, EList<Dependency>> _function = new Function1<ConfigOutputPort, EList<Dependency>>() {
      public EList<Dependency> apply(final ConfigOutputPort it) {
        return it.getOutgoingDependencies();
      }
    };
    final Function1<EList<Dependency>, Boolean> _function_1 = new Function1<EList<Dependency>, Boolean>() {
      public Boolean apply(final EList<Dependency> it) {
        boolean _isEmpty = it.isEmpty();
        return Boolean.valueOf((!_isEmpty));
      }
    };
    final Function1<EList<Dependency>, Dependency> _function_2 = new Function1<EList<Dependency>, Dependency>() {
      public Dependency apply(final EList<Dependency> it) {
        return it.get(0);
      }
    };
    final Function1<Dependency, ConfigInputPort> _function_3 = new Function1<Dependency, ConfigInputPort>() {
      public ConfigInputPort apply(final Dependency it) {
        return it.getGetter();
      }
    };
    final Function1<ConfigInputPort, Boolean> _function_4 = new Function1<ConfigInputPort, Boolean>() {
      public Boolean apply(final ConfigInputPort it) {
        return Boolean.valueOf(true);
      }
    };
    return IterableExtensions.<ConfigInputPort>exists(IterableExtensions.<Dependency, ConfigInputPort>map(IterableExtensions.<EList<Dependency>, Dependency>map(IterableExtensions.<EList<Dependency>>filter(XcoreEListExtensions.<ConfigOutputPort, EList<Dependency>>map(this.getConfigOutputPorts(), _function), _function_1), _function_2), _function_3), _function_4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isHierarchical() {
    return this.getRefinement().isHierarchical();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractActor getChildAbstractActor() {
    Refinement _refinement = this.getRefinement();
    boolean _tripleEquals = (_refinement == null);
    if (_tripleEquals) {
      return null;
    }
    else {
      return this.getRefinement().getAbstractActor();
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PiGraph getSubGraph() {
    boolean _isHierarchical = this.isHierarchical();
    if (_isHierarchical) {
      AbstractActor _childAbstractActor = this.getChildAbstractActor();
      return ((PiGraph) _childAbstractActor);
    }
    else {
      throw new UnsupportedOperationException("Cannot get the subgraph of a non hierarchical actor.");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.ACTOR__REFINEMENT:
        return basicSetRefinement(null, msgs);
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
      case PiMMPackage.ACTOR__REFINEMENT:
        return getRefinement();
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        return getMemoryScriptPath();
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
      case PiMMPackage.ACTOR__REFINEMENT:
        setRefinement((Refinement)newValue);
        return;
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        setMemoryScriptPath((IPath)newValue);
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
      case PiMMPackage.ACTOR__REFINEMENT:
        setRefinement((Refinement)null);
        return;
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        setMemoryScriptPath(MEMORY_SCRIPT_PATH_EDEFAULT);
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
      case PiMMPackage.ACTOR__REFINEMENT:
        return refinement != null;
      case PiMMPackage.ACTOR__MEMORY_SCRIPT_PATH:
        return MEMORY_SCRIPT_PATH_EDEFAULT == null ? memoryScriptPath != null : !MEMORY_SCRIPT_PATH_EDEFAULT.equals(memoryScriptPath);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString() {
    if (eIsProxy()) return super.toString();

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (memoryScriptPath: ");
    result.append(memoryScriptPath);
    result.append(')');
    return result.toString();
  }

} //ActorImpl
