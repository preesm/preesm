/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Abstract Actor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataInputPorts <em>Data Input Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataOutputPorts <em>Data Output Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getConfigOutputPorts <em>Config Output Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getContainingGraph <em>Containing Graph</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AbstractActorImpl extends ConfigurableImpl implements AbstractActor {
  /**
   * The cached value of the '{@link #getDataInputPorts() <em>Data Input Ports</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getDataInputPorts()
   * @generated
   * @ordered
   */
  protected EList<DataInputPort> dataInputPorts;

  /**
   * The cached value of the '{@link #getDataOutputPorts() <em>Data Output Ports</em>}' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getDataOutputPorts()
   * @generated
   * @ordered
   */
  protected EList<DataOutputPort> dataOutputPorts;

  /**
   * The cached value of the '{@link #getConfigOutputPorts() <em>Config Output Ports</em>}' containment reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @see #getConfigOutputPorts()
   * @generated
   * @ordered
   */
  protected EList<ConfigOutputPort> configOutputPorts;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected AbstractActorImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.ABSTRACT_ACTOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<DataInputPort> getDataInputPorts() {
    if (this.dataInputPorts == null) {
      this.dataInputPorts = new EObjectContainmentEList<>(DataInputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS);
    }
    return this.dataInputPorts;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<DataOutputPort> getDataOutputPorts() {
    if (this.dataOutputPorts == null) {
      this.dataOutputPorts = new EObjectContainmentEList<>(DataOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS);
    }
    return this.dataOutputPorts;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<ConfigOutputPort> getConfigOutputPorts() {
    if (this.configOutputPorts == null) {
      this.configOutputPorts = new EObjectContainmentEList<>(ConfigOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS);
    }
    return this.configOutputPorts;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public PiGraph getContainingGraph() {
    if (eContainerFeatureID() != PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH) {
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
    msgs = eBasicSetContainer((InternalEObject) newContainingGraph, PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setContainingGraph(final PiGraph newContainingGraph) {
    if ((newContainingGraph != eInternalContainer())
        || ((eContainerFeatureID() != PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH) && (newContainingGraph != null))) {
      if (EcoreUtil.isAncestor(this, newContainingGraph)) {
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      }
      NotificationChain msgs = null;
      if (eInternalContainer() != null) {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      if (newContainingGraph != null) {
        msgs = ((InternalEObject) newContainingGraph).eInverseAdd(this, PiMMPackage.PI_GRAPH__ACTORS, PiGraph.class, msgs);
      }
      msgs = basicSetContainingGraph(newContainingGraph, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH, newContainingGraph, newContainingGraph));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<DataPort> getAllDataPorts() {
    final BasicEList<DataPort> result = ECollections.newBasicEList();
    result.addAll(getDataInputPorts());
    result.addAll(getDataOutputPorts());
    return ECollections.unmodifiableEList(result);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Port> getAllConfigPorts() {
    final BasicEList<Port> result = ECollections.newBasicEList();
    result.addAll(super.getAllConfigPorts());
    result.addAll(getConfigOutputPorts());
    return ECollections.unmodifiableEList(result);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EList<Port> getAllPorts() {
    final BasicEList<Port> result = ECollections.newBasicEList();
    result.addAll(getAllConfigPorts());
    result.addAll(getAllDataPorts());
    return ECollections.unmodifiableEList(result);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getActorPath() {
    if (getContainingGraph() != null) {
      return getContainingGraph().getActorPath() + "/" + getName();
    }
    return getName();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return ((InternalEList<?>) getDataInputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return ((InternalEList<?>) getDataOutputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return ((InternalEList<?>) getConfigOutputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
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
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.PI_GRAPH__ACTORS, PiGraph.class, msgs);
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return getDataInputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return getDataOutputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return getConfigOutputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        getDataInputPorts().clear();
        getDataInputPorts().addAll((Collection<? extends DataInputPort>) newValue);
        return;
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        getDataOutputPorts().clear();
        getDataOutputPorts().addAll((Collection<? extends DataOutputPort>) newValue);
        return;
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        getConfigOutputPorts().clear();
        getConfigOutputPorts().addAll((Collection<? extends ConfigOutputPort>) newValue);
        return;
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        getDataInputPorts().clear();
        return;
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        getDataOutputPorts().clear();
        return;
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        getConfigOutputPorts().clear();
        return;
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return (this.dataInputPorts != null) && !this.dataInputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return (this.dataOutputPorts != null) && !this.dataOutputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return (this.configOutputPorts != null) && !this.configOutputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__CONTAINING_GRAPH:
        return getContainingGraph() != null;
    }
    return super.eIsSet(featureID);
  }

} // AbstractActorImpl
