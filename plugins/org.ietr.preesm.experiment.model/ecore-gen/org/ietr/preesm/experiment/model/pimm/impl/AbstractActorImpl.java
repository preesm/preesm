/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Abstract Vertex</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataInputPorts <em>Data Input Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getDataOutputPorts <em>Data Output Ports</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#getConfigOutputPorts <em>Config Output Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AbstractActorImpl extends AbstractVertexImpl implements AbstractActor {
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected AbstractActorImpl() {
    super();
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return getDataInputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return getDataOutputPorts();
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return getConfigOutputPorts();
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return ((InternalEList<?>) getDataInputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return ((InternalEList<?>) getDataOutputPorts()).basicRemove(otherEnd, msgs);
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return ((InternalEList<?>) getConfigOutputPorts()).basicRemove(otherEnd, msgs);
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
      case PiMMPackage.ABSTRACT_ACTOR__DATA_INPUT_PORTS:
        return (this.dataInputPorts != null) && !this.dataInputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__DATA_OUTPUT_PORTS:
        return (this.dataOutputPorts != null) && !this.dataOutputPorts.isEmpty();
      case PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS:
        return (this.configOutputPorts != null) && !this.configOutputPorts.isEmpty();
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
    return PiMMPackage.Literals.ABSTRACT_ACTOR;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data input ports
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data output ports
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param featureID
   *          the feature ID
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
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config output ports
   * @generated
   */
  @Override
  public EList<ConfigOutputPort> getConfigOutputPorts() {
    if (this.configOutputPorts == null) {
      this.configOutputPorts = new EObjectContainmentEList<>(ConfigOutputPort.class, this, PiMMPackage.ABSTRACT_ACTOR__CONFIG_OUTPUT_PORTS);
    }
    return this.configOutputPorts;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl#getPortNamed(java.lang.String)
   */
  @Override
  public Port getPortNamed(final String portName) {
    // If the super method return a port, return it
    final Port p = super.getPortNamed(portName);
    if (p != null) {
      return p;
    }

    final List<Port> ports = new ArrayList<>(getDataInputPorts());

    ports.addAll(getDataOutputPorts());
    ports.addAll(getConfigOutputPorts());

    for (final Object port : ports) {
      final String name = ((Port) port).getName();
      if ((name == null) && (portName == null)) {
        return (Port) port;
      }
      if ((name != null) && name.equals(portName)) {
        return (Port) port;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.AbstractActor#getPath()
   */
  @Override
  public String getPath() {
    if ((this.eContainer != null) && (this.eContainer instanceof AbstractActor)) {
      return ((AbstractActor) this.eContainer).getPath() + "/" + getName();
    }
    return getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitAbstractActor(this);
  }

} // AbstractActoImpl
