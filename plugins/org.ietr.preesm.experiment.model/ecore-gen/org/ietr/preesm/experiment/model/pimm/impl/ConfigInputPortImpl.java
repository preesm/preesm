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
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Config Input Port</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.ConfigInputPortImpl#getIncomingDependency <em>Incoming Dependency</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConfigInputPortImpl extends PortImpl implements ConfigInputPort {
  /**
   * The cached value of the '{@link #getIncomingDependency() <em>Incoming Dependency</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getIncomingDependency()
   * @generated
   * @ordered
   */
  protected Dependency incomingDependency;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   */
  protected ConfigInputPortImpl() {
    super();
    this.kind = PiIdentifiers.CONFIGURATION_INPUT_PORT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.CONFIG_INPUT_PORT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the incoming dependency
   * @generated
   */
  @Override
  public Dependency getIncomingDependency() {
    if ((this.incomingDependency != null) && this.incomingDependency.eIsProxy()) {
      final InternalEObject oldIncomingDependency = (InternalEObject) this.incomingDependency;
      this.incomingDependency = (Dependency) eResolveProxy(oldIncomingDependency);
      if (this.incomingDependency != oldIncomingDependency) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, oldIncomingDependency,
              this.incomingDependency));
        }
      }
    }
    return this.incomingDependency;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the dependency
   * @generated
   */
  public Dependency basicGetIncomingDependency() {
    return this.incomingDependency;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newIncomingDependency
   *          the new incoming dependency
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetIncomingDependency(final Dependency newIncomingDependency, NotificationChain msgs) {
    final Dependency oldIncomingDependency = this.incomingDependency;
    this.incomingDependency = newIncomingDependency;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY,
          oldIncomingDependency, newIncomingDependency);
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
   * @param newIncomingDependency
   *          the new incoming dependency
   * @generated
   */
  @Override
  public void setIncomingDependency(final Dependency newIncomingDependency) {
    if (newIncomingDependency != this.incomingDependency) {
      NotificationChain msgs = null;
      if (this.incomingDependency != null) {
        msgs = ((InternalEObject) this.incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
      }
      if (newIncomingDependency != null) {
        msgs = ((InternalEObject) newIncomingDependency).eInverseAdd(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
      }
      msgs = basicSetIncomingDependency(newIncomingDependency, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, newIncomingDependency, newIncomingDependency));
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
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        if (this.incomingDependency != null) {
          msgs = ((InternalEObject) this.incomingDependency).eInverseRemove(this, PiMMPackage.DEPENDENCY__GETTER, Dependency.class, msgs);
        }
        return basicSetIncomingDependency((Dependency) otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
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
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        return basicSetIncomingDependency(null, msgs);
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
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        if (resolve) {
          return getIncomingDependency();
        }
        return basicGetIncomingDependency();
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
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        setIncomingDependency((Dependency) newValue);
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
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        setIncomingDependency((Dependency) null);
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
      case PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY:
        return this.incomingDependency != null;
    }
    return super.eIsSet(featureID);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.PortImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitConfigInputPort(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.PortImpl#getName()
   */
  @Override
  public String getName() {
    if ((this.name == null) && (this.incomingDependency.getSetter() instanceof Parameter)) {
      return ((Parameter) this.incomingDependency.getSetter()).getName();
    } else {
      return this.name;
    }
  }

} // ConfigInputPortImpl
