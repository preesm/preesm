/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
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
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.DataPort;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.PortMemoryAnnotation;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Data Port</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl#getPortRateExpression <em>Port Rate Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DataPortImpl#getAnnotation <em>Annotation</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class DataPortImpl extends PortImpl implements DataPort {
  /**
   * The cached value of the '{@link #getPortRateExpression() <em>Port Rate Expression</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getPortRateExpression()
   * @generated
   * @ordered
   */
  protected Expression portRateExpression;

  /**
   * The default value of the '{@link #getAnnotation() <em>Annotation</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getAnnotation()
   * @generated
   * @ordered
   */
  protected static final PortMemoryAnnotation ANNOTATION_EDEFAULT = PortMemoryAnnotation.NONE;

  /**
   * The cached value of the '{@link #getAnnotation() <em>Annotation</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getAnnotation()
   * @generated
   * @ordered
   */
  protected PortMemoryAnnotation annotation = DataPortImpl.ANNOTATION_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected DataPortImpl() {
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
    return PiMMPackage.Literals.DATA_PORT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the expression
   * @generated
   */
  @Override
  public Expression getPortRateExpression() {
    return this.portRateExpression;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetPortRateExpression(final Expression newPortRateExpression, NotificationChain msgs) {
    final Expression oldPortRateExpression = this.portRateExpression;
    this.portRateExpression = newPortRateExpression;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION, oldPortRateExpression,
          newPortRateExpression);
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
   * @param newExpression
   *          the new expression
   * @generated
   */
  @Override
  public void setPortRateExpression(final Expression newPortRateExpression) {
    if (newPortRateExpression != this.portRateExpression) {
      NotificationChain msgs = null;
      if (this.portRateExpression != null) {
        msgs = ((InternalEObject) this.portRateExpression).eInverseRemove(this,
            InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION, null, msgs);
      }
      if (newPortRateExpression != null) {
        msgs = ((InternalEObject) newPortRateExpression).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION,
            null, msgs);
      }
      msgs = basicSetPortRateExpression(newPortRateExpression, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION, newPortRateExpression, newPortRateExpression));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the annotation
   * @generated
   */
  @Override
  public PortMemoryAnnotation getAnnotation() {
    return this.annotation;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newAnnotation
   *          the new annotation
   * @generated
   */
  @Override
  public void setAnnotation(final PortMemoryAnnotation newAnnotation) {
    final PortMemoryAnnotation oldAnnotation = this.annotation;
    this.annotation = newAnnotation == null ? DataPortImpl.ANNOTATION_EDEFAULT : newAnnotation;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DATA_PORT__ANNOTATION, oldAnnotation, this.annotation));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public AbstractActor getContainingActor() {
    if (eContainer() instanceof AbstractActor) {
      return (AbstractActor) eContainer();
    }
    return null;
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
      case PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION:
        return basicSetPortRateExpression(null, msgs);
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
      case PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION:
        return getPortRateExpression();
      case PiMMPackage.DATA_PORT__ANNOTATION:
        return getAnnotation();
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
      case PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION:
        setPortRateExpression((Expression) newValue);
        return;
      case PiMMPackage.DATA_PORT__ANNOTATION:
        setAnnotation((PortMemoryAnnotation) newValue);
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
      case PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION:
        setPortRateExpression((Expression) null);
        return;
      case PiMMPackage.DATA_PORT__ANNOTATION:
        setAnnotation(DataPortImpl.ANNOTATION_EDEFAULT);
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
      case PiMMPackage.DATA_PORT__PORT_RATE_EXPRESSION:
        return this.portRateExpression != null;
      case PiMMPackage.DATA_PORT__ANNOTATION:
        return this.annotation != DataPortImpl.ANNOTATION_EDEFAULT;
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
    result.append(" (annotation: ");
    result.append(this.annotation);
    result.append(')');
    return result.toString();
  }

} // DataPortImpl
