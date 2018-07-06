/**
 */
package org.ietr.preesm.experiment.model.pimm.impl;

import java.util.Optional;

import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.ietr.preesm.experiment.model.PiGraphException;

import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fifo</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getSourcePort <em>Source Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getTargetPort <em>Target Port</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getDelay <em>Delay</em>}</li>
 *   <li>{@link org.ietr.preesm.experiment.model.pimm.impl.FifoImpl#getType <em>Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FifoImpl extends EdgeImpl implements Fifo {
  /**
   * The cached value of the '{@link #getSourcePort() <em>Source Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSourcePort()
   * @generated
   * @ordered
   */
  protected DataOutputPort sourcePort;

  /**
   * The cached value of the '{@link #getTargetPort() <em>Target Port</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTargetPort()
   * @generated
   * @ordered
   */
  protected DataInputPort targetPort;

  /**
   * The cached value of the '{@link #getDelay() <em>Delay</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDelay()
   * @generated
   * @ordered
   */
  protected Delay delay;

  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final String TYPE_EDEFAULT = "void";

  /**
   * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected String type = TYPE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FifoImpl() {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass() {
    return PiMMPackage.Literals.FIFO;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataOutputPort getSourcePort() {
    if (sourcePort != null && sourcePort.eIsProxy()) {
      InternalEObject oldSourcePort = (InternalEObject)sourcePort;
      sourcePort = (DataOutputPort)eResolveProxy(oldSourcePort);
      if (sourcePort != oldSourcePort) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.FIFO__SOURCE_PORT, oldSourcePort, sourcePort));
      }
    }
    return sourcePort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataOutputPort basicGetSourcePort() {
    return sourcePort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSourcePort(DataOutputPort newSourcePort, NotificationChain msgs) {
    DataOutputPort oldSourcePort = sourcePort;
    sourcePort = newSourcePort;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__SOURCE_PORT, oldSourcePort, newSourcePort);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSourcePort(DataOutputPort newSourcePort) {
    if (newSourcePort != sourcePort) {
      NotificationChain msgs = null;
      if (sourcePort != null)
        msgs = ((InternalEObject)sourcePort).eInverseRemove(this, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, DataOutputPort.class, msgs);
      if (newSourcePort != null)
        msgs = ((InternalEObject)newSourcePort).eInverseAdd(this, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, DataOutputPort.class, msgs);
      msgs = basicSetSourcePort(newSourcePort, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__SOURCE_PORT, newSourcePort, newSourcePort));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataInputPort getTargetPort() {
    if (targetPort != null && targetPort.eIsProxy()) {
      InternalEObject oldTargetPort = (InternalEObject)targetPort;
      targetPort = (DataInputPort)eResolveProxy(oldTargetPort);
      if (targetPort != oldTargetPort) {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.FIFO__TARGET_PORT, oldTargetPort, targetPort));
      }
    }
    return targetPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataInputPort basicGetTargetPort() {
    return targetPort;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetTargetPort(DataInputPort newTargetPort, NotificationChain msgs) {
    DataInputPort oldTargetPort = targetPort;
    targetPort = newTargetPort;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__TARGET_PORT, oldTargetPort, newTargetPort);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setTargetPort(DataInputPort newTargetPort) {
    if (newTargetPort != targetPort) {
      NotificationChain msgs = null;
      if (targetPort != null)
        msgs = ((InternalEObject)targetPort).eInverseRemove(this, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, DataInputPort.class, msgs);
      if (newTargetPort != null)
        msgs = ((InternalEObject)newTargetPort).eInverseAdd(this, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, DataInputPort.class, msgs);
      msgs = basicSetTargetPort(newTargetPort, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__TARGET_PORT, newTargetPort, newTargetPort));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Delay getDelay() {
    return delay;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetDelay(Delay newDelay, NotificationChain msgs) {
    Delay oldDelay = delay;
    delay = newDelay;
    if (eNotificationRequired()) {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__DELAY, oldDelay, newDelay);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDelay(Delay newDelay) {
    if (newDelay != delay) {
      NotificationChain msgs = null;
      if (delay != null)
        msgs = ((InternalEObject)delay).eInverseRemove(this, PiMMPackage.DELAY__CONTAINING_FIFO, Delay.class, msgs);
      if (newDelay != null)
        msgs = ((InternalEObject)newDelay).eInverseAdd(this, PiMMPackage.DELAY__CONTAINING_FIFO, Delay.class, msgs);
      msgs = basicSetDelay(newDelay, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__DELAY, newDelay, newDelay));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getType() {
    return type;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setType(String newType) {
    String oldType = type;
    type = newType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.FIFO__TYPE, oldType, type));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getId() {
    try {
      final Function<DataOutputPort, String> _function = new Function<DataOutputPort, String>() {
        public String apply(final DataOutputPort it) {
          return it.getId();
        }
      };
      final Supplier<PiGraphException> _function_1 = new Supplier<PiGraphException>() {
        public PiGraphException get() {
          return new PiGraphException("Fifo has no source port.");
        }
      };
      String _orElseThrow = Optional.<DataOutputPort>ofNullable(this.getSourcePort()).<String>map(_function).<PiGraphException>orElseThrow(_function_1);
      String _plus = (_orElseThrow + 
        "-");
      final Function<DataInputPort, String> _function_2 = new Function<DataInputPort, String>() {
        public String apply(final DataInputPort it) {
          return it.getId();
        }
      };
      final Supplier<PiGraphException> _function_3 = new Supplier<PiGraphException>() {
        public PiGraphException get() {
          return new PiGraphException("Fifo has no target port.");
        }
      };
      String _orElseThrow_1 = Optional.<DataInputPort>ofNullable(this.getTargetPort()).<String>map(_function_2).<PiGraphException>orElseThrow(_function_3);
      return (_plus + _orElseThrow_1);
    }
    catch (Throwable _e) {
      throw org.eclipse.xtext.xbase.lib.Exceptions.sneakyThrow(_e);
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.FIFO__SOURCE_PORT:
        if (sourcePort != null)
          msgs = ((InternalEObject)sourcePort).eInverseRemove(this, PiMMPackage.DATA_OUTPUT_PORT__OUTGOING_FIFO, DataOutputPort.class, msgs);
        return basicSetSourcePort((DataOutputPort)otherEnd, msgs);
      case PiMMPackage.FIFO__TARGET_PORT:
        if (targetPort != null)
          msgs = ((InternalEObject)targetPort).eInverseRemove(this, PiMMPackage.DATA_INPUT_PORT__INCOMING_FIFO, DataInputPort.class, msgs);
        return basicSetTargetPort((DataInputPort)otherEnd, msgs);
      case PiMMPackage.FIFO__DELAY:
        if (delay != null)
          msgs = ((InternalEObject)delay).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - PiMMPackage.FIFO__DELAY, null, msgs);
        return basicSetDelay((Delay)otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType) {
    switch (featureID) {
      case PiMMPackage.FIFO__SOURCE_PORT:
        if (resolve) return getSourcePort();
        return basicGetSourcePort();
      case PiMMPackage.FIFO__TARGET_PORT:
        if (resolve) return getTargetPort();
        return basicGetTargetPort();
      case PiMMPackage.FIFO__DELAY:
        return getDelay();
      case PiMMPackage.FIFO__TYPE:
        return getType();
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        setSourcePort((DataOutputPort)newValue);
        return;
      case PiMMPackage.FIFO__TARGET_PORT:
        setTargetPort((DataInputPort)newValue);
        return;
      case PiMMPackage.FIFO__DELAY:
        setDelay((Delay)newValue);
        return;
      case PiMMPackage.FIFO__TYPE:
        setType((String)newValue);
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        setSourcePort((DataOutputPort)null);
        return;
      case PiMMPackage.FIFO__TARGET_PORT:
        setTargetPort((DataInputPort)null);
        return;
      case PiMMPackage.FIFO__DELAY:
        setDelay((Delay)null);
        return;
      case PiMMPackage.FIFO__TYPE:
        setType(TYPE_EDEFAULT);
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
      case PiMMPackage.FIFO__SOURCE_PORT:
        return sourcePort != null;
      case PiMMPackage.FIFO__TARGET_PORT:
        return targetPort != null;
      case PiMMPackage.FIFO__DELAY:
        return delay != null;
      case PiMMPackage.FIFO__TYPE:
        return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
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
    result.append(" (type: ");
    result.append(type);
    result.append(')');
    return result.toString();
  }

} //FifoImpl
