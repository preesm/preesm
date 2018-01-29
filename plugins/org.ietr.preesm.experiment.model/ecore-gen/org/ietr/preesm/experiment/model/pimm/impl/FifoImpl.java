/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Optional;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.PiGraphException;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Fifo</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getSourcePort <em>Source Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getTargetPort <em>Target Port</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getDelay <em>Delay</em>}</li>
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected FifoImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.FIFO;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DataOutputPort basicGetSourcePort() {
    return this.sourcePort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DataInputPort basicGetTargetPort() {
    return this.targetPort;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Delay getDelay() {
    return this.delay;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setDelay(final Delay newDelay) {
    if (newDelay != this.delay) {
      NotificationChain msgs = null;
      if (this.delay != null) {
        msgs = ((InternalEObject) this.delay).eInverseRemove(this, PiMMPackage.DELAY__CONTAINING_FIFO, Delay.class, msgs);
      }
      if (newDelay != null) {
        msgs = ((InternalEObject) newDelay).eInverseAdd(this, PiMMPackage.DELAY__CONTAINING_FIFO, Delay.class, msgs);
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getType() {
    return this.type;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> Return a {@link String} composed as follow:<br>
   * "&ltSourceName&gt[.&ltSourcePortName&gt]-&ltTargetName&gt[.&ltTargetPortName&gt]" <br>
   * <br>
   * This ID should be unique since each {@link Port} can only have one {@link Fifo} connected to them. Moreover, a {@link Port} with no name is always the
   * unique data {@link Port} of its owner. <!-- end-user-doc --> <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getId() {
    return Optional.of(getSourcePort()).map(DataPort::getId).orElseThrow(() -> new PiGraphException("Fifo has no source port.")) + "-"
        + Optional.of(getTargetPort()).map(DataPort::getId).orElseThrow(() -> new PiGraphException("Fifo has no target port."));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
      case PiMMPackage.FIFO__DELAY:
        if (this.delay != null) {
          msgs = ((InternalEObject) this.delay).eInverseRemove(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.FIFO__DELAY, null, msgs);
        }
        return basicSetDelay((Delay) otherEnd, msgs);
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
      case PiMMPackage.FIFO__TYPE:
        return getType();
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
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
      case PiMMPackage.FIFO__TYPE:
        return FifoImpl.TYPE_EDEFAULT == null ? this.type != null : !FifoImpl.TYPE_EDEFAULT.equals(this.type);
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
    result.append(" (type: ");
    result.append(this.type);
    result.append(')');
    return result.toString();
  }

} // FifoImpl
