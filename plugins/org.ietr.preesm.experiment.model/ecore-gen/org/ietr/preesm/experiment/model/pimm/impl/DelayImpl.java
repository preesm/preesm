/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien Heulot <julien.heulot@insa-rennes.fr> (2013)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
 * Romina Racca <romina.racca@gmail.com> (2013)
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
import org.ietr.preesm.experiment.model.pimm.Delay;
import org.ietr.preesm.experiment.model.pimm.Expression;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Delay</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DelayImpl#getSizeExpression <em>Size Expression</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.DelayImpl#getContainingFifo <em>Containing Fifo</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DelayImpl extends ConfigurableImpl implements Delay {

  /**
   * The cached value of the '{@link #getSizeExpression() <em>Size Expression</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getSizeExpression()
   * @ordered
   */
  protected Expression sizeExpression;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected DelayImpl() {
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
    return PiMMPackage.Literals.DELAY;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the expression
   * @generated
   */
  @Override
  public Expression getSizeExpression() {
    return this.sizeExpression;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetSizeExpression(final Expression newSizeExpression, NotificationChain msgs) {
    final Expression oldSizeExpression = this.sizeExpression;
    this.sizeExpression = newSizeExpression;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.DELAY__SIZE_EXPRESSION, oldSizeExpression,
          newSizeExpression);
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
  public void setSizeExpression(final Expression newSizeExpression) {
    if (newSizeExpression != this.sizeExpression) {
      NotificationChain msgs = null;
      if (this.sizeExpression != null) {
        msgs = ((InternalEObject) this.sizeExpression).eInverseRemove(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.DELAY__SIZE_EXPRESSION, null,
            msgs);
      }
      if (newSizeExpression != null) {
        msgs = ((InternalEObject) newSizeExpression).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.DELAY__SIZE_EXPRESSION, null, msgs);
      }
      msgs = basicSetSizeExpression(newSizeExpression, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.DELAY__SIZE_EXPRESSION, newSizeExpression, newSizeExpression));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public Fifo getContainingFifo() {
    if (eContainerFeatureID() != PiMMPackage.DELAY__CONTAINING_FIFO) {
      return null;
    }
    return (Fifo) eInternalContainer();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID, NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.DELAY__CONTAINING_FIFO:
        if (eInternalContainer() != null) {
          msgs = eBasicRemoveFromContainer(msgs);
        }
        return eBasicSetContainer(otherEnd, PiMMPackage.DELAY__CONTAINING_FIFO, msgs);
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
      case PiMMPackage.DELAY__SIZE_EXPRESSION:
        return basicSetSizeExpression(null, msgs);
      case PiMMPackage.DELAY__CONTAINING_FIFO:
        return eBasicSetContainer(null, PiMMPackage.DELAY__CONTAINING_FIFO, msgs);
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
      case PiMMPackage.DELAY__CONTAINING_FIFO:
        return eInternalContainer().eInverseRemove(this, PiMMPackage.FIFO__DELAY, Fifo.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
      case PiMMPackage.DELAY__SIZE_EXPRESSION:
        return getSizeExpression();
      case PiMMPackage.DELAY__CONTAINING_FIFO:
        return getContainingFifo();
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
      case PiMMPackage.DELAY__SIZE_EXPRESSION:
        setSizeExpression((Expression) newValue);
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
      case PiMMPackage.DELAY__SIZE_EXPRESSION:
        setSizeExpression((Expression) null);
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
      case PiMMPackage.DELAY__SIZE_EXPRESSION:
        return this.sizeExpression != null;
      case PiMMPackage.DELAY__CONTAINING_FIFO:
        return getContainingFifo() != null;
    }
    return super.eIsSet(featureID);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.ParameterizableImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitDelay(this);
  }

} // DelayImpl
