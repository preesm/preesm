/*******************************************************************************
 * Copyright or © or Copr. 2014 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2014)
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

import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
import org.ietr.preesm.experiment.model.pimm.HRefinement;
import org.ietr.preesm.experiment.model.pimm.PiMMFactory;
import org.ietr.preesm.experiment.model.pimm.PiMMPackage;
import org.ietr.preesm.experiment.model.pimm.visitor.PiMMVisitor;

// TODO: Auto-generated Javadoc
/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>HRefinement</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl#getLoopPrototype <em>Loop Prototype</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.HRefinementImpl#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 *
 * @generated
 */
public class HRefinementImpl extends RefinementImpl implements HRefinement {
  /**
   * The cached value of the '{@link #getLoopPrototype() <em>Loop Prototype</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getLoopPrototype()
   * @generated
   * @ordered
   */
  protected FunctionPrototype loopPrototype;
  /**
   * The cached value of the '{@link #getInitPrototype() <em>Init Prototype</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getInitPrototype()
   * @generated
   * @ordered
   */
  protected FunctionPrototype initPrototype;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected HRefinementImpl() {
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
    return PiMMPackage.Literals.HREFINEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the loop prototype
   * @generated
   */
  @Override
  public FunctionPrototype getLoopPrototype() {
    if ((this.loopPrototype != null) && this.loopPrototype.eIsProxy()) {
      final InternalEObject oldLoopPrototype = (InternalEObject) this.loopPrototype;
      this.loopPrototype = (FunctionPrototype) eResolveProxy(oldLoopPrototype);
      if (this.loopPrototype != oldLoopPrototype) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, this.loopPrototype));
        }
      }
    }
    return this.loopPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function prototype
   * @generated
   */
  public FunctionPrototype basicGetLoopPrototype() {
    return this.loopPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newLoopPrototype
   *          the new loop prototype
   * @generated
   */
  @Override
  public void setLoopPrototype(final FunctionPrototype newLoopPrototype) {
    final FunctionPrototype oldLoopPrototype = this.loopPrototype;
    this.loopPrototype = newLoopPrototype;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype, this.loopPrototype));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl#getAbstractActor()
   */
  @Override
  public AbstractActor getAbstractActor() {
    if (getLoopPrototype() != null) {
      // Create the actor returned by the function
      final AbstractActor result = PiMMFactory.eINSTANCE.createActor();

      // Create all its ports corresponding to parameters of the
      // prototype
      final FunctionPrototype loopProto = getLoopPrototype();
      final List<FunctionParameter> loopParameters = loopProto.getParameters();
      for (final FunctionParameter param : loopParameters) {
        if (!param.isIsConfigurationParameter()) {
          // Data Port
          if (param.getDirection().equals(Direction.IN)) {
            // Data Input
            final DataInputPort port = PiMMFactory.eINSTANCE.createDataInputPort();
            port.setName(param.getName());
            result.getDataInputPorts().add(port);
          } else {
            // Data Output
            final DataOutputPort port = PiMMFactory.eINSTANCE.createDataOutputPort();
            port.setName(param.getName());
            result.getDataOutputPorts().add(port);
          }
        } else {
          // Config Port
          if (param.getDirection().equals(Direction.IN)) {
            // Config Input
            final ConfigInputPort port = PiMMFactory.eINSTANCE.createConfigInputPort();
            port.setName(param.getName());
            result.getConfigInputPorts().add(port);
          } else {
            // Config Output
            final ConfigOutputPort port = PiMMFactory.eINSTANCE.createConfigOutputPort();
            port.setName(param.getName());
            result.getConfigOutputPorts().add(port);
          }
        }
      }

      return result;
    } else {
      return null;
    }

  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the inits the prototype
   * @generated
   */
  @Override
  public FunctionPrototype getInitPrototype() {
    if ((this.initPrototype != null) && this.initPrototype.eIsProxy()) {
      final InternalEObject oldInitPrototype = (InternalEObject) this.initPrototype;
      this.initPrototype = (FunctionPrototype) eResolveProxy(oldInitPrototype);
      if (this.initPrototype != oldInitPrototype) {
        if (eNotificationRequired()) {
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, PiMMPackage.HREFINEMENT__INIT_PROTOTYPE, oldInitPrototype, this.initPrototype));
        }
      }
    }
    return this.initPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the function prototype
   * @generated
   */
  public FunctionPrototype basicGetInitPrototype() {
    return this.initPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @param newInitPrototype
   *          the new inits the prototype
   * @generated
   */
  @Override
  public void setInitPrototype(final FunctionPrototype newInitPrototype) {
    final FunctionPrototype oldInitPrototype = this.initPrototype;
    this.initPrototype = newInitPrototype;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.HREFINEMENT__INIT_PROTOTYPE, oldInitPrototype, this.initPrototype));
    }
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
      case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
        if (resolve) {
          return getLoopPrototype();
        }
        return basicGetLoopPrototype();
      case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
        if (resolve) {
          return getInitPrototype();
        }
        return basicGetInitPrototype();
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
      case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
        setLoopPrototype((FunctionPrototype) newValue);
        return;
      case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
        setInitPrototype((FunctionPrototype) newValue);
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
      case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
        setLoopPrototype((FunctionPrototype) null);
        return;
      case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
        setInitPrototype((FunctionPrototype) null);
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
      case PiMMPackage.HREFINEMENT__LOOP_PROTOTYPE:
        return this.loopPrototype != null;
      case PiMMPackage.HREFINEMENT__INIT_PROTOTYPE:
        return this.initPrototype != null;
    }
    return super.eIsSet(featureID);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.preesm.experiment.model.pimm.impl.RefinementImpl#accept(org.ietr.preesm.experiment.model.pimm.util.PiMMVisitor)
   */
  @Override
  public void accept(final PiMMVisitor v) {
    v.visitHRefinement(this);
  }

} // HRefinementImpl
