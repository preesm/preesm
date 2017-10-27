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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.InterfaceActor;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Interface Vertex</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl#getGraphPort <em>Graph Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.InterfaceActorImpl#getKind <em>Kind</em>}</li>
 * </ul>
 *
 * @generated
 */
public class InterfaceActorImpl extends AbstractActorImpl implements InterfaceActor {
  /**
   * The cached value of the '{@link #getGraphPort() <em>Graph Port</em>}' reference. <!-- begin-user-doc --> This {@link Port} is the corresponding
   * {@link Port} of the {@link PiGraph} containing this {@link Interface} instance. <!-- end-user-doc -->
   *
   * @see #getGraphPort()
   * @generated
   * @ordered
   */
  protected Port graphPort;

  /**
   * The default value of the '{@link #getKind() <em>Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getKind()
   * @generated
   * @ordered
   */
  protected static final String KIND_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getKind() <em>Kind</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getKind()
   * @generated
   * @ordered
   */
  protected String kind = InterfaceActorImpl.KIND_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected InterfaceActorImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the port
   * @generated
   */
  public Port basicGetGraphPort() {
    return this.graphPort;
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
      case PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT:
        if (resolve) {
          return getGraphPort();
        }
        return basicGetGraphPort();
      case PiMMPackage.INTERFACE_ACTOR__KIND:
        return getKind();
    }
    return super.eGet(featureID, resolve, coreType);
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
      case PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT:
        return this.graphPort != null;
      case PiMMPackage.INTERFACE_ACTOR__KIND:
        return InterfaceActorImpl.KIND_EDEFAULT == null ? this.kind != null : !InterfaceActorImpl.KIND_EDEFAULT.equals(this.kind);
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
  @Override
  public void eSet(final int featureID, final Object newValue) {
    switch (featureID) {
      case PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT:
        setGraphPort((Port) newValue);
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
    return PiMMPackage.Literals.INTERFACE_ACTOR;
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
      case PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT:
        setGraphPort((Port) null);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the graph port
   * @generated
   */
  @Override
  public Port getGraphPort() {
    if ((this.graphPort != null) && this.graphPort.eIsProxy()) {
      final InternalEObject oldGraphPort = (InternalEObject) this.graphPort;
      this.graphPort = (Port) eResolveProxy(oldGraphPort);
      if (this.graphPort != oldGraphPort) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT, oldGraphPort, this.graphPort));
        }
      }
    }
    return this.graphPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the kind
   * @generated
   */
  @Override
  public String getKind() {
    return this.kind;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public DataPort getDataPort() {
    // Data in/out interfaces have only one data port
    return getAllDataPorts().get(0);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newGraphPort
   *          the new graph port
   * @generated
   */
  @Override
  public void setGraphPort(final Port newGraphPort) {
    final Port oldGraphPort = this.graphPort;
    this.graphPort = newGraphPort;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.INTERFACE_ACTOR__GRAPH_PORT, oldGraphPort, this.graphPort));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractVertexImpl#setName(java.lang.String)
   */
  @Override
  public void setName(final String newName) {
    super.setName(newName);
    if (getGraphPort() != null) {
      getGraphPort().setName(newName);
    }
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
    result.append(" (kind: ");
    result.append(this.kind);
    result.append(')');
    return result.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.AbstractActorImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitInterfaceActor(this);
  }

} // InterfaceVertexImpl
