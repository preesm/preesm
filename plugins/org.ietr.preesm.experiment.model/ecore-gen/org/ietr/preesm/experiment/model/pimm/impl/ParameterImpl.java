/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import java.util.Objects;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getOutgoingDependencies <em>Outgoing Dependencies</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getValueExpression <em>Value Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ParameterImpl#getContainingGraph <em>Containing Graph</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterImpl extends ConfigurableImpl implements Parameter {
  /**
   * The cached value of the '{@link #getOutgoingDependencies() <em>Outgoing Dependencies</em>}' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getOutgoingDependencies()
   * @generated
   * @ordered
   */
  protected EList<Dependency> outgoingDependencies;

  /**
   * The cached value of the '{@link #getValueExpression() <em>Value Expression</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getValueExpression()
   * @generated
   * @ordered
   */
  protected Expression valueExpression;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected ParameterImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.PARAMETER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Dependency> getOutgoingDependencies() {
    if (this.outgoingDependencies == null) {
      this.outgoingDependencies = new EObjectWithInverseResolvingEList<>(Dependency.class, this, PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES,
          PiMMPackage.DEPENDENCY__SETTER);
    }
    return this.outgoingDependencies;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Expression getValueExpression() {
    return this.valueExpression;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetValueExpression(final Expression newValueExpression, NotificationChain msgs) {
    final Expression oldValueExpression = this.valueExpression;
    this.valueExpression = newValueExpression;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__VALUE_EXPRESSION, oldValueExpression,
          newValueExpression);
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
  public void setValueExpression(final Expression newValueExpression) {
    if (newValueExpression != this.valueExpression) {
      NotificationChain msgs = null;
      if (this.valueExpression != null) {
        msgs = ((InternalEObject) this.valueExpression).eInverseRemove(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__VALUE_EXPRESSION,
            null, msgs);
      }
      if (newValueExpression != null) {
        msgs = ((InternalEObject) newValueExpression).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.PARAMETER__VALUE_EXPRESSION, null,
            msgs);
      }
      msgs = basicSetValueExpression(newValueExpression, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__VALUE_EXPRESSION, newValueExpression, newValueExpression));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PiGraph getContainingGraph() {
    if (eContainerFeatureID() != PiMMPackage.PARAMETER__CONTAINING_GRAPH) {
      return null;
    }
    return (PiGraph) eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetContainingGraph(final PiGraph newContainingGraph, NotificationChain msgs) {
    msgs = eBasicSetContainer((InternalEObject) newContainingGraph, PiMMPackage.PARAMETER__CONTAINING_GRAPH, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setContainingGraph(final PiGraph newContainingGraph) {
    if ((newContainingGraph != eInternalContainer()) || ((eContainerFeatureID() != PiMMPackage.PARAMETER__CONTAINING_GRAPH) && (newContainingGraph != null))) {
      if (EcoreUtil.isAncestor(this, newContainingGraph)) {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null) {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newContainingGraph != null) {
        msgs = ((InternalEObject) newContainingGraph).eInverseAdd(this, PiMMPackage.PI_GRAPH__PARAMETERS, PiGraph.class, msgs);
      }
      msgs = basicSetContainingGraph(newContainingGraph, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.PARAMETER__CONTAINING_GRAPH, newContainingGraph, newContainingGraph));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isLocallyStatic() {
    // a parameter is static if all its setters are static (or it has no setter)
    return getConfigInputPorts().stream().filter(Objects::nonNull).map(ConfigInputPort::getIncomingDependency).filter(Objects::nonNull)
        .map(Dependency::getSetter).filter(Objects::nonNull).allMatch(ISetter::isLocallyStatic);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isDependent() {
    return !getConfigInputPorts().isEmpty();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public boolean isConfigurationInterface() {
    return false;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return ((InternalEList<InternalEObject>) (InternalEList<?>) getOutgoingDependencies()).basicAdd(otherEnd, msgs);
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        if (eInternalContainer() != null) {
          msgs = eBasicRemoveFromContainer(msgs);
        }
        return basicSetContainingGraph((PiGraph) otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return ((InternalEList<?>) getOutgoingDependencies()).basicRemove(otherEnd, msgs);
      case PiMMPackage.PARAMETER__VALUE_EXPRESSION:
        return basicSetValueExpression(null, msgs);
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        return basicSetContainingGraph(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(final NotificationChain msgs) {
    switch (eContainerFeatureID()) {
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.PI_GRAPH__PARAMETERS, PiGraph.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return getOutgoingDependencies();
      case PiMMPackage.PARAMETER__VALUE_EXPRESSION:
        return getValueExpression();
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        return getContainingGraph();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        getOutgoingDependencies().addAll((Collection<? extends Dependency>) newValue);
        return;
      case PiMMPackage.PARAMETER__VALUE_EXPRESSION:
        setValueExpression((Expression) newValue);
        return;
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        setContainingGraph((PiGraph) newValue);
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
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        getOutgoingDependencies().clear();
        return;
      case PiMMPackage.PARAMETER__VALUE_EXPRESSION:
        setValueExpression((Expression) null);
        return;
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        setContainingGraph((PiGraph) null);
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
      case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
        return (this.outgoingDependencies != null) && !this.outgoingDependencies.isEmpty();
      case PiMMPackage.PARAMETER__VALUE_EXPRESSION:
        return this.valueExpression != null;
      case PiMMPackage.PARAMETER__CONTAINING_GRAPH:
        return getContainingGraph() != null;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(final int derivedFeatureID, final Class<?> baseClass) {
    if (baseClass == ISetter.class) {
      switch (derivedFeatureID) {
        case PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES:
          return PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES;
        default:
          return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(final int baseFeatureID, final Class<?> baseClass) {
    if (baseClass == ISetter.class) {
      switch (baseFeatureID) {
        case PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES:
          return PiMMPackage.PARAMETER__OUTGOING_DEPENDENCIES;
        default:
          return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ParameterImpl
