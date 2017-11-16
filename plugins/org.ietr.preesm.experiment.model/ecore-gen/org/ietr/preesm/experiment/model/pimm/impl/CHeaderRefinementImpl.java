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
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.CHeaderRefinement;
import org.ietr.preesm.experiment.model.pimm.ConfigInputPort;
import org.ietr.preesm.experiment.model.pimm.ConfigOutputPort;
import org.ietr.preesm.experiment.model.pimm.DataInputPort;
import org.ietr.preesm.experiment.model.pimm.DataOutputPort;
import org.ietr.preesm.experiment.model.pimm.Direction;
import org.ietr.preesm.experiment.model.pimm.FunctionParameter;
import org.ietr.preesm.experiment.model.pimm.FunctionPrototype;
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
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl#getFilePath <em>File Path</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl#getLoopPrototype <em>Loop Prototype</em>}</li>
 * <li>{@link org.ietr.preesm.experiment.model.pimm.impl.CHeaderRefinementImpl#getInitPrototype <em>Init Prototype</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CHeaderRefinementImpl extends EObjectImpl implements CHeaderRefinement {
  /**
   * The default value of the '{@link #getFilePath() <em>File Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFilePath()
   * @generated
   * @ordered
   */
  protected static final IPath FILE_PATH_EDEFAULT = null;
  /**
   * The cached value of the '{@link #getFilePath() <em>File Path</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getFilePath()
   * @generated
   * @ordered
   */
  protected IPath              filePath           = CHeaderRefinementImpl.FILE_PATH_EDEFAULT;
  /**
   * The cached value of the '{@link #getLoopPrototype() <em>Loop Prototype</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getLoopPrototype()
   * @generated
   * @ordered
   */
  protected FunctionPrototype  loopPrototype;
  /**
   * The cached value of the '{@link #getInitPrototype() <em>Init Prototype</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getInitPrototype()
   * @generated
   * @ordered
   */
  protected FunctionPrototype  initPrototype;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @generated
   */
  protected CHeaderRefinementImpl() {
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
    return PiMMPackage.Literals.CHEADER_REFINEMENT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public IPath getFilePath() {
    return this.filePath;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public void setFilePath(final IPath newFilePath) {
    final IPath oldFilePath = this.filePath;
    this.filePath = newFilePath;
    if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__FILE_PATH, oldFilePath, this.filePath));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the loop prototype
   * @generated
   */
  @Override
  public FunctionPrototype getLoopPrototype() {
    return this.loopPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetLoopPrototype(final FunctionPrototype newLoopPrototype, NotificationChain msgs) {
    final FunctionPrototype oldLoopPrototype = this.loopPrototype;
    this.loopPrototype = newLoopPrototype;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, oldLoopPrototype,
          newLoopPrototype);
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
   * @param newLoopPrototype
   *          the new loop prototype
   * @generated
   */
  @Override
  public void setLoopPrototype(final FunctionPrototype newLoopPrototype) {
    if (newLoopPrototype != this.loopPrototype) {
      NotificationChain msgs = null;
      if (this.loopPrototype != null) {
        msgs = ((InternalEObject) this.loopPrototype).eInverseRemove(this,
            InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, null, msgs);
      }
      if (newLoopPrototype != null) {
        msgs = ((InternalEObject) newLoopPrototype).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE,
            null, msgs);
      }
      msgs = basicSetLoopPrototype(newLoopPrototype, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE, newLoopPrototype, newLoopPrototype));
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public String getFileName() {
    return (getFilePath() == null) ? null : getFilePath().lastSegment();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->.
   *
   * @return the inits the prototype
   * @generated
   */
  @Override
  public FunctionPrototype getInitPrototype() {
    return this.initPrototype;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public NotificationChain basicSetInitPrototype(final FunctionPrototype newInitPrototype, NotificationChain msgs) {
    final FunctionPrototype oldInitPrototype = this.initPrototype;
    this.initPrototype = newInitPrototype;
    if (eNotificationRequired()) {
      final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, oldInitPrototype,
          newInitPrototype);
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
   * @param newInitPrototype
   *          the new inits the prototype
   * @generated
   */
  @Override
  public void setInitPrototype(final FunctionPrototype newInitPrototype) {
    if (newInitPrototype != this.initPrototype) {
      NotificationChain msgs = null;
      if (this.initPrototype != null) {
        msgs = ((InternalEObject) this.initPrototype).eInverseRemove(this,
            InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, null, msgs);
      }
      if (newInitPrototype != null) {
        msgs = ((InternalEObject) newInitPrototype).eInverseAdd(this, InternalEObject.EOPPOSITE_FEATURE_BASE - PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE,
            null, msgs);
      }
      msgs = basicSetInitPrototype(newInitPrototype, msgs);
      if (msgs != null) {
        msgs.dispatch();
      }
    } else if (eNotificationRequired()) {
      eNotify(new ENotificationImpl(this, Notification.SET, PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE, newInitPrototype, newInitPrototype));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID, final NotificationChain msgs) {
    switch (featureID) {
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        return basicSetLoopPrototype(null, msgs);
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
        return basicSetInitPrototype(null, msgs);
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        return getFilePath();
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        return getLoopPrototype();
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
        return getInitPrototype();
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        setFilePath((IPath) newValue);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        setLoopPrototype((FunctionPrototype) newValue);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        setFilePath(CHeaderRefinementImpl.FILE_PATH_EDEFAULT);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        setLoopPrototype((FunctionPrototype) null);
        return;
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
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
      case PiMMPackage.CHEADER_REFINEMENT__FILE_PATH:
        return CHeaderRefinementImpl.FILE_PATH_EDEFAULT == null ? this.filePath != null : !CHeaderRefinementImpl.FILE_PATH_EDEFAULT.equals(this.filePath);
      case PiMMPackage.CHEADER_REFINEMENT__LOOP_PROTOTYPE:
        return this.loopPrototype != null;
      case PiMMPackage.CHEADER_REFINEMENT__INIT_PROTOTYPE:
        return this.initPrototype != null;
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
    result.append(" (filePath: ");
    result.append(this.filePath);
    result.append(')');
    return result.toString();
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
