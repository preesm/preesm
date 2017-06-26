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
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.Dependency;
import org.ietr.preesm.experiment.model.pimm.ISetter;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dependency</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getSetter <em>Setter</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DependencyImpl#getGetter <em>Getter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependencyImpl extends EObjectImpl implements Dependency {
  /**
   * The cached value of the '{@link #getSetter() <em>Setter</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSetter()
   * @generated
   * @ordered
   */
  protected ISetter setter;

  /**
   * The cached value of the '{@link #getGetter() <em>Getter</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getGetter()
   * @generated
   * @ordered
   */
  protected ConfigInputPort getter;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected DependencyImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.DEPENDENCY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the setter
   * @generated
   */
  @Override
  public ISetter getSetter() {
    if ((this.setter != null) && this.setter.eIsProxy()) {
      final InternalEObject oldSetter = (InternalEObject) this.setter;
      this.setter = (ISetter) eResolveProxy(oldSetter);
      if (this.setter != oldSetter) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__SETTER, oldSetter, this.setter));
        }
      }
    }
    return this.setter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the i setter
   * @generated
   */
  public ISetter basicGetSetter() {
    return this.setter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSetter
   *          the new setter
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetSetter(final ISetter newSetter, NotificationChain msgs) {
    final ISetter oldSetter = this.setter;
    this.setter = newSetter;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, oldSetter, newSetter);
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
   * @param newSetter
   *          the new setter
   * @generated
   */
  @Override
  public void setSetter(final ISetter newSetter) {
    if (newSetter != this.setter) {
      NotificationChain msgs = null;
      if (this.setter != null) {
        msgs = ((InternalEObject) this.setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
      }
      if (newSetter != null) {
        msgs = ((InternalEObject) newSetter).eInverseAdd(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
      }
      msgs = basicSetSetter(newSetter, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__SETTER, newSetter, newSetter));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the getter
   * @generated
   */
  @Override
  public ConfigInputPort getGetter() {
    if ((this.getter != null) && this.getter.eIsProxy()) {
      final InternalEObject oldGetter = (InternalEObject) this.getter;
      this.getter = (ConfigInputPort) eResolveProxy(oldGetter);
      if (this.getter != oldGetter) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DEPENDENCY__GETTER, oldGetter, this.getter));
        }
      }
    }
    return this.getter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the config input port
   * @generated
   */
  public ConfigInputPort basicGetGetter() {
    return this.getter;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newGetter
   *          the new getter
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetGetter(final ConfigInputPort newGetter, NotificationChain msgs) {
    final ConfigInputPort oldGetter = this.getter;
    this.getter = newGetter;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, oldGetter, newGetter);
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
   * @param newGetter
   *          the new getter
   * @generated
   */
  @Override
  public void setGetter(final ConfigInputPort newGetter) {
    if (newGetter != this.getter) {
      NotificationChain msgs = null;
      if (this.getter != null) {
        msgs = ((InternalEObject) this.getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
      }
      if (newGetter != null) {
        msgs = ((InternalEObject) newGetter).eInverseAdd(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
      }
      msgs = basicSetGetter(newGetter, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DEPENDENCY__GETTER, newGetter, newGetter));
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
      case PiMMPackage.DEPENDENCY__SETTER:
        if (this.setter != null) {
          msgs = ((InternalEObject) this.setter).eInverseRemove(this, PiMMPackage.ISETTER__OUTGOING_DEPENDENCIES, ISetter.class, msgs);
        }
        return basicSetSetter((ISetter) otherEnd, msgs);
      case PiMMPackage.DEPENDENCY__GETTER:
        if (this.getter != null) {
          msgs = ((InternalEObject) this.getter).eInverseRemove(this, PiMMPackage.CONFIG_INPUT_PORT__INCOMING_DEPENDENCY, ConfigInputPort.class, msgs);
        }
        return basicSetGetter((ConfigInputPort) otherEnd, msgs);
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
      case PiMMPackage.DEPENDENCY__SETTER:
        return basicSetSetter(null, msgs);
      case PiMMPackage.DEPENDENCY__GETTER:
        return basicSetGetter(null, msgs);
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
      case PiMMPackage.DEPENDENCY__SETTER:
        if (resolve) {
          return getSetter();
        }
        return basicGetSetter();
      case PiMMPackage.DEPENDENCY__GETTER:
        if (resolve) {
          return getGetter();
        }
        return basicGetGetter();
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
      case PiMMPackage.DEPENDENCY__SETTER:
        setSetter((ISetter) newValue);
        return;
      case PiMMPackage.DEPENDENCY__GETTER:
        setGetter((ConfigInputPort) newValue);
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
      case PiMMPackage.DEPENDENCY__SETTER:
        setSetter((ISetter) null);
        return;
      case PiMMPackage.DEPENDENCY__GETTER:
        setGetter((ConfigInputPort) null);
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
      case PiMMPackage.DEPENDENCY__SETTER:
        return this.setter != null;
      case PiMMPackage.DEPENDENCY__GETTER:
        return this.getter != null;
    }
    return super.eIsSet(featureID);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitDependency(this);
  }

} // DependencyImpl
