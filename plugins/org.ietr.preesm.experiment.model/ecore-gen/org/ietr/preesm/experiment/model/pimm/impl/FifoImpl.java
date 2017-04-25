/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012 - 2015)
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
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.Port;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object ' <em><b>Fifo</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getSourcePort <em>Source Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getTargetPort <em>Target Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getDelay <em>Delay</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getId <em>Id</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getType <em>Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FifoImpl extends EObjectImpl implements Fifo {
  /**
   * The cached value of the '{@link #getSourcePort() <em>Source Port</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getSourcePort()
   * @generated
   * @ordered
   */
  protected DataOutputPort sourcePort;

  /**
   * The cached value of the '{@link #getTargetPort() <em>Target Port</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getTargetPort()
   * @generated
   * @ordered
   */
  protected DataInputPort targetPort;

  /**
   * The cached value of the '{@link #getDelay() <em>Delay</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDelay()
   * @generated
   * @ordered
   */
  protected Delay delay;

  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final String ID_EDEFAULT = null;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final String TYPE_EDEFAULT = "void";

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getType()
   * @generated
   * @ordered
   */
  protected String type = FifoImpl.TYPE_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected FifoImpl() {
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
    return PiMMPackage.Literals.FIFO;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the source port
   * @generated
   */
  @Override
  public DataOutputPort getSourcePort() {
    if ((this.sourcePort != null) && this.sourcePort.eIsProxy()) {
      final InternalEObject oldSourcePort = (InternalEObject) this.sourcePort;
      this.sourcePort = (DataOutputPort) eResolveProxy(oldSourcePort);
      if (this.sourcePort != oldSourcePort) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.FIFO__SOURCE_PORT, oldSourcePort, this.sourcePort));
        }
      }
    }
    return this.sourcePort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data output port
   * @generated
   */
  public DataOutputPort basicGetSourcePort() {
    return this.sourcePort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newSourcePort
   *          the new source port
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetSourcePort(final DataOutputPort newSourcePort, NotificationChain msgs) {
    final DataOutputPort oldSourcePort = this.sourcePort;
    this.sourcePort = newSourcePort;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__SOURCE_PORT, oldSourcePort, newSourcePort);
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
   * @param newSourcePort
   *          the new source port
   * @generated
   */
  @Override
  public void setSourcePort(final DataOutputPort newSourcePort) {
    if (newSourcePort != this.sourcePort) {
      NotificationChain msgs = null;
      if (this.sourcePort != null) {
        msgs = ((InternalEObject) this.sourcePort).eInverseRemove(this, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, DataOutputPort.class, msgs);
      }
      if (newSourcePort != null) {
        msgs = ((InternalEObject) newSourcePort).eInverseAdd(this, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, DataOutputPort.class, msgs);
      }
      msgs = basicSetSourcePort(newSourcePort, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__SOURCE_PORT, newSourcePort, newSourcePort));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the target port
   * @generated
   */
  @Override
  public DataInputPort getTargetPort() {
    if ((this.targetPort != null) && this.targetPort.eIsProxy()) {
      final InternalEObject oldTargetPort = (InternalEObject) this.targetPort;
      this.targetPort = (DataInputPort) eResolveProxy(oldTargetPort);
      if (this.targetPort != oldTargetPort) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.FIFO__TARGET_PORT, oldTargetPort, this.targetPort));
        }
      }
    }
    return this.targetPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the data input port
   * @generated
   */
  public DataInputPort basicGetTargetPort() {
    return this.targetPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newTargetPort
   *          the new target port
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetTargetPort(final DataInputPort newTargetPort, NotificationChain msgs) {
    final DataInputPort oldTargetPort = this.targetPort;
    this.targetPort = newTargetPort;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__TARGET_PORT, oldTargetPort, newTargetPort);
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
   * @param newTargetPort
   *          the new target port
   * @generated
   */
  @Override
  public void setTargetPort(final DataInputPort newTargetPort) {
    if (newTargetPort != this.targetPort) {
      NotificationChain msgs = null;
      if (this.targetPort != null) {
        msgs = ((InternalEObject) this.targetPort).eInverseRemove(this, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, DataInputPort.class, msgs);
      }
      if (newTargetPort != null) {
        msgs = ((InternalEObject) newTargetPort).eInverseAdd(this, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, DataInputPort.class, msgs);
      }
      msgs = basicSetTargetPort(newTargetPort, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__TARGET_PORT, newTargetPort, newTargetPort));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the delay
   * @generated
   */
  @Override
  public Delay getDelay() {
    return this.delay;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newDelay
   *          the new delay
   * @param msgs
   *          the msgs
   * @return the notification chain
   * @generated
   */
  public NotificationChain basicSetDelay(final Delay newDelay, NotificationChain msgs) {
    final Delay oldDelay = this.delay;
    this.delay = newDelay;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__DELAY, oldDelay, newDelay);
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
   * @param newDelay
   *          the new delay
   * @generated
   */
  @Override
  public void setDelay(final Delay newDelay) {
    if (newDelay != this.delay) {
      NotificationChain msgs = null;
      if (this.delay != null) {
        msgs = ((InternalEObject) this.delay).eInverseRemove(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.FIFO__DELAY, null, msgs);
      }
      if (newDelay != null) {
        msgs = ((InternalEObject) newDelay).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.FIFO__DELAY, null, msgs);
      }
      msgs = basicSetDelay(newDelay, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__DELAY, newDelay, newDelay));
    }
  }

  /**
   * <!-- begin-user-doc --> Return a {@link String} composed as follow:<br>
   * "&ltSourceName&gt[.&ltSourcePortName&gt]-&ltTargetName&gt[.&ltTargetPortName&gt]" <br>
   * <br>
   * This ID should be unique since each {@link Port} can only have one {@link Fifo} connected to them. Moreover, a {@link Port} with no name is always the
   * unique data {@link Port} of its owner. <!-- end-user-doc -->
   *
   * @return the id
   */
  @Override
  public String getId() {

    final Port srcPort = getSourcePort();
    final Port tgtPort = getTargetPort();

    if ((srcPort == null) || (tgtPort == null)) {
      throw new RuntimeException("Fifo has no source or no target port.");
    }

    final AbstractActor src = (AbstractActor) srcPort.eContainer();
    final AbstractActor tgt = (AbstractActor) tgtPort.eContainer();

    String id = src.getName();
    if ((srcPort.getName() != null) && !srcPort.getName().isEmpty()) {
      id += "." + srcPort.getName();
    }
    id += "-" + tgt.getName();
    if ((tgtPort.getName() != null) && !tgtPort.getName().isEmpty()) {
      id += "." + tgtPort.getName();
    }

    return id;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return true, if is sets the id
   * @generated
   */
  @Override
  public boolean isSetId() {
    // TODO: implement this method to return whether the 'Id' attribute is set
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the type
   * @generated
   */
  @Override
  public String getType() {
    return this.type;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newType
   *          the new type
   * @generated
   */
  @Override
  public void setType(final String newType) {
    final String oldType = this.type;
    this.type = newType;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__TYPE, oldType, this.type));
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        if (this.sourcePort != null) {
          msgs = ((InternalEObject) this.sourcePort).eInverseRemove(this, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, DataOutputPort.class, msgs);
        }
        return basicSetSourcePort((DataOutputPort) otherEnd, msgs);
      case PiMMPackage.FIFO__TARGET_PORT:
        if (this.targetPort != null) {
          msgs = ((InternalEObject) this.targetPort).eInverseRemove(this, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, DataInputPort.class, msgs);
        }
        return basicSetTargetPort((DataInputPort) otherEnd, msgs);
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        return basicSetSourcePort(null, msgs);
      case PiMMPackage.FIFO__TARGET_PORT:
        return basicSetTargetPort(null, msgs);
      case PiMMPackage.FIFO__DELAY:
        return basicSetDelay(null, msgs);
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        if (resolve) {
          return getSourcePort();
        }
        return basicGetSourcePort();
      case PiMMPackage.FIFO__TARGET_PORT:
        if (resolve) {
          return getTargetPort();
        }
        return basicGetTargetPort();
      case PiMMPackage.FIFO__DELAY:
        return getDelay();
      case PiMMPackage.FIFO__ID:
        return getId();
      case PiMMPackage.FIFO__TYPE:
        return getType();
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        setSourcePort((DataOutputPort) newValue);
        return;
      case PiMMPackage.FIFO__TARGET_PORT:
        setTargetPort((DataInputPort) newValue);
        return;
      case PiMMPackage.FIFO__DELAY:
        setDelay((Delay) newValue);
        return;
      case PiMMPackage.FIFO__TYPE:
        setType((String) newValue);
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        setSourcePort((DataOutputPort) null);
        return;
      case PiMMPackage.FIFO__TARGET_PORT:
        setTargetPort((DataInputPort) null);
        return;
      case PiMMPackage.FIFO__DELAY:
        setDelay((Delay) null);
        return;
      case PiMMPackage.FIFO__TYPE:
        setType(FifoImpl.TYPE_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * Two {@link Fifo} are equals if they have the same {@link #getId()}.
   *
   * @param obj
   *          the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Fifo) {
      try {
        return getId().equals(((Fifo) obj).getId());
      } catch (final RuntimeException e) {
        return super.equals(obj);
      }
    } else {
      return false;
    }
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        return this.sourcePort != null;
      case PiMMPackage.FIFO__TARGET_PORT:
        return this.targetPort != null;
      case PiMMPackage.FIFO__DELAY:
        return this.delay != null;
      case PiMMPackage.FIFO__ID:
        return isSetId();
      case PiMMPackage.FIFO__TYPE:
        return FifoImpl.TYPE_EDEFAULT == null ? this.type != null : !FifoImpl.TYPE_EDEFAULT.equals(this.type);
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
    result.append(" (type: ");
    result.append(this.type);
    result.append(')');
    return result.toString();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.util.PiMMVisitable#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitFifo(this);
  }

} // FifoImpl
