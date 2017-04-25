/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
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
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputInterface;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.util.PiIdentifiers;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Input Port</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataInputPortImpl#getIncomingFifo <em>Incoming Fifo</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataInputPortImpl extends DataPortImpl implements DataInputPort {
  /**
   * The cached value of the '{@link #getIncomingFifo() <em>Incoming Fifo</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getIncomingFifo()
   * @generated
   * @ordered
   */
  protected Fifo incomingFifo;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   */
  protected DataInputPortImpl() {
    super();
    this.kind = PiIdentifiers.DATA_INPUT_PORT;

    setExpression(PiMMFactory.eINSTANCE.createExpression());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the e class
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.DATA_INPUT_PORT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the incoming fifo
   * @generated
   */
  @Override
  public Fifo getIncomingFifo() {
    if ((this.incomingFifo != null) && this.incomingFifo.eIsProxy()) {
      final InternalEObject oldIncomingFifo = (InternalEObject) this.incomingFifo;
      this.incomingFifo = (Fifo) eResolveProxy(oldIncomingFifo);
      if (this.incomingFifo != oldIncomingFifo) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, oldIncomingFifo, this.incomingFifo));
        }
      }
    }
    return this.incomingFifo;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the fifo
   * @generated
   */
  public Fifo basicGetIncomingFifo() {
    return this.incomingFifo;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newIncomingFifo
   *          the new incoming fifo
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetIncomingFifo(final Fifo newIncomingFifo, NotificationChain msgs) {
    final Fifo oldIncomingFifo = this.incomingFifo;
    this.incomingFifo = newIncomingFifo;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, oldIncomingFifo,
          newIncomingFifo);
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
   * @param newIncomingFifo
   *          the new incoming fifo
   * @generated
   */
  @Override
  public void setIncomingFifo(final Fifo newIncomingFifo) {
    if (newIncomingFifo != this.incomingFifo) {
      NotificationChain msgs = null;
      if (this.incomingFifo != null) {
        msgs = ((InternalEObject) this.incomingFifo).eInverseRemove(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
      }
      if (newIncomingFifo != null) {
        msgs = ((InternalEObject) newIncomingFifo).eInverseAdd(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
      }
      msgs = basicSetIncomingFifo(newIncomingFifo, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, newIncomingFifo, newIncomingFifo));
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
      case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
        if (this.incomingFifo != null) {
          msgs = ((InternalEObject) this.incomingFifo).eInverseRemove(this, PiMMPackage.FIFO__TARGET_PORT, Fifo.class, msgs);
        }
        return basicSetIncomingFifo((Fifo) otherEnd, msgs);
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
      case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
        return basicSetIncomingFifo(null, msgs);
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
      case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
        if (resolve) {
          return getIncomingFifo();
        }
        return basicGetIncomingFifo();
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
      case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
        setIncomingFifo((Fifo) newValue);
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
      case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
        setIncomingFifo((Fifo) null);
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
      case PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO:
        return this.incomingFifo != null;
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
    v.visitDataInputPort(this);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.PortImpl#getName()
   */
  @Override
  public String getName() {
    String name = super.getName();
    if ((name == null) && (this.eContainer instanceof DataOutputInterface)) {
      name = ((DataOutputInterface) this.eContainer).getName();
    }
    return name;
  }

} // InputPortImpl
